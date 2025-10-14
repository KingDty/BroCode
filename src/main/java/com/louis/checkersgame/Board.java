package com.louis.checkersgame;

public class Board {

    // Board represented as an 8x8 2D array
    // Each cell can contain a Piece or be null (empty)
    private Piece[][] board;

    // Board size is constant
    private static final int BOARD_SIZE = 8;

    // Constructor - creates an empty board and initializes the starting positions
    public Board() {
        this.board = new Piece[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();
    }

    // Returns board size
    public int getSize() {
        return BOARD_SIZE;
    }

    // Returns the 2D array representing the board
    public Piece[][] getBoard() {
        return board;
    }

    // Gets the piece at a specific position
    public Piece getPieceAt(int row, int col) {
        if (!isValidPosition(row, col)) {
            return null;
        }
        return board[row][col];
    }

    // Places a piece at a specific position
    public void setPieceAt(int row, int col, Piece piece) {
        if (isValidPosition(row, col)) {
            board[row][col] = piece;
            // Update the piece’s position to match where it’s being placed
            if (piece != null) {
                piece.setPosition(row, col);
            }
        }
    }

    // Removes a piece from a specific position
    public Piece removePieceAt(int row, int col) {
        if (!isValidPosition(row, col)) {
            return null;
        }
        Piece removedPiece = board[row][col];
        board[row][col] = null;
        return removedPiece;
    }

    // Moves a piece from one position to another
    public boolean movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        // Validate source and destination positions
        if (!isValidPosition(fromRow, fromCol) || !isValidPosition(toRow, toCol)) {
            return false;
        }

        // Check if there’s actually a piece to move
        Piece piece = board[fromRow][fromCol];
        if (piece == null) {
            return false;
        }

        // Check if destination is empty
        if (board[toRow][toCol] != null) {
            return false;
        }

        // Move the piece
        board[toRow][toCol] = piece;
        board[fromRow][fromCol] = null;
        piece.setPosition(toRow, toCol);

        // Optional: auto-kinging if reaching the opposite end
        if (piece.getColor() == Piece.PieceColor.RED && toRow == BOARD_SIZE - 1) {
            piece.makeKing();
        } else if (piece.getColor() == Piece.PieceColor.BLACK && toRow == 0) {
            piece.makeKing();
        }

        return true;
    }

    // Checks if a position is within the board boundaries
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    /**
     * Initializes the board with pieces in their starting positions.
     * RED pieces start at the top (rows 0–2)
     * BLACK pieces start at the bottom (rows 5–7)
     * Pieces are only placed on dark squares (where (row + col) is odd)
     */
    public void initializeBoard() {
        // Clear the board first
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = null;
            }
        }

        // Place RED pieces (rows 0–2)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row + col) % 2 == 1) { // Only dark squares
                    board[row][col] = new Piece(Piece.PieceColor.RED, row, col);
                }
            }
        }

        // Place BLACK pieces (rows 5–7)
        for (int row = 5; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row + col) % 2 == 1) { // Only dark squares
                    board[row][col] = new Piece(Piece.PieceColor.BLACK, row, col);
                }
            }
        }
    }
}
