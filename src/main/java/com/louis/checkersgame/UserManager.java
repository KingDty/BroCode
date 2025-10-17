package com.louis.checkersgame;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Manages user authentication and registration
 */
public class UserManager {
    
    /**
     * Register a new user
     */
    public static boolean registerUser(String username, String password) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return false;
        }
        
        try {
            Connection conn = DatabaseManager.getConnection();
            
            // Check if username already exists
            PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT user_id FROM users WHERE username = ?"
            );
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Username already exists
                rs.close();
                checkStmt.close();
                return false;
            }
            rs.close();
            checkStmt.close();
            
            // Hash password
            String hashedPassword = hashPassword(password);
            
            // Insert new user
            PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO users (username, password) VALUES (?, ?)"
            );
            insertStmt.setString(1, username);
            insertStmt.setString(2, hashedPassword);
            
            int rowsAffected = insertStmt.executeUpdate();
            insertStmt.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Authenticate a user and return User object if successful
     */
    public static User loginUser(String username, String password) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return null;
        }
        
        try {
            Connection conn = DatabaseManager.getConnection();
            
            // Hash password
            String hashedPassword = hashPassword(password);
            
            // Check credentials
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT user_id, username FROM users WHERE username = ? AND password = ?"
            );
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String dbUsername = rs.getString("username");
                
                User user = new User(userId, dbUsername);
                
                rs.close();
                stmt.close();
                
                return user;
            }
            
            rs.close();
            stmt.close();
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("Error logging in user: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Hash password using SHA-256
     */
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            
            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return password; // Fallback (not secure, but prevents crashes)
        }
    }
    
    /**
     * Check if username exists
     */
    public static boolean usernameExists(String username) {
        try {
            Connection conn = DatabaseManager.getConnection();
            
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT user_id FROM users WHERE username = ?"
            );
            stmt.setString(1, username);
            
            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next();
            
            rs.close();
            stmt.close();
            
            return exists;
            
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            return false;
        }
    }
}