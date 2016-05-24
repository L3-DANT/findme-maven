package controllers;

import com.pusher.rest.Pusher;
import connections.PusherConnection;
import exceptions.DuplicateDataException;
import exceptions.NotFoundException;
import exceptions.UnauthorisedException;
import models.User;
import services.UserService;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller that manages {@link User}
 */
@Path("/user")
@Produces("application/json")
public class UserController extends Controller{

    private Pusher pusher = PusherConnection.getPusher();
    private UserService userService = new UserService();

    /**
     * Gets the user and updates its {@link User#friendList}
     * @param pseudo the pseudo that identifies the {@link User}
     * @return the {@link User}
     * @throws WebApplicationException 404 if the {@link User} can't be found
     */
    @Path("v1/{pseudo}")
    @GET
    public String getUser(@PathParam("pseudo") String pseudo){
        try {
            String user = gson.toJson(userService.getUser(pseudo));
            return user;
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    /**
     * Inserts a {@link User} in database
     * @param userAsString the user to put in database
     * @return the created {@link User}
     * @throws WebApplicationException 409 if the {@link User} is already in databse
     */
    @Path("v1")
    @PUT
    @Consumes("application/json")
    public String signUp(String userAsString){
        try {
            User user = gson.fromJson(userAsString,User.class);
            return gson.toJson(userService.insertUser(user));
        } catch (DuplicateDataException e){
            throw new WebApplicationException(Response.Status.CONFLICT);
        }
    }

    /**
     * Updates given {@link User} in database
     * @param userAsString the {@link User} to update
     * @return the serialized {@link User}
     * @throws WebApplicationException 404 if the {@link User} can't be found
     * @throws WebApplicationException 400 if the given {@link User} has a bigger {@link User#friendList}, or a same sized one with different friends
     * To manage friends, please use {@link FriendRequestController}
     */
    @Path("v1")
    @POST
    @Consumes("application/json")
    public String updateUser(String userAsString){
        try {
            User user = gson.fromJson(userAsString,User.class);
            String[] array = userService.updateUser(user);
            if(array != null){
                pusher.trigger("private-"+user.getPseudo(),"friends-removed",array);
            }
            return userAsString;
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (UnauthorisedException e){
            throw new WebApplicationException("If you want to add friends, please proceed by calling friendrequest urls.",Response.Status.BAD_REQUEST);
        }
    }

    /**
     *  Connects the user to the application
     *  @param credentials a json containing a pseudo and a password
     *  @return the {@link User} if the credentials match one database {@link User}
     *  @throws WebApplicationException 401 if the credentials are wrong
     *  @throws WebApplicationException 404 if the {@link User} doesn't exist in database
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

    /**
     * Lighter version of POST method, only updating the {@link User} {@link User#latitude} and {@link User#longitude}
     * @param userAsString the {@link User} to update
     * @throws NotFoundException if the {@link User} can't be found in database
     */
    @Path("v1/update-coordinates")
    @POST
    @Consumes("application/json")
    public void updateCoordinates(String userAsString){
        try {
            User user = gson.fromJson(userAsString,User.class);
            userService.updateCoordinates(user);
            user.clearFriendList();
            pusher.trigger("private-"+user.getPseudo(),"position-updated",user);
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

    }

    /**
     * Removes a {@link User} from database
     * @param pseudo the {@link User#pseudo} to remove
     * @throws WebApplicationException 404 if the {@link User} can't be found in database
     */
    @Path("/v1/{pseudo}")
    @DELETE
    public void deleteUser(@PathParam("pseudo") String pseudo) {
        try {
            userService.deleteUser(pseudo);
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
