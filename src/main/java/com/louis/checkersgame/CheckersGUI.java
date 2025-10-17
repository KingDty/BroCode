package com.louis.checkersgame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class CheckersGUI extends JFrame {
    private static final int TILE_SIZE = 80;
    private static final int BOARD_SIZE = 8;
    private static final int HEADER_HEIGHT = 50;

    private final Board board;
    private final GameController controller;
    private final User currentUser;

    private int hoverRow = -1;
    private int hoverCol = -1;

    public CheckersGUI(User user) {
        this.currentUser = user;

        setTitle("Checkers Game - " + user.getUsername());
        setSize(TILE_SIZE * BOARD_SIZE, TILE_SIZE * BOARD_SIZE + HEADER_HEIGHT + 30);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        // Initialize board and controller
        board = new Board();
        controller = new GameController(board);

        // Custom drawing panel
        JPanel gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                drawHeader(g2d);
                drawBoard(g2d);
            }
        };

        // Hover effect for tiles
        gamePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int col = e.getX() / TILE_SIZE;
                int row = (e.getY() - HEADER_HEIGHT) / TILE_SIZE;

                if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                    if (hoverRow != row || hoverCol != col) {
                        hoverRow = row;
                        hoverCol = col;
                        gamePanel.repaint();
                    }
                } else {
                    if (hoverRow != -1 || hoverCol != -1) {
                        hoverRow = -1;
                        hoverCol = -1;
                        gamePanel.repaint();
                    }
                }
            }
        });

        // Click-to-select and move logic + Game Over popup
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoverRow = -1;
                hoverCol = -1;
                gamePanel.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int col = e.getX() / TILE_SIZE;
                int row = (e.getY() - HEADER_HEIGHT) / TILE_SIZE;

                if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                    if (controller.handleClick(row, col)) {
                        gamePanel.repaint();

                        // ✅ Game Over popup
                        if (controller.isGameOver()) {
                            String winner = controller.getWinner();
                            JOptionPane.showMessageDialog(
                                CheckersGUI.this,
                                "Game Over! " + winner + " wins!",
                                "Game Finished",
                                JOptionPane.INFORMATION_MESSAGE
                            );

                            int option = JOptionPane.showConfirmDialog(
                                CheckersGUI.this,
                                "Would you like to start a new game?",
                                "Restart Game",
                                JOptionPane.YES_NO_OPTION
                            );

                            if (option == JOptionPane.YES_OPTION) {
                                restartGame();
                            } else {
                                dispose();
                            }
                        }
                    }
                }
            }
        });

        add(gamePanel);
        setVisible(true);
    }

    // === Restart the game ===
    private void restartGame() {
        getContentPane().removeAll();
        CheckersGUI newGame = new CheckersGUI(currentUser);
        newGame.setVisible(true);
        dispose();
    }

    // === Header with player + turn indicator ===
    private void drawHeader(Graphics2D g) {
        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, TILE_SIZE * BOARD_SIZE, HEADER_HEIGHT);

        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.DARK_GRAY);
        g.drawString("Current Turn:", 15, 30);

        int circleX = 145;
        g.setColor(controller.getCurrentTurn() == Piece.PieceColor.RED ? Color.RED : Color.BLACK);
        g.fillOval(circleX, 12, 30, 30);

        g.setColor(Color.GRAY);
        g.drawOval(circleX, 12, 30, 30);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.setColor(Color.DARK_GRAY);
        g.drawString("Player: " + currentUser.getUsername(), 250, 30);
    }

    // === Draw Board & Pieces ===
    private void drawBoard(Graphics2D g) {
        int selectedRow = controller.getSelectedRow();
        int selectedCol = controller.getSelectedCol();
        List<GameController.ValidMove> validMoves = controller.getValidMovesForSelected();

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                boolean isDark = (row + col) % 2 == 1;
                int x = col * TILE_SIZE;
                int y = row * TILE_SIZE + HEADER_HEIGHT;

                g.setColor(isDark ? Color.DARK_GRAY : Color.LIGHT_GRAY);
                g.fillRect(x, y, TILE_SIZE, TILE_SIZE);

                if (row == hoverRow && col == hoverCol && isDark) {
                    g.setColor(new Color(100, 100, 100, 100));
                    g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                }

                if (row == selectedRow && col == selectedCol) {
                    g.setColor(new Color(255, 255, 0, 120));
                    g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                }

                // Valid move highlights
                for (GameController.ValidMove move : validMoves) {
                    if (move.row == row && move.col == col) {
                        g.setColor(move.isCapture ? new Color(255, 100, 100, 140) : new Color(100, 255, 100, 120));
                        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                        g.setColor(new Color(255, 255, 255, 200));
                        int circleSize = 20;
                        g.fillOval(x + TILE_SIZE / 2 - circleSize / 2, 
                                   y + TILE_SIZE / 2 - circleSize / 2, 
                                   circleSize, circleSize);
                    }
                }

                // Draw pieces
                Piece piece = board.getPieceAt(row, col);
                if (piece != null) {
                    int pieceSize = TILE_SIZE - 20;
                    int offset = 10;

                    if (row == hoverRow && col == hoverCol) {
                        pieceSize = TILE_SIZE - 16;
                        offset = 8;
                    }

                    g.setColor(piece.getColor() == Piece.PieceColor.RED ? Color.RED : Color.BLACK);
                    g.fillOval(x + offset, y + offset, pieceSize, pieceSize);
                    g.setColor(new Color(255, 255, 255, 80));
                    g.drawOval(x + offset, y + offset, pieceSize, pieceSize);

                    if (piece.isKing()) {
                        g.setColor(Color.YELLOW);
                        g.setFont(new Font("Arial", Font.BOLD, 24));
                        g.drawString("K", x + TILE_SIZE / 2 - 8, y + TILE_SIZE / 2 + 8);
                    }
                }
            }
        }
    }

    // === Entry point for testing (optional) ===
    public static void main(String[] args) {
        DatabaseManager.initialize();
        SwingUtilities.invokeLater(() -> new CheckersGUI(new User(0, "Guest")));
    }
}
