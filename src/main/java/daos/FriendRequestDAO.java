package daos;

import com.mongodb.client.MongoCursor;
import exceptions.DuplicateDataException;
import exceptions.NotFoundException;
import models.FriendRequest;
import org.bson.Document;

import javax.ejb.Singleton;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

/**
 * DAO class that manages {@link FriendRequest}
 * Note that in most cases, order of {@link FriendRequest#caller} and {@link FriendRequest#receiver} doesn't matter
 */
@Singleton
public class FriendRequestDAO extends DAO{

    public FriendRequestDAO(){
        super("friendrequest");
    }

    public List<FriendRequest> findAll(){
        List<FriendRequest> list = new ArrayList<FriendRequest>();
        MongoCursor<Document> cursor = coll.find().iterator();
        try {
            while (cursor.hasNext()) {
                list.add(gson.fromJson(cursor.next().toJson(),FriendRequest.class));
            }
        } finally {
            cursor.close();
        }
        return list;
    }


    /**
     * Inserts a friend request in the database
     * @param fr the friend request to insert
     * @return the just inserted {@link FriendRequest}
     * @throws DuplicateDataException if the friend request already exists
     */
    public FriendRequest insertOne(FriendRequest fr) throws DuplicateDataException{
        try{
            findOneByPseudos(fr.getCaller(),fr.getReceiver());
            throw new DuplicateDataException("FriendRequest already exists in database");
        } catch (NotFoundException e){
            Document doc = new Document(Document.parse(gson.toJson(fr)));
            coll.insertOne(doc);
        }
        return fr;
    }


    /**
     * Searches for a {@link FriendRequest} in the database
     * @param pseudo1 the {@link models.User} that created or received the {@link FriendRequest}
     * @param pseudo2 the {@link models.User} that created or received the {@link FriendRequest}
     * @return the {@link FriendRequest} related to the two users
     * @throws NotFoundException if the {@link FriendRequest} is not found in the database
     */
    public FriendRequest findOneByPseudos(String pseudo1, String pseudo2)  throws NotFoundException{
        Document doc = coll.find(or(and(eq("caller",pseudo1),eq("receiver",pseudo2)),and(eq("caller",pseudo2),eq("receiver",pseudo1)))).first();
        if(doc == null){
            throw new NotFoundException("Friend request not found");
        }
        FriendRequest fr = gson.fromJson(doc.toJson(),FriendRequest.class);
        return fr;
    }

    /**
     * Checks if the exact {@link FriendRequest} exists in database
     * @param fr the {@link FriendRequest} to check
     * @return true if it exists, false otherwise
     */
    public boolean existFriendRequest(FriendRequest fr){
        Document doc = coll.find(and(eq("caller",fr.getCaller()),eq("receiver",fr.getReceiver()))).first();
        return !(doc == null);
    }

    /**
     * Removes a {@link FriendRequest} from the database
     * @param fr the {@link FriendRequest} to remove
     * @throws NotFoundException if the {@link FriendRequest} is not found in the database
     */
    public void deleteOne(FriendRequest fr) throws NotFoundException{
        int i = 0;
        Document doc = new Document("caller",fr.getCaller()).append("receiver",fr.getReceiver());
        i += coll.deleteOne(doc).getDeletedCount();
        // Also removes opposite FriendRequest, great for lazy / inattentive developers
        doc = new Document("caller",fr.getReceiver()).append("receiver",fr.getCaller());
        i += coll.deleteOne(doc).getDeletedCount();
        if(i < 1)
            throw new NotFoundException("Friend request not found");
    }

    /**
     * Deletes all the {@link FriendRequest} related to the all the {@link models.User#pseudo}, either as a all the {@link FriendRequest#caller} or a all the {@link FriendRequest#receiver}
     * @param pseudo the {@link models.User#pseudo}
     */
    public void deleteMany(String pseudo) {
        coll.deleteMany(new Document("caller",pseudo));
        coll.deleteMany(new Document("receiver",pseudo));
    }

    /**
     * Finds all the {@link FriendRequest} that matches the parameters
     * @param field the name of the field to find
     * @param pseudo the value of the field
     * @return a {@link List} of {@link FriendRequest}
     */
    public List<FriendRequest> findByField(String field, String pseudo){
        List<FriendRequest> list = new ArrayList<FriendRequest>();
        MongoCursor<Document> cursor = coll.find(eq(field,pseudo)).iterator();

        try {
            while (cursor.hasNext()) {
                list.add(gson.fromJson(cursor.next().toJson(),FriendRequest.class));
            }
        } finally {
            cursor.close();
        }
        return list;
    }
}
