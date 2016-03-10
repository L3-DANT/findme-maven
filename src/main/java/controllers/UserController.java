package controllers;

import com.google.gson.Gson;
import services.UserService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@RequestScoped
@Path("/user")
public class UserController {

    @Inject
    private UserService userService;

    @Path("/v1/users")
    @GET
    @Produces("application/json")
    public String findAll(){
        return new Gson().toJson(userService.findAll());
    }

}
