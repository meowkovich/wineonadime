package com.example.wineonadime;

import java.util.ArrayList;

public class User {
    public String firstName, lastName, email;
    public ArrayList<FavoriteItem> favorites;

    public User() {

    }

    public User(String firstName, String lastName, String email, ArrayList<FavoriteItem> favorites) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.favorites = new ArrayList<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<FavoriteItem> getFavorites() { return favorites; }
}
