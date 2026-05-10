package Logic;

import Model.*;
import Pieces.Piece;

import java.util.List;

public class MoveValidator {

    public static boolean isValidMove(Board board, Move move) {

        Piece piece = board.getTile(
                move.getFromRow(),
                move.getFromCol()
        ).getPiece();

        if (piece == null) return false;

        List<Move> moves = piece.getValidMoves(
                board,
                move.getFromRow(),
                move.getFromCol()
        );

        for (Move m : moves) {
            if (m.getToRow() == move.getToRow()
                    && m.getToCol() == move.getToCol()) {
                return true;
            }
        }

        return false;
    }
}