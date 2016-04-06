package controllers;


import com.google.gson.Gson;
import models.FriendRequest;
import models.User;
import services.FriendRequestService;
import services.UserService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;

@RequestScoped
@Path("/friendrequest")
@Produces("application/json")
public class FriendRequestController {

    @Inject
    private FriendRequestService frService;

    @Inject
    private UserService userService;

    @Path("/v1/createfriendrequest")
    @PUT
    @Consumes("application/json")
    public String insertFriendRequest(User... listUser) throws Exception {
        if(listUser.length != 2) {
            throw new Exception("");
        } else {
            User asker = listUser[0];
            User receiver = listUser[1];
            FriendRequest fr = new FriendRequest();
            fr.setAsker(asker.getPseudo());
            fr.setReceiver(receiver.getPseudo());
            frService.insertFriendRequest(fr);
            return new Gson().toJson(fr);
        }
    }

    @Path("/v1/acceptfriendrequest")
    @POST
    @Consumes("application/json")
    public String acceptFriendRequest(User... listUser) throws Exception {
        if(listUser.length != 2) {
            throw new Exception("");
        } else {
            User asker = listUser[0];
            User receiver = listUser[1];
            FriendRequest fr = frService.acceptFriendRequest(asker.getPseudo(), receiver.getPseudo());
            userService.addFriend(asker, receiver);
            return new Gson().toJson(fr);
        }
    }

    @Path("/v1/declinefriendrequest")
    @POST
    @Consumes("application/json")
    public String declineFriendRequest(User... listUser) throws Exception {
        if(listUser.length != 2) {
            throw new Exception("");
        } else {
            User asker = listUser[0];
            User receiver = listUser[1];
            FriendRequest fr = frService.declineFriendRequest(asker.getPseudo(), receiver.getPseudo());
            return new Gson().toJson(fr);
        }
    }

}
