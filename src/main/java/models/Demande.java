package models;

public class Demande {

    private String asker;
    private String receiver;
    private State state;

    enum State {

        NOT_VIEWED,ACCEPTED,DENIED;

    }

    public Demande(String asker, String receiver){
        this.asker = asker;
        this.receiver = receiver;
        state = State.NOT_VIEWED;
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
}
