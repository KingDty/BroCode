package com.louis.checkersgame;

import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class CheckersGUI extends JFrame {
    private Piece.PieceColor currentTurn = Piece.PieceColor.RED;
    private boolean isMultiJumping = false;
    private ComputerPlayer computerPlayer = null;
    private boolean isAIGame = false;
    private static final int TILE_SIZE = 80;
    private static final int BOARD_SIZE = 8;
    private final Board board;
    private Piece selectedPiece =null;
    private int selectedRow = -1;
    private int selectedCol = -1;

    public CheckersGUI() {
        setTitle("Checkers Game - Sprint 1 Demo");
        setSize(TILE_SIZE * BOARD_SIZE, TILE_SIZE * BOARD_SIZE + 80); // Made taller for button
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null); // Center window on screen

        board = new Board(); // Initialize board
        board.initializeBoard(); // Place pieces

        // Create the game panel
        JPanel gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }

            @Override
            public void addNotify() {
                super.addNotify();
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleClick(e.getX(), e.getY());
                    }
                });
            }
        };

        // Create button panel
        JPanel buttonPanel = new JPanel();
        JButton restartButton = new JButton("Restart Game");
        restartButton.addActionListener(e -> restartGame());
        buttonPanel.add(restartButton);
        JButton aiToggleButton = new JButton("Play vs AI");
        aiToggleButton.addActionListener(e -> {
            isAIGame = !isAIGame;
            if (isAIGame) {
                computerPlayer = new ComputerPlayer(Piece.PieceColor.BLACK);
                aiToggleButton.setText("Play vs Human");
                System.out.println("AI mode enabled - You are RED, AI is BLACK");
            } else {
                computerPlayer = null;
                aiToggleButton.setText("Play vs AI");
                System.out.println("2-player mode enabled");
            }
            restartGame();
        });
        buttonPanel.add(aiToggleButton);

        // Add components to frame
        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }

    // Draw board squares and pieces
    private void drawBoard(Graphics g) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                boolean isDark = (row + col) % 2 == 1;
                g.setColor(isDark ? Color.DARK_GRAY : Color.LIGHT_GRAY);
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                // Highlight selected square with a green border
                if (row == selectedRow && col == selectedCol) {
                    g.setColor(Color.GREEN);
                    g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    // Redraw the dark square slightly smaller to create a border effect
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(col * TILE_SIZE + 5, row * TILE_SIZE + 5, TILE_SIZE - 10, TILE_SIZE - 10);
                }

                Piece piece = board.getPieceAt(row, col);
                if (piece != null) {
                    g.setColor(piece.getColor() == Piece.PieceColor.RED ? Color.RED : Color.BLACK);
                    g.fillOval(col * TILE_SIZE + 10, row * TILE_SIZE + 10,
                               TILE_SIZE - 20, TILE_SIZE - 20);

                    // Add a white outline to the selected piece for extra visibility
                    if (row == selectedRow && col == selectedCol) {
                        g.setColor(Color.WHITE);
                        g.drawOval(col * TILE_SIZE + 8, row * TILE_SIZE + 8,
                                   TILE_SIZE - 16, TILE_SIZE - 16);
                        g.drawOval(col * TILE_SIZE + 9, row * TILE_SIZE + 9,
                                   TILE_SIZE - 18, TILE_SIZE - 18);
                    }

                    if (piece.isKing()) {
                        // Draw a gold crown symbol
                        g.setColor(Color.YELLOW);
                        // Draw bold "K" or crown symbol
                        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
                        g.drawString("♔", col * TILE_SIZE + 28, row * TILE_SIZE + 50);

                        // Add a golden ring around kinged pieces
                        g.setColor(new Color(255, 215, 0)); // Gold color
                        g.drawOval(col * TILE_SIZE + 8, row * TILE_SIZE + 8,
                                   TILE_SIZE - 16, TILE_SIZE - 16);
                        g.drawOval(col * TILE_SIZE + 7, row * TILE_SIZE + 7,
                                   TILE_SIZE - 14, TILE_SIZE - 14);
                    }
                }
            }
        }
    }

    // Handle mouse clicks on the board
    private void handleClick(int x, int y) {
        int col = x / TILE_SIZE;
        int row = y / TILE_SIZE;

        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            return;
        }

        // If we're in multi-jump mode, only allow continuing the jump
        if (isMultiJumping) {
            // Try to make another jump with the selected piece
            int rowDiff = Math.abs(row - selectedRow);
            int colDiff = Math.abs(col - selectedCol);

            // Check if this is a jump move (2 squares)
            if (rowDiff == 2 && colDiff == 2) {
                if (board.movePiece(selectedRow, selectedCol, row, col)) {
                    System.out.println("Multi-jump! Moved to (" + row + ", " + col + ")");

                    // Update selected piece position
                    selectedRow = row;
                    selectedCol = col;
                    selectedPiece = board.getPieceAt(row, col);

                    // Check if we can jump again
                    if (board.canJumpAgain(row, col)) {
                        System.out.println("You can jump again! Keep jumping!");
                        repaint();
                        return; // Stay in multi-jump mode
                    } else {
                        System.out.println("No more jumps available. Turn complete.");
                        // End multi-jump sequence
                        isMultiJumping = false;
                        selectedPiece = null;
                        selectedRow = -1;
                        selectedCol = -1;

                        // Switch turns
                        currentTurn = (currentTurn == Piece.PieceColor.RED)
                                      ? Piece.PieceColor.BLACK
                                      : Piece.PieceColor.RED;
                        System.out.println("Now it's " + currentTurn + "'s turn");
                        checkWinCondition();
                        // If it's AI's turn, let the AI make a move
                        if (isAIGame && currentTurn == Piece.PieceColor.BLACK && computerPlayer != null) {
                            javax.swing.Timer aiTimer = new javax.swing.Timer(800, evt -> {
                                computerPlayer.makeMove(board);
                                currentTurn = Piece.PieceColor.RED;
                                System.out.println("Now it's RED's turn");
                                repaint();
                                checkWinCondition();
                            });
                            aiTimer.setRepeats(false);
                            aiTimer.start();
                        }
                        repaint();
                        return;
                    }
                } else {
                    System.out.println("Invalid jump!");
                }
            } else {
                System.out.println("You must continue jumping!");
            }
            return;
        }

        // Normal selection/movement logic (not in multi-jump mode)
        if (selectedPiece == null) {
            Piece clickedPiece = board.getPieceAt(row, col);

            if (clickedPiece != null && clickedPiece.getColor() == currentTurn) {
                selectedPiece = clickedPiece;
                selectedRow = row;
                selectedCol = col;
                System.out.println("Selected " + currentTurn + " piece at (" + row + ", " + col + ")");
                repaint();
            }
        }
        else {
            int rowDiff = Math.abs(row - selectedRow);
            int colDiff = Math.abs(col - selectedCol);
            boolean isJump = (rowDiff == 2 && colDiff == 2);

            if (board.movePiece(selectedRow, selectedCol, row, col)) {
                System.out.println("Moved piece to (" + row + ", " + col + ")");

                // If this was a jump, check if we can jump again
                if (isJump && board.canJumpAgain(row, col)) {
                    System.out.println("You can jump again! Click where to jump next.");
                    isMultiJumping = true;
                    selectedRow = row;
                    selectedCol = col;
                    selectedPiece = board.getPieceAt(row, col);
                    repaint();
                    return; // Don't switch turns yet
                }

                // Move complete, switch turns
                currentTurn = (currentTurn == Piece.PieceColor.RED)
                              ? Piece.PieceColor.BLACK
                              : Piece.PieceColor.RED;
                System.out.println("Now it's " + currentTurn + "'s turn");
                checkWinCondition();
                // If it's AI's turn, let the AI make a move
                if (isAIGame && currentTurn == Piece.PieceColor.BLACK && computerPlayer != null) {
                    javax.swing.Timer aiTimer = new javax.swing.Timer(800, evt -> {
                        computerPlayer.makeMove(board);
                        currentTurn = Piece.PieceColor.RED;
                        System.out.println("Now it's RED's turn");
                        repaint();
                        checkWinCondition();
                    });
                    aiTimer.setRepeats(false);
                    aiTimer.start();
                }
            } else {
                System.out.println("Invalid move!");
            }

            selectedPiece = null;
            selectedRow = -1;
            selectedCol = -1;
            repaint();
        }
    }

    // Check if the game is over and display winner
    private void checkWinCondition() {
        Piece.PieceColor opponent = (currentTurn == Piece.PieceColor.RED)
                                    ? Piece.PieceColor.BLACK
                                    : Piece.PieceColor.RED;

        // Check if current player has no pieces
        if (board.countPieces(currentTurn) == 0) {
            announceWinner(opponent);
            return;
        }

        // Check if current player has no valid moves
        if (!board.hasAnyValidMoves(currentTurn)) {
            announceWinner(opponent);
            return;
        }
    }

    // Display winner message
    private void announceWinner(Piece.PieceColor winner) {
        String winnerName = (winner == Piece.PieceColor.RED) ? "RED" : "BLACK";
        javax.swing.JOptionPane.showMessageDialog(this,
            winnerName + " wins the game!",
            "Game Over",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
        System.out.println(winnerName + " wins!");
    }

    // Restart the game
    private void restartGame() {
        // Reset board
        board.initializeBoard();

        // Reset game state
        selectedPiece = null;
        selectedRow = -1;
        selectedCol = -1;
        currentTurn = Piece.PieceColor.RED;
        isMultiJumping = false;

        // Redraw
        repaint();

        System.out.println("Game restarted! RED's turn.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CheckersGUI::new);
    }
}
