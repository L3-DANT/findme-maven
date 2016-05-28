package controllers;

import exceptions.DuplicateDataException;
import exceptions.NotFoundException;
import models.FriendRequest;
import models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 *  Controller that manages {@link FriendRequest}
 */
@Path("/friendrequest")
@Produces("application/json")
public class FriendRequestController extends Controller{

    private final static Logger logger = LogManager.getLogger(FriendRequestController.class);


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
                logger.info("Called GET \"friendrequest/v1?caller="+caller+"&receiver="+receiver+"\". Everything went right.");
                return gson.toJson(frService.getFriendRequestByPseudos(caller,receiver));
            }catch (NotFoundException e){
                logger.error("Calling GET \"friendrequest/v1?caller="+caller+"&receiver="+receiver+"\" threw NotFoundException.",e);
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
        } else if(caller == null){
            try {
                userService.getUser(receiver);
            } catch(NotFoundException e) {
                logger.error("Calling GET \"friendrequest/v1?receiver="+receiver+"\" threw NotFoundException : User "+ receiver +" doesn't exist.",e);
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            logger.info("Called GET \"friendrequest/v1?receiver="+receiver+"\". Everything went right.");
            return gson.toJson(frService.findByReceiver(receiver));
        } else {
            try {
                userService.getUser(caller);
            } catch (NotFoundException e) {
                logger.error("Calling GET \"friendrequest/v1?caller="+caller+"\" threw NotFoundException : User "+ caller +" doesn't exist.",e);
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            logger.info("Called GET \"friendrequest/v1?caller="+caller+"\". Everything went right.");
            return gson.toJson(frService.findByCaller(caller));
        }

    }

    /**
     * Creates a {@link FriendRequest} related to two users
     * If the opposed {@link FriendRequest} already exists, makes the two {@link User} friends.
     * @param fr the parsed json
     * @throws WebApplicationException 401 if the users are already friends
     * @throws WebApplicationException 404 if at least one of the users in the {@link FriendRequest} doesn't exist in database
     * @throws WebApplicationException 409 if the {@link FriendRequest}
     */
    @Path("/v1")
    @PUT
    @Consumes("application/json")
    public void createFriendRequest(FriendRequest fr) {
        try {
            User user1 = userService.getUser(fr.getCaller());
            User user2 = userService.getUser(fr.getReceiver());
            if(user1.getFriendList().size() > user2.getFriendList().size()){
                if(user2.getFriendList().contains(user1))
                    logger.error("Calling PUT \"friendrequest/v1\" went wrong : "+user1.getPseudo()+" and "+user2.getPseudo()+" are alredy friends.");
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            } else {
                if(user1.getFriendList().contains(user2)){
                    logger.error("Calling PUT \"friendrequest/v1\" went wrong : "+user1.getPseudo()+" and "+user2.getPseudo()+" are alredy friends.");
                    throw new WebApplicationException(Response.Status.UNAUTHORIZED);
                }
            }
            if(frService.existFriendRequest(new FriendRequest(fr.getReceiver(),fr.getCaller()))){
                logger.info("Calling PUT \"friendrequest/v1\" when the opposite FriendRequest already exists. Forwarding to POST friendrequest/v1 with data\n"+gson.toJson(fr));
                acceptFriendRequest(fr);
            } else {
                frService.insertFriendRequest(fr);
                logger.info("Called PUT \"friendrequest/v1\" with data \n"+gson.toJson(fr)+"\nEverything went right.");
            }
        } catch(NotFoundException e) {
            logger.info("Calling PUT \"friendrequest/v1\" threw NotFoundException."+fr.getCaller()+" or "+fr.getReceiver()+" doesn't exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch(DuplicateDataException e){
            logger.error("Calling PUT \"friendrequest/v1\" threw DuplicateDataException.",e);
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
            logger.info("Called POST \"friendrequest/v1\" with data \n"+gson.toJson(fr)+"\nEverything went right.");
        } catch (NotFoundException e) {
            logger.info("Calling POST \"friendrequest/v1\" threw NotFoundException.",e);
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
        if(caller == null){
            logger.error("Calling DELETE \"friendrequest/v1?receiver="+receiver+"\" went wrong. Missing argument : caller.");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        } else if(receiver == null){
            logger.error("Calling DELETE \"friendrequest/v1?caller="+caller+"\" went wrong. Missing argument : receiver.");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        try {
            frService.deleteOne(new FriendRequest(caller,receiver));
            logger.info("Called DELETE \"friendrequest/v1?caller="+caller+"&receiver="+receiver+"\". Everything went right");
        } catch (NotFoundException e) {
            logger.info("Calling DELETE DELETE \"friendrequest/v1?caller="+caller+"&receiver="+receiver+"\" threw NotFoundException.",e);
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
