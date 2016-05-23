package connections;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import javax.inject.Singleton;

/**
 * Singleton giving access to a Mongo database
 */
public class MongoConnection {

    public static MongoDatabase getDb(){
       return MongoInitializer.db;
    }

	private MongoConnection() {}

	private static final class MongoInitializer {

		private static final MongoDatabase db = new MongoClient("127.0.0.1", 27017).getDatabase("findme");

	}

}
