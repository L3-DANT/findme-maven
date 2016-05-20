package dao;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongoCmdOptionsBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;


public abstract class DAOTest {

    /**
     * Instance du process mongod
     */
    private static MongodProcess mongodProcess;
    /**
     * Instance de l'exécutable
     */
    private static MongodExecutable mongodExecutable;
    protected static final String IP = "127.0.0.1";
    protected static final int PORT = 27017;
    protected static final String DB_NAME = "findme";

    /**
     * Initialises test database
     * @throws IOException
     */
    @BeforeClass
    public static void beforeClass() throws IOException {
        MongodStarter starter = MongodStarter.getDefaultInstance();
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(IP, PORT, false))
                .cmdOptions(new MongoCmdOptionsBuilder()
                        .useSmallFiles(true)
                        .useNoJournal(true)
                        .useNoPrealloc(true)
                        .build()).build();
        mongodExecutable = starter.prepare(mongodConfig);
        System.out.println("Launching database ...");
        mongodProcess = mongodExecutable.start();
        System.out.println("Launch successful");
    }


    /**
     * Drop database and kills mongod after all tests
     * @throws UnknownHostException
     */
    @AfterClass
    public static void destroyMongodInstance() throws UnknownHostException {
        System.out.println("Dropping database " + DB_NAME+"...");
        MongoClient client = new MongoClient(IP, PORT);
        client.dropDatabase(DB_NAME);
        client.close();
        System.out.println("Database " + DB_NAME + " successfully dropped");
        System.out.println("Stopping mongo process...");
        if (mongodProcess != null) {
            mongodProcess.stop();
        }
        System.out.println("stopping executable...");
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }
        System.out.println("Tests end");
    }
}
