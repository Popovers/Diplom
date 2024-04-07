package com.example.diplom;

public class User {
    private int id;
    private String username;
    private String email;
    private String role;  // Added this line

    public User(int id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;  // Added this line to the constructor
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;  // Added getter method for role
    }

    public void setRole(String role) {
        this.role = role;  // Added setter method for role
    }
}
