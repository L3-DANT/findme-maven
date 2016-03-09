package daos;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import connections.ConnectionFactory;
import models.User;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserDAO {

//    private Mongo mongo;
//    private DB db;
    private MongoCollection<Document> coll;

    private User constructUser(DBObject dbObject){

        return null;
    }

    public UserDAO(){
            coll = ConnectionFactory.getMongoConnection().getCollection("bob");
    }

    public List<User> getall(){
        List<User> list = new ArrayList<User>();
        MongoCursor<Document> cursor = coll.find().iterator();
        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    public User insertOne(Map<String,String> values){
        Document doc = new Document();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            doc.append(entry.getKey(),entry.getValue());
        }
        coll.insertOne(doc);
        return null;
    }


}
