package controllers;

import com.google.gson.Gson;
import models.User;
import services.UserService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

@RequestScoped
@Path("/user")
@Produces("application/json")
public class UserController{

    @Inject
    private UserService userService;

    @Path("/v1/users")
    @GET
    public String findAll(){
        return new Gson().toJson(userService.findAll());
    }

    @Path("/fixtures")
    @GET
    public String insertTest(){
        return new Gson().toJson(userService.insertTest());
    }


    @Path("v1/updateuser")
    @POST
    @Consumes("application/json")
    public String updateUser(User user){
        userService.updateUser(user);
        return new Gson().toJson(user);
    }

    @Path("v1/getfriendscooordinates")
    @GET
    @Consumes("application/json")
    public String getFriendsCoordinates(User user){
        return new Gson().toJson(userService.getFriendsCoordinates(user));
    }
}
