package dao;


import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import daos.UserDAO;
import exceptions.DuplicateDataException;
import exceptions.NotFoundException;
import models.User;
import org.bson.Document;
import org.junit.*;


public class UserDAOTest extends DAOTest{

    private UserDAO dao = new UserDAO();
    private User user = new User("Alfred","123");

    @Before
    public void insertBefore(){
        MongoClient client = new MongoClient(IP, PORT);
        MongoCollection<Document> collection = client.getDatabase(DB_NAME).getCollection("user");
        collection.insertOne(Document.parse(new Gson().toJson(user)));
    }

    @After
    public void dropColl(){
        dao.clearCollection();
    }

    @Test
    public void findOneByPseudoSuccess() throws NotFoundException{
        String pseudo = "Alfred";
        Object u = dao.findOneByPseudo(pseudo);
        Assert.assertNotNull(u);
        Assert.assertTrue(u instanceof User);
        Assert.assertEquals(((User)u).getPseudo(),pseudo);
    }

    @Test(expected = NotFoundException.class)
    public void findOneByPseudoNotFoundException() throws NotFoundException{
        dao.findOneByPseudo("Jean-Georges");
    }

    @Test
    public void testInsertSuccess() throws DuplicateDataException, NotFoundException {
        User u = dao.insertOne(new User("Jean-Georges","123"));
        Assert.assertEquals(dao.findOneByPseudo(u.getPseudo()),u);
    }

    @Test(expected = DuplicateDataException.class)
    public void testInsertDuplicateException() throws DuplicateDataException {
        dao.insertOne(user);
    }


    @Test
    public void replaceOneSuccess() throws NotFoundException{
        User u = new User("Alfred","456");
        User test = dao.findOneByPseudo(u.getPseudo());
        Assert.assertFalse(u.getPassword().equals(test.getPassword()));

        dao.replaceOne(u);
        test = dao.findOneByPseudo(u.getPseudo());
        Assert.assertTrue(test.equals(u));
        Assert.assertTrue(u.getPassword().equals(test.getPassword()));
    }

}
