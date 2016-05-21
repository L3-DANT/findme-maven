package controller;

import controllers.UserController;
import models.User;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import security.BCrypt;
import utils.DatabaseUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserControllerTest extends AbstractControllerTest {

    private User alfred = new User("Alfred", BCrypt.hashpw("123", (BCrypt.gensalt(12))));

    @Before
    public void insertBefore(){
        DatabaseUtils.initialiseCollection("user", alfred);
    }

    @After
    public void dropColl(){
        DatabaseUtils.clearCollection("user");
    }

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
        Response response = target("user/v1/Meuporg").request().get();
        assertEquals(response.getStatus(),404);
    }

    @Test
    public void PUTSuccess() {
        User user = new User("John","789");
        String s = "{\"pseudo\":\"" + user.getPseudo() + "\",\"password\":\"" + user.getPassword() + "\"}";
        Response response = target("user/v1").request().put(Entity.json(s));

        //checks response
        assertTrue(response.getStatus() < 300);
        User u = gson.fromJson(response.readEntity(String.class),User.class);
        assertEquals(user,u);

        //checks database
        response = target("user/v1/"+user.getPseudo()).request().get();
        assertTrue(response.getStatus() < 300);
        u = gson.fromJson(response.readEntity(String.class),User.class);
        assertEquals(user,u);
    }

    @Test
    public void PUT409() {
        String s = "{\"pseudo\":\"" + alfred.getPseudo() + "\",\"password\":\"" + alfred.getPassword() + "\"}";
        Response response = target("user/v1").request().put(Entity.json(s));
        assertEquals(response.getStatus(),409);
    }

    @Test
    public void POSTsuccess() {
        alfred.setPassword("abc");
        //checks response
        String s = "{\"pseudo\":\"" + alfred.getPseudo() + "\",\"password\":\"" + alfred.getPassword() + "\"}";
        Response response = target("user/v1").request().post(Entity.json(s));
        assertTrue(response.getStatus() < 300);

        //checks database
        response = target("user/v1/"+alfred.getPseudo()).request().get();
        assertTrue(response.getStatus() < 300);
        User u = gson.fromJson(response.readEntity(String.class),User.class);
        assertEquals(alfred,u);
    }

    @Test
    public void POST404() {
        String s = "{\"pseudo\":\"Meuporg\",\"password\":\"123\"}";
        Response response = target("user/v1").request().post(Entity.json(s));
        assertEquals(response.getStatus(),404);
    }

    @Test
    public void DELETESuccess() {
        //checks response
        Response response = target("user/v1/"+alfred.getPseudo()).request().delete();
        assertTrue(response.getStatus() < 300);

        //checks database
        response = target("user/v1/"+alfred.getPseudo()).request().get();
        assertEquals(response.getStatus(),404);
    }

    @Test
    public void DELETE404() {
        Response response = target("user/v1/Meuporg").request().delete();
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
        String s = "{\"pseudo\":\"Meuporg\",\"password\":\"123\"}";
        Response response = target("user/v1/login").request().post(Entity.json(s));
        assertEquals(response.getStatus(),404);
    }
}
