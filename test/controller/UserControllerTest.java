package controller;


import controllers.UserController;
import models.User;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import utils.DatabaseUtils;

import static org.junit.Assert.assertEquals;

public class UserControllerTest extends AbstractControllerTest {

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

    @Override
    protected Application configure() {
        return new ResourceConfig(UserController.class);
    }


    @Test
    public void GETSuccess() {
        Response response = target("user/v1/Alfred").request().get();
        assertEquals(response.getStatus(),200);
        User u = gson.fromJson(response.readEntity(String.class),User.class);
        assertEquals(alfred,u);
    }

    @Test
    public void GET404() {
        Response response = target("user/v1/Meuporg").request().get();
        assertEquals(response.getStatus(),404);
    }


}
