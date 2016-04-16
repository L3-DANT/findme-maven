package daos;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import connections.MongoConnection;
import exceptions.NotFoundException;
import models.FriendRequest;
import org.bson.Document;
import utils.Utils;

import static com.mongodb.client.model.Filters.*;
import static utils.Utils.gson;

import javax.ejb.Stateless;

/**
 * DAO class that manages {@link FriendRequest}
 * Note that in most cases, order of {@link FriendRequest#asker} and {@link FriendRequest#receiver} doesn't matter
 */
@Stateless
public class FriendRequestDAO extends DAO{

    public FriendRequestDAO(){
        super("friendRequest");
        // Ensures that a FriendRequest can be identified by a pair of asker-receiver
        coll.createIndex(new Document("asker",1).append("receiver",1),new IndexOptions().unique(true));
    }


    /**
     * Inserts a friend request in the database
     * @param fr the friend request to insert
     * @return false if FriendRequest already exists in database, true otherwise
     * @throws MongoException if the friend request already exists
     */
    public boolean insertOne(FriendRequest fr) throws MongoException{
        Document doc = new Document(Document.parse(gson.toJson(fr)));
        try{
            coll.insertOne(doc);
        } catch(MongoException e){
            System.out.println("FriendRequest already exists in database.");
            return false;
        }
        return true;
    }


    /**
     * Searches for a {@link FriendRequest} in the database
     * @param pseudo1 the {@link models.User} that created or received the {@link FriendRequest}
     * @param pseudo2 the {@link models.User} that created or received the {@link FriendRequest}
     * @return the {@link FriendRequest} related to the two users
     * @throws NotFoundException if the {@link FriendRequest} is not found in the database
     */
    public FriendRequest findOneByPseudos(String pseudo1, String pseudo2)  throws NotFoundException{
        Document doc = coll.find(or(and(eq("asker",pseudo1),eq("receiver",pseudo2)),and(eq("asker",pseudo2),eq("receiver",pseudo1)))).first();
        if(doc == null){
            throw new NotFoundException("Friend request not found");
        }
        FriendRequest fr = gson.fromJson(doc.toJson(),FriendRequest.class);
        return fr;
    }


    /**
     * Removes a {@link FriendRequest} from the database
     * @param fr the {@link FriendRequest} to remove
     */
    public void deleteOne(FriendRequest fr){
        Document doc = new Document("asker",fr.getAsker()).append("receiver",fr.getReceiver());
        coll.deleteOne(doc);
        // Also removes opposite FriendRequest, great for lazy / inattentive developpers
        doc = new Document("asker",fr.getReceiver()).append("receiver",fr.getAsker());
        coll.deleteOne(doc);
    }
}
