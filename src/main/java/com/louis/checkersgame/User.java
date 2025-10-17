package com.louis.checkersgame;

/**
 * Represents a user in the checkers game system
 */
public class User {
    private int userId;
    private String username;
    
    public User(int userId, String username) {
        this.userId = userId;
        this.username = username;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    @Override
    public String toString() {
        return username;
    }
}