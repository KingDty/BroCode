package com.louis.checkersgame;

import java.util.ArrayList;
import java.util.List;

public class MoveValidator {
    private final Board board;

    public MoveValidator(Board board) {
        this.board = board;
    }

    // Returns a list of valid moves for a piece
    public List<Move> getValidMoves(int row, int col) {
        List<Move> validMoves = new ArrayList<>();
        Piece piece = board.getPieceAt(row, col);

        if (piece == null) {
            return validMoves;
        }

        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int[] dir : directions) {
            checkRegularMove(row, col, piece, dir[0], dir[1], validMoves);
            checkJumpMove(row, col, piece, dir[0], dir[1], validMoves);
        }

        return validMoves;
    }

    // Checks if a simple diagonal move is valid
    private void checkRegularMove(int row, int col, Piece piece, int rowDir, int colDir, List<Move> validMoves) {
        if (!piece.isKing()) {
            if (piece.getColor() == Piece.PieceColor.RED && rowDir < 0) return;
            if (piece.getColor() == Piece.PieceColor.BLACK && rowDir > 0) return;
        }

        int newRow = row + rowDir;
        int newCol = col + colDir;

        if (board.isValidPosition(newRow, newCol) && board.getPieceAt(newRow, newCol) == null) {
            validMoves.add(new Move(row, col, newRow, newCol, false));
        }
    }

    // Checks if a capture move is valid
    private void checkJumpMove(int row, int col, Piece piece, int rowDir, int colDir, List<Move> validMoves) {
        if (!piece.isKing()) {
            if (piece.getColor() == Piece.PieceColor.RED && rowDir < 0) return;
            if (piece.getColor() == Piece.PieceColor.BLACK && rowDir > 0) return;
        }

        int jumpedRow = row + rowDir;
        int jumpedCol = col + colDir;
        int landRow = row + (rowDir * 2);
        int landCol = col + (colDir * 2);

        if (!board.isValidPosition(jumpedRow, jumpedCol) || !board.isValidPosition(landRow, landCol)) {
            return;
        }

        Piece jumpedPiece = board.getPieceAt(jumpedRow, jumpedCol);
        Piece landPiece = board.getPieceAt(landRow, landCol);

        if (jumpedPiece != null && jumpedPiece.getColor() != piece.getColor() && landPiece == null) {
            validMoves.add(new Move(row, col, landRow, landCol, true, jumpedRow, jumpedCol));
        }
    }

    // Verifies if a move is in the valid list
    public boolean isValidMove(Move move) {
        List<Move> validMoves = getValidMoves(move.fromRow, move.fromCol);
        for (Move validMove : validMoves) {
            if (validMove.toRow == move.toRow && validMove.toCol == move.toCol) {
                return true;
            }
        }
        return false;
    }

    // === Inner class to represent a Move ===
    public static class Move {
        public final int fromRow;
        public final int fromCol;
        public final int toRow;
        public final int toCol;
        public final boolean isCapture;
        public final int capturedRow;
        public final int capturedCol;

        // Regular move constructor
        public Move(int fromRow, int fromCol, int toRow, int toCol, boolean isCapture) {
            this.fromRow = fromRow;
            this.fromCol = fromCol;
            this.toRow = toRow;
            this.toCol = toCol;
            this.isCapture = isCapture;
            this.capturedRow = -1;
            this.capturedCol = -1;
        }

        // Capture move constructor
        public Move(int fromRow, int fromCol, int toRow, int toCol, boolean isCapture, int capturedRow, int capturedCol) {
            this.fromRow = fromRow;
            this.fromCol = fromCol;
            this.toRow = toRow;
            this.toCol = toCol;
            this.isCapture = isCapture;
            this.capturedRow = capturedRow;
            this.capturedCol = capturedCol;
        }
    }
}
