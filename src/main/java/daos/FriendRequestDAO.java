package daos;

import com.google.gson.Gson;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import connections.MongoConnection;
import models.FriendRequest;
import org.bson.Document;
import static com.mongodb.client.model.Filters.*;

import javax.ejb.Stateless;

@Stateless
public class FriendRequestDAO {

    private MongoCollection<Document> coll;

    public FriendRequestDAO(){
        coll = MongoConnection.getDb().getCollection("friendRequest");
        coll.createIndex(new Document("asker",1).append("receiver",1),new IndexOptions().unique(true));
    }

    public void insertOne(FriendRequest fr){
        Document doc = new Document(Document.parse(new Gson().toJson(fr)));
        try{
            coll.insertOne(doc);
        } catch(MongoException e){
            e.printStackTrace();
            System.out.println("FriendRequest already exists in database.");
        }
    }

    public FriendRequest findOneByAskerReceiver(String asker, String receiver){
        Document doc = coll.find(and(eq("asker",asker),eq("receiver",receiver))).first();
        FriendRequest fr = new Gson().fromJson(doc.toJson(),FriendRequest.class);
        return fr;
    }

    public void replaceOne(FriendRequest fr){
        Document formerDoc = new Document("asker",fr.getAsker()).append("receiver",fr.getReceiver());
        Document newDoc = new Document(Document.parse(new Gson().toJson(fr)));
        coll.replaceOne(formerDoc,newDoc);
    }

    public void deleteOne(FriendRequest fr) {
        Document doc = new Document("asker",fr.getAsker()).append("receiver",fr.getReceiver());
        coll.deleteOne(doc);
    }
}
