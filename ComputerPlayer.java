package com.louis.checkersgame;

import java.util.ArrayList;
import java.util.Random;

public class ComputerPlayer {

    private Piece.PieceColor aiColor;
    private Random random;

    public ComputerPlayer(Piece.PieceColor color) {
        this.aiColor = color;
        this.random = new Random();
    }

    // Main method - AI makes a move on the board
    public void makeMove(Board board) {
        System.out.println("AI is thinking...");

        // Strategy priority:
        // 1. Look for jumps (mandatory anyway) - prefer multi-jump sequences
        // 2. If no jumps, make the best strategic move

        ArrayList<Move> possibleJumps = getAllJumps(board);

        if (!possibleJumps.isEmpty()) {
            // Score all jumps and pick the best one
            Move bestJump = selectBestJump(board, possibleJumps);
            executeMove(board, bestJump);

            // Check for multi-jumps
            handleMultiJump(board, bestJump.toRow, bestJump.toCol);
        } else {
            // No jumps available, make the best normal move
            ArrayList<Move> possibleMoves = getAllNormalMoves(board);

            if (!possibleMoves.isEmpty()) {
                Move bestMove = selectBestMove(board, possibleMoves);
                executeMove(board, bestMove);
            } else {
                System.out.println("AI has no valid moves!");
            }
        }
    }

    // Select the best jump based on strategic scoring
    private Move selectBestJump(Board board, ArrayList<Move> jumps) {
        Move bestJump = jumps.get(0);
        int bestScore = scoreJump(board, bestJump);

        for (Move jump : jumps) {
            int score = scoreJump(board, jump);
            if (score > bestScore) {
                bestScore = score;
                bestJump = jump;
            }
        }

        System.out.println("AI selected jump with score: " + bestScore);
        return bestJump;
    }

    // Score a jump move
    private int scoreJump(Board board, Move move) {
        int score = 100; // Base score for any jump

        Piece piece = board.getPieceAt(move.fromRow, move.fromCol);

        // Bonus for moves that lead to kinging
        if (aiColor == Piece.PieceColor.BLACK && move.toRow == 0) {
            score += 50; // About to become a king!
        } else if (aiColor == Piece.PieceColor.RED && move.toRow == 7) {
            score += 50;
        }

        // Bonus if the piece is already a king
        if (piece != null && piece.isKing()) {
            score += 10;
        }

        // Bonus for multi-jump potential
        if (canContinueJumping(board, move)) {
            score += 75; // Multi-jumps are very valuable
        }

        // Bonus for center control
        score += getCenterControlScore(move.toRow, move.toCol);

        return score;
    }

    // Check if a move can lead to more jumps
    private boolean canContinueJumping(Board board, Move move) {
        // Check if jumping to this position would allow another jump
        ArrayList<int[]> nextJumps = board.getAvailableJumps(move.toRow, move.toCol);
        return !nextJumps.isEmpty();
    }

    // Select the best normal move based on strategic scoring
    private Move selectBestMove(Board board, ArrayList<Move> moves) {
        Move bestMove = moves.get(0);
        int bestScore = scoreMove(board, bestMove);

        for (Move move : moves) {
            int score = scoreMove(board, move);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        System.out.println("AI selected move with score: " + bestScore);
        return bestMove;
    }

    // Score a normal move
    private int scoreMove(Board board, Move move) {
        int score = 0;
        Piece piece = board.getPieceAt(move.fromRow, move.fromCol);

        // High priority: Move toward kinging
        if (aiColor == Piece.PieceColor.BLACK) {
            score += (7 - move.toRow) * 10; // Closer to row 0 is better
            if (move.toRow == 0) {
                score += 100; // Becoming a king!
            }
        } else if (aiColor == Piece.PieceColor.RED) {
            score += move.toRow * 10; // Closer to row 7 is better
            if (move.toRow == 7) {
                score += 100; // Becoming a king!
            }
        }

        // Bonus for moving kings (they're more valuable)
        if (piece != null && piece.isKing()) {
            score += 20;
        }

        // Bonus for center control (columns 2-5 are better)
        score += getCenterControlScore(move.toRow, move.toCol);

        // Penalty for moving to edges (easier to get trapped)
        if (move.toCol == 0 || move.toCol == 7) {
            score -= 15;
        }

        // Safety check: avoid moving into danger
        if (isPositionDangerous(board, move.toRow, move.toCol)) {
            score -= 30;
        }

        return score;
    }

    // Calculate center control score
    private int getCenterControlScore(int row, int col) {
        // Center columns (2-5) are more valuable
        if (col >= 2 && col <= 5) {
            return 10;
        }
        return 0;
    }

    // Check if a position is dangerous (can be captured)
    private boolean isPositionDangerous(Board board, int row, int col) {
        // Check if opponent can jump to this position
        Piece.PieceColor opponentColor = (aiColor == Piece.PieceColor.RED)
                                         ? Piece.PieceColor.BLACK
                                         : Piece.PieceColor.RED;

        // Check all four diagonal positions for opponent pieces that could jump here
        int[][] opponentPositions = {
            {row - 2, col - 2}, {row - 2, col + 2},
            {row + 2, col - 2}, {row + 2, col + 2}
        };

        for (int[] pos : opponentPositions) {
            int opRow = pos[0];
            int opCol = pos[1];

            if (!board.isValidPosition(opRow, opCol)) {
                continue;
            }

            Piece opponentPiece = board.getPieceAt(opRow, opCol);
            if (opponentPiece != null && opponentPiece.getColor() == opponentColor) {
                // Check if this opponent piece can jump to our position
                if (board.hasJumpAvailable(opRow, opCol)) {
                    return true;
                }
            }
        }

        return false;
    }

    // Handle multi-jump sequences
    private void handleMultiJump(Board board, int row, int col) {
        while (board.canJumpAgain(row, col)) {
            ArrayList<Move> nextJumps = getJumpsForPiece(board, row, col);
            if (nextJumps.isEmpty()) {
                break;
            }

            // Score and select the best follow-up jump
            Move bestJump = selectBestJump(board, nextJumps);
            System.out.println("AI multi-jumping from (" + row + ", " + col + ") to ("
                             + bestJump.toRow + ", " + bestJump.toCol + ")");
            executeMove(board, bestJump);
            row = bestJump.toRow;
            col = bestJump.toCol;
        }
    }

    // Execute a move on the board
    private void executeMove(Board board, Move move) {
        System.out.println("AI moving from (" + move.fromRow + ", " + move.fromCol + ") to ("
                         + move.toRow + ", " + move.toCol + ")");
        board.movePiece(move.fromRow, move.fromCol, move.toRow, move.toCol);

        // Small delay so player can see what happened
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Get all possible jumps for AI's pieces
    private ArrayList<Move> getAllJumps(Board board) {
        ArrayList<Move> jumps = new ArrayList<>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(row, col);
                if (piece != null && piece.getColor() == aiColor) {
                    jumps.addAll(getJumpsForPiece(board, row, col));
                }
            }
        }

        return jumps;
    }

    // Get all possible jumps for a specific piece
    private ArrayList<Move> getJumpsForPiece(Board board, int row, int col) {
        ArrayList<Move> jumps = new ArrayList<>();
        ArrayList<int[]> jumpDestinations = board.getAvailableJumps(row, col);

        for (int[] dest : jumpDestinations) {
            jumps.add(new Move(row, col, dest[0], dest[1]));
        }

        return jumps;
    }

    // Get all possible normal (non-jump) moves for AI's pieces
    private ArrayList<Move> getAllNormalMoves(Board board) {
        ArrayList<Move> moves = new ArrayList<>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(row, col);
                if (piece != null && piece.getColor() == aiColor) {
                    moves.addAll(getNormalMovesForPiece(board, row, col, piece));
                }
            }
        }

        return moves;
    }

    // Get all normal moves for a specific piece
    private ArrayList<Move> getNormalMovesForPiece(Board board, int row, int col, Piece piece) {
        ArrayList<Move> moves = new ArrayList<>();

        // Check all 4 diagonal directions
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            // Check if this is a valid normal move
            if (board.isValidPosition(newRow, newCol) && board.getPieceAt(newRow, newCol) == null) {
                // Check direction constraints for non-kings
                if (!piece.isKing()) {
                    if (aiColor == Piece.PieceColor.RED && dir[0] < 0) {
                        continue; // Red can't move backwards
                    }
                    if (aiColor == Piece.PieceColor.BLACK && dir[0] > 0) {
                        continue; // Black can't move backwards
                    }
                }

                // Check if it's a dark square
                if ((newRow + newCol) % 2 == 1) {
                    // Test if the move is actually valid
                    if (canMakeMove(board, row, col, newRow, newCol)) {
                        moves.add(new Move(row, col, newRow, newCol));
                    }
                }
            }
        }

        return moves;
    }

    // Helper to check if a move would be valid
    private boolean canMakeMove(Board board, int fromRow, int fromCol, int toRow, int toCol) {
        return board.isValidPosition(toRow, toCol) &&
               board.getPieceAt(toRow, toCol) == null &&
               (toRow + toCol) % 2 == 1;
    }

    // Inner class to represent a move
    private static class Move {
        int fromRow, fromCol, toRow, toCol;

        Move(int fromRow, int fromCol, int toRow, int toCol) {
            this.fromRow = fromRow;
            this.fromCol = fromCol;
            this.toRow = toRow;
            this.toCol = toCol;
        }
    }
}
