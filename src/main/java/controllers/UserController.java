package controllers;

import com.pusher.rest.Pusher;
import connections.PusherConnection;
import exceptions.DuplicateDataException;
import exceptions.NotFoundException;
import exceptions.UnauthorisedException;
import models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;


/**
 * Controller that manages {@link User}
 */
@Path("/user")
@Produces("application/json")
public class UserController extends Controller{

    private Pusher pusher = PusherConnection.getPusher();
    private final static Logger logger = LogManager.getLogger(UserController.class);

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
            logger.info("Called GET \"user/v1/"+pseudo+"\". Everything went right.");
            return user;
        } catch (NotFoundException e) {
            logger.error("Calling GET \"user/v1/"+pseudo+"\" threw NotFoundException.",e);
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
            logger.info("Called PUT \"user/v1\" with data \n"+userAsString+"\nEverything went right.");
            return gson.toJson(userService.insertUser(user));
        } catch (DuplicateDataException e){
            logger.error("Calling PUT \"user/v1\" with data \n"+userAsString+"\nthrew DuplicateDataExepction.",e);
            throw new WebApplicationException(Response.Status.CONFLICT);
        }
    }

    /**
     * Updates given {@link User} in database
     * If only the {@link User#pseudo}, {@link User#latitude} and {@link User#longitude} are given, triggers a "position-updated" even on {@link Pusher}
     * If friends are removed, sends a "friends-removed" event on {@link Pusher}
     * @param userAsString the {@link User} to update
     * @return the serialized {@link User}
     * @throws WebApplicationException 404 if the {@link User} can't be found
     * @throws WebApplicationException 400 if the given {@link User} has a bigger {@link User#friendList}, or a same sized one with different friends
     * To manage friends, please use {@link FriendRequestController}
     * @see Pusher
     */
    @Path("v1")
    @POST
    @Consumes("application/json")
    public String updateUser(String userAsString){
        try {
            String logs;
            User user = gson.fromJson(userAsString,User.class);
            if(user.getPseudo() != null && user.getLatitude() != 0 && user.getLongitude() != 0 && user.getState() != null
                    && user.getPassword() == null && (user.getFriendList() == null || user.getFriendList().size() == 0) && user.getPhoneNumber() == null) {
                userService.updateCoordinates(user);
                user.setFriendList(null);
                pusher.trigger("private-"+user.getPseudo(),"position-updated",user);
                logs = "Called POST \"user/v1\" with data\n"+userAsString+"\nEverything went right.\nPusher event \"position-updated\" triggered with data\n"+gson.toJson(user);
            } else {
                String[] userList = userService.updateUser(user);
                logs = "Called POST \"user/v1\" with data\n"+userAsString+"\nEverything went right.";
                if (userList != null) {
                    pusher.trigger("private-" + user.getPseudo(), "friends-removed", userList);
                    logs+= "\nPusher event \"friends-removed\" triggered with data\n"+gson.toJson(userList);
                }
            }
            logger.info(logs);
            return userAsString;
        } catch (NotFoundException e) {
            logger.error("Calling POST \"user/v1\" with data \n"+userAsString+"\nthrew NotFoundException.",e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (UnauthorisedException e){
            logger.error("Calling POST \"user/v1\" with data \n"+userAsString+"\nthrew UnauthorisedException.",e);
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
        User user = gson.fromJson(credentials,User.class);
        try {
            if(userService.connect(user.getPseudo(),user.getPassword())){
                logger.info("Called POST \"user/v1/login\" with data \n"+credentials+"\nEverything went right.");
                return gson.toJson(userService.getUser(user.getPseudo()));
            } else {
                logger.error("Called POST \"user/v1/login\" with data \n"+credentials+"\n went wrong : credentials don't match.");
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }
        } catch (NotFoundException e) {
            logger.error("Calling POST \"user/v1\" with data \n"+credentials+"\nthrew NotFoundException.",e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }


    /**
     * Removes a {@link User} from database, also deletes every {@link models.FriendRequest} related to it
     * @param pseudo the {@link User#pseudo} to remove
     * @throws WebApplicationException 404 if the {@link User} can't be found in database
     */
    @Path("/v1/{pseudo}")
    @DELETE
    public void deleteUser(@PathParam("pseudo") String pseudo) {
        try {
            String logs = "Called DELETE \"user/v1/"+pseudo+"\". Everything went right.";
            User user = userService.getUser(pseudo);
            userService.deleteUser(pseudo);
            frService.deleteMany(pseudo);
            if(user.getFriendList() != null && user.getFriendList().size() > 0){
                String[] userList = new String[user.getFriendList().size()];
                for (int i = 0 ; i < userList.length ; i++) {
                    userList[i] = user.getFriendList().get(i).getPseudo();
                }
                pusher.trigger("private-"+pseudo,"friends-removed",userList);
                logs+= "\nPusher event \"friends-removed\" triggered with data\n"+gson.toJson(userList);
            }
            logger.info(logs);
        } catch (NotFoundException e) {
            logger.error("Calling DELETE \"user/v1/"+pseudo+"\" threw NotFoundException.",e);
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
