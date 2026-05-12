package Logic;

import Model.Board;
import Model.Move;
import Model.Tile;
import Pieces.Bishop;
import Pieces.King;
import Pieces.Knight;
import Pieces.Pawn;
import Pieces.Piece;
import Pieces.Queen;
import Pieces.Rook;
import Util.SoundPlayer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameController {
    private Board board;
    private boolean whiteTurn = true;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private List<Move> currentValidMoves = new ArrayList<>();
    private boolean gameOver = false;
    private boolean checkWarningPlayed = false;
    private Move lastMove;
    private String statusMessage = "White to move";
    private final List<String> moveHistory = new ArrayList<>();

    public GameController() {
        restartGame();
    }

    public void restartGame() {
        board = new Board();
        whiteTurn = true;
        selectedRow = -1;
        selectedCol = -1;
        currentValidMoves = new ArrayList<>();
        gameOver = false;
        checkWarningPlayed = false;
        lastMove = null;
        statusMessage = "White to move";
        moveHistory.clear();
    }

    public void handleClick(int row, int col) {
        if (gameOver) {
            JOptionPane.showMessageDialog(null, "Game Over! Please restart.");
            return;
        }

        Tile tile = board.getTile(row, col);

        if (selectedRow == -1) {
            selectPieceIfPossible(row, col, tile);
            return;
        }

        if (tile.isOccupied() && tile.getPiece().isWhite() == whiteTurn) {
            selectPieceIfPossible(row, col, tile);
            return;
        }

        List<Move> matchingMoves = currentValidMoves.stream()
                .filter(m -> m.getToRow() == row && m.getToCol() == col)
                .collect(Collectors.toList());

        Move selectedMove = resolveMoveChoice(matchingMoves);

        if (selectedMove != null) {
            executeMove(selectedMove);
        }

        clearSelection();
    }

    private void selectPieceIfPossible(int row, int col, Tile tile) {
        clearSelection();
        if (tile.isOccupied() && tile.getPiece().isWhite() == whiteTurn) {
            selectedRow = row;
            selectedCol = col;
            currentValidMoves = filterIllegalMoves(tile.getPiece().getValidMoves(board, row, col), whiteTurn);
        }
    }

    private void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        currentValidMoves.clear();
    }

    private Move resolveMoveChoice(List<Move> matchingMoves) {
        if (matchingMoves.isEmpty()) {
            return null;
        }

        if (matchingMoves.size() == 1 || !"promotion".equals(matchingMoves.get(0).getMoveType())) {
            return matchingMoves.get(0);
        }

        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        String choice = (String) JOptionPane.showInputDialog(
                null,
                "Choose a piece for promotion:",
                "Pawn Promotion",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == null) {
            return null;
        }

        String promotionCode = switch (choice) {
            case "Rook" -> "R";
            case "Bishop" -> "B";
            case "Knight" -> "N";
            default -> "Q";
        };

        return matchingMoves.stream()
                .filter(move -> promotionCode.equals(move.getPromotionPiece()))
                .findFirst()
                .orElse(matchingMoves.get(0));
    }

    public void executeMove(Move move) {
        Piece movingPiece = board.getTile(move.getFromRow(), move.getFromCol()).getPiece();
        if (movingPiece == null) {
            return;
        }

        applyMove(board, move, true);
        lastMove = move;
        addMoveToHistory(move, movingPiece);

        whiteTurn = !whiteTurn;
        checkWarningPlayed = false;
        updateStatusMessage();
        checkGameOver();
    }

    private void updateStatusMessage() {
        String activePlayer = whiteTurn ? "White" : "Black";
        if (CheckDetector.isKingInCheck(board, whiteTurn)) {
            statusMessage = activePlayer + " is in check";
        } else {
            statusMessage = activePlayer + " to move";
        }
    }

    private void checkGameOver() {
        if (CheckDetector.isKingInCheck(board, whiteTurn) && !checkWarningPlayed) {
            SoundPlayer.play("check.wav");
            checkWarningPlayed = true;
        }

        if (CheckDetector.isCheckmate(board, whiteTurn, this)) {
            String winner = whiteTurn ? "Black" : "White";
            SoundPlayer.play("checkmate.wav");
            SoundPlayer.play("win.wav");
            gameOver = true;
            statusMessage = "Checkmate - " + winner + " wins";
            JOptionPane.showMessageDialog(
                    null,
                    "Checkmate!\n\n" + winner + " wins!",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else if (CheckDetector.isStalemate(board, whiteTurn, this)) {
            SoundPlayer.play("win.wav");
            gameOver = true;
            statusMessage = "Draw by stalemate";
            JOptionPane.showMessageDialog(
                    null,
                    "Draw! Stalemate.",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    public List<Move> filterIllegalMoves(List<Move> moves) {
        return filterIllegalMoves(moves, whiteTurn);
    }

    public List<Move> filterIllegalMoves(List<Move> moves, boolean movingWhite) {
        return moves.stream()
                .filter(move -> isMoveLegal(board, move, movingWhite))
                .collect(Collectors.toList());
    }

    public boolean isMoveLegal(Board sourceBoard, Move move, boolean movingWhite) {
        Piece movingPiece = sourceBoard.getTile(move.getFromRow(), move.getFromCol()).getPiece();
        if (movingPiece == null || movingPiece.isWhite() != movingWhite) {
            return false;
        }

        if ("castling".equals(move.getMoveType())) {
            if (CheckDetector.isKingInCheck(sourceBoard, movingWhite)) {
                return false;
            }

            int direction = move.getToCol() > move.getFromCol() ? 1 : -1;
            Board stepBoard = sourceBoard.copy();
            movePiece(stepBoard, move.getFromRow(), move.getFromCol(), move.getFromRow(), move.getFromCol() + direction);
            if (CheckDetector.isKingInCheck(stepBoard, movingWhite)) {
                return false;
            }
        }

        Board simulatedBoard = sourceBoard.copy();
        applyMove(simulatedBoard, move, false);
        return !CheckDetector.isKingInCheck(simulatedBoard, movingWhite);
    }

    public void applyMove(Board targetBoard, Move move) {
        applyMove(targetBoard, move, false);
    }

    public void applyMove(Board targetBoard, Move move, boolean playSounds) {
        Tile from = targetBoard.getTile(move.getFromRow(), move.getFromCol());
        Tile to = targetBoard.getTile(move.getToRow(), move.getToCol());
        Piece movingPiece = from.getPiece();
        Piece capturedPieceBeforeMove = resolveCapturedPiece(targetBoard, move);

        if (movingPiece == null) {
            return;
        }

        if (movingPiece instanceof Pawn && Math.abs(move.getToRow() - move.getFromRow()) == 2) {
            targetBoard.setLastPawnMove(move.getFromRow(), move.getFromCol(), move.getToRow(), move.getToCol());
        } else {
            targetBoard.clearLastPawnMove();
        }

        if ("castling".equals(move.getMoveType())) {
            applyCastling(targetBoard, move);
            playSound(playSounds, "move.wav");
        } else if ("en_passant".equals(move.getMoveType())) {
            applyEnPassant(targetBoard, move, movingPiece);
            playSound(playSounds, "capture.wav");
        } else if ("promotion".equals(move.getMoveType())) {
            applyPromotion(targetBoard, move, movingPiece);
            playSound(playSounds, capturedPieceBeforeMove != null ? "capture.wav" : "move.wav");
        } else {
            if (capturedPieceBeforeMove != null) {
                playSound(playSounds, "capture.wav");
            } else {
                playSound(playSounds, "move.wav");
            }
            to.setPiece(movingPiece);
            from.setPiece(null);
        }

        if (movingPiece instanceof King) {
            targetBoard.setKingMoved(movingPiece.isWhite());
        } else if (movingPiece instanceof Rook) {
            if (move.getFromCol() == 0) {
                targetBoard.setRookMoved(movingPiece.isWhite(), true);
            } else if (move.getFromCol() == 7) {
                targetBoard.setRookMoved(movingPiece.isWhite(), false);
            }
        }

        if (capturedPieceBeforeMove instanceof Rook && capturedPieceBeforeMove.isWhite() != movingPiece.isWhite()) {
            updateCapturedRookState(targetBoard, move.getToRow(), move.getToCol(), capturedPieceBeforeMove.isWhite());
        }
    }

    private void applyCastling(Board targetBoard, Move move) {
        movePiece(targetBoard, move.getFromRow(), move.getFromCol(), move.getToRow(), move.getToCol());

        if (move.getToCol() == 6) {
            movePiece(targetBoard, move.getFromRow(), 7, move.getFromRow(), 5);
        } else if (move.getToCol() == 2) {
            movePiece(targetBoard, move.getFromRow(), 0, move.getFromRow(), 3);
        }
    }

    private void applyEnPassant(Board targetBoard, Move move, Piece pawn) {
        Tile from = targetBoard.getTile(move.getFromRow(), move.getFromCol());
        Tile to = targetBoard.getTile(move.getToRow(), move.getToCol());
        Tile capturedPawn = targetBoard.getTile(move.getFromRow(), move.getToCol());

        to.setPiece(pawn);
        from.setPiece(null);
        capturedPawn.setPiece(null);
    }

    private void applyPromotion(Board targetBoard, Move move, Piece pawn) {
        Tile from = targetBoard.getTile(move.getFromRow(), move.getFromCol());
        Tile to = targetBoard.getTile(move.getToRow(), move.getToCol());
        Piece promotedPiece = createPromotedPiece(move.getPromotionPiece(), pawn.isWhite());

        to.setPiece(promotedPiece);
        from.setPiece(null);
    }

    private void movePiece(Board targetBoard, int fromRow, int fromCol, int toRow, int toCol) {
        Tile from = targetBoard.getTile(fromRow, fromCol);
        Tile to = targetBoard.getTile(toRow, toCol);
        to.setPiece(from.getPiece());
        from.setPiece(null);
    }

    private Piece resolveCapturedPiece(Board targetBoard, Move move) {
        if ("en_passant".equals(move.getMoveType())) {
            return targetBoard.getTile(move.getFromRow(), move.getToCol()).getPiece();
        }
        return targetBoard.getTile(move.getToRow(), move.getToCol()).getPiece();
    }

    private void playSound(boolean enabled, String fileName) {
        if (enabled) {
            SoundPlayer.play(fileName);
        }
    }

    private void updateCapturedRookState(Board targetBoard, int row, int col, boolean capturedRookIsWhite) {
        if (capturedRookIsWhite && row == 7) {
            if (col == 0) targetBoard.setRookMoved(true, true);
            if (col == 7) targetBoard.setRookMoved(true, false);
        } else if (!capturedRookIsWhite && row == 0) {
            if (col == 0) targetBoard.setRookMoved(false, true);
            if (col == 7) targetBoard.setRookMoved(false, false);
        }
    }

    private Piece createPromotedPiece(String piece, boolean isWhite) {
        if (piece == null || "Q".equals(piece)) {
            return new Queen(isWhite);
        }

        return switch (piece) {
            case "R" -> new Rook(isWhite);
            case "B" -> new Bishop(isWhite);
            case "N" -> new Knight(isWhite);
            default -> throw new IllegalArgumentException("Invalid promotion piece: " + piece);
        };
    }

    private void addMoveToHistory(Move move, Piece movingPiece) {
        String entry = String.format(
                "%d. %s %s %s",
                (moveHistory.size() / 2) + 1,
                movingPiece.isWhite() ? "White" : "Black",
                movingPiece.getClass().getSimpleName(),
                formatMove(move)
        );
        moveHistory.add(entry);
    }

    private String formatMove(Move move) {
        String from = toSquare(move.getFromRow(), move.getFromCol());
        String to = toSquare(move.getToRow(), move.getToCol());

        return switch (move.getMoveType()) {
            case "castling" -> move.getToCol() == 6 ? "castled king-side" : "castled queen-side";
            case "en_passant" -> from + " x " + to + " e.p.";
            case "promotion" -> from + " -> " + to + "=" + move.getPromotionPiece();
            default -> from + " -> " + to;
        };
    }

    private String toSquare(int row, int col) {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return String.valueOf(file) + rank;
    }

    public Board getBoard() { return board; }
    public boolean isWhiteTurn() { return whiteTurn; }
    public List<Move> getCurrentValidMoves() { return currentValidMoves; }
    public boolean isGameOver() { return gameOver; }
    public int getSelectedRow() { return selectedRow; }
    public int getSelectedCol() { return selectedCol; }
    public Move getLastMove() { return lastMove; }
    public String getStatusMessage() { return statusMessage; }
    public List<String> getMoveHistory() { return new ArrayList<>(moveHistory); }
}
