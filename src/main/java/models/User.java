package models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User entity
 */
public class User implements Serializable {

    @Expose
    private String pseudo;
    @Expose(serialize = false)
    private  String password;
    @Expose
    private float latitude,longitude;
    @Expose
    private List<User> friendList;
    @Expose
    private String phoneNumber;

    public User(){
        this(null, null,0,0);
    }

    public User(String pseudo, String password){
        this(pseudo,password,0,0);
    }

    public User(String pseudo, String password,float latitude, float longitude){
        this.pseudo = pseudo;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
        this.friendList = new ArrayList<User>();
        this.phoneNumber = "0000000000";
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public void setPassword(String password) { this.password = password; }

    public String getPassword(){
        return this.password;
    }


    public List<User> getFriendList(){
        return friendList;
    }

    public void addFriend(User pseudo){
        friendList.add(pseudo);
    }

    public void removeFriend(User pseudo){
       friendList.remove(pseudo);
    }

    public void clearFriendList(){
        friendList = null;
    }

	@Override
	public boolean equals(Object o) {
        return o instanceof User && ((User) o).getPseudo().equals(this.pseudo);
	}

	@Override
	public int hashCode() {
		return pseudo.hashCode();
	}


    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
