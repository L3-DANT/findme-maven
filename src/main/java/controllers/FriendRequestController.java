package controllers;

import exceptions.DuplicateDataException;
import exceptions.NotFoundException;
import models.FriendRequest;
import models.User;
import services.FriendRequestService;
import services.UserService;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 *  Controller that manages {@link FriendRequest}
 */
@Path("/friendrequest")
@Produces("application/json")
public class FriendRequestController extends Controller{

    private FriendRequestService frService = new FriendRequestService();

    private UserService userService = new UserService();

    /**
     * @param caller the user that asked or received the {@link FriendRequest}
     * @param receiver the user that asked or received the {@link FriendRequest}
     * @return the {@link FriendRequest} if two parameters are given, or a list of pseudos depending on which parameter has been given
     * @throws WebApplicationException 404 is the {@link FriendRequest} can't be found or if the given pseudo is not related to a {@link User#pseudo} in Database
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
     * @throws WebApplicationException 404 if at least one of the users in the {@link FriendRequest} doesn't exist in database
     * @throws WebApplicationException 409 if the {@link FriendRequest} already exists
     */
    @Path("/v1")
    @PUT
    @Consumes("application/json")
    public void createFriendRequest(FriendRequest fr) {
        try {
            userService.getUser(fr.getCaller());
            userService.getUser(fr.getReceiver());
            frService.insertFriendRequest(fr);
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
     * @throws WebApplicationException 404 if the {@link FriendRequest} can't be found
     */
    @Path("/v1")
    @POST
    @Consumes("application/json")
    public void acceptFriendRequest(FriendRequest fr) {
        try {
            userService.addFriend(fr.getCaller(), fr.getReceiver());
            frService.deleteOne(fr);
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    /**
     * Declines a {@link FriendRequest}, which means removing it from database
     * @param caller the caller (or receiver)
     * @param receiver the receiver (or caller)
     * @throws WebApplicationException 404 if the {@link FriendRequest} can't be found
     */
    @Path("/v1")
    @DELETE
    public void declineFriendRequest(@QueryParam("caller") String caller,
                                       @QueryParam("receiver") String receiver) {
        if(caller == null || receiver == null){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        try {
            frService.deleteOne(new FriendRequest(caller,receiver));
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
