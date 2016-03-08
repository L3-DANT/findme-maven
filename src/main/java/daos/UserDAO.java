package daos;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import models.User;

import java.net.UnknownHostException;

/**
 * Created by Illidan Stormrage on 08/03/2016.
 */
public class UserDAO {

    private DBCollection coll;

    public UserDAO(){
        try {
            coll = new Mongo("127.0.0.1",27017).getDB("findme").getCollection("users");
        } catch (UnknownHostException e) {
            System.out.print("Erreur de connexion à la base de données");
            e.printStackTrace();
        }
    }

    public User insert(String... parameters){
        DBObject db = new BasicDBObject();
        for (String parameter : parameters) {
            db.put("jesaispas",parameter);
        }
        coll.insert(db);
        return null;
    }


}
