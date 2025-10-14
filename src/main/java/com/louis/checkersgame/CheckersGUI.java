package com.louis.checkersgame;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class CheckersGUI extends JFrame {
    private static final int TILE_SIZE = 80;
    private static final int BOARD_SIZE = 8;
    private final Board board;

    public CheckersGUI() {
        setTitle("Checkers Game - Sprint 1 Demo");
        setSize(TILE_SIZE * BOARD_SIZE, TILE_SIZE * BOARD_SIZE + 30);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null); // Center window on screen

        board = new Board(); // Initialize board and pieces

        // Add custom drawing panel
        add(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        });

        setVisible(true);
    }

    // Draw board squares and pieces
    private void drawBoard(Graphics g) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                boolean isDark = (row + col) % 2 == 1;
                g.setColor(isDark ? Color.DARK_GRAY : Color.LIGHT_GRAY);
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                Piece piece = board.getPieceAt(row, col);
                if (piece != null) {
                    g.setColor(piece.getColor() == Piece.PieceColor.RED ? Color.RED : Color.BLACK);
                    g.fillOval(col * TILE_SIZE + 10, row * TILE_SIZE + 10,
                               TILE_SIZE - 20, TILE_SIZE - 20);
                    if (piece.isKing()) {
                        g.setColor(Color.YELLOW);
                        g.drawString("K", col * TILE_SIZE + 35, row * TILE_SIZE + 45);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CheckersGUI::new);
    }
}
