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

@Named
@Stateless
public class UserService {
    @Inject
    private UserDAO dao;

    public List<User> findAll() {
        return dao.findAll();
    }

    public List<User> insertTest(){
        List<User> list = new ArrayList<User>();
        list.add(new User("Antoine",124f,127f));
        list.add(new User("François",42f,42f));
        list.add(new User("Maxime",147f,25845f));
        list.add(new User("Nicolas",7984f,98f));
        list.add(new User("Adrien",48f,878f));
        list.add(new User("Olivier",0f,0f));
        for (User user : list) {
            dao.insertOne(user);
        }
        return dao.findAll();
    }
}
