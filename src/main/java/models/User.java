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
    private String password;
    @Expose
    private float latitude, longitude;
    @Expose
    private List<User> friendList;
    @Expose
    private String phoneNumber;

    /**
     * Constructs the user with empty parameters
     */
    public User() {
        this(null, null, 0, 0, null);
    }

    /**
     * Constructs the User with a pseudo and empty parameters
     * @param pseudo the pseudo
     */
    public User(String pseudo) {
        this(pseudo, null, 0, 0, null);
    }

    /**
     * Constructs the user with a pseudo and coordinates
     * @param pseudo the pseudo
     * @param latitude the latitude
     * @param longitude the longitude
     */
    public User(String pseudo, float latitude, float longitude){
        this(pseudo,null,latitude,longitude,null);
    }

    /**
     * Constructs the user with a pseudo and a password
     * @param pseudo the pseudo
     * @param password the password
     */
    public User(String pseudo, String password) {
        this(pseudo, password, 0, 0, null);
    }

    /**
     * Constructs the user without password
     * @param pseudo the pseudo
     * @param latitude the latitude
     * @param longitude the longitude
     * @param phoneNumber the phone number
     */
    public User(String pseudo, float latitude, float longitude, String phoneNumber) {
        this(pseudo, null, latitude, longitude, phoneNumber);
    }

    /**
     * Constructs a full user
     * @param pseudo the pseudo
     * @param password the password
     * @param latitude the latitude
     * @param longitude the longitude
     * @param phoneNumber the phone number
     */
    public User(String pseudo, String password, float latitude, float longitude, String phoneNumber) {
        this.pseudo = pseudo;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
        this.friendList = new ArrayList<User>();
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the user's pseudo
     * @return the pseudo {@code String}
     */
    public String getPseudo() {
        return pseudo;
    }

    /**
     * Sets the user's pseudo
     * @param pseudo the pseudo
     */
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    /**
     * Sets the user's password
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user's password
     * @return the password {@code String}
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Gets the list of the user's friends
     * @return a {@code List} of users
     */
    public List<User> getFriendList() {
        return friendList;
    }

    /**
     * Adds a friend to the user's friend list
     * @param user the {@link User} to add
     */
    public void addFriend(User user) {
        friendList.add(user);
    }

    /**
     * Removes a friend from the user's friend list
     * @param user the {@link User} to remove
     */
    public void removeFriend(User user) {
        friendList.remove(user);
    }

    /**
     * Reinits the user's friend list
     */
    public void clearFriendList() {
        friendList = new ArrayList<User>();
    }

    /**
     * Overrides {@link Object#equals(Object)}
     * @param o any Object
     * @return true if the object equals the user, false otherwise
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof User && ((User) o).getPseudo().equals(this.pseudo);
    }

    /**
     * Overrides {@link Object#hashCode()}
     * @return the pseudo's hashcode
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return pseudo.hashCode();
    }

    /**
     * Gets the user's latitude
     * @return the latitude as {@code float}
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * Sets the user's latitude
     * @param latitude the latitude
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the user's longitude
     * @return the longitude as {@code float}
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * Sets the user's longitude
     * @param longitude the longitude
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the user's phone number
     * @return the phone number as {@code String}
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the user's phone number
     * @param phoneNumber the phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
