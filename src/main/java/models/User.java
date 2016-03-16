package models;

import java.io.Serializable;

public class User implements Serializable {

    private String pseudo;
    private float x,y;

    public User(){
        this(null, 0,0);
    }

    public User(String pseudo){
        this(pseudo,0,0);
    }

    public User(String pseudo,float x, float y){
        this.pseudo = pseudo;
        this.x = x;
        this.y = y;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

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

	@Override
	public boolean equals(Object o) {
        return o instanceof User && ((User) o).getPseudo().equals(this.pseudo);
	}

	@Override
	public int hashCode() {
		return pseudo.hashCode();
	}


}
