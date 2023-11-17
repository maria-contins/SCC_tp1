package scc.azfunctions;


import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Azure Functions
 */
public class TimerFunction {
    @FunctionName("periodicGarbageCollection")
    public void cosmosFunction( @TimerTrigger(name = "garbageCollection",
    								schedule = "30 */5 * * * *")
    				String timerInfo,
    				ExecutionContext context) {

					final String DELETE_USER_TASK = "deleteUser";
					final String DELETE_HOUSE_TASK = "deleteHouse";
					final String MONGO_CONNECTION_STRING = System.getenv("mongoConnectionString");
					final String DB_NAME = System.getenv("DB_NAME");

					ConnectionString connectionString = new ConnectionString(MONGO_CONNECTION_STRING);
					CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
					CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

					MongoClientSettings clientSettings = MongoClientSettings.builder()
					.applyConnectionString(connectionString)
					.codecRegistry(codecRegistry)
					.build();
					MongoClient mongoClient = MongoClients.create(clientSettings);
					MongoDatabase database = mongoClient.getDatabase(DB_NAME);

        MongoCollection<DeleteTaskDAO> tasks = database.getCollection("tombstone", DeleteTaskDAO.class);


        MongoCollection<UserDAO> users = database.getCollection("users", UserDAO.class);
        MongoCollection<HouseDAO> houses = database.getCollection("houses", HouseDAO.class);
        MongoCollection<RentalDAO> rentals = database.getCollection("rentals", RentalDAO.class);
        MongoCollection<QuestionDAO> questions = database.getCollection("questions", QuestionDAO.class);


        FindIterable<DeleteTaskDAO> scheduledTasksIterate = tasks.find(new Document());

        for (DeleteTaskDAO mt : scheduledTasksIterate) {
            switch (mt.getTaskType()) {
                case DELETE_USER_TASK:
                    String deletedUserId = mt.getEntityId();

                    //Delete User from DB
                    UserDAO user = users.findOneAndDelete(new Document("id", deletedUserId));

                    //Replace owner of houses owned by deletedUser
                    houses.updateMany(new Document("ownerId", deletedUserId), new Document("$set", new Document("ownerId", "deletedUser")));

                    //Replace deleted user's id in rentals they have made
                    rentals.updateMany(new Document("renterId", deletedUserId), new Document("$set", new Document("renterId", "deletedUser")));
                    rentals.updateMany(new Document("renterId", deletedUserId), new Document("$set", new Document("ownerId", "deletedUser")));


                    //Replace deleted user's id in questions they posted
                    questions.updateMany(new Document("authorId", deletedUserId), new Document("$set", new Document("authorId", "deletedUser")));

                    //delete task
                    tasks.deleteOne(new Document("id", mt.getId()));

                    break;
                case DELETE_HOUSE_TASK:
                    String deletedHouseId = mt.getEntityId();

                    //Delete House from DB
                    houses.deleteOne(new Document("id", deletedHouseId));

                    //Delete questions related to deleted house
                    questions.deleteMany(new Document("houseId", deletedHouseId));

                    //Delete rentals related to deleted house
                    rentals.deleteMany(new Document("houseId", deletedHouseId));

                    tasks.deleteOne(new Document("id", mt.getId()));

                default:
                    break;
            }
        }

    }
}
