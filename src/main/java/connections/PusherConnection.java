package connections;

import com.pusher.rest.Pusher;

/**
 * Singleton giving access to Pusher
 */
public class PusherConnection {

    public static Pusher getPusher(){
        return PusherInitializer.getPusher();
    }

    private PusherConnection() {}

    private static final class PusherInitializer {

        private static final Pusher getPusher(){
            Pusher pusher = new Pusher(Config.APP_ID, Config.KEY, Config.SECRET);
            pusher.setCluster("eu");
            pusher.setEncrypted(true);
            return pusher;
        }
    }
}
