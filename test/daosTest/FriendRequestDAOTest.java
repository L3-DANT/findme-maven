package daosTest;

import com.google.gson.Gson;
import daos.FriendRequestDAO;
import exceptions.DuplicateDataException;
import exceptions.NotFoundException;
import models.FriendRequest;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.DatabaseUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FriendRequestDAOTest extends AbstractDAOTest {

    private FriendRequestDAO dao;
    private FriendRequest fr = new FriendRequest("a", "b");
    private FriendRequest fr2 = new FriendRequest("a", "c");
    private FriendRequest fr3 = new FriendRequest("a", "d");
    private FriendRequest fr4 = new FriendRequest("e", "b");
    private FriendRequest fr5 = new FriendRequest("f", "b");


    private FriendRequest getDBFR(String pseudo1, String pseudo2) {
        Document doc = DatabaseUtils.getColl("friendrequest").find(or(and(eq("caller", pseudo1), eq("receiver", pseudo2)), and(eq("caller", pseudo2), eq("receiver", pseudo1)))).first();
        return doc == null ? null : new Gson().fromJson(doc.toJson(), FriendRequest.class);
    }

    @Before
    public void insertBefore() {
        dao = new FriendRequestDAO();
        DatabaseUtils.initialiseCollection("friendrequest", fr, fr2, fr3, fr4, fr5);
    }

    @After
    public void dropColl() {
        DatabaseUtils.clearCollection("friendrequest");
    }

    @Test
    public void insertOneSuccess() throws DuplicateDataException {
        FriendRequest u = dao.insertOne(new FriendRequest("Alfred", "Hitchcock"));
        assertEquals(getDBFR(u.getCaller(), u.getReceiver()), u);
    }

    @Test(expected = DuplicateDataException.class)
    public void insertOneRightOrderDuplicateDataException() throws DuplicateDataException {
        dao.insertOne(new FriendRequest(fr.getCaller(), fr.getReceiver()));
    }

    @Test(expected = DuplicateDataException.class)
    public void insertOneWrongOrderDuplicateDataException() throws DuplicateDataException {
        dao.insertOne(new FriendRequest(fr.getReceiver(), fr.getCaller()));
    }

    @Test
    public void findOneByPseudosSuccess() throws NotFoundException {
        assertEquals(dao.findOneByPseudos(fr.getCaller(), fr.getReceiver()), fr);
        assertEquals(dao.findOneByPseudos(fr.getReceiver(), fr.getCaller()), fr);
    }

    @Test(expected = NotFoundException.class)
    public void findOneByPseudosNotFoundException() throws NotFoundException {
        dao.findOneByPseudos("a", "Georges");
    }

    @Test
    public void deleteOneSuccess() throws NotFoundException {
        dao.deleteOne(fr);
        assertNull(getDBFR(fr.getCaller(), fr.getReceiver()));
        dao.deleteOne(fr2);
        assertNull(getDBFR(fr2.getReceiver(), fr2.getCaller()));
    }

    @Test(expected = NotFoundException.class)
    public void deleteOneNotFoundException() throws NotFoundException {
        dao.deleteOne(new FriendRequest("Jean", "Georges"));
    }

    @Test
    public void deleteManySuccess() {
        dao.deleteMany("a");
        assertNull(getDBFR("a",fr.getReceiver()));
        assertNull(getDBFR("a",fr2.getReceiver()));
        assertNull(getDBFR("a",fr3.getReceiver()));
    }

    @Test
    public void findByFieldCallerSuccess() {
        List<FriendRequest> result = dao.findByField("caller", "a");
        List<FriendRequest> expected = new ArrayList<FriendRequest>();
        expected.add(fr);
        expected.add(fr2);
        expected.add(fr3);
        FriendRequest[] res = result.toArray(new FriendRequest[result.size()]);
        FriendRequest[] exp = result.toArray(new FriendRequest[expected.size()]);
        Comparator<FriendRequest> comparator = new Comparator<FriendRequest>() {
            public int compare(FriendRequest o1, FriendRequest o2) {
                return o1.getReceiver().compareTo(o2.getReceiver());
            }
        };
        Arrays.sort(exp,comparator);
        Arrays.sort(res, comparator);
        assertArrayEquals(res,exp);
    }

    @Test
    public void findByFieldReceiverSuccess() {
        List<FriendRequest> result = dao.findByField("receiver", "b");
        List<FriendRequest> expected = new ArrayList<FriendRequest>();
        expected.add(fr);
        expected.add(fr2);
        expected.add(fr5);
        FriendRequest[] res = result.toArray(new FriendRequest[result.size()]);
        FriendRequest[] exp = result.toArray(new FriendRequest[expected.size()]);
        Comparator<FriendRequest> comparator = new Comparator<FriendRequest>() {
            public int compare(FriendRequest o1, FriendRequest o2) {
                return o1.getReceiver().compareTo(o2.getCaller());
            }
        };
        Arrays.sort(exp,comparator);
        Arrays.sort(res, comparator);
        assertArrayEquals(res,exp);
    }

    @Test
    public void findByFieldEmptyList() {
        List<FriendRequest> result = dao.findByField("caller","c");
        assertEquals(result.size(),0);
        result = dao.findByField("receiver","e");
        assertEquals(result.size(),0);
        result = dao.findByField("meuporg","a");
        assertEquals(result.size(),0);
    }
}
