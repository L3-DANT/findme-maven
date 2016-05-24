package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pusher.rest.Pusher;
import connections.PusherConnection;

/**
 * Abstract class used to factor uses of {@link Pusher}
 */
public abstract class Controller {

    protected Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

}
