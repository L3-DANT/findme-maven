package services;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import daos.UserDAO;
import exceptions.NotFoundException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import models.User;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UserDAO.class,UserService.class})
@PowerMockIgnore( {"javax.management.*"})
public class UserServiceTest {

    private UserDAO userDAO;
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.whenNew(UserDAO.class).withNoArguments().thenReturn(PowerMockito.mock(UserDAO.class));
        userDAO = new UserDAO();
        userService = new UserService();
    }

    @Test
    public void getUserNoFriendsAndSuccess() throws NotFoundException {
        User user = new User("Toto");
        when(userDAO.findOneByPseudo(anyString())).thenReturn(user);
        assertEquals(user, userService.getUser("Toto"));
        verify(userDAO, times(1)).findOneByPseudo(anyString());
    }

    @Test
    public void getUserWithFriendsAndSuccess() {
//        User user = new User("Toto", "");
//        User friend1 = new User("Friend1", "");
//        User friend2 = new User("Friend2", "");
//        user.addFriend(friend1);
//        user.addFriend(friend2);
//
//        try {
//            when(userDAO.findOneByPseudo(anyString())).thenReturn(user);
//            assertEquals("User must be the same", user, userService.getUser("Toto"));
//            verify(userDAO, times(3)).findOneByPseudo(anyString());
//        } catch (NotFoundException e) {
//            String expectedMessage = "User not found";
//            Assert.assertEquals("Exception message must be correct", expectedMessage, e.getMessage());
//        }
    }

    @Test
    public void getUserWithFail() {
//        User user = new User("Toto", "");
//        NotFoundException exception = new NotFoundException("User not found");
//
//        try {
//            //when(userDAO.findOneByPseudo(anyString())).thenReturn(user);
//            doThrow(new NotFoundException("User not found")).when(userDAO).findOneByPseudo(anyString());
//            assertEquals("Exception must be handle", exception, userService.getUser("Toto"));
//        } catch (NotFoundException e) {
//            System.out.println(e.getMessage());
//            String expectedMessage = "User not found";
//            Assert.assertEquals("Exception message must be correct", expectedMessage, e.getMessage());
//        }
    }
}
