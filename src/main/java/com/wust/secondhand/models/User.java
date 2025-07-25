package com.wust.secondhand.models;
import com.wust.secondhand.models.enums.UserRole;

public class User {
    private String username;
    private String password;
    private UserRole role;

    public User(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public UserRole getRole() { return role; }
}