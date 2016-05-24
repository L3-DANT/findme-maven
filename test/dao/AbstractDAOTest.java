package dao;

import java.io.IOException;
import java.net.UnknownHostException;

import org.bson.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import utils.DatabaseUtils;

import static com.mongodb.client.model.Filters.eq;

public abstract class AbstractDAOTest {
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
