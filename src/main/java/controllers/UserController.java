package controllers;

import com.mongodb.MongoException;
import exceptions.NotFoundException;
import models.User;
import services.UserService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;

import static utils.Utils.gson;

/**
 * Controller that manages {@link User}
 */
@RequestScoped
@Path("/user")
@Produces("application/json")
public class UserController extends Controller{

    @Inject
    private UserService userService;

    /**
     * Gets every {@link User} in the database
     * @return the serialized list of users
     */
    @Path("/v1/users")
    @GET
    public String findAll(){
        return gson.toJson(userService.findAll());
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

    @Path("testpusher")
    @GET
    public void testPusher(){
        pusher.trigger("a","o","i");
    }

    /**
     * Updates given {@link User} in database and notify it on the pusher channel
     * @param user the {@link User} to update
     * @return the serialized {@link User}
     */
    @Path("v1/update")
    @POST
    @Consumes("application/json")
    public String updateUser(User user){
        try {
            userService.updateUser(user);
            pusher.trigger(user.getPseudo(),"updateUser",gson.toJson(user));
            return jsonResponse(0,"success",null);
        } catch (NotFoundException e) {
            return jsonResponse(-1,"user not found",null);
        }
    }

    /**
     * Gets the user and update its {@link User#friendList}
     * @param pseudo the pseudo that identifies the {@link User}
     * @return the {@link User} or false if not found
     */
    @Path("v1/user-by-pseudo")
    @GET
    public String getUser(@QueryParam("pseudo") String pseudo){
        try {
            return jsonResponse(0,"success",gson.toJson(userService.getUser(pseudo)));
        } catch (NotFoundException e) {
            return jsonResponse(0,"user not found",null);
        }
    }

    /**
     *  Connect the user to the application
     *  @param pseudo the pseudo that identifies the {@Link User}
     *  @param password the password of the {@Link User}
     *  @return the {@Link User} if tseudo & password is correct or null if they're not
     */
    @Path("v1/login")
    @POST
    public String login(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password){
        try {
            if(userService.connect(pseudo,password)){
                return jsonResponse(0,"succes",gson.toJson(userService.getUser(pseudo)));
            } else {
                return jsonResponse(-1,"provided pseudo and password don't match",null);
            }
        } catch (NotFoundException e) {
            return jsonResponse(-2,"user not found",null);
        }
    }

    @Path("v1/sign-up")
    @PUT
    @Consumes("application/json")
    public String signUp(User user){
        try {
            userService.insertUser(user);
            return jsonResponse(0,"succes",gson.toJson(user));
        } catch (MongoException e){
            return jsonResponse(-1,"user already exists in database",gson.toJson(user));
        }
    }

    @Path("/v1/delete")
    @DELETE
    public String deleteUser(@QueryParam("pseudo") String pseudo) {
        try {
            userService.deleteUser(pseudo);
            return jsonResponse(0,"succes",null);
        } catch (NotFoundException e) {
            return jsonResponse(-1,"User not found",null);
        }
    }

}
