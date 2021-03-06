package controllerTest;

import controllers.FriendRequestController;
import exceptions.NotFoundException;
import models.FriendRequest;
import models.User;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.util.Arrays;

import static org.junit.Assert.*;

public class FriendRequestControllerTest extends AbstractControllerTest{

    @Override
    protected Application configure() {
        return new ResourceConfig(FriendRequestController.class);
    }

    @Test
    public void GETSuccess() {
        Response response = target("friendrequest/v1").queryParam("caller",fr.getCaller()).queryParam("receiver",fr.getReceiver()).request().get();
        assertTrue(response.getStatus() < 300);
    }

    @Test
    public void GET404() {
        Response response = target("friendrequest/v1").queryParam("caller","Alfred").queryParam("receiver","Meuporg").request().get();
        assertEquals(response.getStatus(),404);
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
    public void PUTSuccess() throws NotFoundException {
        FriendRequest f = new FriendRequest("Meuporg","John");
        String s = "{\"caller\":\"" + f.getCaller() + "\",\"receiver\":\"" + f.getReceiver() + "\"}";
        Response response = target("friendrequest/v1").request().put(Entity.json(s));

        //checks response
        assertTrue(response.getStatus() < 300);

        //checks database
        FriendRequest friendRequest = frService.getFriendRequestByPseudos(f.getCaller(),f.getReceiver());
        assertEquals(friendRequest,f);

        s = "{\"caller\":\"" + fr3.getReceiver() + "\",\"receiver\":\"" + fr3.getCaller() + "\"}";
        response = target("friendrequest/v1").request().put(Entity.json(s));
        assertTrue(response.getStatus() < 300);
        User u1 = userService.getUser(fr3.getCaller());
        User u2 = userService.getUser(fr3.getReceiver());
        int size1 = u1.getFriendList().size();
        int size2 = u2.getFriendList().size();
        assertNotEquals(u1.getFriendList().size(),size1+1);
        assertTrue(u1.getFriendList().contains(u2));
        assertNotEquals(u2.getFriendList().size(),size2+1);
        assertTrue(u2.getFriendList().contains(u1));

    }

    @Test
    public void PUT401() {
        String s = "{\"caller\":\"" + fr.getCaller() + "\",\"receiver\":\"" + fr.getReceiver() + "\"}";
        Response response = target("friendrequest/v1").request().put(Entity.json(s));
        assertEquals(response.getStatus(),401);
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
        String s = "{\"caller\":\"" + fr3.getCaller() + "\",\"receiver\":\"" + fr3.getReceiver() + "\"}";
        Response response = target("friendrequest/v1").request().put(Entity.json(s));
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
        int size1 = u1.getFriendList().size();
        int size2 = u2.getFriendList().size();
        assertNotEquals(u1.getFriendList().size(),size1+1);
        assertTrue(u1.getFriendList().contains(u2));
        assertNotEquals(u2.getFriendList().size(),size2+1);
        assertTrue(u2.getFriendList().contains(u1));
    }

    @Test
    public void POST404(){
        String s = "{\"caller\":\"" + fr.getCaller() + "\",\"receiver\":\"Jean-Georges\"}";
        Response response = target("friendrequest/v1").request().post(Entity.json(s));
        assertEquals(response.getStatus(), 404);
    }

    @Test(expected = NotFoundException.class)
    public void DELETESuccess() throws NotFoundException {
        Response  response = target("friendrequest/v1").queryParam("caller",fr.getCaller()).queryParam("receiver",fr.getReceiver()).request().delete();
        assertTrue(response.getStatus() < 300);
        frService.getFriendRequestByPseudos(fr.getCaller(),fr.getReceiver());
    }

    @Test
    public void DELETE400() {
        Response  response = target("friendrequest/v1").queryParam("caller",fr.getCaller()).request().delete();
        assertEquals(response.getStatus(), 400);
    }

    @Test
    public void DELETE404() {
        Response  response = target("friendrequest/v1").queryParam("caller","John Lennon").queryParam("receiver",fr.getReceiver()).request().delete();
        assertEquals(response.getStatus(), 404);
    }
}
