package servicesTest;

import static org.junit.Assert.*;
import daos.FriendRequestDAO;
import exceptions.DuplicateDataException;
import exceptions.NotFoundException;
import models.FriendRequest;
import org.junit.*;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import services.FriendRequestService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FriendRequestService.class})
public class FriendRequestServiceTest {
    private FriendRequestDAO frDAO;
    private FriendRequestService frService;

    @Before
    public void setUp() throws Exception {
        PowerMockito.whenNew(FriendRequestDAO.class).withNoArguments().thenReturn(PowerMockito.mock(FriendRequestDAO.class));
        frDAO = new FriendRequestDAO();
        frService = new FriendRequestService();
    }

    @Test
    public void insertFriendRequestSuccess() throws DuplicateDataException {
        FriendRequest fr = new FriendRequest("Fred","Jamy");
        when(frDAO.insertOne(any(FriendRequest.class))).thenReturn(fr);
        assertEquals(fr,frService.insertFriendRequest(fr));
        verify(frDAO,times(1)).insertOne(any(FriendRequest.class));
    }

    @Test(expected = DuplicateDataException.class)
    public void insertFriendRequestDuplicateDataException() throws DuplicateDataException {
        doThrow(new DuplicateDataException("FriendRequest already exists in database")).when(frDAO).insertOne(any(FriendRequest.class));
        frService.insertFriendRequest(new FriendRequest("Tomtom","Nana"));
    }

    @Test
    public void getFriendRequestByPseudosSuccess() throws NotFoundException {
        FriendRequest fr = new FriendRequest("Fred","Jamy");
        when(frDAO.findOneByPseudos(anyString(),anyString())).thenReturn(fr);
        assertEquals(fr,frService.getFriendRequestByPseudos(fr.getCaller(),fr.getReceiver()));
        verify(frDAO,times(1)).findOneByPseudos(anyString(),anyString());
    }

    @Test(expected = NotFoundException.class)
    public void getFriendRequestByPseudosNotFoundException() throws NotFoundException {
        doThrow(new NotFoundException("FriendRequest not found")).when(frDAO).findOneByPseudos(anyString(),anyString());
        frService.getFriendRequestByPseudos("John","Lennon");
    }

    @Test
    public void existFriendRequest(){
        when(frDAO.existFriendRequest(any(FriendRequest.class))).thenReturn(true).thenReturn(false);
        assertTrue(frService.existFriendRequest(new FriendRequest("John","Jean-Paul")));
        assertFalse(frService.existFriendRequest(new FriendRequest("John","Jean-Paul")));
        verify(frDAO,times(2)).existFriendRequest(any(FriendRequest.class));
    }

    @Test
    public void deleteOneSuccess() throws NotFoundException {
        frService.deleteOne(new FriendRequest("Fred","Jamy"));
        verify(frDAO,times(1)).deleteOne(any(FriendRequest.class));
    }

    @Test
    public void deleteManySuccess() {
        frService.deleteMany("John");
        verify(frDAO,times(1)).deleteMany(anyString());
    }

    @Test(expected = NotFoundException.class)
    public void deleteOneNotFoundException() throws NotFoundException {
        doThrow(new NotFoundException("FriendRequest not found")).when(frDAO).deleteOne(any(FriendRequest.class));
        frService.deleteOne(new FriendRequest("Fred","Jamy"));
    }

    @Test
    public void findByCaller(){
        FriendRequest fr1 = new FriendRequest("a","b");
        FriendRequest fr2 = new FriendRequest("a","c");
        FriendRequest fr3 = new FriendRequest("a","d");
        List<FriendRequest> list = new ArrayList<FriendRequest>();
        list.add(fr1);
        list.add(fr2);
        list.add(fr3);
        when(frDAO.findByField(anyString(),anyString())).thenReturn(list);
        String[] expected = {"b","c","d"};
        String[] result = frService.findByCaller("a");
        Arrays.sort(expected);
        Arrays.sort(result);
        assertArrayEquals(result,expected);
        verify(frDAO,times(1)).findByField("caller","a");
    }

    @Test
    public void findByReceiver(){
        FriendRequest fr1 = new FriendRequest("1","a");
        FriendRequest fr2 = new FriendRequest("2","a");
        FriendRequest fr3 = new FriendRequest("3","a");
        List<FriendRequest> list = new ArrayList<FriendRequest>();
        list.add(fr1);
        list.add(fr2);
        list.add(fr3);
        when(frDAO.findByField(anyString(),anyString())).thenReturn(list);
        String[] expected = {"1","2","3"};
        String[] result = frService.findByReceiver("a");
        Arrays.sort(expected);
        Arrays.sort(result);
        assertArrayEquals(result,expected);
        verify(frDAO,times(1)).findByField("receiver","a");
    }

}
