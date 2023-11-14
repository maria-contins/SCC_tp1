package scc.data;

import static com.mongodb.client.model.Filters.eq;
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
import jakarta.ws.rs.core.NewCookie;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import scc.entities.House.Availability.Availability;
import scc.entities.House.House;
import scc.entities.House.HouseDAO;
import scc.entities.House.Availability.AvailabilityDAO;
import scc.entities.Question.Question;
import scc.entities.Question.QuestionDAO;
import scc.entities.User.Auth;
import scc.entities.User.User;
import scc.entities.User.UserDAO;
import scc.exceptions.DuplicateException;
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
    MongoCollection<AvailabilityDAO> availability;

    //TODO: add picture checks to user
    //TODO: should one check house IDs?

    String BlobStoreconnectionString = System.getenv("storageAccountConnectionString");

    BlobContainerClient containerClient = new BlobContainerClientBuilder().connectionString(BlobStoreconnectionString).containerName("media").buildClient();

    public MongoDBLayer() {
        users = database.getCollection("users", UserDAO.class);
        houses = database.getCollection("houses", HouseDAO.class);
        questions = database.getCollection("questions", QuestionDAO.class);
        availability = database.getCollection("availability", AvailabilityDAO.class);
    }

    // AUX

    public List<User> getAllUsers() {
        List<User> us = new ArrayList<>();
        for (UserDAO u : users.find(new Document())) {
            us.add(u.toUser());
        }
        return us;
    }

   // searches for house with houseId availability
    public List<Availability> getHouseAvailability(String houseId) {
        List<Availability> as = new ArrayList<>();
        for (AvailabilityDAO a : availability.find(eq("houseId", houseId))) {
            as.add(a.toAvailability());
        }
        return as;
    }

    // USERS

    public User createUser(User user) throws DuplicateException, NotFoundException {
        try {
            //check if pic exists else 404

            UserDAO checkUser = users.find(eq("id", user.getId())).first();
            if (checkUser != null)
                throw new DuplicateException();

            user.setDeleted(false);

            String password = user.getPassword();
            user.setPassword(Hash.of(password));

            users.insertOne(User.toDAO(user));
            //add to cache

            return user;
        } catch (Exception e) {
            throw new DuplicateException();
            // add + adequate exceptions
        }
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
        try {
            Document userUpdate = new Document();
            if (user.getNickname() != null && !user.getNickname().isEmpty())
                userUpdate.append("nickname", user.getNickname());
            if (user.getPassword() != null && !user.getPassword().isEmpty())
                userUpdate.append("password", Hash.of(user.getPassword()));
            if (user.getPhotoId() != null && !user.getPhotoId().isEmpty())
                userUpdate.append("photoId", user.getPhotoId());

            Document doc = new Document("$set", userUpdate);
            users.updateOne(new Document("id", id), doc);

            User updatedUser = Objects.requireNonNull(users.find(eq("id", id)).first()).toUser();
            // update/add to cache

            return updatedUser;
        } catch (Exception e) {
            throw new NotFoundException();
        }
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
    public Question createQuestion(Question q) throws Exception {
            try {
            questions.insertOne(Question.toDAO(q));
            //add to cache

            return q;
        } catch (Exception e) {
            throw new Exception();
            // add + adequate exceptions
        }
    }

    public Question readQuestion(String id) throws Exception {
            try {
            QuestionDAO qDAO = questions.find(eq("_id", id)).first();
            if (qDAO == null) // or isDeleted?
                throw new NotFoundException(); 
            //add to cache        
            return (qDAO.toQuestion());
        } catch (Exception e) {
            throw new Exception();
            // add + adequate exceptions
        }
    }


    public String replyQuestion(String id, String reply) throws Exception {
            try {
            QuestionDAO qDAO = questions.find(eq("_id", id)).first();
            if (qDAO == null) // or isDeleted?
                throw new NotFoundException();         
            qDAO.newReply(reply);
            questions.insertOne(qDAO);
            //add to cache

            return reply;
        } catch (Exception e) {
            throw new Exception();
            // add + adequate exceptions
        }
    }

    public List<Question> listHouseQuestions() throws Exception {
        try {
            //TODO
            return null;
        } catch (Exception e) {
            throw new Exception();
            // add + adequate exceptions
        }
    }

    // HOUSES TODO new ArrayList<>() is a placeholder for collection of availability

    public House createHouse(House house) throws DuplicateException {
        try {
            //check if pic exists else 404

            HouseDAO checkHouse = houses.find(eq("id", house.getId())).first();
            if (checkHouse != null && !checkHouse.isDeleted())
                throw new DuplicateException();

            house.setDeleted(false);

            List<Availability> availabilityList = house.getAvailability();

            houses.insertOne(House.toDAO(house));
            this.availability.insertMany(availabilityList.stream().map(Availability::toDAO).toList());

            //add to cache

            return house;
        } catch (DuplicateException e) {
            throw new DuplicateException();
            // add + adequate exceptions
        }
    }

    public House getHouse(String id) throws NotFoundException {

        // look for user in cache
        //if not found:
        HouseDAO houseDAO = houses.find(eq("id", id)).first();
        List<Availability> availabilityList = getHouseAvailability(id);

        if (houseDAO == null || houseDAO.isDeleted())
            throw new NotFoundException();

        //add to cache

        return (houseDAO.toHouse(availabilityList));
        // add + adequate exceptions
    }

    public House updateHouse(String id, House house) throws NotFoundException {
        try {
            Document houseUpdate = new Document();
            if (house.getName() != null && !house.getName().isEmpty())
                houseUpdate.append("name", house.getName());
            if (house.getLocation() != null && !house.getLocation().isEmpty())
                houseUpdate.append("location", house.getLocation());
            if (house.getDescription() != null && !house.getDescription().isEmpty())
                houseUpdate.append("description", house.getDescription());
            if (house.getMedia() != null && !house.getMedia().isEmpty())
                houseUpdate.append("media", house.getMedia());
            if (house.getAvailability() != null && !house.getAvailability().isEmpty())
                houseUpdate.append("availability", house.getAvailability());

            Document doc = new Document("$set", houseUpdate);
            houses.updateOne(new Document("id", id), doc);

            List<Availability> availabilityList = getHouseAvailability(id);
            availability.insertMany(house.getAvailability().stream().map(Availability::toDAO).toList());

            // update/add to cache

            return Objects.requireNonNull(houses.find(eq("id", id)).first()).toHouse(availabilityList);
        } catch (Exception e) {
            throw new NotFoundException();
        }
    }


    public House deleteHouse(String id) throws NotFoundException {
        Document doc = new Document("deleted", true);
        HouseDAO houseToDelete = houses.findOneAndUpdate(new Document("id", id), new Document("$set", doc));
        if (houseToDelete == null)
            throw new NotFoundException();
        else {
            //remove from cache
            //scheduleDeleteTask
            return houseToDelete.toHouse(new ArrayList<>());
        }
    }

    // RENTALS
}
