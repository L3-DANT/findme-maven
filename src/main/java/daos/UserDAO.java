package daos;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.IndexOptions;
import connections.MongoConnection;
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
public class UserDAO extends DAO{

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
        return user;
    }


    /**
     * Adds an {@link User} in database
     * @param user the {@link User} to add
     * @return true if succeded, false if the user already exists
     */
    public boolean insertOne(User user){
        Document doc = new Document(Document.parse(gson.toJson(user)));
        try{
            coll.insertOne(doc);
        } catch(MongoException e){
            System.out.println("User already exists in database.");
            return false;
        }
        return true;
    }

    /**
     * Updates one {@link User} in database
     * @param user the {@link User} to update
     */
    public void replaceOne(User user){
        Document formerDoc = new Document("pseudo",user.getPseudo());
        Document newDoc =  new Document(Document.parse(gson.toJson(user)));
        coll.replaceOne(formerDoc,newDoc);
    }


}
