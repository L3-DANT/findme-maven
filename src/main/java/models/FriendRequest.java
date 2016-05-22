package models;

import com.google.gson.annotations.Expose;

/**
 * FriendRequest entity
 */
public class FriendRequest {

    @Expose
    private String caller,receiver;


    public FriendRequest(){
        this(null,null);
    }

    public FriendRequest(String caller, String receiver){
        this.caller = caller;
        this.receiver = receiver;
    }

    public String getCaller() {
        return caller;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String toString(){
        return "caller:"+caller+"/receiver:"+receiver;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof FriendRequest
                && ((((FriendRequest) o).getReceiver().equals(receiver)
                    && ((FriendRequest) o).getCaller().equals(caller))
                || (((FriendRequest) o).getReceiver().equals(caller)
                    && ((FriendRequest) o).getCaller().equals(receiver)));
    }
}
