package com.louis.checkersgame;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages H2 database connections and schema initialization
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:h2:./checkersdb";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    private static Connection connection;
    
    /**
     * Initialize database connection and create tables if they don't exist
     */
    public static void initialize() {
        try {
            // Load H2 driver
            Class.forName("org.h2.Driver");
            
            // Create connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // Create tables
            createTables();
            
            System.out.println("Database initialized successfully!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("H2 Driver not found! Make sure h2.jar is in your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create database tables
     */
    private static void createTables() throws SQLException {
        Statement stmt = connection.createStatement();
        
        // Users table
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS users (" +
            "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "username VARCHAR(50) UNIQUE NOT NULL, " +
            "password VARCHAR(100) NOT NULL, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")"
        );
        
        // Games table
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS games (" +
            "game_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "player1_id INT NOT NULL, " +
            "player2_id INT, " +
            "game_mode VARCHAR(20) NOT NULL, " +
            "game_state TEXT, " +
            "current_turn VARCHAR(10), " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "is_completed BOOLEAN DEFAULT FALSE, " +
            "winner_id INT, " +
            "FOREIGN KEY (player1_id) REFERENCES users(user_id), " +
            "FOREIGN KEY (player2_id) REFERENCES users(user_id), " +
            "FOREIGN KEY (winner_id) REFERENCES users(user_id)" +
            ")"
        );
        
        // Game moves table (for replay functionality)
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS game_moves (" +
            "move_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "game_id INT NOT NULL, " +
            "move_number INT NOT NULL, " +
            "from_row INT NOT NULL, " +
            "from_col INT NOT NULL, " +
            "to_row INT NOT NULL, " +
            "to_col INT NOT NULL, " +
            "is_capture BOOLEAN DEFAULT FALSE, " +
            "captured_row INT, " +
            "captured_col INT, " +
            "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (game_id) REFERENCES games(game_id)" +
            ")"
        );
        
        stmt.close();
    }
    
    /**
     * Get database connection
     */
    public static Connection getConnection() {
        return connection;
    }
    
    /**
     * Close database connection
     */
    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database: " + e.getMessage());
        }
    }
}