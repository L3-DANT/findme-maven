package services;

import daos.UserDAO;
import exceptions.DuplicateDataException;
import exceptions.NotFoundException;
import exceptions.UnauthorisedException;
import models.User;
import security.BCrypt;

import javax.ejb.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class that manages {@link User}
 */
@Singleton
public class UserService {

    private UserDAO dao = new UserDAO();
    private final int saltSize = 12;

    public List<User> insertTest(){
        dao.clearCollection();
        List<User> list = new ArrayList<User>();
        list.add(new User("Antoine",BCrypt.hashpw("123", (BCrypt.gensalt(saltSize))), 48.84927f,2.35268f, User.State.ONLINE,"0650555075"));
        list.add(new User("François",BCrypt.hashpw("123", (BCrypt.gensalt(saltSize))), 48.84862f,2.36071f,User.State.ONLINE,"0660769944"));
        list.add(new User("Maxime",BCrypt.hashpw("123", (BCrypt.gensalt(saltSize))), 48.84723f,2.35835f,User.State.ONLINE,"+33667479299"));
        list.add(new User("Nicolas",BCrypt.hashpw("123", (BCrypt.gensalt(saltSize))), 48.84461f,2.35221f,User.State.ONLINE,"+33602241793"));
        list.add(new User("Adrien",BCrypt.hashpw("123", (BCrypt.gensalt(saltSize))), 48.84427f,2.35865f,User.State.AWAY,"0606060606"));
        list.add(new User("Olivier",BCrypt.hashpw("123", (BCrypt.gensalt(saltSize))), 48.84138f,2.35972f,User.State.OFFLINE,"0606060606"));
        list.get(0).addFriend(list.get(1));
        list.get(4).addFriend(list.get(1));
        list.get(4).addFriend(list.get(2));
        for (User user : list) {
            try {
                dao.insertOne(user);
            } catch (DuplicateDataException e) {
                continue;
            }
        }
        return dao.findAll();
    }

    /**
     * Inserts a {@link User} in database
     * Uses {@link BCrypt} to hash password
     * @param user the {@link User} to insert
     * @return the {@link User} just inserted
     * @throws DuplicateDataException if the user already exists in database
     * @see BCrypt
     */
    public User insertUser(User user) throws DuplicateDataException {
        if(user.getPassword() != null){
            user.setPassword(BCrypt.hashpw(user.getPassword(), (BCrypt.gensalt(saltSize))));
        }
        for (User friend : user.getFriendList()) {
            friend.setFriendList(null);
        }
        return dao.insertOne(user);
    }

    /**
     * Updates an user. Makes sure that it is not modifying its {@link User#friendList} but by removing friends.
     * Also checks if the password has changed, and use {@link BCrypt} to hash it.
     * @param user the {@link User} to update
     * @return an array of {@code String} representing the {@link User#pseudo} of every friend removed, null if none was removed
     * @throws NotFoundException if the {@link User} can't be found in database
     * @throws UnauthorisedException if the {@link User#friendList} is not valid
     * @see BCrypt
     */
    public String[] updateUser(User user) throws NotFoundException, UnauthorisedException {
        User userDB = dao.findOneByPseudo(user.getPseudo());
        List<User> listUser = user.getFriendList();
        List<User> listUserDB = userDB.getFriendList();
        if(listUser != null && listUser.size() > listUserDB.size()){
            throw new UnauthorisedException();
        } else if(listUser.size() == listUserDB.size()){
            for (User u : listUser) {
                if(!listUserDB.contains(u)){
                    throw new UnauthorisedException();
                }
            }
        }

        //copy of userDB used in the loop to avoid concurrent access to userDB.friendList
        User insert = new User(userDB.getPseudo(),userDB.getPassword(),userDB.getLatitude(),userDB.getLongitude(),userDB.getState(),userDB.getPhoneNumber());

        //check for eventual password change
        if(user.getPassword() != null && !BCrypt.checkpw(user.getPassword(),insert.getPassword())){
            insert.setPassword(BCrypt.hashpw(user.getPassword(), (BCrypt.gensalt(saltSize))));
        }

        List<String> ret = null;


        insert.setLatitude(user.getLatitude());
        insert.setLongitude(user.getLongitude());
        if(user.getPhoneNumber() != null)
            insert.setPhoneNumber(user.getPhoneNumber());
        if(user.getState() != null)
            insert.setState(user.getState());

        //check for eventual removal of friends
        if(listUser != null && listUser.size() < listUserDB.size()) {
            ret = new ArrayList<String>();
            for (User iter : listUserDB) {
                User tmp = dao.findOneByPseudo(iter.getPseudo());
                if(!listUser.contains(iter)) {
                    tmp.removeFriend(insert);
                    dao.replaceOne(tmp);
                    ret.add(tmp.getPseudo());
                } else {
                    tmp.setPassword(null);
                    tmp.setFriendList(null);
                    insert.addFriend(tmp);
                }
            }
        }
        insert.clearFriendList();
        for (User friend : user.getFriendList()) {
            friend.setFriendList(null);
            insert.addFriend(friend);
        }
        dao.replaceOne(insert);
        return ret == null  ? null : ret.toArray(new String[ret.size()]);
    }

    /**
     * Adds a friend, which means adding a {@link User} with no {@link User#password} and no {@link User#friendList} in one's {@link User#friendList}
     * @param pseudo1 one of the {@link User#pseudo}
     * @param pseudo2 the other {@link User#pseudo}
     * @throws NotFoundException if one of the {@link User} can't be found
     */
    public void addFriend(String pseudo1, String pseudo2) throws NotFoundException{
        // Getting users from DB, clearing password and friendList
        User friend1 = dao.findOneByPseudo(pseudo1);
        User friend2 = dao.findOneByPseudo(pseudo2);

        //Clearing passwords
        friend1.setPassword(null);
        friend2.setPassword(null);
        // Clearing friendList
        friend1.setFriendList(null);
        friend2.setFriendList(null);

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
            friend.setState(tmp.getState());
            friend.setFriendList(null);
        }
        return user;
    }

    /**
     * Make sure the credentials given match a {@link User} in database
     * @param pseudo the {@link User#pseudo}
     * @param password the {@link User#password}
     * @return true if parameters match, false otherwise
     * @throws NotFoundException if the user can't be found in database
     */
    public boolean connect(String pseudo, String password) throws NotFoundException{
        User user = dao.findOneByPseudo(pseudo);
        return BCrypt.checkpw(password,user.getPassword());
    }

    /**
     * Removes a user from database, and removes it from its friends' {@link User#friendList}
     * @param pseudo the {@link User#pseudo}
     * @throws NotFoundException if the user can't be found in database
     */
    public void deleteUser(String pseudo) throws NotFoundException {
        User user = new User(pseudo);
        for (User friend: user.getFriendList()) {
            User friendDB = dao.findOneByPseudo(friend.getPseudo());
            friendDB.removeFriend(user);
            dao.replaceOne(friendDB);
        }
        dao.deleteOne(pseudo);
    }

    /**
     * Light version of {@link UserService#updateUser(User)}, only updating the {@link User}'s coordinates
     * @param user the {@link User} to update
     * @throws NotFoundException if the user can't be found in database
     */
    public void updateCoordinates(User user) throws NotFoundException {
        User userDB = dao.findOneByPseudo(user.getPseudo());
        userDB.setLongitude(user.getLongitude());
        userDB.setLatitude(user.getLatitude());
        userDB.setState(user.getState());
        dao.replaceOne(userDB);
    }

}
