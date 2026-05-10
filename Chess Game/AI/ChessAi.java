package AI;

import Model.*;
import Logic.GameController;
import Pieces.Piece;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ChessAi {

    public enum Difficulty {
        EASY(1),
        MEDIUM(3),
        HARD(5);

        public final int depth;
        Difficulty(int depth) {
            this.depth = depth;
        }
    }

    private Difficulty difficulty;
    private Random random = new Random();

    public ChessAi(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Move getBestMove(Board board, GameController controller) {
        List<Move> allLegalMoves = new ArrayList<>();

        // Get all legal moves for black
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Tile tile = board.getTile(r, c);
                if (tile.isOccupied() && !tile.getPiece().isWhite()) {
                    List<Move> moves = tile.getPiece().getValidMoves(board, r, c);
                    allLegalMoves.addAll(controller.filterIllegalMoves(moves));
                }
            }
        }

        if (allLegalMoves.isEmpty()) return null;

        // Use minimax algorithm to find best move
        Move bestMove = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (Move move : allLegalMoves) {
            // Simulate move
            Piece movingPiece = board.getTile(move.getFromRow(), move.getFromCol()).getPiece();
            Piece captured = board.getTile(move.getToRow(), move.getToCol()).getPiece();

            board.getTile(move.getToRow(), move.getToCol()).setPiece(movingPiece);
            board.getTile(move.getFromRow(), move.getFromCol()).setPiece(null);

            // Evaluate position
            double score = minimax(board, difficulty.depth - 1, false, controller);

            // Undo move
            board.getTile(move.getFromRow(), move.getFromCol()).setPiece(movingPiece);
            board.getTile(move.getToRow(), move.getToCol()).setPiece(captured);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private double minimax(Board board, int depth, boolean isMaximizing, GameController controller) {
        // Base case: reach depth limit or game end
        if (depth == 0) {
            return evaluateBoard(board);
        }

        List<Move> allMoves = new ArrayList<>();

        // Get all legal moves for current player
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Tile tile = board.getTile(r, c);
                if (tile.isOccupied() && tile.getPiece().isWhite() == isMaximizing) {
                    List<Move> moves = tile.getPiece().getValidMoves(board, r, c);
                    allMoves.addAll(controller.filterIllegalMoves(moves));
                }
            }
        }

        if (allMoves.isEmpty()) {
            // Checkmate or stalemate
            return isMaximizing ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        }

        if (isMaximizing) {
            double maxScore = Double.NEGATIVE_INFINITY;
            for (Move move : allMoves) {
                Piece movingPiece = board.getTile(move.getFromRow(), move.getFromCol()).getPiece();
                Piece captured = board.getTile(move.getToRow(), move.getToCol()).getPiece();

                board.getTile(move.getToRow(), move.getToCol()).setPiece(movingPiece);
                board.getTile(move.getFromRow(), move.getFromCol()).setPiece(null);

                double score = minimax(board, depth - 1, false, controller);

                board.getTile(move.getFromRow(), move.getFromCol()).setPiece(movingPiece);
                board.getTile(move.getToRow(), move.getToCol()).setPiece(captured);

                maxScore = Math.max(maxScore, score);
            }
            return maxScore;
        } else {
            double minScore = Double.POSITIVE_INFINITY;
            for (Move move : allMoves) {
                Piece movingPiece = board.getTile(move.getFromRow(), move.getFromCol()).getPiece();
                Piece captured = board.getTile(move.getToRow(), move.getToCol()).getPiece();

                board.getTile(move.getToRow(), move.getToCol()).setPiece(movingPiece);
                board.getTile(move.getFromRow(), move.getFromCol()).setPiece(null);

                double score = minimax(board, depth - 1, true, controller);

                board.getTile(move.getFromRow(), move.getFromCol()).setPiece(movingPiece);
                board.getTile(move.getToRow(), move.getToCol()).setPiece(captured);

                minScore = Math.min(minScore, score);
            }
            return minScore;
        }
    }

    private double evaluateBoard(Board board) {
        double score = 0;

        // Piece values
        double[] pieceValues = {
                1.0,   // Pawn
                3.0,   // Knight
                3.2,   // Bishop
                5.0,   // Rook
                9.0,   // Queen
                200.0  // King (high value to avoid capture)
        };

        // Count material for both sides
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Tile tile = board.getTile(r, c);
                if (tile.isOccupied()) {
                    Piece piece = tile.getPiece();
                    double pieceValue = getPieceValue(piece, pieceValues);

                    // Add positional bonus
                    pieceValue += getPositionalBonus(piece, r, c);

                    if (piece.isWhite()) {
                        score += pieceValue;
                    } else {
                        score -= pieceValue;
                    }
                }
            }
        }

        return score;
    }

    private double getPieceValue(Piece piece, double[] values) {
        String type = piece.getClass().getSimpleName();
        return switch (type) {
            case "Pawn" -> values[0];
            case "Knight" -> values[1];
            case "Bishop" -> values[2];
            case "Rook" -> values[3];
            case "Queen" -> values[4];
            case "King" -> values[5];
            default -> 0;
        };
    }

    private double getPositionalBonus(Piece piece, int row, int col) {
        // Encourage piece development and center control
        String type = piece.getClass().getSimpleName();

        if (type.equals("Pawn")) {
            // Encourage pawns to advance
            if (piece.isWhite()) {
                return (6 - row) * 0.1; // Closer to promotion is better
            } else {
                return (row - 1) * 0.1;
            }
        }

        if (type.equals("Knight") || type.equals("Bishop")) {
            // Encourage center control
            double centerDistance = Math.abs(row - 3.5) + Math.abs(col - 3.5);
            return (8 - centerDistance) * 0.05;
        }

        if (type.equals("Rook")) {
            // Encourage open files and 7th/2nd rank
            if (piece.isWhite() && row == 1) return 0.5;
            if (!piece.isWhite() && row == 6) return 0.5;
        }

        return 0;
    }
}