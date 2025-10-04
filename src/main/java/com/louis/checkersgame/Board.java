package com.louis.checkersgame;

public class Board {

    //Board represented as an 8x8 2D array
    //each cell can contain a piece or be null (empty)
    private Piece [][] board;

    //Board size is constant
    private static final int BOARD_SIZE = 8;

    // Constuctor - creates empty board
    public Board() {
        this.board = new Piece[BOARD_SIZE] [BOARD_SIZE];
    }

    // returns board size
    public int getSize() {
        return BOARD_SIZE;
    }

    //returns the 2D array representing the board
    public Piece[][] getBoard() {
        return board;
    }

    //gets the piece at a specific position
    public Piece getPieceAt(int row, int col) {
        if (!isValidPosition(row, col)) {
            return null;
        }
        return board[row][col];
    }

    //places a piece at a specific position
    public void setPieceAt(int row, int col, Piece piece) {
        if (isValidPosition(row, col)) {
            board[row][col] = piece;
            //update the piece's position to match where it's being placed
            if (piece != null) {
                piece.setPosition(row, col);
            }
        }
    }

    //removes a piece from a specific position
    public Piece removePieceAt(int row, int col) {
        if (!isValidPosition(row, col)) {
            return null;
        }
        Piece removedPiece = board[row][col];
        board[row][col] = null;
        return removedPiece;
    }

    //Moves a piece from one position to another
public boolean movePiece(int fromRow, int fromCol, int toRow int toCol) {
        !isValidPosition(fromRow, fromCol) || !isValidPosition(toRow, toCol) {
            return false;
    }

    //checks if there's actually a piece to move
    Piece piece = board[fromRow][fromCol];
        if(piece == null) {
            return false;
        }

    // check if destination is empty
    if (board[toRow][toCol] != null) {
        return false;
    }

    //move the piece
    board[toRow][toCol] = piece;
    board[fromRow][fromCol] = null;
    piece.setPosition(toRow, toCol);

    return true;
}

//checks if a positon is within the board boundaries
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }
    //* Initializes the board with pieces in their starting positions
    //     * RED pieces start at the top (rows 0-2)
    //     * BLACK pieces start at the bottom (rows 5-7)
    //     * Pieces are only placed on dark squares (checkerboard pattern)
    public void initializeBoard() {
        //clear board first
        for (int row = 0; row < BOARD_SIZE; row++) {
            for(int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = null;
            }
        }
    }

    //place RED pieces (rows 0-2)
    for (int row = 0; row < 3; row++) {
        for (int col = 0; col < BOARD_SIZE; col++) {
            //only place pieces on dark squares
            //Dark squares are where (row + col) is odd
            if ((row + col) % 2 == 1) {
                board[row][col] = new Piece(Piece.PieceColor.RED, row, col);
            }
        }
    }

    // place BLACK pieces (rows 5-7)
    for (int row = 5; row < BOARD_SIZE; row++) {
        for (int col = 0; col < BOARD_SIZE; col++) {
            //only place pieces on dark squares
            if ((row + col) % 2 == 1) {
                board [row][col] = new Piece(Piece.PieceColor.BLACK, row, col);
            }
        }
    }
}
