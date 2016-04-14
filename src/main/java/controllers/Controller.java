package controllers;

import com.pusher.rest.Pusher;
import connections.PusherConnection;

public abstract class Controller {

    protected static final Pusher pusher = PusherConnection.getPusher();

}
