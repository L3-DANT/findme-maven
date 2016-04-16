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
            Pusher pusher = new Pusher("http://03576a442aa390f473c6:ec540b16927f1ff8e6b9@api.pusherapp.com:80/apps/197835");
            pusher.setCluster("eu");
            pusher.setEncrypted(true);

            return pusher;
        }
    }
}
