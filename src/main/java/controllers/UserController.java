package controllers;

import models.User;
import services.UserService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
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
    public JsonArray findAll(){
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (User user : userService.findAll()) {
            builder.add(Json.createObjectBuilder().add("pseudo",user.getPseudo()));
            builder.add(Json.createObjectBuilder().add("x",user.getX()));
            builder.add(Json.createObjectBuilder().add("y",user.getY()));
        }
        return builder.build();

    }

}
