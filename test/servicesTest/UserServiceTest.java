package servicesTest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.gson.Gson;
import daos.UserDAO;
import exceptions.DuplicateDataException;
import exceptions.NotFoundException;
import exceptions.UnauthorisedException;
import org.junit.*;
import org.junit.runner.RunWith;
import models.User;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.yaml.snakeyaml.Yaml;
import security.BCrypt;
import services.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UserService.class})
public class UserServiceTest {

    private UserDAO userDAO;
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        PowerMockito.whenNew(UserDAO.class).withNoArguments().thenReturn(PowerMockito.mock(UserDAO.class));
        userDAO = new UserDAO();
        userService = new UserService();
    }

    @Test
    public void insertUserSuccess() throws DuplicateDataException {
        User user = new User("Toto");
        when(userDAO.insertOne(any(User.class))).thenReturn(user);

        User u = userService.insertUser(user);
        assertEquals(user,u);

        verify(userDAO, times(1)).insertOne(any(User.class));
    }

    @Test(expected = DuplicateDataException.class)
    public void insertUserDuplicateDataException() throws DuplicateDataException {
        doThrow(new DuplicateDataException("User already exists in database")).when(userDAO).insertOne(any(User.class));
        userService.insertUser(new User("John"));
    }


    @Test
    public void updateUserWithSmallerFriendListDifferentPassword() throws Exception {
        User user = new User("Bob", BCrypt.hashpw("123", (BCrypt.gensalt(12))));
        User usercopy = new User("Bob","abc");
        User mock = PowerMockito.mock(User.class);
        User u1 = new User("John");
        User u2 = new User("Fred");
        User u3 = new User("Jamy");
        user.addFriend(u1);
        user.addFriend(u2);
        user.addFriend(u3);
        usercopy.addFriend(u1);
        when(userDAO.findOneByPseudo(usercopy.getPseudo())).thenReturn(user);
        when(userDAO.findOneByPseudo(u1.getPseudo())).thenReturn(u1);
        when(userDAO.findOneByPseudo(u2.getPseudo())).thenReturn(u2);
        when(userDAO.findOneByPseudo(u3.getPseudo())).thenReturn(u3);

        when(mock.getPseudo()).thenReturn(user.getPseudo());
        when(mock.getPassword()).thenReturn(user.getPassword());
        when(mock.getLatitude()).thenReturn(user.getLatitude());
        when(mock.getLongitude()).thenReturn(user.getLongitude());
        when(mock.getFriendList()).thenReturn(user.getFriendList());
        when(mock.getPhoneNumber()).thenReturn(user.getPhoneNumber());
        PowerMockito.whenNew(User.class).withArguments(anyString(),anyString(),anyFloat(),anyFloat(),any(User.State.class),anyString()).thenReturn(mock);


        String[] result = userService.updateUser(usercopy);
        String[] expected = {u2.getPseudo(),u3.getPseudo()};
        Arrays.sort(result);
        Arrays.sort(expected);
        assertArrayEquals(result,expected);

        //make sure password has been changed
        verify(mock, times(1)).setPassword(anyString());
        //make sure the user has one friend
        verify(mock,times(1)).addFriend(any(User.class));
        verify(userDAO,times(4)).findOneByPseudo(anyString());
        verify(userDAO,times(3)).replaceOne(any(User.class));


    }

    @Test
    public void updateUserWithSameFriendListSamePassword() throws Exception {
        User user = new User("Bob", "123");
        User mock = PowerMockito.mock(User.class);
        when(userDAO.findOneByPseudo(anyString())).thenReturn(user);
        when(mock.getPseudo()).thenReturn(user.getPseudo());
        when(mock.getPassword()).thenReturn(BCrypt.hashpw("123", (BCrypt.gensalt(12))));
        when(mock.getLatitude()).thenReturn(user.getLatitude());
        when(mock.getLongitude()).thenReturn(user.getLongitude());
        when(mock.getFriendList()).thenReturn(user.getFriendList());
        when(mock.getPhoneNumber()).thenReturn(user.getPhoneNumber());
        PowerMockito.whenNew(User.class).withArguments(anyString(),anyString(),anyFloat(),anyFloat(),any(User.State.class),anyString()).thenReturn(mock);

        String [] result = userService.updateUser(user);
        assertNull(result);
        verify(mock, times(0)).setPassword(anyString());
        verify(mock,times(0)).addFriend(any(User.class));
        verify(userDAO,times(1)).findOneByPseudo(anyString());
        verify(userDAO,times(1)).replaceOne(any(User.class));
    }

    @Test(expected = UnauthorisedException.class)
    public void updateUserWithBiggerFriendListUnauthorisedException() throws NotFoundException, UnauthorisedException {
        User user = new User("Bob", "123");
        User usercopy = new User("Bob","123");
        usercopy.addFriend(new User("John"));
        when(userDAO.findOneByPseudo(anyString())).thenReturn(user);
        userService.updateUser(usercopy);
    }

    @Test(expected = UnauthorisedException.class)
    public void updateUserWithDifferentFriendListUnauthorisedException() throws NotFoundException, UnauthorisedException {
        User user = new User("Bob", "123");
        user.addFriend(new User("John"));
        User usercopy = new User("Bob","123");
        usercopy.addFriend(new User("Fred"));
        when(userDAO.findOneByPseudo(anyString())).thenReturn(user);
        userService.updateUser(usercopy);
    }


    @Test(expected = NotFoundException.class)
    public void updateUserNotFoundException() throws NotFoundException, UnauthorisedException {
        doThrow(new NotFoundException("User not found")).when(userDAO).findOneByPseudo(anyString());
        userService.updateUser(new User("John"));
    }

    @Test
    public void addFriendSuccess() throws NotFoundException {
        User u1 = new User("Fred");
        User u2 = new User("Jamy");
        when(userDAO.findOneByPseudo(u1.getPseudo())).thenReturn(new User(u1.getPseudo())).thenReturn(u1);
        when(userDAO.findOneByPseudo(u2.getPseudo())).thenReturn(new User(u2.getPseudo())).thenReturn(u2);
        userService.addFriend(u1.getPseudo(),u2.getPseudo());
        assertEquals(u1.getFriendList().size(),1);
        assertEquals(u1.getFriendList().get(0),u2);
        assertEquals(u2.getFriendList().size(),1);
        assertEquals(u2.getFriendList().get(0),u1);

        verify(userDAO,times(4)).findOneByPseudo(anyString());
    }

    @Test(expected = NotFoundException.class)
    public void addFriendNotFoundException() throws NotFoundException{
        doThrow(new NotFoundException("User not found")).when(userDAO).findOneByPseudo(anyString());
        userService.addFriend("John","Lennon");
    }

    @Test
    public void getUserSuccess() throws NotFoundException {
        User user = new User("Toto");
        User u1 = new User("Bob");
        User u2 = new User("John");
        user.addFriend(u1);
        user.addFriend(u2);
        when(userDAO.findOneByPseudo(anyString())).thenReturn(user);

        User ret = userService.getUser("Toto");
        assertEquals(user, ret);
        for (User u : ret.getFriendList()) {
            assertTrue(u.equals(u1) || u.equals(u2));
        }

        verify(userDAO, times(3)).findOneByPseudo(anyString());
    }


    @Test(expected = NotFoundException.class)
    public void getUserNotFoundException() throws NotFoundException {
        doThrow(new NotFoundException("User not found")).when(userDAO).findOneByPseudo(anyString());
        userService.getUser("Toto");
    }

    @Test
    public void connectSuccess() throws NotFoundException {
        User user = new User("Bob", BCrypt.hashpw("123", (BCrypt.gensalt(12))));
        when(userDAO.findOneByPseudo(anyString())).thenReturn(user);
        assertTrue(userService.connect("Bob","123"));
        verify(userDAO,times(1)).findOneByPseudo(anyString());
    }

    @Test
    public void connectFailure() throws NotFoundException {
        User user = new User("Bob", BCrypt.hashpw("123", (BCrypt.gensalt(12))));
        when(userDAO.findOneByPseudo(anyString())).thenReturn(user);
        assertFalse(userService.connect("Bob","abc"));
    }

    @Test(expected = NotFoundException.class)
    public void connectNotFoundException() throws NotFoundException {
        doThrow(new NotFoundException("User not found")).when(userDAO).findOneByPseudo(anyString());
        userService.connect("Bob","123");
    }

    @Test
    public void deleteSuccess() throws NotFoundException {
        userService.deleteUser("John");
        verify(userDAO,times(1)).deleteOne(anyString());
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotFoundException() throws NotFoundException {
        doThrow(new NotFoundException("User not found")).when(userDAO).deleteOne(anyString());
        userService.deleteUser("John");
    }

    @Test
    public void updateCoordinatesSuccess() throws NotFoundException {
        float lat = 2.85f;
        float lon = 21.785f;
        float epsilon = 0.00000001f;
        User user = new User("Bob",452.1f,48.36f,User.State.ONLINE);
        User u = new User("Bob",lat,lon,User.State.ONLINE);
        when(userDAO.findOneByPseudo(anyString())).thenReturn(user);
        userService.updateCoordinates(user);
        verify(userDAO,times(1)).findOneByPseudo(anyString());
        assertTrue(Math.abs(u.getLatitude() - lat) < epsilon);
        assertTrue(Math.abs(u.getLongitude() - lon) < epsilon);
    }

    @Test(expected = NotFoundException.class)
    public void updateCoordinatesNotFoundException() throws NotFoundException {
        doThrow(new NotFoundException("User not found")).when(userDAO).findOneByPseudo(anyString());
        userService.updateCoordinates(new User("John",45f,78f, User.State.ONLINE));
    }


}
