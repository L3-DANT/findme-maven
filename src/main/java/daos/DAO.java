package daos;

import com.mongodb.client.MongoCollection;
import connections.MongoConnection;
import org.bson.Document;

/**
 * Abstract DAO class with a collection
 */
public abstract class DAO {

    protected MongoCollection<Document> coll;

    public DAO(String collection){
        coll = MongoConnection.getDb().getCollection(collection);
    }
}
