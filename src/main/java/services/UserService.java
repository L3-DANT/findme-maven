package services;

import daos.UserDAO;
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

@Stateless
public class UserService {
    @Inject
    private UserDAO dao;

    public List<User> findAll() {
        return dao.findAll();
    }

    public List<User> insertTest(){
        List<User> list = new ArrayList<User>();
        list.add(new User("Antoine","123", 124f,127f));
        list.add(new User("François","123", 42f,42f));
        list.add(new User("Maxime","123", 147f,25845f));
        list.add(new User("Nicolas","123", 7984f,98f));
        list.add(new User("Adrien","123", 48f,878f));
        list.add(new User("Olivier","123", 0f,0f));
        for (User user : list) {
            dao.insertOne(user);
        }
        return dao.findAll();
    }

    public void updateUser(User user) {
        dao.replaceOne(user);
    }

    public void addFriend(User user1, User user2){
        user1.setPassword(null);
        user1.clearFriendList();
        user2.setPassword(null);
        user2.clearFriendList();

        User userDB1 = dao.findOneByPseudo(user1.getPseudo());
        userDB1.addFriend(user2);
        dao.replaceOne(userDB1);

        User userDB2 = dao.findOneByPseudo(user2.getPseudo());
        userDB2.addFriend(user1);
        dao.replaceOne(userDB2);
    }

    public User getFriendsCoordinates(User user) {
        for (User friend : user.getFriendList()) {
            User tmp = dao.findOneByPseudo(friend.getPseudo());
            friend.setX(tmp.getX());
            friend.setY(tmp.getY());
        }
        return user;
    }
}
