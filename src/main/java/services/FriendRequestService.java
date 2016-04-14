package services;

import daos.FriendRequestDAO;
import models.FriendRequest;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class FriendRequestService {

    @Inject
    private FriendRequestDAO dao;

    public void insertFriendRequest(FriendRequest fr){
        dao.insertOne(fr);
    }

    public void deleteOne(String user1, String user2){
        dao.deleteOne(new FriendRequest(user1,user2));
    }

}
