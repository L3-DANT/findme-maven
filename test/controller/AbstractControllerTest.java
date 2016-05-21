package controller;


import com.google.gson.Gson;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import utils.DatabaseUtils;

import java.io.IOException;
import java.net.UnknownHostException;

public class AbstractControllerTest extends JerseyTest{

    protected Gson gson = new Gson();

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
