package connections;

import com.mongodb.client.MongoDatabase;

public class ConnectionFactory {

    public static MongoDatabase getMongoConnection(){
        return MongoConnection.getDb();
    }
}
