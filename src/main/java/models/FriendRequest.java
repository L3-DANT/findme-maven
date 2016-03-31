package models;

public class FriendRequest {
    private String asker;
    private String receiver;
    private State state;

    enum State {

        NOT_VIEWED,ACCEPTED,DENIED;
    }

    public FriendRequest(){
        state = State.NOT_VIEWED;
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String toString(){
        return "asker:"+asker+"/receiver:"+receiver+"/state:"+state;
    }
}
