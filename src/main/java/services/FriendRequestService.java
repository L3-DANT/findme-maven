package services;

import daos.FriendRequestDAO;
import exceptions.NotFoundException;
import models.FriendRequest;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Service class that manages {@link FriendRequest}
 */
@Stateless
public class FriendRequestService {

    @Inject
    private FriendRequestDAO dao;


    /**
     * Insert a {@link FriendRequest}
     * @param fr
     * @return false if FriendRequest already exists
     */
    public boolean insertFriendRequest(FriendRequest fr) {
        return dao.insertOne(fr);
    }


    /**
     * Gets a {@link FriendRequest}
     * @param pseudo1
     * @param pseudo2
     * @return the {@link FriendRequest}
     * @throws NotFoundException if unable to find the FriendRequest
     */
    public FriendRequest getFriendRequestByPseudos(String pseudo1,String pseudo2) throws NotFoundException{
        return dao.findOneByPseudos(pseudo1,pseudo2);
    }

    /**
     * Deletes the {@link FriendRequest}
     * @param fr
     */
    public void deleteOne(FriendRequest fr) {
        dao.deleteOne(fr);
    }

}
