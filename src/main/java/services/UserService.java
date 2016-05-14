package services;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import daos.UserDAO;
import exceptions.DuplicateDataException;
import exceptions.NotFoundException;
import models.User;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class that manages {@link User}
 */
@Stateless
public class UserService {
    @Inject
    private UserDAO dao;

    /**
     * Finds all users
     * @return List of every {@link User}
     */
    public List<User> findAll() {
        return dao.findAll();
    }

    public List<User> insertTest(){
        List<User> list = new ArrayList<User>();
        list.add(new User("Antoine","123", 48.84927f,2.35268f));
        list.add(new User("François","123", 48.84862f,2.36071f));
        list.add(new User("Maxime","123", 48.84723f,2.35835f));
        list.add(new User("Nicolas","123", 48.84461f,2.35221f));
        list.add(new User("Adrien","123", 48.84427f,2.35865f));
        list.add(new User("Olivier","123", 48.84138f,2.35972f));
        for (User user : list) {
//            dao.insertOne(user);
        }
        return dao.findAll();
    }

    public User insertUser(User user) throws DuplicateDataException {
        return dao.insertOne(user);
    }

    /**
     * Updates an user
     * @param user the user to update
     */
    public void updateUser(User user) throws NotFoundException{
        dao.replaceOne(user);
    }

    /**
     * Adds a friend, which means adding a {@link User} with no {@link User#password} and no {@link User#friendList} in one's {@link User#friendList}
     * @param pseudo1
     * @param pseudo2
     * @throws NotFoundException if one of the Users can't be found
     */
    public void addFriend(String pseudo1, String pseudo2) throws NotFoundException{
        // Getting users from DB, clearing password and friendList
        User user1 = dao.findOneByPseudo(pseudo1);
        User user2 = dao.findOneByPseudo(pseudo2);

        // Clearing passwords
        user1.setPassword(null);
        user2.setPassword(null);

        // Clearing friendList
        user1.clearFriendList();
        user2.clearFriendList();

        // Getting users from DB
        User userDB1 = dao.findOneByPseudo(pseudo1);
        User userDB2 = dao.findOneByPseudo(pseudo2);

        // Adding friends in both friendLists
        userDB1.addFriend(user1);
        userDB2.addFriend(user2);

        // Replacing in DB
        dao.replaceOne(userDB1);
        dao.replaceOne(userDB2);
    }

    /**
     * Gets a {@link User} and updates its {@link User#friendList}
     * @param pseudo the {@link User#pseudo}
     * @return the {@link User}
     * @throws NotFoundException if the User can't be found
     */
    public User getUser(String pseudo) throws NotFoundException{
        User user = dao.findOneByPseudo(pseudo);
        for (User friend : user.getFriendList()) {
            User tmp = dao.findOneByPseudo(friend.getPseudo());
            friend.setLatitude(tmp.getLatitude());
            friend.setLongitude(tmp.getLongitude());
        }
        return user;
    }

    public boolean connect(String pseudo, String password) throws NotFoundException{
        User user = dao.findOneByPseudo(pseudo);
        return password.equals(user.getPassword());
    }

    public void deleteUser(String pseudo) throws NotFoundException {
        dao.deleteOne(pseudo);
    }

}
