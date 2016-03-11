package controllers;

import com.google.gson.Gson;
import services.UserService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/user")
@Produces("application/json")
public class UserController {

    @Inject
    private UserService userService;

    @Path("/v1/users")
    @GET
    public String findAll(){
        return new Gson().toJson(userService.findAll());
    }

}
