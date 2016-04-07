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

    public FriendRequest acceptFriendRequest(String asker, String receiver){
        FriendRequest fr = dao.findOneByAskerReceiver(asker,receiver);
        dao.replaceOne(fr);
        return fr;
    }

    public FriendRequest declineFriendRequest(String asker, String receiver) {
        FriendRequest fr = dao.findOneByAskerReceiver(asker,receiver);
        dao.replaceOne(fr);
        return fr;
    }
}
