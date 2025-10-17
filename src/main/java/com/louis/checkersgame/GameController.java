package com.louis.checkersgame;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private final Board board;
    private final MoveValidator moveValidator;

    private Piece.PieceColor currentTurn;
    private int selectedRow = -1;
    private int selectedCol = -1;

    private boolean gameOver = false;
    private Piece.PieceColor winner = null;

    public GameController(Board board) {
        this.board = board;
        this.moveValidator = new MoveValidator(board);
        this.currentTurn = Piece.PieceColor.RED; // Red starts first
    }

    // === Handles user clicks from the GUI ===
    public boolean handleClick(int row, int col) {
        if (gameOver) return false;

        Piece clickedPiece = board.getPieceAt(row, col);

        // Select a piece belonging to the current player
        if (clickedPiece != null && clickedPiece.getColor() == currentTurn) {
            selectedRow = row;
            selectedCol = col;
            return true;
        }

        // Attempt a move
        if (selectedRow != -1 && selectedCol != -1) {
            MoveValidator.Move attemptedMove =
                new MoveValidator.Move(selectedRow, selectedCol, row, col, false);

            if (moveValidator.isValidMove(attemptedMove)) {
                List<MoveValidator.Move> validMoves =
                        moveValidator.getValidMoves(selectedRow, selectedCol);

                MoveValidator.Move chosenMove = validMoves.stream()
                        .filter(m -> m.toRow == row && m.toCol == col)
                        .findFirst()
                        .orElse(null);

                if (chosenMove != null) {
                    executeMove(chosenMove);
                    return true;
                }
            }
        }

        return false;
    }

    // === Executes a valid move ===
    private void executeMove(MoveValidator.Move move) {
        Piece piece = board.getPieceAt(move.fromRow, move.fromCol);
        if (piece == null) return;

        // Capture opponent piece
        if (move.isCapture) {
            board.removePieceAt(move.capturedRow, move.capturedCol);
        }

        // Move the piece
        board.movePiece(move.fromRow, move.fromCol, move.toRow, move.toCol);

        // Kinging check
        if (piece.getColor() == Piece.PieceColor.RED && move.toRow == 7) {
            piece.makeKing();
        } else if (piece.getColor() == Piece.PieceColor.BLACK && move.toRow == 0) {
            piece.makeKing();
        }

        // Reset selection, switch turn, check win
        selectedRow = -1;
        selectedCol = -1;
        switchTurn();
        checkGameOver();
    }

    // === Switches the turn to the other player ===
    private void switchTurn() {
        currentTurn = (currentTurn == Piece.PieceColor.RED)
                ? Piece.PieceColor.BLACK
                : Piece.PieceColor.RED;
    }

    // === Checks if the opponent has no pieces or no valid moves left ===
    private void checkGameOver() {
        Piece.PieceColor opponent =
                (currentTurn == Piece.PieceColor.RED)
                        ? Piece.PieceColor.BLACK
                        : Piece.PieceColor.RED;

        boolean opponentHasPiece = false;
        boolean opponentHasMove = false;

        // Scan entire board for opponent pieces and moves
        for (int r = 0; r < board.getSize(); r++) {
            for (int c = 0; c < board.getSize(); c++) {
                Piece piece = board.getPieceAt(r, c);

                if (piece != null && piece.getColor() == opponent) {
                    opponentHasPiece = true;

                    // Check if this piece has any valid moves
                    List<MoveValidator.Move> moves = moveValidator.getValidMoves(r, c);
                    if (!moves.isEmpty()) {
                        opponentHasMove = true;
                        break;
                    }
                }
            }
            if (opponentHasMove) break;
        }

        // 🧠 FIX: Explicitly check if the opponent has no pieces left
        if (!opponentHasPiece) {
            gameOver = true;
            winner = currentTurn;
            return;
        }

        // If opponent has pieces but no moves, also end the game
        if (!opponentHasMove) {
            gameOver = true;
            winner = currentTurn;
        }
    }

    // === Public Getters / Setters / Utilities ===
    public Piece.PieceColor getCurrentTurn() {
        return currentTurn;
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public int getSelectedCol() {
        return selectedCol;
    }

    /** Returns valid moves for highlighting in the GUI. */
    public List<ValidMove> getValidMovesForSelected() {
        List<ValidMove> guiMoves = new ArrayList<>();

        if (selectedRow == -1 || selectedCol == -1) return guiMoves;
        Piece piece = board.getPieceAt(selectedRow, selectedCol);
        if (piece == null) return guiMoves;

        List<MoveValidator.Move> validMoves =
                moveValidator.getValidMoves(selectedRow, selectedCol);

        for (MoveValidator.Move m : validMoves) {
            guiMoves.add(new ValidMove(m.toRow, m.toCol, m.isCapture));
        }

        return guiMoves;
    }

    // === Added for GameState compatibility ===
    public void setCurrentTurn(Piece.PieceColor turn) {
        this.currentTurn = turn;
    }

    public void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
    }

    // === Added for CheckersGUI compatibility ===
    public boolean isGameOver() {
        return gameOver;
    }

    public String getWinner() {
        return (winner == null) ? "None" : winner.name();
    }

    // === Inner Helper Class for GUI Highlights ===
    public static class ValidMove {
        public final int row;
        public final int col;
        public final boolean isCapture;

        public ValidMove(int row, int col, boolean isCapture) {
            this.row = row;
            this.col = col;
            this.isCapture = isCapture;
        }
    }
}
