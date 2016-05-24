package models;

import com.google.gson.annotations.Expose;

/**
 * FriendRequest entity
 */
public class FriendRequest {

    @Expose
    private String caller,receiver;

    /**
     * Empty constructor (used for Jersey's json parser
     */
    public FriendRequest(){
        this(null,null);
    }

    /**
     * Creates a FriendRequest
     * @param caller a {@link User#pseudo}
     * @param receiver a {@link User#pseudo}
     */
    public FriendRequest(String caller, String receiver){
        this.caller = caller;
        this.receiver = receiver;
    }

    /**
     * Gets the caller
     * @return a {@code String} that represents the {@link User#pseudo} of the caller
     */
    public String getCaller() {
        return caller;
    }

    /**
     * Gets the receiver
     * @return a {@code String} that represents the {@link User#pseudo} of the receiver
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * Sets the caller
     * @param caller a  a {@code String} that represents the {@link User#pseudo} of the caller
     */
    public void setCaller(String caller) {
        this.caller = caller;
    }

    /**
     * Sets the receiver
     * @param receiver  a {@code String} that represents the {@link User#pseudo} of the receiver
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * Returns a {@code String} that represents the friend request
     * @return a {@code String} where caller and receiver are printed
     */
    public String toString(){
        return "caller:"+caller+"/receiver:"+receiver;
    }

    /**
     * Overrides {@link Object#equals(Object)}
     * @param o any Object
     * @return true if the object equals the friendrequest, false otherwise
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof FriendRequest
                && ((((FriendRequest) o).getReceiver().equals(receiver)
                    && ((FriendRequest) o).getCaller().equals(caller))
                || (((FriendRequest) o).getReceiver().equals(caller)
                    && ((FriendRequest) o).getCaller().equals(receiver)));
    }
}
