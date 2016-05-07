package controllers;

import com.pusher.rest.Pusher;
import connections.PusherConnection;

/**
 * Abstract class used to factor uses of {@link Pusher}
 */
public abstract class Controller {

    protected Pusher pusher = PusherConnection.getPusher();

    protected String jsonResponse(int code, String message, String data){
        String s = "{\"status\":" + code +"," +
                "\"message\":\"" + message + "\"," +
                "\"data\":" + data +"}";
        return s;
    }

}
