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
import java.util.List;

@Named
@Stateless
public class UserService {
    @Inject
    private UserDAO dao;

    public List<User> findAll() {
        return dao.findAll();
    }
}
