package controllers;

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
     * Insert false datas in database (use only for tests)
     * @return the serialized list of the users
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
     * Updates given {@link User} in database
     * @param user the {@link User} to update
     * @return the serialized {@link User}
     */
    @Path("v1/updateuser")
    @POST
    @Consumes("application/json")
    public String updateUser(User user){
        userService.updateUser(user);
        return gson.toJson(user);
    }

    /**
     * Gets the user and update its {@link User#friendList}
     * @param pseudo the pseudo that identifies the {@link User}
     * @return the {@link User} or false if not found
     */
    @Path("v1/getUser")
    @GET
    public String getUser(@QueryParam("pseudo") String pseudo){
        try {
            return gson.toJson(userService.getUser(pseudo));
        } catch (NotFoundException e) {
            return "false";
        }
    }

    /**
     *  Connect the user to the application
     *  @param pseudo the pseudo that identifies the {@Link User}
     *  @param password the password of the {@Link User}
     *  @return the {@Link User} if tseudo & password is correct or null if they're not
     */
    @Path("v1/signIn")
    @POST
    public String signIn(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password){
        try {
            User user = userService.getUser(pseudo);
            if(user.checkPassword(password)) {
                return gson.toJson(user);
            }
            else
                return null;
        } catch (NotFoundException e) {
            return null;
        }
    }

    @Path("v1/signUp")
    @POST
    public String signUp(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password){
        return userService.addUser(pseudo,password);
    }
}
