package com.louis.checkersgame;

public class Board {

    //Board represented as an 8x8 2D array
    //each cell can contain a piece or be null (empty)
    private Piece[][] board;

    //Board size is constant
    private static final int BOARD_SIZE = 8;

    // Constuctor - creates empty board
    public Board() {
        this.board = new Piece[BOARD_SIZE][BOARD_SIZE];
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


    // Moves a piece from one position to another with full checkers rules
    public boolean movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValidPosition(fromRow, fromCol) || !isValidPosition(toRow, toCol)) {
            return false;
        }

        Piece piece = board[fromRow][fromCol];
        if (piece == null) {
            return false;
        }

        // Destination must be empty
        if (board[toRow][toCol] != null) {
            return false;
        }

        // Must move to a dark square (where row + col is odd)
        if ((toRow + toCol) % 2 == 0) {
            return false;
        }

        int rowDiff = toRow - fromRow;
        int colDiff = Math.abs(toCol - fromCol);

        // Must move diagonally
        if (Math.abs(rowDiff) != colDiff) {
            return false;
        }

        // Check direction based on piece color and king status
        if (!piece.isKing()) {
            // Red pieces move down (positive row direction)
            // Black pieces move up (negative row direction)
            if (piece.getColor() == Piece.PieceColor.RED && rowDiff < 0) {
                return false;
            }
            if (piece.getColor() == Piece.PieceColor.BLACK && rowDiff > 0) {
                return false;
            }
        }

        // Normal move (1 square diagonal)
       // Normal move (1 square diagonal)
        if (Math.abs(rowDiff) == 1) {
            // Check if player has any jumps available - if so, they MUST jump
            if (hasAnyJumpAvailable(piece.getColor())) {
                System.out.println("You must jump when a jump is available!");
                return false;
            }

            board[toRow][toCol] = piece;
            board[fromRow][fromCol] = null;
            piece.setPosition(toRow, toCol);
            checkAndPromoteKing(piece, toRow);
            return true;
        }

        // Jump move (2 squares diagonal)
        if (Math.abs(rowDiff) == 2) {
            int middleRow = (fromRow + toRow) / 2;
            int middleCol = (fromCol + toCol) / 2;
            Piece jumpedPiece = board[middleRow][middleCol];

            // Must be jumping over an opponent's piece
            if (jumpedPiece == null || jumpedPiece.getColor() == piece.getColor()) {
                return false;
            }

            // Execute the jump
            board[toRow][toCol] = piece;
            board[fromRow][fromCol] = null;
            board[middleRow][middleCol] = null; // Remove jumped piece
            piece.setPosition(toRow, toCol);
            checkAndPromoteKing(piece, toRow);
            return true;
        }

        return false;
    }

    // Check if piece should be promoted to king
    private void checkAndPromoteKing(Piece piece, int row) {
        if (piece.getColor() == Piece.PieceColor.RED && row == BOARD_SIZE - 1) {
            piece.makeKing();
            System.out.println("RED piece promoted to King!");
        } else if (piece.getColor() == Piece.PieceColor.BLACK && row == 0) {
            piece.makeKing();
            System.out.println("BLACK piece promoted to King!");
        }
    }

    // Check if a specific piece has any jump moves available
    public boolean hasJumpAvailable(int row, int col) {
        Piece piece = board[row][col];
        if (piece == null) {
            return false;
        }



        // Check all 4 diagonal jump directions
        int[][] jumpDirections = {
            {-2, -2}, {-2, 2},  // Up-left, Up-right
            {2, -2}, {2, 2}     // Down-left, Down-right
        };

        for (int[] dir : jumpDirections) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            int middleRow = row + dir[0] / 2;
            int middleCol = col + dir[1] / 2;

            // Skip if out of bounds
            if (!isValidPosition(newRow, newCol)) {
                continue;
            }

            // Skip if destination is not empty
            if (board[newRow][newCol] != null) {
                continue;
            }

            // Skip if not a dark square
            if ((newRow + newCol) % 2 == 0) {
                continue;
            }

            // Check direction constraint for non-kings
            if (!piece.isKing()) {
                if (piece.getColor() == Piece.PieceColor.RED && dir[0] < 0) {
                    continue; // Red can't jump backwards
                }
                if (piece.getColor() == Piece.PieceColor.BLACK && dir[0] > 0) {
                    continue; // Black can't jump backwards
                }
            }

            // Check if there's an opponent piece to jump over
            Piece middlePiece = board[middleRow][middleCol];
            if (middlePiece != null && middlePiece.getColor() != piece.getColor()) {
                return true; // Found a valid jump!
            }
        }

        return false;
    }

    // Check if any piece of the given color has a jump available
    public boolean hasAnyJumpAvailable(Piece.PieceColor color) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.getColor() == color) {
                    if (hasJumpAvailable(row, col)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Check if a piece at a given position can make another jump (for multi-jumps)
    public boolean canJumpAgain(int row, int col) {
        return hasJumpAvailable(row, col);
    }

    // Get all valid jump destinations for a piece (returns list of {row, col} pairs)
    public java.util.ArrayList<int[]> getAvailableJumps(int row, int col) {
        java.util.ArrayList<int[]> jumps = new java.util.ArrayList<>();
        Piece piece = board[row][col];
        if (piece == null) {
            return jumps;
        }

        int[][] jumpDirections = {
            {-2, -2}, {-2, 2},  // Up-left, Up-right
            {2, -2}, {2, 2}     // Down-left, Down-right
        };

        for (int[] dir : jumpDirections) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            int middleRow = row + dir[0] / 2;
            int middleCol = col + dir[1] / 2;

            if (!isValidPosition(newRow, newCol)) {
                continue;
            }

            if (board[newRow][newCol] != null) {
                continue;
            }

            if ((newRow + newCol) % 2 == 0) {
                continue;
            }

            if (!piece.isKing()) {
                if (piece.getColor() == Piece.PieceColor.RED && dir[0] < 0) {
                    continue;
                }
                if (piece.getColor() == Piece.PieceColor.BLACK && dir[0] > 0) {
                    continue;
                }
            }

            Piece middlePiece = board[middleRow][middleCol];
            if (middlePiece != null && middlePiece.getColor() != piece.getColor()) {
                jumps.add(new int[]{newRow, newCol});
            }
        }

        return jumps;
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
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = null;
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
                    board[row][col] = new Piece(Piece.PieceColor.BLACK, row, col);
                }
            }
        }
    }
    // Count how many pieces a player has left
    public int countPieces(Piece.PieceColor color) {
        int count = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.getColor() == color) {
                    count++;
                }
            }
        }
        return count;
    }

    // Check if a player has any valid moves left
    public boolean hasAnyValidMoves(Piece.PieceColor color) {
        // First check for jumps
        if (hasAnyJumpAvailable(color)) {
            return true;
        }

        // Then check for normal moves
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.getColor() == color) {
                    // Check all adjacent diagonal squares
                    int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
                    for (int[] dir : directions) {
                        int newRow = row + dir[0];
                        int newCol = col + dir[1];

                        // Skip if out of bounds
                        if (!isValidPosition(newRow, newCol)) {
                            continue;
                        }

                        // Skip if not empty
                        if (board[newRow][newCol] != null) {
                            continue;
                        }

                        // Skip if not dark square
                        if ((newRow + newCol) % 2 == 0) {
                            continue;
                        }

                        // Check direction constraint for non-kings
                        if (!piece.isKing()) {
                            if (piece.getColor() == Piece.PieceColor.RED && dir[0] < 0) {
                                continue;
                            }
                            if (piece.getColor() == Piece.PieceColor.BLACK && dir[0] > 0) {
                                continue;
                            }
                        }

                        return true; // Found at least one valid move
                    }
                }
            }
        }
        return false;
    }


}
