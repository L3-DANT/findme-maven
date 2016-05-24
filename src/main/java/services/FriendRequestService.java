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
     * @param fr the {@link FriendRequest} to insert
     * @return the just inserted {@link FriendRequest}
     * @throws DuplicateDataException if the {@link FriendRequest} already exists in database
     */
    public FriendRequest insertFriendRequest(FriendRequest fr) throws DuplicateDataException {
        return dao.insertOne(fr);
    }


    /**
     * Gets a {@link FriendRequest}
     * @param pseudo1 one of the {@link FriendRequest}'s {@link models.User#pseudo}
     * @param pseudo2 the other {@link FriendRequest}'s {@link models.User#pseudo}
     * @return the {@link FriendRequest}
     * @throws NotFoundException if unable to find the FriendRequest
     */
    public FriendRequest getFriendRequestByPseudos(String pseudo1,String pseudo2) throws NotFoundException{
        return dao.findOneByPseudos(pseudo1,pseudo2);
    }

    /**
     * Deletes the {@link FriendRequest}
     * @param fr the {@link FriendRequest} to remove
     * @throws NotFoundException if the {@link FriendRequest} can't be found in database
     */
    public void deleteOne(FriendRequest fr) throws NotFoundException{
        dao.deleteOne(fr);
    }

    /**
     * Finds all {@link models.User#pseudo} that one user asked for friendship
     * @param pseudo the {@link models.User#pseudo}
     * @return a string list of the {@link models.User#pseudo} found
     */
    public String[] findByCaller(String pseudo) {
        return asArray(dao.findByField("caller",pseudo),false);
    }

    /**
     * Finds all {@link models.User#pseudo} that asked for the user for friendship
     * @param pseudo the {@link models.User#pseudo}
     * @return a string list of the {@link models.User#pseudo} found
     */
    public String[] findByReceiver(String pseudo) {
        return asArray(dao.findByField("receiver",pseudo),true);
    }

    /**
     * Private method used to construct the list for the {@link FriendRequestService#findByCaller(String)} and {@link FriendRequestService#findByReceiver(String)}  methods
     * @param list the {@code List} of {@link FriendRequest} that matched the condition
     * @param from true if {@link FriendRequestService#findByCaller(String)} has been called, false if {@link FriendRequestService#findByReceiver(String)} was
     * @return the list of pseudos exctracted from the {@code List} of {@link FriendRequest}
     */
    private String[] asArray(List<FriendRequest> list, boolean from) {
        String[] arrayPseudos = new String[list.size()];
        int i = 0;
        if(from){
            for(FriendRequest fr : list){
                arrayPseudos[i++] = fr.getCaller();
            }
        } else {
            for(FriendRequest fr : list){
                arrayPseudos[i++] = fr.getReceiver();
            }
        }
        return arrayPseudos;
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
