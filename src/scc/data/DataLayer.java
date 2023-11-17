package scc.data;

import static com.mongodb.client.model.Filters.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.azure.core.credential.AzureKeyCredential;
/*import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchClientBuilder;
import com.azure.search.documents.models.SearchOptions;
import com.azure.search.documents.util.SearchPagedIterable;
import com.azure.search.documents.util.SearchPagedResponse;*/
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Cookie;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import scc.entities.Deleted.DeleteTaskDAO;
import scc.entities.House.House;
import scc.entities.House.HouseDAO;
import scc.entities.Question.Question;
import scc.entities.Question.QuestionDAO;
import scc.entities.Rental.Rental;
import scc.entities.Rental.RentalDAO;
import scc.entities.User.*;
import scc.entities.User.Auth;
import scc.exceptions.DuplicateException;
import scc.exceptions.ForbiddenException;
import scc.exceptions.NotFoundException;
import scc.utils.Hash;

import java.util.*;

public class DataLayer {
    //private static final String SearchServiceQueryKey = System.getenv("SEARCH_QUERY_KEY");
    //private static final String SearchServiceUrl = System.getenv("SEARCH_URL");
    //private static final String IndexName = System.getenv("SEARCH_INDEX");
    //SearchClient searchClient;
    //SearchOptions searchOptions;

    private static final String DELETE_USER_TASK = "deleteUser";

    private static final String DELETE_HOUSE_TASK = "deleteHouse";
    ConnectionString connectionString = new ConnectionString(System.getenv("mongoConnectionString"));
    //ConnectionString connectionString = new ConnectionString("mongodb://scc-backend-db-57503:kt1hrzGzkMgclgXFaL5tDmTmhGZK61ERGvUwHix4SebXjWUG9JsndhTsA14RmZWa85Q6gctlBJ4BACDbYK5yIg==@scc-backend-db-57503.mongo.cosmos.azure.com:10255/?ssl=true&replicaSet=globaldb&retrywrites=false&maxIdleTimeMS=120000&appName=@scc-backend-db-57503@");
    CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
    CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

    MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connectionString).codecRegistry(codecRegistry).build();

    MongoClient mongoClient = MongoClients.create(settings);

    MongoDatabase database = mongoClient.getDatabase("RentalApp");

    // collections
    MongoCollection<UserDAO> users;
    MongoCollection<HouseDAO> houses;
    MongoCollection<QuestionDAO> questions;

    MongoCollection<RentalDAO> rentals;

    MongoCollection<DeleteTaskDAO> tombstone;

    String BlobStoreconnectionString = System.getenv("storageAccountConnectionString");

    BlobContainerClient containerClient = new BlobContainerClientBuilder().connectionString(BlobStoreconnectionString).containerName("media").buildClient();


    CacheLayer cache;

    public DataLayer() {
        users = database.getCollection("users", UserDAO.class);
        houses = database.getCollection("houses", HouseDAO.class);
        rentals = database.getCollection("rentals", RentalDAO.class);
        questions = database.getCollection("questions", QuestionDAO.class);
        tombstone = database.getCollection("tombstone", DeleteTaskDAO.class);
        cache = new CacheLayer();

        //if(SearchServiceQueryKey != null && SearchServiceUrl != null && IndexName != null){
            //searchClient = new SearchClientBuilder().credential(new AzureKeyCredential(SearchServiceQueryKey)).endpoint(SearchServiceUrl).indexName(IndexName).buildClient();
            //searchOptions = new SearchOptions().setIncludeTotalCount(true).setSelect("id", "name", "location", "description").setSearchFields("description").setTop(20);
        //}
    }


    //Auth related
    public void bindCookie(NewCookie cookie, String userId) {
        // add to cache
        this.cache.addCache(CacheLayer.CacheType.COOKIE, cookie.getValue(), new Session(userId, cookie.getValue()));
    }

    public String getUserIdFromCookie(Cookie cookie) throws NotFoundException {
        Session ss = cache.readCache(CacheLayer.CacheType.COOKIE, cookie.getValue(), Session.class);
        return ss == null ? null : ss.getUserId();
    }

    private Session sessionFromCookie(Cookie cookie) {
        if (cookie == null) {
            return null;
        }
        // get from cache
        return this.cache.readCache(CacheLayer.CacheType.COOKIE, cookie.getValue(), Session.class);
    }


    public boolean hasAuth(Cookie cookie) {
        return this.sessionFromCookie(cookie) != null;
    }



    public boolean matchUserToCookie(Cookie cookie, String userId) {
        Session session = this.sessionFromCookie(cookie);
        if (session == null) {
            return false;
        }

        return session.getUserId().equals(userId);
    }


    // AUXILIARY METHODS

    private boolean pictureExists(String photoId) {
        return photoId.isEmpty() || containerClient.getBlobClient(photoId).exists();
    }

    private boolean houseExists(String houseId) {
        if (cache.readCache(CacheLayer.CacheType.HOUSE, houseId, House.class) != null)
            return true;

        return houses.find(new Document("id", houseId)).first() != null;
    }

    private boolean userExists(String userId) {
        if (cache.readCache(CacheLayer.CacheType.USER, userId, User.class) != null)
            return true;

        return users.find(new Document("id", userId)).first() != null;
    }

    public boolean verifyUser(Auth auth) throws NotFoundException, ForbiddenException {
        String password = auth.getPassword();
        String nickname = auth.getNickname();

        UserDAO userDAO = users.find(new Document("nickname", nickname)).first();
        if (userDAO == null || userDAO.isDeleted()) // or isDeleted?
            throw new NotFoundException();

        if (userDAO.getPassword() == null || userDAO.getPassword().isEmpty())
            throw new ForbiddenException();

        return userDAO.getPassword().equals(Hash.of(password));
    }

    // FOR DEBUG
    public User getUser(String id) throws NotFoundException {

        // look for user in cache
        //if not found:
        UserDAO userDAO = users.find(eq("id", id)).first();
        if (userDAO == null || userDAO.isDeleted()) // or isDeleted?
            throw new NotFoundException();

        //add user to cache

        return UserDAO.toUser(userDAO);
    }

    public List<User> getAllUsers() {
        List<User> us = new ArrayList<>();
        for (UserDAO u : users.find(new Document())) {
            us.add(u.toUser());
        }
        return us;
    }

    // USERS

    public User createUser(User user) throws DuplicateException, NotFoundException {

        if (!this.pictureExists(user.getPhotoId())) throw new NotFoundException();

        if (userExists(user.getId())) throw new DuplicateException();

        user.setDeleted(false);

        String password = user.getPassword();
        user.setPassword(Hash.of(password));

        users.insertOne(User.toDAO(user));

        cache.addCache(CacheLayer.CacheType.USER, user.getId(), User.class);

        return user;
    }

    public void deleteUser(String id, Cookie cookie) throws NotFoundException {

        if (!userExists(id)) throw new NotFoundException();

        users.updateOne(new Document("id", id), new Document("$set", new Document("deleted", true)));

        cache.removeCache(CacheLayer.CacheType.USER, id);
        cache.removeCache(CacheLayer.CacheType.COOKIE, cookie.getValue());

        tombstone.insertOne(new DeleteTaskDAO(DELETE_USER_TASK, id));

    }

    public User updateUser(String id, User user) throws NotFoundException {
        Document userUpdate = new Document();
        if (user.getNickname() != null && !user.getNickname().isEmpty())
            userUpdate.append("nickname", user.getNickname());
        if (user.getPassword() != null && !user.getPassword().isEmpty())
            userUpdate.append("password", Hash.of(user.getPassword()));
        if (user.getPhotoId() != null && !user.getPhotoId().isEmpty()) {
            if (this.pictureExists(user.getPhotoId())) throw new NotFoundException();
            userUpdate.append("photoId", user.getPhotoId());
        }

        Document doc = new Document("$set", userUpdate);

        UserDAO result = users.findOneAndUpdate(new Document("id", id), doc, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));

        if (result == null) throw new NotFoundException();

        cache.removeCache(CacheLayer.CacheType.USER, id);

        return UserDAO.toUser(result);
    }


    // QUESTIONS
    public Question createQuestion(String houseId, Question question) throws NotFoundException, ForbiddenException, DuplicateException {

        if (question.getRepliedToId() != null && !question.getRepliedToId().isEmpty()) {

            QuestionDAO repliedTo = questions.find(eq("id", question.getRepliedToId())).first();
            if (repliedTo == null) throw new NotFoundException();

            if (!repliedTo.getRepliedToId().isEmpty()) throw new ForbiddenException();

            if (repliedTo.getId().equals(question.getId())) throw new DuplicateException();

            if (repliedTo.isAnswered()) throw new ForbiddenException();

            UserDAO replier = users.find(eq("id", question.getAuthorId())).first();
            if (replier == null || replier.isDeleted()) throw new NotFoundException();

            questions.updateOne(new Document("id", question.getRepliedToId()), new Document("$set", new Document("answered", true)));

            questions.insertOne(Question.toDAO(question));
        } else {
            // Here it needs verification. When replying, we can assume that the houseId of the user was verified upon creation
            HouseDAO houseDAO = houses.find(eq("id", question.getHouseId())).first();

            if (houseDAO == null || houseDAO.isDeleted()) throw new NotFoundException();

            if (!houseId.equals(houseDAO.getId())) throw new ForbiddenException();

            QuestionDAO checkQuestion = questions.find(eq("id", question.getId())).first();
            if (checkQuestion != null) throw new DuplicateException();

            UserDAO userDAO = users.find(eq("id", question.getAuthorId())).first();
            if (userDAO == null || userDAO.isDeleted()) throw new NotFoundException();

            questions.insertOne(Question.toDAO(question));
        }

        cache.removeCache(CacheLayer.CacheType.QUESTION_LIST, houseId);

        return question;
    }

    public List<Question> listQuestions(String houseId) throws NotFoundException {
        if (!this.houseExists(houseId)) throw new NotFoundException();
        
        Question[] listQuestionCache = cache.readCache(CacheLayer.CacheType.QUESTION_LIST, houseId, Question[].class);
            if (listQuestionCache != null) return Arrays.asList(listQuestionCache);

        List<Question> listQuestions = new ArrayList<>();
        for (QuestionDAO q : questions.find(eq("houseId", houseId)))
            listQuestions.add(q.toQuestion());

        cache.addCache(CacheLayer.CacheType.QUESTION_LIST, houseId, listQuestions.toArray());

        return listQuestions;
    }

    // HOUSES

    public House createHouse(House house) throws DuplicateException, NotFoundException {

        HouseDAO checkHouse = houses.find(eq("id", house.getId())).first();
        if (checkHouse != null && !checkHouse.isDeleted()) throw new DuplicateException();

        house.setDeleted(false);

        for (String i : house.getMedia())
            if (!this.pictureExists(i)) throw new NotFoundException();

        houses.insertOne(House.toDAO(house));

        cache.addCache(CacheLayer.CacheType.HOUSE, house.getId(), house);

        return house;
    }

    public House getHouse(String id) throws NotFoundException {

        HouseDAO houseDAO;
        House house = cache.readCache(CacheLayer.CacheType.HOUSE, id, House.class);

        if (house == null) {
            houseDAO = houses.find(eq("id", id)).first();
            if (houseDAO == null || houseDAO.isDeleted()) throw new NotFoundException();
            house = houseDAO.toHouse();

            cache.addCache(CacheLayer.CacheType.HOUSE, id, house);
        }

        return house;
    }

    public House updateHouse(String id, House house) throws NotFoundException {

        Document houseUpdate = new Document();
        if (house.getName() != null && !house.getName().isEmpty()) houseUpdate.append("name", house.getName());
        if (house.getLocation() != null && !house.getLocation().isEmpty())
            houseUpdate.append("location", house.getLocation());
        if (house.getDescription() != null && !house.getDescription().isEmpty())
            houseUpdate.append("description", house.getDescription());
        if (house.getMedia() != null && !house.getMedia().isEmpty()) houseUpdate.append("media", house.getMedia());

        Document doc = new Document("$set", houseUpdate);
        houses.updateOne(new Document("id", id), doc);

        HouseDAO result = houses.findOneAndUpdate(new Document("id", id).append("deleted", false), doc, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));

        if (result == null) throw new NotFoundException();

        cache.removeCache(CacheLayer.CacheType.HOUSE, id);
        cache.removeCache(CacheLayer.CacheType.HOUSES_LOCATION, result.getLocation());
        cache.removeCache(CacheLayer.CacheType.HOUSE_USER, result.getOwnerId());
        cache.removeCache(CacheLayer.CacheType.HOUSES_DISCOUNTED, "discount");

        return HouseDAO.toHouse(result);
    }


    public House deleteHouse(String houseId, String userId) throws NotFoundException, ForbiddenException {

        HouseDAO houseDAO = houses.find(new Document("id", houseId)).first();
        if (houseDAO == null) throw new NotFoundException();
        if (!houseDAO.getOwnerId().equals(userId)) throw new ForbiddenException();

        Document doc = new Document("deleted", true);
        HouseDAO houseToDelete = houses.findOneAndUpdate(new Document("id", houseId), new Document("$set", doc));

        if (houseToDelete == null) throw new NotFoundException();

        cache.removeCache(CacheLayer.CacheType.HOUSE, houseId);

        cache.removeCache(CacheLayer.CacheType.HOUSE, houseId);
        cache.removeCache(CacheLayer.CacheType.HOUSES_LOCATION, houseToDelete.getLocation());
        cache.removeCache(CacheLayer.CacheType.HOUSE_USER, houseToDelete.getOwnerId());
        cache.removeCache(CacheLayer.CacheType.HOUSES_DISCOUNTED, "discount");

        tombstone.insertOne(new DeleteTaskDAO(DELETE_HOUSE_TASK, houseId));

        return houseToDelete.toHouse();
    }

    public List<House> getAllHouses() {
        House[] cached = cache.readCache(CacheLayer.CacheType.HOUSES, "houses", House[].class);
        if (cached != null) return Arrays.asList(cached);

        List<House> result = new ArrayList<>();
        for (HouseDAO house : houses.find(new Document("deleted", false))) {
            result.add(HouseDAO.toHouse(house));
        }

        cache.addCache(CacheLayer.CacheType.HOUSES, "houses", result.toArray());

        return result;
    }

    // RENTALS

    public Rental createAvailable(String houseId, Rental rental) throws NotFoundException, ForbiddenException {
        HouseDAO houseDAO = houses.find(new Document("id", houseId)).first();
        if (houseDAO == null || houseDAO.isDeleted()) throw new NotFoundException();
        if (!houseDAO.getOwnerId().equals(rental.getOwnerId())) throw new ForbiddenException();

        if (rental.getRenterId().equals(houseDAO.getOwnerId())) throw new ForbiddenException();
        rental.setFree(true);

        rentals.insertOne(Rental.toDAO(rental));

        cache.removeCache(CacheLayer.CacheType.RENTALS, houseId);
        if (!rental.getDiscount().isEmpty())
            cache.removeCache(CacheLayer.CacheType.HOUSES_DISCOUNTED, "discount");

        return rental;
    }

    public Rental updateRental(String houseId, String rentalId, Rental rental) throws NotFoundException, ForbiddenException {
        HouseDAO houseDAO = houses.find(new Document("id", houseId)).first();
        if (houseDAO == null || houseDAO.isDeleted()) throw new NotFoundException();

        Document rentalUpdate = new Document();

        if (rental.getHouseId() != null && !rental.getHouseId().isEmpty())
            rentalUpdate.append("houseId", rental.getHouseId());
        if (rental.getRenterId() != null && !rental.getRenterId().isEmpty())
            rentalUpdate.append("renterId", rental.getRenterId());
        if (rental.getPrice() != null && !rental.getPrice().isEmpty()) rentalUpdate.append("price", rental.getPrice());
        if (rental.getFromDate() != null && !rental.getFromDate().isEmpty())
            rentalUpdate.append("fromDate", rental.getFromDate());
        if (rental.getToDate() != null && !rental.getToDate().isEmpty())
            rentalUpdate.append("toDate", rental.getToDate());
        if (rental.getDiscount() != null && !rental.getDiscount().isEmpty())
            rentalUpdate.append("discount", rental.getDiscount());
        if (rental.getOwnerId() != null && !rental.getOwnerId().isEmpty())
            rentalUpdate.append("ownerId", rental.getOwnerId());

        Document doc = new Document("$set", rentalUpdate);
        RentalDAO result = rentals.findOneAndUpdate(new Document("id", rentalId), doc, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));

        cache.removeCache(CacheLayer.CacheType.RENTALS, houseId);

        if (!rental.getDiscount().isEmpty())
            cache.removeCache(CacheLayer.CacheType.HOUSES_DISCOUNTED, "discount");

        if (result == null) throw new NotFoundException();

        return RentalDAO.toRental(result);
    }

    public List<Rental> listRentals(String houseId) throws NotFoundException {

        cache.readCache(CacheLayer.CacheType.RENTALS, houseId, House[].class);

        HouseDAO houseDAO = houses.find(new Document("id", houseId)).first();
        if (houseDAO == null || houseDAO.isDeleted()) throw new NotFoundException();

        List<Rental> rentalsList = new ArrayList<>();
        for (RentalDAO rental : rentals.find(eq("houseId", houseId))) {
            rentalsList.add(RentalDAO.toRental(rental));
        }
        return rentalsList;
    }

    public Rental createRent(String houseId, String rentalId, Renter renter) throws NotFoundException {

        Document doc = new Document("renterId", renter.getId());
        doc.append("free", false);

        RentalDAO result = rentals.findOneAndUpdate(new Document("id", rentalId).append("houseId", houseId), new Document("$set", doc), new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));

        // update/add to cache
        if (result == null) throw new NotFoundException();

        cache.removeCache(CacheLayer.CacheType.RENTALS, houseId);
        if (!result.getDiscount().isEmpty())
            cache.removeCache(CacheLayer.CacheType.HOUSES_DISCOUNTED, "discount");

        return RentalDAO.toRental(result);
    }

    public List<House> getLocationHouses(String location) throws NotFoundException {
        House[] housesCached = cache.readCache(CacheLayer.CacheType.HOUSES_LOCATION, location, House[].class);
        if (housesCached != null) return Arrays.asList(housesCached);

        List<House> result = new ArrayList<>();
        for (HouseDAO house : houses.find(new Document("location", location).append("deleted", false))) {
            result.add(HouseDAO.toHouse(house));
        }

        cache.addCache(CacheLayer.CacheType.HOUSES_LOCATION, location, result.toArray());

        return result;
    }

    /*public List<House> searchHouses(String query) {
        List<House> found = new ArrayList<>();

        SearchPagedIterable searchPagedIterable = searchClient.search(query, searchOptions, null);

        for (SearchPagedResponse resultResponse : searchPagedIterable.iterableByPage()) {
            resultResponse.getValue().forEach(searchResult -> {
                found.add(searchResult.getDocument(House.class));
            });
        }

        return found;
    }*/

    public List<House> getDiscountHouses() {
        House[] housesCached = cache.readCache(CacheLayer.CacheType.HOUSES_DISCOUNTED, "discount", House[].class);
        if (housesCached != null) return Arrays.asList(housesCached);

        HashMap<String, House> result = new HashMap<>();
        for (RentalDAO rental : rentals.find(new Document("discount", new Document("$ne", "0")))) {
            if (rental.isFree()) {
                String id = rental.getHouseId();
                result.put(id, Objects.requireNonNull(houses.find(new Document("id", rental.getHouseId())).first()).toHouse());
            }
        }

        cache.addCache(CacheLayer.CacheType.HOUSES_DISCOUNTED, "discount", result.values().toArray());

        return result.values().stream().toList();
    }

    public List<House> getUserHouses(String id) throws NotFoundException {

        House[] cached = cache.readCache(CacheLayer.CacheType.HOUSE_USER, id, House[].class);
        if (cached != null) return Arrays.asList(cached);

        UserDAO userDAO = users.find(new Document("id", id)).first();

        if (userDAO == null) throw new NotFoundException();

        List<House> result = new ArrayList<>();
        for (HouseDAO house : houses.find(eq("ownerId", id))) {
            result.add(HouseDAO.toHouse(house));
        }

        cache.addCache(CacheLayer.CacheType.HOUSE_USER, id, result.toArray());

        return result;
    }
}
