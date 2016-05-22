package controller;


import controllers.FriendRequestController;
import controllers.UserController;
import exceptions.NotFoundException;
import models.FriendRequest;
import models.User;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import services.UserService;
import utils.DatabaseUtils;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class FriendRequestControllerTest extends AbstractControllerTest{

    private User alfred = new User("Alfred","123");
    private User bob = new User("Bob", "123");
    private User john = new User("John", "123");
    private User meuporg = new User("Meuporg", "123");

    private FriendRequest fr = new FriendRequest("Alfred","Bob");
    private FriendRequest fr2 = new FriendRequest("Alfred","John");
    private FriendRequest fr3 = new FriendRequest("John","Bob");

    private UserService userService = new UserService();

    @Before
    public void insertBefore(){
        DatabaseUtils.initialiseCollection("user", alfred,bob,john,meuporg);
        DatabaseUtils.initialiseCollection("friendrequest",fr,fr2,fr3);
    }

    @After
    public void dropColl(){
        DatabaseUtils.clearCollection("user","friendrequest");
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(FriendRequestController.class);
    }

    @Test
    public void GETSuccess() {
        Response response = target("friendrequest/v1").queryParam("caller","Alfred").queryParam("receiver","Meuporg").request().get();
        assertEquals(response.getStatus(),404);
    }

    @Test
    public void GET404() {
        Response response = target("friendrequest/v1").queryParam("caller",fr.getCaller()).queryParam("receiver",fr.getReceiver()).request().get();
        assertTrue(response.getStatus() < 300);
    }

    @Test
    public void GETCallersSuccess() {
        Response response = target("friendrequest/v1").queryParam("caller",fr.getCaller()).request().get();
        assertTrue(response.getStatus() < 300);
        String[] list = gson.fromJson(response.readEntity(String.class),String[].class);
        String[] list2 = {fr.getReceiver(),fr2.getReceiver()};
        Arrays.sort(list);
        Arrays.sort(list2);
        assertArrayEquals(list,list2);
    }

    @Test
    public void GETCallers404() {
        Response response = target("friendrequest/v1").queryParam("caller","Swag").request().get();
        assertEquals(response.getStatus(),404);
    }

    @Test
    public void GETReceiversSuccess() {
        Response response = target("friendrequest/v1").queryParam("receiver",fr.getReceiver()).request().get();
        assertTrue(response.getStatus() < 300);
        String[] list = gson.fromJson(response.readEntity(String.class),String[].class);
        String[] list2 = {fr.getCaller(),fr3.getCaller()};
        Arrays.sort(list);
        Arrays.sort(list2);
        assertArrayEquals(list,list2);
    }

    @Test
    public void GETReceivers404() {
        Response response = target("friendrequest/v1").queryParam("receiver","Swag").request().get();
        assertEquals(response.getStatus(),404);
    }

    @Test
    public void PUTSuccess() {
        FriendRequest f = new FriendRequest("Meuporg","John");
        String s = "{\"caller\":\"" + f.getCaller() + "\",\"receiver\":\"" + f.getReceiver() + "\"}";
        Response response = target("friendrequest/v1").request().put(Entity.json(s));

        //checks response
        assertTrue(response.getStatus() < 300);

        //checks database
        response = target("friendrequest/v1").queryParam("caller",f.getCaller()).queryParam("receiver",f.getReceiver()).request().get();
        assertTrue(response.getStatus() < 300);
        FriendRequest friendRequest = gson.fromJson(response.readEntity(String.class),FriendRequest.class);
        assertEquals(friendRequest,f);
    }

    @Test
    public void PUT404() {
        FriendRequest f = new FriendRequest("Jean","John");
        String s = "{\"caller\":\"" + f.getCaller() + "\",\"receiver\":\"" + f.getReceiver() + "\"}";
        Response response = target("friendrequest/v1").request().put(Entity.json(s));
        assertEquals(response.getStatus(),404);
    }

    @Test
    public void PUT409() {
        String s = "{\"caller\":\"" + fr.getCaller() + "\",\"receiver\":\"" + fr.getReceiver() + "\"}";
        Response response = target("friendrequest/v1").request().put(Entity.json(s));
        assertEquals(response.getStatus(),409);
        s = "{\"caller\":\"" + fr.getReceiver() + "\",\"receiver\":\"" + fr.getCaller() + "\"}";
        response = target("friendrequest/v1").request().put(Entity.json(s));
        assertEquals(response.getStatus(),409);
    }

    @Test
    public void POSTSuccess() throws NotFoundException {
        String s = "{\"caller\":\"" + fr.getCaller() + "\",\"receiver\":\"" + fr.getReceiver() + "\"}";
        Response response = target("friendrequest/v1").request().post(Entity.json(s));
        assertTrue(response.getStatus() < 300);
        response = target("friendrequest/v1").queryParam("caller",fr.getCaller()).queryParam("receiver",fr.getReceiver()).request().get();
        assertEquals(response.getStatus(), 404);

        User u1 = userService.getUser(fr.getCaller());
        User u2 = userService.getUser(fr.getReceiver());
        assertNotEquals(u1.getFriendList().size(),0);
        assertEquals(u1.getFriendList().get(0),u2);
        assertNotEquals(u2.getFriendList().size(),0);
        assertEquals(u2.getFriendList().get(0),u1);
    }
}
