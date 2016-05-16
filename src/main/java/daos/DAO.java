package daos;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import connections.MongoConnection;
import org.bson.Document;

/**
 * Abstract DAO class with a collection
 */
public abstract class DAO {

    protected MongoCollection<Document> coll;
    protected Gson gson = new Gson();

    public DAO(String collection){
        coll = MongoConnection.getDb().getCollection(collection);
    }
}
