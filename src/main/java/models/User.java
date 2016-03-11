package models;

import java.io.Serializable;

public class User implements Serializable, Comparable<User> {

    private String pseudo;
    private float x,y;

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
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		User user = (User) o;

		return pseudo.equals(user.pseudo);

	}

	@Override
	public int hashCode() {
		return pseudo.hashCode();
	}

	@Override
	public String toString() {
		return pseudo;
	}

	public int compareTo(User o) {
		return pseudo.compareTo(o.pseudo);
	}
}
