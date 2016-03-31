package controllers;


import com.google.gson.Gson;
import models.FriendRequest;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@RequestScoped
@Path("/friendrequest")
@Produces("application/json")
public class FriendRequestController {

    @Path("/v1/createfriendrequest")
    @PUT
    @Consumes("application/json")
    public String insertDemande(FriendRequest fr){
        return new Gson().toJson(fr);
    }

}
