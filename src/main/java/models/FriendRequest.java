package models;

/**
 * FriendRequest entity
 */
public class FriendRequest {
    private String caller;
    private String receiver;


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
}
