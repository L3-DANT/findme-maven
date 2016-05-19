package services;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import daos.UserDAO;
import exceptions.NotFoundException;
import org.junit.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import models.User;

public class UserServiceTest {
    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        //Add Mockito to JUnit
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        userService = null;
    }

    @Test
    public void getUserNoFriendsAndSuccess() {
        User user = new User("Toto", "");

        try {
            when(userDAO.findOneByPseudo(anyString())).thenReturn(user);
            assertEquals("User must be the same", user, userService.getUser("Toto"));
            verify(userDAO, times(1)).findOneByPseudo(anyString());
        } catch (NotFoundException e) {
            String expectedMessage = "User not found";
            Assert.assertEquals("Exception message must be correct", expectedMessage, e.getMessage());
        }
    }

    @Test
    public void getUserWithFriendsAndSuccess() {
        User user = new User("Toto", "");
        User friend1 = new User("Friend1", "");
        User friend2 = new User("Friend2", "");
        user.addFriend(friend1);
        user.addFriend(friend2);

        try {
            when(userDAO.findOneByPseudo(anyString())).thenReturn(user);
            assertEquals("User must be the same", user, userService.getUser("Toto"));
            verify(userDAO, times(3)).findOneByPseudo(anyString());
        } catch (NotFoundException e) {
            String expectedMessage = "User not found";
            Assert.assertEquals("Exception message must be correct", expectedMessage, e.getMessage());
        }
    }

    @Test
    public void getUserWithFail() {
        User user = new User("Toto", "");
        NotFoundException exception = new NotFoundException("User not found");

        try {
            //when(userDAO.findOneByPseudo(anyString())).thenReturn(user);
            doThrow(new NotFoundException("User not found")).when(userDAO).findOneByPseudo(anyString());
            assertEquals("Exception must be handle", exception, userService.getUser("Toto"));
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
            String expectedMessage = "User not found";
            Assert.assertEquals("Exception message must be correct", expectedMessage, e.getMessage());
        }
    }
}
