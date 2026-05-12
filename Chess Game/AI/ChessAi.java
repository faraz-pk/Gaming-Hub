package AI;

import Logic.CheckDetector;
import Logic.GameController;
import Model.Board;
import Model.Move;
import Model.Tile;
import Pieces.Piece;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ChessAi {
    private static final long EASY_TIME_LIMIT_MS = 120;
    private static final long MEDIUM_TIME_LIMIT_MS = 300;
    private static final long HARD_TIME_LIMIT_MS = 700;

    public enum Difficulty {
        EASY(1),
        MEDIUM(2),
        HARD(3);

        public final int depth;

        Difficulty(int depth) {
            this.depth = depth;
        }
    }

    private final Difficulty difficulty;
    private final Random random = new Random();

    public ChessAi(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Move getBestMove(Board board, GameController controller) {
        List<Move> legalMoves = collectLegalMoves(board, false, controller);
        if (legalMoves.isEmpty()) {
            return null;
        }

        long deadlineNanos = System.nanoTime() + getTimeLimitMs() * 1_000_000L;
        Move bestMove = null;
        double bestScore = Double.POSITIVE_INFINITY;

        for (Move move : legalMoves) {
            if (System.nanoTime() >= deadlineNanos && bestMove != null) {
                break;
            }

            Board simulatedBoard = board.copy();
            controller.applyMove(simulatedBoard, move);
            double score = minimax(simulatedBoard, difficulty.depth - 1, true, controller,
                    Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, deadlineNanos);

            if (score < bestScore) {
                bestScore = score;
                bestMove = move;
            } else if (score == bestScore && random.nextBoolean()) {
                bestMove = move;
            }
        }

        return bestMove;
    }

    private double minimax(Board board, int depth, boolean whiteToMove, GameController controller,
                           double alpha, double beta, long deadlineNanos) {
        if (depth == 0 || System.nanoTime() >= deadlineNanos) {
            return evaluateBoard(board);
        }

        List<Move> legalMoves = collectLegalMoves(board, whiteToMove, controller);
        if (legalMoves.isEmpty()) {
            if (CheckDetector.isKingInCheck(board, whiteToMove)) {
                return whiteToMove ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            }
            return 0;
        }

        if (whiteToMove) {
            double bestScore = Double.NEGATIVE_INFINITY;
            for (Move move : legalMoves) {
                if (System.nanoTime() >= deadlineNanos) {
                    break;
                }
                Board simulatedBoard = board.copy();
                controller.applyMove(simulatedBoard, move);
                bestScore = Math.max(bestScore, minimax(
                        simulatedBoard,
                        depth - 1,
                        false,
                        controller,
                        alpha,
                        beta,
                        deadlineNanos
                ));
                alpha = Math.max(alpha, bestScore);
                if (beta <= alpha) {
                    break;
                }
            }
            return bestScore;
        }

        double bestScore = Double.POSITIVE_INFINITY;
        for (Move move : legalMoves) {
            if (System.nanoTime() >= deadlineNanos) {
                break;
            }
            Board simulatedBoard = board.copy();
            controller.applyMove(simulatedBoard, move);
            bestScore = Math.min(bestScore, minimax(
                    simulatedBoard,
                    depth - 1,
                    true,
                    controller,
                    alpha,
                    beta,
                    deadlineNanos
            ));
            beta = Math.min(beta, bestScore);
            if (beta <= alpha) {
                break;
            }
        }
        return bestScore;
    }

    private List<Move> collectLegalMoves(Board board, boolean whitePieces, GameController controller) {
        List<Move> legalMoves = new ArrayList<>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Tile tile = board.getTile(row, col);
                if (!tile.isOccupied() || tile.getPiece().isWhite() != whitePieces) {
                    continue;
                }

                List<Move> candidateMoves = tile.getPiece().getValidMoves(board, row, col);
                legalMoves.addAll(controller.filterIllegalMoves(candidateMoves, whitePieces));
            }
        }

        legalMoves.sort(Comparator.comparingDouble(move -> -capturePriority(board, move)));
        return legalMoves;
    }

    private long getTimeLimitMs() {
        return switch (difficulty) {
            case EASY -> EASY_TIME_LIMIT_MS;
            case MEDIUM -> MEDIUM_TIME_LIMIT_MS;
            case HARD -> HARD_TIME_LIMIT_MS;
        };
    }

    private double capturePriority(Board board, Move move) {
        Tile targetTile = board.getTile(move.getToRow(), move.getToCol());
        if (!targetTile.isOccupied()) {
            return 0;
        }
        return getPieceValue(targetTile.getPiece());
    }

    private double evaluateBoard(Board board) {
        double score = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Tile tile = board.getTile(row, col);
                if (!tile.isOccupied()) {
                    continue;
                }

                Piece piece = tile.getPiece();
                double pieceValue = getPieceValue(piece) + getPositionalBonus(piece, row, col);
                score += piece.isWhite() ? pieceValue : -pieceValue;
            }
        }

        return score;
    }

    private double getPieceValue(Piece piece) {
        return switch (piece.getClass().getSimpleName()) {
            case "Pawn" -> 1.0;
            case "Knight" -> 3.2;
            case "Bishop" -> 3.3;
            case "Rook" -> 5.0;
            case "Queen" -> 9.0;
            case "King" -> 200.0;
            default -> 0;
        };
    }

    private double getPositionalBonus(Piece piece, int row, int col) {
        String type = piece.getClass().getSimpleName();
        int advancedRow = piece.isWhite() ? 7 - row : row;

        if ("Pawn".equals(type)) {
            return advancedRow * 0.08;
        }

        if ("Knight".equals(type) || "Bishop".equals(type)) {
            double centerDistance = Math.abs(row - 3.5) + Math.abs(col - 3.5);
            return (7 - centerDistance) * 0.08;
        }

        if ("Rook".equals(type) && (row == 1 || row == 6)) {
            return 0.35;
        }

        if ("King".equals(type)) {
            return advancedRow < 2 ? 0.2 : 0;
        }

        return 0;
    }
}
