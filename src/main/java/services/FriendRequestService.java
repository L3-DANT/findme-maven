package services;

import daos.FriendRequestDAO;
import exceptions.DuplicateDataException;
import exceptions.NotFoundException;
import models.FriendRequest;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class that manages {@link FriendRequest}
 */
@Singleton
public class FriendRequestService {

    private FriendRequestDAO dao = new FriendRequestDAO();


    /**
     * Insert a {@link FriendRequest}
     * @param fr
     * @return false if FriendRequest already exists
     */
    public FriendRequest insertFriendRequest(FriendRequest fr) throws DuplicateDataException {
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
    public void deleteOne(FriendRequest fr) throws NotFoundException{
        dao.deleteOne(fr);
    }

    public String[] findByCaller(String pseudo) {
        return asArray(dao.findByField("caller",pseudo),false);
    }

    public String[] findByReceiver(String pseudo) {
        return asArray(dao.findByField("receiver",pseudo),true);
    }

    private String[] asArray(List<FriendRequest> list, boolean from) {
        String[] ret = new String[list.size()];
        int i = 0;
        if(from){
            for(FriendRequest fr : list){
                ret[i++] = fr.getCaller();
            }
        } else {
            for(FriendRequest fr : list){
                ret[i++] = fr.getReceiver();
            }
        }
        return ret;
    }


    public List<FriendRequest> insertTest(){
        dao.clearCollection();
        List<FriendRequest> list = new ArrayList<FriendRequest>();
        list.add(new FriendRequest("Antoine","Maxime"));
        list.add(new FriendRequest("Olivier","Adrien"));
        list.add(new FriendRequest("Antoine","Nicolas"));
        list.add(new FriendRequest("Adrien","Nicolas"));
        for (FriendRequest fr : list) {
            try {
                dao.insertOne(fr);
            } catch (DuplicateDataException e) {
                continue;
            }
        }
        return dao.findAll();
    }
}
