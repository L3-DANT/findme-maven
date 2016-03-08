package services;

import daos.UserDAO;
import models.User;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class UserService {
    @Inject
    private UserDAO dao;

    public List<User> getAll(){
        return null;
    }
}
