package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pusher.rest.Pusher;
import connections.PusherConnection;
import exceptions.DuplicateDataException;
import exceptions.NotFoundException;
import exceptions.UnauthorisedException;
import models.User;
import services.UserService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller that manages {@link User}
 */
@RequestScoped
@Path("/user")
@Produces("application/json")
public class UserController extends Controller{

    private Pusher pusher = PusherConnection.getPusher();
    private UserService userService = new UserService();

    /**
     * Gets the user and update its {@link User#friendList}
     * @param pseudo the pseudo that identifies the {@link User}
     * @return the {@link User} or false if not found
     */
    @Path("v1/{pseudo}")
    @GET
    public String getUser(@PathParam("pseudo") String pseudo){
        try {
            String s = gson.toJson(userService.getUser(pseudo));
            return s;
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @Path("v1")
    @PUT
    @Consumes("application/json")
    public String signUp(String u){
        try {
            User user = gson.fromJson(u,User.class);
            return gson.toJson(userService.insertUser(user));
        } catch (DuplicateDataException e){
            throw new WebApplicationException(Response.Status.CONFLICT);
        }
    }

    /**
     * Updates given {@link User} in database
     * @param user the {@link User} to update
     * @return the serialized {@link User}
     */
    @Path("v1")
    @POST
    @Consumes("application/json")
    public String updateUser(String u){
        try {
            User user = gson.fromJson(u,User.class);
            String[] array = userService.updateUser(user);
            if(array != null){
                pusher.trigger("private-"+user.getPseudo(),"friends-removed",array);
            }
            return u;
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (UnauthorisedException e){
            throw new WebApplicationException("If you want to add friends, please proceed by calling friendrequest urls.",Response.Status.BAD_REQUEST);
        }
    }

    /**
     *  Connect the user to the application
     *  @param login the pseudo that identifies the {@Link User}
     *  @return the {@Link User} if pseudo & password is correct or null if they're not
     */
    @Path("v1/login")
    @POST
    @Consumes("application/json")
    public String login(String credentials){
        Map<String,String> map = gson.fromJson(credentials, HashMap.class);
        try {
            if(userService.connect(map.get("pseudo"),map.get("password"))){
                return gson.toJson(userService.getUser(map.get("pseudo")));
            } else {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @Path("v1/update-coordinates")
    @POST
    @Consumes("application/json")
    public String updateCoordinates(String u){
        try {
            User user = gson.fromJson(u,User.class);
            userService.updateCoordinates(user);
            user.clearFriendList();
            pusher.trigger("private-"+user.getPseudo(),"position-updated",user);
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return null;

    }

    @Path("/v1/{pseudo}")
    @DELETE
    public String deleteUser(@PathParam("pseudo") String pseudo) {
        try {
            userService.deleteUser(pseudo);
            return null;
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }



    /**
     * Insert false data in database (use only for tests)
     * @return a serialized list of the users
     */
    @Path("/fixtures")
    @GET
    public String insertTest(){
        return gson.toJson(userService.insertTest());
    }

}
