package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import services.FriendRequestService;
import services.UserService;

/**
 * Abstract class used to factor uses of {@link Gson}
 */
public abstract class Controller {

    protected Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    protected UserService userService = new UserService();
    protected FriendRequestService frService = new FriendRequestService();

}
