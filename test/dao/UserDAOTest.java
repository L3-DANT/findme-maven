package dao;


import daos.UserDAO;
import exceptions.DuplicateDataException;
import exceptions.NotFoundException;
import models.User;
import org.junit.*;
import utils.DatabaseUtils;


public class UserDAOTest extends AbstractDAOTest {

    private UserDAO dao = new UserDAO();
    private User alfred = new User("Alfred","123");
    private User bob = new User("Bob","123");

    @Before
    public void insertBefore(){
        DatabaseUtils.initialiseCollection("user", alfred,bob);
    }

    @After
    public void dropColl(){
        DatabaseUtils.clearCollection("user");
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
        dao.findOneByPseudo("Meuporg");
    }

    @Test
    public void testInsertSuccess() throws DuplicateDataException, NotFoundException {
        User u = dao.insertOne(new User("Jean-Georges","123"));
        Assert.assertEquals(dao.findOneByPseudo(u.getPseudo()),u);
    }

    @Test(expected = DuplicateDataException.class)
    public void testInsertDuplicateException() throws DuplicateDataException {
        dao.insertOne(alfred);
    }

    @Test
    public void replaceOneSuccess() throws NotFoundException{
        User u = new User("Alfred","456");
        User test = dao.findOneByPseudo(u.getPseudo());
        Assert.assertNotEquals(u.getPassword(),test.getPassword());

        dao.replaceOne(u);
        test = dao.findOneByPseudo(u.getPseudo());
        Assert.assertEquals(test,u);
        Assert.assertEquals(u.getPassword(),test.getPassword());
    }

    @Test(expected = NotFoundException.class)
    public void replaceOneNotFoundException() throws NotFoundException {
        User u = new User("Meuporg","123");
        dao.replaceOne(u);
    }

    @Test(expected = NotFoundException.class)
    public void deleteOneSuccess() throws NotFoundException {
        dao.deleteOne("Alfred");
        dao.findOneByPseudo("Alfred");
    }

    @Test(expected = NotFoundException.class)
    public void deleteOneNotFoundException() throws NotFoundException {
        dao.deleteOne("Meuporg");
    }

}
