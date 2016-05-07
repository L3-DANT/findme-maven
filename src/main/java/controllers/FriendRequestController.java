package controllers;

import com.mongodb.MongoException;
import exceptions.NotFoundException;
import models.FriendRequest;
import models.User;
import services.FriendRequestService;
import services.UserService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;

import java.util.List;

import static utils.Utils.gson;

/**
 *  Controller that manages {@link FriendRequest}
 */
@RequestScoped
@Path("/friendrequest")
@Produces("application/json")
public class FriendRequestController extends Controller{

    @Inject
    private FriendRequestService frService;

    @Inject
    private UserService userService;


    /**
     * Creates a {@link FriendRequest} related to two users
     * @param fr the parsed json
     * @return true if the user's created, or false if it already exists
     */
    @Path("/v1/create")
    @PUT
    @Consumes("application/json")
    public String createFriendRequest(FriendRequest fr) {
        try {
            return jsonResponse(0, "success", null);
        } catch(MongoException e){
            return jsonResponse(-1,"friendRequest is already in database",null);
        }

    }

    /**
     * Accepts a {@link FriendRequest}, which means removing it from database and
     * adding the first {@link User} in the second's {@link User#friendList} and reversely
     * @param fr the parsed json
     * @return true if everything went ok, false if at least one of the users were not found
     */
    @Path("/v1/accept")
    @PUT
    @Consumes("application/json")
    public String acceptFriendRequest(FriendRequest fr) {
        try {
            userService.addFriend(fr.getCaller(), fr.getReceiver());
            frService.deleteOne(fr);
            return jsonResponse(0,"success",null);
        } catch (NotFoundException e) {
            return jsonResponse(-1,"friendRequest not found",null);
        }
    }

    /**
     * Declines a {@link FriendRequest}, which means removing it from database
     * @param fr the parsed json
     */
    @Path("/v1/decline")
    @DELETE
    public String declineFriendRequest(FriendRequest fr) {
        try {
            frService.deleteOne(fr);
            return jsonResponse(0, "success", null);
        } catch (NotFoundException e) {
            return jsonResponse(-1, "friendRequest not found", null);
        }
    }


    /**
     * Gets a {@link FriendRequest}
     * @param pseudo1 the user that asked or received the {@link FriendRequest}
     * @param pseudo2 the user that asked or received the {@link FriendRequest}
     * @return the {@link FriendRequest} or false if not found
     */
    @Path("/v1/get")
    @GET
    public String getFriendRequest(@QueryParam("pseudo1") String pseudo1,
                                   @QueryParam("pseudo2") String pseudo2) {
        try{
            FriendRequest fr = frService.getFriendRequestByPseudos(pseudo1,pseudo2);
            return jsonResponse(0, "success",gson.toJson(fr));
        }catch (NotFoundException e){
            return jsonResponse(-1,"friendRequest not found",null);
        }
    }

    @Path("/v1/callers")
    @GET
    public String getCaller(@QueryParam("pseudo") String pseudo) {
        try {
            List<FriendRequest> list = frService.findByReceiver(pseudo);
            String data = "[";
            int i = 0;
            for(FriendRequest fr : list){
                if(++i == list.size())
                    data += "\""+fr.getCaller()+"\"";
                else
                    data += "\""+fr.getCaller()+"\",";
            }
            data += "]";
            return jsonResponse(0,"success",data);
        } catch (NotFoundException e) {
            return jsonResponse(-1,"no caller found",null);
        }
    }

    @Path("/v1/receivers")
    @GET
    public String getReceivers(@QueryParam("pseudo") String pseudo) {
        try {
            List<FriendRequest> list = frService.findByCaller(pseudo);
            String data = "[";
            int i = 0;
            for(FriendRequest fr : list){
                if(++i == list.size())
                    data += "\""+fr.getReceiver()+"\"";
                else
                    data += "\""+fr.getReceiver()+"\",";
            }
            data += "]";
            return jsonResponse(0,"success",data);
        } catch (NotFoundException e) {
            return jsonResponse(-1,"no receiver found",null);
        }
    }

}
