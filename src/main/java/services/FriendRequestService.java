package services;

import daos.FriendRequestDAO;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class FriendRequestService {

    @Inject
    private FriendRequestDAO dao;



}
