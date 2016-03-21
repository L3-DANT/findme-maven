package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private String pseudo,password;
    private float x,y;
    private List<User> friendList;

    public User(){
        this(null, null,0,0);
    }

    public User(String pseudo, String password){
        this(pseudo,password,0,0);
    }

    public User(String pseudo, String password,float x, float y){
        this.pseudo = pseudo;
        this.password = password;
        this.x = x;
        this.y = y;
        this.friendList = new ArrayList<User>();
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public void setPassword(String password) { this.password = password; }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public List<User> getFriendList(){
        return friendList;
    }

    public void addFriend(User u){
        friendList.add(u);
    }

    public void removeFriend(String pseudo){
       friendList.remove(new User(pseudo,null));
    }

	@Override
	public boolean equals(Object o) {
        return o instanceof User && ((User) o).getPseudo().equals(this.pseudo);
	}

	@Override
	public int hashCode() {
		return pseudo.hashCode();
	}


}
