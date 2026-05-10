package Logic;

import Model.*;
import Pieces.Piece;
import Util.SoundPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import Logic.CheckDetector;

import javax.swing.*;

public class GameController {
    private Board board;
    private boolean whiteTurn = true;
    private int selectedRow = -1, selectedCol = -1;
    private List<Move> currentValidMoves = new ArrayList<>();
    private boolean gameOver = false;
    private boolean checkWarningPlayed = false;

    public GameController() {
        board = new Board();
    }

    public void handleClick(int row, int col) {
        // Prevent moves after game ends
        if (gameOver) {
            JOptionPane.showMessageDialog(null, "Game Over! Please restart.");
            return;
        }

        Tile tile = board.getTile(row, col);

        if (selectedRow == -1) {
            if (tile.isOccupied() && tile.getPiece().isWhite() == whiteTurn) {
                selectedRow = row;
                selectedCol = col;
                currentValidMoves = filterIllegalMoves(tile.getPiece().getValidMoves(board, row, col));
            }
        } else {
            Move selectedMove = currentValidMoves.stream()
                    .filter(m -> m.getToRow() == row && m.getToCol() == col)
                    .findFirst().orElse(null);

            if (selectedMove != null) {
                makeMove(selectedMove);
                whiteTurn = !whiteTurn;
                checkWarningPlayed = false; // Reset check warning for next turn
                checkGameOver();
            }
            selectedRow = -1; selectedCol = -1;
            currentValidMoves.clear();
        }
    }

    private void checkGameOver() {
        // Play check warning if king is in check
        if (CheckDetector.isKingInCheck(board, whiteTurn) && !checkWarningPlayed) {
            SoundPlayer.play("check.wav");
            checkWarningPlayed = true;
        }

        if (CheckDetector.isCheckmate(board, whiteTurn, this)) {
            String winner = whiteTurn ? "Black" : "White";

            // Play checkmate sound
            SoundPlayer.play("checkmate.wav");

            gameOver = true;
            JOptionPane.showMessageDialog(null,
                    "🎉 CHECKMATE! 🎉\n\n" + winner + " wins!",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (CheckDetector.isStalemate(board, whiteTurn, this)) {
            SoundPlayer.play("stalemate.wav");
            gameOver = true;
            JOptionPane.showMessageDialog(null,
                    "Draw! Stalemate.",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public List<Move> filterIllegalMoves(List<Move> moves) {
        return moves.stream().filter(move -> {
            Tile from = board.getTile(move.getFromRow(), move.getFromCol());
            Tile to = board.getTile(move.getToRow(), move.getToCol());
            Piece movingPiece = from.getPiece();
            Piece capturedPiece = to.getPiece();

            // Simulate move
            to.setPiece(movingPiece);
            from.setPiece(null);

            // For en passant, also remove the captured pawn
            Piece enPassantCaptured = null;
            if (move.getMoveType().equals("en_passant")) {
                int capturedRow = move.getFromRow();
                int capturedCol = move.getToCol();
                Tile capturedTile = board.getTile(capturedRow, capturedCol);
                enPassantCaptured = capturedTile.getPiece();
                capturedTile.setPiece(null);
            }

            boolean inCheck = CheckDetector.isKingInCheck(board, whiteTurn);

            // Undo simulation
            from.setPiece(movingPiece);
            to.setPiece(capturedPiece);
            if (enPassantCaptured != null) {
                board.getTile(move.getFromRow(), move.getToCol()).setPiece(enPassantCaptured);
            }

            return !inCheck;
        }).collect(Collectors.toList());
    }

    private void makeMove(Move move) {
        Tile from = board.getTile(move.getFromRow(), move.getFromCol());
        Tile to = board.getTile(move.getToRow(), move.getToCol());
        Piece movingPiece = from.getPiece();

        // Track pawn moves for en passant
        if (movingPiece.getClass().getSimpleName().equals("Pawn")) {
            board.setLastPawnMove(move.getFromRow(), move.getFromCol(), move.getToRow(), move.getToCol());
        }

        // Handle castling
        if (move.getMoveType().equals("castling")) {
            handleCastling(move, movingPiece);
        }
        // Handle en passant
        else if (move.getMoveType().equals("en_passant")) {
            handleEnPassant(move, movingPiece);
        }
        // Handle pawn promotion
        else if (move.getMoveType().equals("promotion")) {
            handlePromotion(move, movingPiece);
        }
        // Normal move
        else {
            if (to.isOccupied()) SoundPlayer.play("capture.wav");
            else SoundPlayer.play("move.wav");

            to.setPiece(movingPiece);
            from.setPiece(null);
        }

        // Track king and rook moves for castling availability
        if (movingPiece.getClass().getSimpleName().equals("King")) {
            board.setKingMoved(movingPiece.isWhite());
        } else if (movingPiece.getClass().getSimpleName().equals("Rook")) {
            if (move.getFromCol() == 0) {
                board.setRookMoved(movingPiece.isWhite(), true); // Queen-side
            } else if (move.getFromCol() == 7) {
                board.setRookMoved(movingPiece.isWhite(), false); // King-side
            }
        }
    }

    private void handleCastling(Move move, Piece king) {
        SoundPlayer.play("castling.wav");

        Tile kingFrom = board.getTile(move.getFromRow(), move.getFromCol());
        Tile kingTo = board.getTile(move.getToRow(), move.getToCol());

        // Move king
        kingTo.setPiece(king);
        kingFrom.setPiece(null);

        // Move rook
        if (move.getToCol() == 6) { // Kingside castling
            Tile rookFrom = board.getTile(move.getFromRow(), 7);
            Tile rookTo = board.getTile(move.getFromRow(), 5);
            rookTo.setPiece(rookFrom.getPiece());
            rookFrom.setPiece(null);
        } else if (move.getToCol() == 2) { // Queenside castling
            Tile rookFrom = board.getTile(move.getFromRow(), 0);
            Tile rookTo = board.getTile(move.getFromRow(), 3);
            rookTo.setPiece(rookFrom.getPiece());
            rookFrom.setPiece(null);
        }
    }

    private void handleEnPassant(Move move, Piece pawn) {
        SoundPlayer.play("capture.wav");

        Tile from = board.getTile(move.getFromRow(), move.getFromCol());
        Tile to = board.getTile(move.getToRow(), move.getToCol());
        Tile capturedPawn = board.getTile(move.getFromRow(), move.getToCol());

        to.setPiece(pawn);
        from.setPiece(null);
        capturedPawn.setPiece(null); // Remove captured pawn
    }

    private void handlePromotion(Move move, Piece pawn) {
        SoundPlayer.play("promotion.wav");

        Tile from = board.getTile(move.getFromRow(), move.getFromCol());
        Tile to = board.getTile(move.getToRow(), move.getToCol());

        // Create promoted piece
        Piece promotedPiece = createPromotedPiece(move.getPromotionPiece(), pawn.isWhite());

        to.setPiece(promotedPiece);
        from.setPiece(null);
    }

    private Piece createPromotedPiece(String piece, boolean isWhite) {
        return switch (piece) {
            case "Q" -> new Pieces.Queen(isWhite);
            case "R" -> new Pieces.Rook(isWhite);
            case "B" -> new Pieces.Bishop(isWhite);
            case "N" -> new Pieces.Knight(isWhite);
            default -> throw new IllegalArgumentException("Invalid promotion piece: " + piece);
        };
    }

    // Getters
    public Board getBoard() { return board; }
    public boolean isWhiteTurn() { return whiteTurn; }
    public List<Move> getCurrentValidMoves() { return currentValidMoves; }
    public boolean isGameOver() { return gameOver; }
}