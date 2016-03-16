package daos;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.IndexOptions;
import connections.MongoConnection;
import models.User;
import org.bson.Document;

import javax.ejb.Stateless;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
@Stateless
public class UserDAO {

    private MongoCollection<Document> coll;

    public UserDAO(){
        coll = MongoConnection.getDb().getCollection("user");
        coll.createIndex(new Document("pseudo",1),new IndexOptions().unique(true));
    }

    public List<User> findAll(){
        List<User> list = new ArrayList<User>();
        MongoCursor<Document> cursor = coll.find().iterator();
        try {
            while (cursor.hasNext()) {
                list.add(new Gson().fromJson(cursor.next().toJson(),User.class));
            }
        } finally {
            cursor.close();
    }
        return list;
    }

    public void insertOne(User user){
        Document doc = new Document(Document.parse(new Gson().toJson(user)));
        coll.insertOne(doc);
    }


}
