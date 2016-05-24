package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoException;
import exceptions.DuplicateDataException;
import exceptions.NotFoundException;
import models.FriendRequest;
import models.User;
import services.FriendRequestService;
import services.UserService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.util.List;

/**
 *  Controller that manages {@link FriendRequest}
 */
@RequestScoped
@Path("/friendrequest")
@Produces("application/json")
public class FriendRequestController extends Controller{

    private FriendRequestService frService = new FriendRequestService();

    private UserService userService = new UserService();

    /**
     * Gets a {@link FriendRequest}
     * @param caller the user that asked or received the {@link FriendRequest}
     * @param receiver the user that asked or received the {@link FriendRequest}
     * @return the {@link FriendRequest} or false if not found
     */
    @Path("/v1")
    @GET
    public String getFriendRequest(@QueryParam("caller") String caller,
                                   @QueryParam("receiver") String receiver) {
        if(caller != null && receiver != null){
            try{
                return gson.toJson(frService.getFriendRequestByPseudos(caller,receiver));
            }catch (NotFoundException e){
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
        } else if(caller == null){
            try {
                userService.getUser(receiver);
            } catch(NotFoundException e) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            return gson.toJson(frService.findByReceiver(receiver));
        } else {
            try {
                userService.getUser(caller);
            } catch (NotFoundException e) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            return gson.toJson(frService.findByCaller(caller));
        }

    }

    /**
     * Creates a {@link FriendRequest} related to two users
     * @param fr the parsed json
     * @return true if the user's created, or false if it already exists
     */
    @Path("/v1")
    @PUT
    @Consumes("application/json")
    public String createFriendRequest(FriendRequest fr) {
        try {
            userService.getUser(fr.getCaller());
            userService.getUser(fr.getReceiver());
            frService.insertFriendRequest(fr);
            return null;
        } catch(NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch(DuplicateDataException e){
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

    }

    /**
     * Accepts a {@link FriendRequest}, which means removing it from database and
     * adding the first {@link User} in the second's {@link User#friendList} and reversely
     * @param fr the parsed json
     * @return true if everything went ok, false if at least one of the users were not found
     */
    @Path("/v1")
    @POST
    @Consumes("application/json")
    public String acceptFriendRequest(FriendRequest fr) {
        try {
            userService.addFriend(fr.getCaller(), fr.getReceiver());
            frService.deleteOne(fr);
            return null;
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    /**
     * Declines a {@link FriendRequest}, which means removing it from database
     * @param caller the caller (or receiver)
     * @param receiver the receiver (or caller)
     */
    @Path("/v1")
    @DELETE
    public String declineFriendRequest(@QueryParam("caller") String caller,
                                       @QueryParam("receiver") String receiver) {
        if(caller == null || receiver == null){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        try {
            frService.deleteOne(new FriendRequest(caller,receiver));
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
        return gson.toJson(frService.insertTest());
    }


}
