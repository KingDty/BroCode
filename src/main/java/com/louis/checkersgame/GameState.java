package com.louis.checkersgame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Handles saving and loading game state efficiently
 */
public class GameState {
    
    /**
     * Simple class to represent a piece's state for serialization
     */
    private static class PieceData {
        int row;
        int col;
        String color; // "RED" or "BLACK"
        boolean isKing;
        
        PieceData(Piece piece) {
            this.row = piece.getRow();
            this.col = piece.getCol();
            this.color = piece.getColor().name();
            this.isKing = piece.isKing();
        }
    }
    
    /**
     * Save the current game state to a file
     */
    public static boolean saveGame(Board board, Piece.PieceColor currentTurn, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Write current turn
            writer.write("TURN:" + currentTurn.name());
            writer.newLine();
            
            // Write all pieces
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    Piece piece = board.getPieceAt(row, col);
                    if (piece != null) {
                        writer.write(row + "," + col + "," + 
                                   piece.getColor().name() + "," + 
                                   (piece.isKing() ? "K" : "N"));
                        writer.newLine();
                    }
                }
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Load a game state from a file
     */
    public static boolean loadGame(Board board, GameController controller, String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // Clear the board first
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    board.setPieceAt(row, col, null);
                }
            }
            
            String line;
            Piece.PieceColor loadedTurn = Piece.PieceColor.RED;
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("TURN:")) {
                    // Load current turn
                    String turnStr = line.substring(5);
                    loadedTurn = Piece.PieceColor.valueOf(turnStr);
                } else {
                    // Load piece data
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        int row = Integer.parseInt(parts[0]);
                        int col = Integer.parseInt(parts[1]);
                        Piece.PieceColor color = Piece.PieceColor.valueOf(parts[2]);
                        boolean isKing = parts[3].equals("K");
                        
                        Piece piece = new Piece(color, row, col);
                        if (isKing) {
                            piece.makeKing();
                        }
                        board.setPieceAt(row, col, piece);
                    }
                }
            }
            
            // Set the turn in controller using reflection or a new method
            controller.setCurrentTurn(loadedTurn);
            controller.clearSelection();
            
            return true;
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading game: " + e.getMessage());
            return false;
        }
    }
}