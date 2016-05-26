package controllerTest;


import com.google.gson.Gson;
import models.FriendRequest;
import models.User;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import security.BCrypt;
import services.FriendRequestService;
import services.UserService;
import utils.DatabaseUtils;

import java.io.IOException;
import java.net.UnknownHostException;

public class AbstractControllerTest extends JerseyTest{

    protected Gson gson = new Gson();
    protected User alfred = new User("Alfred", BCrypt.hashpw("123", (BCrypt.gensalt(12))));
    protected User u = new User("Bob","123");
    protected User u2 = new User("John","123");
    protected User u3 = new User("Fred","123");

    protected User bob = new User("Bobby", "123");
    protected User john = new User("Johny", "123");
    protected User meuporg = new User("Meuporg", "123");

    protected FriendRequest fr = new FriendRequest(alfred.getPseudo(),"Bob");
    protected FriendRequest fr2 = new FriendRequest(alfred.getPseudo(),"John");
    protected FriendRequest fr3 = new FriendRequest("John","Bob");

    protected UserService userService;
    protected FriendRequestService frService;

    @Before
    public void insertBefore(){
        User ucopy = new User("Bob","123");
        User u2copy = new User("John","123");
        User u3copy = new User("Fred","123");
        u.addFriend(alfred);
        u2.addFriend(alfred);
        u3.addFriend(alfred);
        alfred.addFriend(ucopy);
        alfred.addFriend(u2copy);
        alfred.addFriend(u3copy);

        userService = new UserService();
        frService = new FriendRequestService();
        DatabaseUtils.initialiseCollection("user", alfred,bob,john,meuporg,u,u2,u3);
        DatabaseUtils.initialiseCollection("friendrequest",fr,fr2,fr3);
    }

    @After
    public void dropColl(){
        DatabaseUtils.clearCollection("user","friendrequest");
    }

    /**
     * Initialises test database
     * @throws IOException
     */
    @BeforeClass
    public static void beforeClass() throws IOException {
        DatabaseUtils.lauchMongodInstance();
    }


    /**
     * Drop database and kills mongod after all tests
     * @throws UnknownHostException
     */
    @AfterClass
    public static void destroyMongodInstance() throws UnknownHostException {
        DatabaseUtils.destroyMongodInstance();
    }
}
