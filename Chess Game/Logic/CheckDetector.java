package Logic;

import Model.*;
import Pieces.Piece;
import java.util.List;

public class CheckDetector {

    public static boolean isKingInCheck(Board board, boolean isWhite) {

        int kingRow = -1, kingCol = -1;

        // find king
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Tile tile = board.getTile(r, c);

                if (tile.isOccupied()) {
                    Piece p = tile.getPiece();
                    if (p.getClass().getSimpleName().equals("King")
                            && p.isWhite() == isWhite) {
                        kingRow = r;
                        kingCol = c;
                    }
                }
            }
        }

        // check attacks
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Tile tile = board.getTile(r, c);

                if (tile.isOccupied() &&
                        tile.getPiece().isWhite() != isWhite) {

                    for (Move m : tile.getPiece().getValidMoves(board, r, c)) {
                        if (m.getToRow() == kingRow && m.getToCol() == kingCol) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    // Checks if the player has ANY legal moves left
    public static boolean hasLegalMoves(Board board, boolean isWhite, GameController controller) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Tile tile = board.getTile(r, c);
                if (tile.isOccupied() && tile.getPiece().isWhite() == isWhite) {
                    List<Move> candidateMoves = tile.getPiece().getValidMoves(board, r, c);
                    for (Move move : candidateMoves) {
                        if (controller.isMoveLegal(board, move, isWhite)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isCheckmate(Board board, boolean isWhite, GameController controller) {
        return isKingInCheck(board, isWhite) && !hasLegalMoves(board, isWhite, controller);
    }

    public static boolean isStalemate(Board board, boolean isWhite, GameController controller) {
        return !isKingInCheck(board, isWhite) && !hasLegalMoves(board, isWhite, controller);
    }
}
