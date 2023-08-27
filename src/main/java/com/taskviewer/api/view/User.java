package com.taskviewer.api.view;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class User {
    private int id;
    private String username;
    private String email;
    private String role;
    private String firstname;
    private String lastname;

    public User() {
    }

    public User(int id, String username, String email, String role, String firstname, String lastname) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.firstname = firstname;
        this.lastname = lastname;
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
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    // Метод для десериализации JSON-строки в объект User
    public static User fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, User.class);
    }
    public static List<User> fromJsonList(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<List<User>>() {}.getType());
    }
}

