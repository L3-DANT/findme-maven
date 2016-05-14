package models;


public class Login {

    private String pseudo,password;


    public Login(){
        this(null,null);
    }

    public Login(String pseudo,String password){
        this.pseudo = pseudo;
        this.password = password;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }



}
