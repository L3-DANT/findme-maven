package daos;

import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.IndexOptions;
import exceptions.DuplicateDataException;
import exceptions.NotFoundException;
import models.User;
import org.bson.Document;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static utils.Utils.gson;

/**
 * DAO class that manages {@link User}
 */
@Stateless
public class UserDAO extends DAO {

    public UserDAO(){
        super("user");
        // Ensures that every pseudo is unique
        coll.createIndex(new Document("pseudo",1),new IndexOptions().unique(true));
    }

    /**
     *
     * Gets all users from database
     * @return list of {@link User}
     */
    public List<User> findAll(){
        List<User> list = new ArrayList<User>();
        MongoCursor<Document> cursor = coll.find().iterator();
        try {
            while (cursor.hasNext()) {
                list.add(gson.fromJson(cursor.next().toJson(),User.class));
            }
        } finally {
            cursor.close();
        }
        return list;
    }


    /**
     * Finds one {@link User} in database
     * @param pseudo the {@link User#pseudo}
     * @return the {@link User}
     * @throws NotFoundException
     */
    public User findOneByPseudo(String pseudo) throws NotFoundException{
        Document doc = coll.find(eq("pseudo",pseudo)).first();
        if(doc == null)
            throw new NotFoundException("User not found");
        User user = gson.fromJson(doc.toJson(),User.class);
        user.setPassword(doc.get("password").toString());
        return user;
    }


    /**
     * Adds an {@link User} in database
     * @param user the {@link User} to add
     * @return true if succeded, false if the user already exists
     */
    public User insertOne(User user) throws DuplicateDataException {
        Document doc = new Document(Document.parse(gson.toJson(user)));
        try{
            coll.insertOne(doc);
        } catch (Exception e){
            throw new DuplicateDataException("User already exists in database");
        }
        return user;
    }

    /**
     * Updates one {@link User} in database
     * @param user the {@link User} to update
     */
    public void replaceOne(User user) throws NotFoundException{
        Document formerDoc = coll.find(eq("pseudo",user.getPseudo())).first();
        if(formerDoc == null)
            throw new NotFoundException("User not found");
        Document newDoc =  new Document(Document.parse(gson.toJson(user)));
        coll.replaceOne(formerDoc,newDoc);
    }

    public void deleteOne(String pseudo) throws NotFoundException{
        if(coll.deleteOne(new Document("pseudo", pseudo)).getDeletedCount() < 1)
            throw new NotFoundException("User not found");
    }

}
