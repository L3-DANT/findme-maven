package utils;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import connections.Config;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongoCmdOptionsBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import org.bson.Document;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseUtils {
    /**
     * Mongod process instance
     */
    private static MongodProcess mongodProcess;
    /**
     * Executable Instance
     */
    private static MongodExecutable mongodExecutable;

    public static final String IP = Config.DATABASE_HOST;
    public static final int PORT = Config.DATABASE_PORT;
    public static final String DB_NAME = Config.DATABASE_NAME;

    private static Map<String,MongoCollection<Document>> map = new HashMap<String,MongoCollection<Document>>();
    private static MongoClient client;

    /**
     * Initialises test database
     * @throws IOException
     */
    public static void lauchMongodInstance() throws IOException {
        MongodStarter starter = MongodStarter.getDefaultInstance();
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(IP, PORT, false))
                .cmdOptions(new MongoCmdOptionsBuilder()
                        .useSmallFiles(true)
                        .useNoJournal(true)
                        .useNoPrealloc(true)
                        .build()).build();
        System.out.println("Starting executable...");
        mongodExecutable = starter.prepare(mongodConfig);
        System.out.println("Launching database ...");
        mongodProcess = mongodExecutable.start();
        System.out.println("Launch successful");
        client = new MongoClient(IP, PORT);
    }


    /**
     * Drop database and kills mongod after all tests
     * @throws UnknownHostException
     */
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

    public static void initialiseCollection(String name, Object... docs) {
        map.put(name,client.getDatabase(DB_NAME).getCollection(name));
        for (Object o : docs) {
            map.get(name).insertOne(Document.parse(new Gson().toJson(o)));
        }
    }

    public static void clearCollection(String... names) {
        for (String name : names) {
            map.get(name).drop();
            map.remove(name);
        }
    }

    public static MongoCollection<Document> getColl(String name) {
        return map.get(name);
    }
}
