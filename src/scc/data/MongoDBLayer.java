package scc.data;

import static com.mongodb.client.model.Filters.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
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
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import scc.entities.House.House;
import scc.entities.House.HouseDAO;
import scc.entities.Question.Question;
import scc.entities.Question.QuestionDAO;
import scc.entities.Rental.Rental;
import scc.entities.Rental.RentalDAO;
import scc.entities.User.Auth;
import scc.entities.User.User;
import scc.entities.User.UserDAO;
import scc.exceptions.DuplicateException;
import scc.exceptions.ForbiddenException;
import scc.exceptions.NotFoundException;
import scc.utils.Hash;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MongoDBLayer {

    ConnectionString connectionString = new ConnectionString(System.getenv("mongoConnectionString"));
    //ConnectionString connectionString = new ConnectionString("mongodb://scc-backend-db-57503:kt1hrzGzkMgclgXFaL5tDmTmhGZK61ERGvUwHix4SebXjWUG9JsndhTsA14RmZWa85Q6gctlBJ4BACDbYK5yIg==@scc-backend-db-57503.mongo.cosmos.azure.com:10255/?ssl=true&replicaSet=globaldb&retrywrites=false&maxIdleTimeMS=120000&appName=@scc-backend-db-57503@");
    CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
    CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

    MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .codecRegistry(codecRegistry)
            .build();

    MongoClient mongoClient = MongoClients.create(settings);

    MongoDatabase database = mongoClient.getDatabase("RentalApp");

    // collections
    MongoCollection<UserDAO> users;
    MongoCollection<HouseDAO> houses;
    MongoCollection<QuestionDAO> questions;

    MongoCollection<RentalDAO> rentals;

    String BlobStoreconnectionString = System.getenv("storageAccountConnectionString");

    BlobContainerClient containerClient = new BlobContainerClientBuilder().connectionString(BlobStoreconnectionString).containerName("media").buildClient();

    public MongoDBLayer() {
        users = database.getCollection("users", UserDAO.class);
        houses = database.getCollection("houses", HouseDAO.class);
        rentals = database.getCollection("rentals", RentalDAO.class);
        questions = database.getCollection("questions", QuestionDAO.class);
    }

    // AUXILIARY METHODS

    public List<User> getAllUsers() {
        List<User> us = new ArrayList<>();
        for (UserDAO u : users.find(new Document())) {
            us.add(u.toUser());
        }
        return us;
    }

   // searches for house with houseId availability
    public List<Rental> getHouseAvailability(String houseId) {
        List<Rental> as = new ArrayList<>();
        for (RentalDAO a : rentals.find(eq("houseId", houseId))) {
            as.add(RentalDAO.toRental(a));
        }
        return as;
    }

    private boolean pictureExists(String photoId) {
        return !photoId.isEmpty() && containerClient.getBlobClient(photoId).exists();
    }

    private boolean houseExists(String houseId) {
        return houses.find(eq("id", houseId)).first() == null;
    }

    // USERS

    public User createUser(User user) throws DuplicateException, NotFoundException {
            //check if pic exists else 404

            if (!this.pictureExists(user.getPhotoId()))
                throw new NotFoundException();

            UserDAO checkUser = users.find(eq("id", user.getId())).first();
            if (checkUser != null)
                throw new DuplicateException();

            user.setDeleted(false);

            String password = user.getPassword();
            user.setPassword(Hash.of(password));

            users.insertOne(User.toDAO(user));
            //add to cache

            return user;
    }

    public User deleteUser(String id) throws NotFoundException {
        Document doc = new Document("deleted", true);
        UserDAO userToDelete = users.findOneAndUpdate(new Document("id", id), new Document("$set", doc));
        if (userToDelete == null)
            throw new NotFoundException();
        else {
            //remove from cache
            //scheduleDeleteTask
            return userToDelete.toUser();
        }
    }

    public User getUser(String id) throws NotFoundException {

        // look for user in cache
        //if not found:
        UserDAO userDAO = users.find(eq("id", id)).first();
        if (userDAO == null || userDAO.isDeleted()) // or isDeleted?
            throw new NotFoundException();

        //add user to cache

        return UserDAO.toUser(userDAO);
    }

    public User updateUser(String id, User user) throws NotFoundException {
        Document userUpdate = new Document();
        if (user.getNickname() != null && !user.getNickname().isEmpty())
            userUpdate.append("nickname", user.getNickname());
        if (user.getPassword() != null && !user.getPassword().isEmpty())
            userUpdate.append("password", Hash.of(user.getPassword()));
        if (user.getPhotoId() != null && !user.getPhotoId().isEmpty()) {
            if (this.pictureExists(user.getPhotoId()))
                throw new NotFoundException();
            userUpdate.append("photoId", user.getPhotoId());
        }

        Document doc = new Document("$set", userUpdate);

        UserDAO result = users.findOneAndUpdate(new Document("id", id), doc, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
        // update/add to cache
        if (result == null)
            throw new NotFoundException();

        return UserDAO.toUser(result);
    }

    public boolean verifyUser(Auth auth) throws NotFoundException {
        String password = auth.getPassword();
        String nickname = auth.getNickname();

        UserDAO userDAO = users.find(eq("nickname", nickname)).first();
        if (userDAO == null || userDAO.isDeleted()) // or isDeleted?
            throw new NotFoundException();

        return password.equals(Hash.of(userDAO.getPassword()));
    }

    public void bindCookie(NewCookie cookie, String id) {
        // add to cache
    }

    // QUESTIONS
    public Question createQuestion(Question question) throws NotFoundException, ForbiddenException {

        if (question.getRepliedToId() != null && !question.getRepliedToId().isEmpty()) {
            QuestionDAO repliedTo = questions.find(eq("_id", question.getRepliedToId())).first();
            if (repliedTo == null)
                throw new NotFoundException();

            if (repliedTo.isAnswered())
                throw new ForbiddenException();

            questions.updateOne(new Document("_id", question.getRepliedToId()), new Document("$set", new Document("answered", true)));

            questions.insertOne(Question.toDAO(question));

            //add to cache
        }

        return question;
    }

    public List<Question> listQuestions(String houseId) throws NotFoundException {
        if(this.houseExists(houseId))
            throw new NotFoundException();

        List<Question> qs = new ArrayList<>();
        for (QuestionDAO q : questions.find(eq("houseId", houseId))) {
            qs.add(q.toQuestion());
        }

        return qs;
    }

    // HOUSES

    public House createHouse(House house) throws DuplicateException, NotFoundException {

            HouseDAO checkHouse = houses.find(eq("id", house.getId())).first();
            if (checkHouse != null && !checkHouse.isDeleted())
                throw new DuplicateException();

            house.setDeleted(false);

            houses.insertOne(House.toDAO(house));

            for (String i : house.getMedia())
                if (!this.pictureExists(i))
                    throw new NotFoundException();

            //add to cache

            return house;
    }

    public House getHouse(String id) throws NotFoundException {

        // look for user in cache
        //if not found:
        HouseDAO houseDAO = houses.find(eq("id", id)).first();

        if (houseDAO == null || houseDAO.isDeleted())
            throw new NotFoundException();

        //add to cache

        return (houseDAO.toHouse());
        // add + adequate exceptions
    }

    public House updateHouse(String id, House house) throws NotFoundException {

        Document houseUpdate = new Document();
        if (house.getName() != null && !house.getName().isEmpty())
            houseUpdate.append("name", house.getName());
        if (house.getLocation() != null && !house.getLocation().isEmpty())
            houseUpdate.append("location", house.getLocation());
        if (house.getDescription() != null && !house.getDescription().isEmpty())
            houseUpdate.append("description", house.getDescription());
        if (house.getMedia() != null && !house.getMedia().isEmpty())
            houseUpdate.append("media", house.getMedia());

        Document doc = new Document("$set", houseUpdate);
        houses.updateOne(new Document("id", id), doc);

        HouseDAO result = houses.findOneAndUpdate(new Document("id", id), doc, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));

        // update/add to cache

        if (result == null)
            throw new NotFoundException();

        return HouseDAO.toHouse(result);

    }


    public House deleteHouse(String id) throws NotFoundException {
        Document doc = new Document("deleted", true);
        HouseDAO houseToDelete = houses.findOneAndUpdate(new Document("id", id), new Document("$set", doc));
        if (houseToDelete == null)
            throw new NotFoundException();
        else {
            //remove from cache
            //scheduleDeleteTask
            return houseToDelete.toHouse();
        }
    }

    // RENTALS

    public Rental createAvailable(String houseId, String userId, Rental rental) throws NotFoundException, ForbiddenException {
        HouseDAO houseDAO = houses.find(new Document("id", houseId)).first();
        if (houseDAO == null)
            throw new NotFoundException();
        if (!Objects.equals(houseDAO.getOwnerId(), userId))
            throw new ForbiddenException();

        rental.setFree(true);

        rentals.insertOne(Rental.toDAO(rental));

        return rental;
    }

    public Rental updateRental(String houseId, String rentalId, String userId, Rental rental) throws NotFoundException, ForbiddenException {
        HouseDAO houseDAO = houses.find(new Document("id", houseId)).first();
        if (houseDAO == null)
            throw new NotFoundException();

        UserDAO userDAO = users.find(new Document("id", userId)).first();
        if (userDAO == null)
            throw new NotFoundException();


        Document rentalUpdate = new Document();

            if (rental.getHouseId() != null && !rental.getHouseId().isEmpty())
                rentalUpdate.append("houseId", rental.getHouseId());
            if (rental.getRenterId() != null && !rental.getRenterId().isEmpty())
                rentalUpdate.append("renterId", rental.getRenterId());
            if (rental.getPrice() != null && !rental.getPrice().isEmpty())
                rentalUpdate.append("price", rental.getPrice());
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

            // update/add to cache
            if (result == null)
                throw new NotFoundException();

            return RentalDAO.toRental(result);
    }

    public List<Rental> listRentals(String houseId) throws NotFoundException {
        HouseDAO houseDAO = houses.find(new Document("id", houseId)).first();
        if (houseDAO == null)
            throw new NotFoundException();

        List<Rental> rentalsList = new ArrayList<>();
        for (RentalDAO rental : rentals.find(eq("houseId", houseId))) {
            rentalsList.add(RentalDAO.toRental(rental));
        }
        return rentalsList;
    }

    public Rental createRent(String houseId, String rentalId, String userId) throws NotFoundException, DuplicateException, ForbiddenException {
        HouseDAO house = houses.find(eq(new Document("id", houseId))).first();
        if (house == null)
            throw new NotFoundException();

        Document doc = new Document("renterId", userId);
        doc.append("free", false);
        RentalDAO result = rentals.findOneAndUpdate(new Document("id", rentalId).append("houseId", houseId), doc, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));

        // update/add to cache
        if (result == null)
            throw new NotFoundException();

        return RentalDAO.toRental(result);
    }

    public List<House> getLocationHouses(String houseId, String location) throws NotFoundException {
        HouseDAO houseDAO = houses.find(new Document("id", houseId)).first();

        if (houseDAO == null)
            throw new NotFoundException();

        List<House> result = new ArrayList<>();
        for (HouseDAO house : houses.find(eq("location", location))) {
            result.add(HouseDAO.toHouse(house));
        }

        return result;
    }

    public List<House> getDiscountHouses(String houseId) throws NotFoundException {
        HouseDAO houseDAO = houses.find(new Document("id", houseId)).first();

        if (houseDAO == null)
            throw new NotFoundException();

        List<House> result = new ArrayList<>();
        for (HouseDAO house : houses.find(ne("discount", ""))) {
            result.add(HouseDAO.toHouse(house));
        }

        return result;
    }

    public List<String> getUserHouses(String id) throws NotFoundException {
        UserDAO userDAO = users.find(new Document("id", id)).first();

        if (userDAO == null)
            throw new NotFoundException();

        List<String> result = new ArrayList<>();
        for (HouseDAO house : houses.find(eq("ownerId", id))) {
            result.add(house.getId());
        }
        return result;
    }
}
