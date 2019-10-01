package com.pkl.stafftracking.Model;

public class User {
    //Deklarasi Variable

    private String username;
    private int user_id;

    public int getId() {
        return user_id;
    }
    public void setId(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }



    //Konstruktor dengan beberapa parameter, untuk mendapatkan Input Data dari User
    public User(String username) {
        this.username = username;
    }

    public User() {
    }
}