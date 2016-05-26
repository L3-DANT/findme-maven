package controllerTest;

import controllers.UserController;
import exceptions.NotFoundException;
import models.User;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import security.BCrypt;
import services.UserService;
import utils.DatabaseUtils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserControllerTest extends AbstractControllerTest {

//    private User alfred = new User("Alfred", BCrypt.hashpw("123", (BCrypt.gensalt(12))));
//    private User u = new User("Bob","123");
//    private User u2 = new User("John","123");
//    private User u3 = new User("Fred","123");
//
//    private UserService userService;
//
//    @Before
//    public void insertBefore(){
//        User ucopy = new User("Bob","123");
//        User u2copy = new User("John","123");
//        User u3copy = new User("Fred","123");
//        u.addFriend(alfred);
//        u2.addFriend(alfred);
//        u3.addFriend(alfred);
//        alfred.addFriend(ucopy);
//        alfred.addFriend(u2copy);
//        alfred.addFriend(u3copy);
//
//
//
//        userService = new UserService();
//        DatabaseUtils.initialiseCollection("user", alfred,u,u2,u3);
//    }

//    @After
//    public void dropColl(){
//        DatabaseUtils.clearCollection("user");
//    }

    @Override
    protected Application configure() {
        return new ResourceConfig(UserController.class);
    }


    @Test
    public void GETSuccess() {
        Response response = target("user/v1/"+alfred.getPseudo()).request().get();
        assertTrue(response.getStatus() < 300);
        User u = gson.fromJson(response.readEntity(String.class),User.class);
        assertEquals(alfred,u);
    }

    @Test
    public void GET404() {
        Response response = target("user/v1/Archer").request().get();
        assertEquals(response.getStatus(),404);
    }

    @Test
    public void PUTSuccess() throws NotFoundException {
        User user = new User("Archer","789");
        String s = "{\"pseudo\":\"" + user.getPseudo() + "\",\"password\":\"" + user.getPassword() + "\"}";
        Response response = target("user/v1").request().put(Entity.json(s));

        //checks response
        assertTrue(response.getStatus() < 300);
        User u = gson.fromJson(response.readEntity(String.class),User.class);
        assertEquals(user,u);

        //checks database
        u = userService.getUser(user.getPseudo());
        assertEquals(user,u);
    }

    @Test
    public void PUT409() {
        String s = "{\"pseudo\":\"" + alfred.getPseudo() + "\",\"password\":\"" + alfred.getPassword() + "\"}";
        Response response = target("user/v1").request().put(Entity.json(s));
        assertEquals(response.getStatus(),409);
    }

    @Test
    public void POSTsuccess() throws NotFoundException {
        alfred.setPassword("abc");
        //checks response
        String s = "{\"pseudo\":\"" + alfred.getPseudo() + "\",\"password\":\"" + alfred.getPassword() + "\"}";
        Response response = target("user/v1").request().post(Entity.json(s));
        assertTrue(response.getStatus() < 300);

        //checks database
        User u = userService.getUser(alfred.getPseudo());
        assertEquals(alfred,u);

        assertTrue(userService.connect(alfred.getPseudo(),"abc"));
    }

    @Test
    public void POSTRemoveFriend() throws NotFoundException {
        //make sure users do have friends
        User alfredcopy = userService.getUser(alfred.getPseudo());
        assertEquals(alfredcopy.getFriendList().size(),3);
        User ucopy = userService.getUser(u.getPseudo());
        assertEquals(ucopy.getFriendList().size(),1);

        //remove two friends, including u (= ucopy), keeps u3 (Fred) by not putting them in the json
        String s3 = "{\"pseudo\":\"" + u3.getPseudo() + "\",\"password\":\"" + u3.getPassword() + "\"}";
        String s = "{\"pseudo\":\"" + alfred.getPseudo() + "\",\"password\":\"" + alfred.getPassword() + "\",\"friendList\":["+ s3 + "]}";
        Response response = target("user/v1").request().post(Entity.json(s));
        assertTrue(response.getStatus() < 300);
        User ret = gson.fromJson(response.readEntity(String.class),User.class);
        assertEquals(alfred,ret);

        //make sure friends have been removed from the two users
        alfredcopy = userService.getUser(alfred.getPseudo());
        assertEquals(alfred,alfredcopy);
        assertEquals(alfredcopy.getFriendList().size(),1);
        assertEquals(alfredcopy.getFriendList().get(0),u3);
        ucopy = userService.getUser(u.getPseudo());
        assertEquals(u,ucopy);
        assertEquals(ucopy.getFriendList().size(),0);
    }

    @Test
    public void POSTupdateCoordinates() throws NotFoundException {
        float lat = 2.85f;
        float lon = 21.785f;
        float epsilon = 0.00000001f;
        String s = "{\"pseudo\":\"" + alfred.getPseudo() + "\",\"latitude\":" + lat + ", \"longitude\":" + lon + "}";
        Response response = target("user/v1/").request().post(Entity.json(s));
        assertTrue(response.getStatus() < 300);

        User u = userService.getUser(alfred.getPseudo());
        assertEquals(alfred,u);
        assertTrue(Math.abs(u.getLatitude() - lat) < epsilon);
        assertTrue(Math.abs(u.getLongitude() - lon) < epsilon);
    }

    @Test
    public void POST404() {
        String s = "{\"pseudo\":\"Archer\",\"password\":\"123\"}";
        Response response = target("user/v1").request().post(Entity.json(s));
        assertEquals(response.getStatus(),404);
    }

    @Test(expected = NotFoundException.class)
    public void DELETESuccess() throws NotFoundException {
        //checks response
        assertTrue(frService.findByCaller(alfred.getPseudo()).length != 0 || frService.findByReceiver(alfred.getPseudo()).length != 0);
        Response response = target("user/v1/"+alfred.getPseudo()).request().delete();
        assertTrue(response.getStatus() < 300);

        //checks database
        userService.getUser(alfred.getPseudo());
        //checks friendlist database
        assertTrue(frService.findByCaller(alfred.getPseudo()).length == 0 && frService.findByReceiver(alfred.getPseudo()).length == 0);
    }

    @Test
    public void DELETE404() {
        Response response = target("user/v1/Archer").request().delete();
        assertEquals(response.getStatus(),404);
    }

    @Test
    public void loginSuccess() {
        String s = "{\"pseudo\":\"" + alfred.getPseudo() + "\",\"password\":\"123\"}";
        Response response = target("user/v1/login").request().post(Entity.json(s));
        assertTrue(response.getStatus() < 300);
    }

    @Test
    public void login401() {
        String s = "{\"pseudo\":\"" + alfred.getPseudo() + "\",\"password\":\"abc\"}";
        Response response = target("user/v1/login").request().post(Entity.json(s));
        assertEquals(response.getStatus(),401);
    }

    @Test
    public void login404() {
        String s = "{\"pseudo\":\"Archer\",\"password\":\"123\"}";
        Response response = target("user/v1/login").request().post(Entity.json(s));
        assertEquals(response.getStatus(),404);
    }

}
