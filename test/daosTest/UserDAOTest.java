package daosTest;


import com.google.gson.Gson;
import daos.UserDAO;
import exceptions.DuplicateDataException;
import exceptions.NotFoundException;
import models.User;
import org.bson.Document;
import org.junit.*;
import utils.DatabaseUtils;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.Assert.*;


public class UserDAOTest extends AbstractDAOTest {

    private UserDAO dao;
    private User alfred = new User("Alfred","123");

    private User getDBUser(String pseudo) {
        Document doc = DatabaseUtils.getColl("user").find(eq("pseudo",pseudo)).first();
        return doc == null ? null : new Gson().fromJson(doc.toJson(),User.class);
    }

    @Before
    public void insertBefore(){
        dao = new UserDAO();
        DatabaseUtils.initialiseCollection("user", alfred);

    }

    @After
    public void dropColl(){
        DatabaseUtils.clearCollection("user");
    }

    @Test
    public void findOneByPseudoSuccess() throws NotFoundException{
        String pseudo = "Alfred";
        Object u = dao.findOneByPseudo(pseudo);
        assertNotNull(u);
        assertTrue(u instanceof User);
        assertEquals(((User)u).getPseudo(),pseudo);
    }

    @Test(expected = NotFoundException.class)
    public void findOneByPseudoNotFoundException() throws NotFoundException{
        dao.findOneByPseudo("Meuporg");
    }

    @Test
    public void insertOneSuccess() throws DuplicateDataException {
        User u = dao.insertOne(new User("Jean-Georges","123"));
        assertEquals(getDBUser(u.getPseudo()),u);
    }

    @Test(expected = DuplicateDataException.class)
    public void insertOneDuplicateException() throws DuplicateDataException {
        dao.insertOne(alfred);
    }

    @Test
    public void replaceOneSuccess() throws NotFoundException{
        User u = new User("Alfred","456");
        User test = getDBUser(u.getPseudo());
        assertNotEquals(u.getPassword(),test.getPassword());

        dao.replaceOne(u);
        test = getDBUser(u.getPseudo());
        assertEquals(test,u);
        assertEquals(u.getPassword(),test.getPassword());
    }

    @Test(expected = NotFoundException.class)
    public void replaceOneNotFoundException() throws NotFoundException {
        User u = new User("Meuporg","123");
        dao.replaceOne(u);
    }

    @Test
    public void deleteOneSuccess() throws NotFoundException {
        dao.deleteOne("Alfred");
        assertNull(getDBUser("Alfred"));
    }

    @Test(expected = NotFoundException.class)
    public void deleteOneNotFoundException() throws NotFoundException {
        dao.deleteOne("Meuporg");
    }

}
