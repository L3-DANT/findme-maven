package models;

public class FriendRequest {
    private String asker;
    private String receiver;


    public FriendRequest(){

    }

    public String getAsker() {
        return asker;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setAsker(String asker) {
        this.asker = asker;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String toString(){
        return "asker:"+asker+"/receiver:"+receiver;
    }
}
