package com.louis.checkersgame;

public class Piece {

    public enum PieceColor {
        RED,
        BLACK
    }

    // color of the piece
    private PieceColor color;
    // wether this piece has been kinged
    private boolean isKing;
    // current row position
    private int row;
    // current column position
    private int col;

    // Constructor
    public Piece (PieceColor color, int row, int col) {
        this.color = color;
        this.row = row;
         this.col = col;
         this.isKing = false;
    }

    // get color of piece
    public PieceColor getColor() {
        return color;
    }

    //ture if king, false if piece is regular
    public boolean isKing() {
        return isKing;
    }

    // promotes the piece to king
    public void makeKing() {
        this.isKing = true;
    }

    //gets current row position
    public int getRow() {
        return row;
    }
    //gets current column position
    public int getCol() {
        return col;
    }

    public void setPosition ( int newRow, int newCol) {
        this.row = newRow;
        this.col = newCol;
    }
}
