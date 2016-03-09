package connections;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {

    private static MongoDatabase db;


    public static MongoDatabase getDb(){
        if(db == null){
            db = new MongoClient("127.0.0.1",27017).getDatabase("findme");
            return db;
        } else {
            return db;
        }
    }

}
