package services;

import daos.UserDAO;
import exceptions.DuplicateDataException;
import exceptions.NotFoundException;
import models.User;
import security.BCrypt;

import javax.ejb.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Service class that manages {@link User}
 */
@Singleton
public class UserService {

    private UserDAO dao = new UserDAO();

    /**
     * Finds all users
     * @return List of every {@link User}
     */
    public List<User> findAll() {
        return dao.findAll();
    }

    public List<User> insertTest(){
        dao.clearCollection();
        List<User> list = new ArrayList<User>();
        list.add(new User("Antoine",BCrypt.hashpw("123", (BCrypt.gensalt(12))), 48.84927f,2.35268f,"0650555075"));
        list.add(new User("François",BCrypt.hashpw("123", (BCrypt.gensalt(12))), 48.84862f,2.36071f,"06 60 76 99 44"));
        list.add(new User("Maxime",BCrypt.hashpw("123", (BCrypt.gensalt(12))), 48.84723f,2.35835f,"+33667479299"));
        list.add(new User("Nicolas",BCrypt.hashpw("123", (BCrypt.gensalt(12))), 48.84461f,2.35221f,"+33 6 02 24 17 93"));
        list.add(new User("Adrien",BCrypt.hashpw("123", (BCrypt.gensalt(12))), 48.84427f,2.35865f,null));
        list.add(new User("Olivier",BCrypt.hashpw("123", (BCrypt.gensalt(12))), 48.84138f,2.35972f,null));
        for (User user : list) {
            try {
                dao.insertOne(user);
            } catch (DuplicateDataException e) {
                continue;
            }
        }
        return dao.findAll();
    }

    public User insertUser(User user) throws DuplicateDataException {
        if(user.getPassword() != null){
            user.setPassword(BCrypt.hashpw(user.getPassword(), (BCrypt.gensalt(12))));
        }
        return dao.insertOne(user);
    }

    /**
     * Updates an user
     * @param user the user to update
     */
    public List<String> updateUser(User user) throws NotFoundException{
        User userDB = dao.findOneByPseudo(user.getPseudo());
        User insert = new User(userDB.getPseudo(),userDB.getPassword(),userDB.getLatitude(),userDB.getLongitude(),userDB.getPhoneNumber());
        for (User usr : userDB.getFriendList()) {
            insert.addFriend(new User(usr.getPseudo(),userDB.getLatitude(),userDB.getLongitude(),userDB.getPhoneNumber()));
        }
        List<String> ret = null;

        if(user.getLatitude() != 0)
            insert.setLatitude(user.getLatitude());
        if(user.getLongitude() != 0)
            insert.setLongitude(user.getLongitude());
        if(user.getPhoneNumber() != null)
            insert.setPhoneNumber(user.getPhoneNumber());

        //check for eventual removal of friends
        List<User> listUser = user.getFriendList();
        List<User> listUserDB = userDB.getFriendList();
        if(listUser != null && listUser.size() < listUserDB.size()) {
            ret = new ArrayList<String>();
            for (User iter : listUserDB) {
                if(!listUser.contains(iter)) {
                    User tmp = dao.findOneByPseudo(iter.getPseudo());
                    tmp.removeFriend(insert);
                    dao.replaceOne(tmp);
                    ret.add(tmp.getPseudo());
                    insert.removeFriend(new User(iter.getPseudo()));
                }
            }
        }

        //check for eventual password change
        if(user.getPassword() != null && !BCrypt.checkpw(user.getPassword(),insert.getPassword())){
            insert.setPassword(BCrypt.hashpw(user.getPassword(), (BCrypt.gensalt(12))));
        }
        dao.replaceOne(insert);
        return ret;
    }

    /**
     * Adds a friend, which means adding a {@link User} with no {@link User#password} and no {@link User#friendList} in one's {@link User#friendList}
     * @param pseudo1
     * @param pseudo2
     * @throws NotFoundException if one of the Users can't be found
     */
    public void addFriend(String pseudo1, String pseudo2) throws NotFoundException{
        // Getting users from DB, clearing password and friendList
        User friend1 = dao.findOneByPseudo(pseudo1);
        User friend2 = dao.findOneByPseudo(pseudo2);

        // Clearing friendList
        friend1.clearFriendList();
        friend2.clearFriendList();

        // Getting users from DB
        User user1 = dao.findOneByPseudo(pseudo1);
        User user2 = dao.findOneByPseudo(pseudo2);

        // Adding friends in both friendLists
        user1.addFriend(friend2);
        user2.addFriend(friend1);

        // Replacing in DB
        dao.replaceOne(user1);
        dao.replaceOne(user2);
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
        return BCrypt.checkpw(password,user.getPassword());
    }

    public void deleteUser(String pseudo) throws NotFoundException {
        dao.deleteOne(pseudo);
    }

    public void updateCoordinates(User user) throws NotFoundException {
        User u = dao.findOneByPseudo(user.getPseudo());
        u.setLongitude(user.getLongitude());
        u.setLatitude(user.getLatitude());
        dao.replaceOne(u);
    }

}
