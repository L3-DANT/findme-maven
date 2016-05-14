package controllers;

import com.pusher.rest.Pusher;
import connections.PusherConnection;

/**
 * Abstract class used to factor uses of {@link Pusher}
 */
public abstract class Controller {

    protected Pusher pusher = PusherConnection.getPusher();

}
