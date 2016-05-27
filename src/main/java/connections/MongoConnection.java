package connections;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Singleton giving access to a Mongo database
 */
public class MongoConnection {

    public static MongoDatabase getDb(){
		return MongoInitializer.db;
    }

	private MongoConnection() {}

	private static final class MongoInitializer {

		private static final MongoDatabase db = new MongoClient(Config.DATABASE_HOST, Config.DATABASE_PORT).getDatabase(Config.DATABASE_NAME);

	}

}
