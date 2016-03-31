package daos;

import com.google.gson.Gson;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.IndexOptions;
import connections.MongoConnection;
import models.FriendRequest;
import models.User;
import org.bson.Document;

import javax.ejb.Stateless;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class FriendRequestDAO {

    private MongoCollection<Document> coll;

    public FriendRequestDAO(){
        coll = MongoConnection.getDb().getCollection("friendRequest");
        coll.createIndex(new Document("asker",1).append("receiver",1),new IndexOptions().unique(true));
    }

//    public void insertOne(FriendRequest fr){
//        Document doc = new Document(Document.parse(new Gson().toJson(fr)));
//
//    }
}
