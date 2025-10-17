package com.louis.checkersgame;

/**
 * Simple test class to verify database setup
 * Run this before creating the login GUI
 */
public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("=== Testing Database Setup ===");
        
        // Initialize database
        DatabaseManager.initialize();
        
        // Test user registration
        System.out.println("\n--- Testing User Registration ---");
        boolean registered = UserManager.registerUser("testuser", "password123");
        System.out.println("User registration: " + (registered ? "SUCCESS" : "FAILED"));
        
        // Test duplicate username
        boolean duplicate = UserManager.registerUser("testuser", "differentpass");
        System.out.println("Duplicate username blocked: " + (!duplicate ? "SUCCESS" : "FAILED"));
        
        // Test login with correct credentials
        System.out.println("\n--- Testing User Login ---");
        User user = UserManager.loginUser("testuser", "password123");
        System.out.println("Login with correct password: " + (user != null ? "SUCCESS - Welcome " + user.getUsername() : "FAILED"));
        
        // Test login with wrong password
        User wrongPass = UserManager.loginUser("testuser", "wrongpassword");
        System.out.println("Login with wrong password blocked: " + (wrongPass == null ? "SUCCESS" : "FAILED"));
        
        // Test login with non-existent user
        User nonExistent = UserManager.loginUser("fakeuser", "password");
        System.out.println("Non-existent user blocked: " + (nonExistent == null ? "SUCCESS" : "FAILED"));
        
        System.out.println("\n=== All Tests Complete ===");
        
        // Close database
        DatabaseManager.close();
    }
}