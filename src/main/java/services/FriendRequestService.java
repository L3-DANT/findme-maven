package services;

import daos.FriendRequestDAO;
import exceptions.DuplicateDataException;
import exceptions.NotFoundException;
import models.FriendRequest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

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

    public String findByCaller(String pseudo) {
        return serializeList(dao.findByField("caller",pseudo),0);
    }

    public String findByReceiver(String pseudo) {
        return serializeList(dao.findByField("receiver",pseudo),1);
    }

    private String serializeList(List<FriendRequest> list, int from) {
        String data = "";
        int i = 0;
        if(from == 0){
            for(FriendRequest fr : list){
                if(++i == list.size())
                    data += "\""+fr.getReceiver()+"\"";
                else
                    data += "\""+fr.getReceiver()+"\",";
            }
        } else {
            for(FriendRequest fr : list){
                if(++i == list.size())
                    data += "\""+fr.getCaller()+"\"";
                else
                    data += "\""+fr.getCaller()+"\",";
            }
        }
        return "["+data+"]";
    }

}
