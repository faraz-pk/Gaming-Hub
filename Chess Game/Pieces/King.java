package Pieces;

import Model.Board;
import Model.Move;
import Model.Tile;
import Util.ImageLoader;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    public King(boolean isWhite) {
        super(isWhite);
    }

    @Override
    protected ImageIcon loadIcon() {
        String path = isWhite ?
                "/Assets/Pieces/white_king.png" :
                "/Assets/Pieces/black_king.png";
        return ImageLoader.loadScaled(path, 60, 60);
    }

    public List<Move> getValidMoves(Board board, int row, int col) {

        List<Move> moves = new ArrayList<>();

        // Normal king moves (one square in any direction)
        int[][] dirs = {
                {1,0}, {-1,0}, {0,1}, {0,-1},
                {1,1}, {1,-1}, {-1,1}, {-1,-1}
        };

        for (int[] d : dirs) {

            int r = row + d[0];
            int c = col + d[1];

            if (r >= 0 && r < 8 && c >= 0 && c < 8) {

                Tile tile = board.getTile(r, c);

                if (!tile.isOccupied() || tile.getPiece().isWhite() != isWhite) {
                    moves.add(new Move(row, col, r, c));
                }
            }
        }

        // CASTLING MOVES
        addCastlingMoves(board, row, col, moves);

        return moves;
    }

    private void addCastlingMoves(Board board, int row, int col, List<Move> moves) {
        // King must be on starting position and not have moved
        if (board.hasKingMoved(isWhite)) return;
        if ((isWhite && row != 7) || (!isWhite && row != 0)) return;
        if (col != 4) return; // King starts at column 4

        // KINGSIDE CASTLING (0-0)
        // Rook at position 7, no pieces between king and rook, rook hasn't moved
        if (!board.hasRookMoved(isWhite, false)) {
            Tile rookTile = board.getTile(row, 7);
            if (rookTile.isOccupied() && rookTile.getPiece().getClass().getSimpleName().equals("Rook")
                    && rookTile.getPiece().isWhite() == isWhite) {
                // Check if squares between are empty
                if (!board.getTile(row, 5).isOccupied() && !board.getTile(row, 6).isOccupied()) {
                    moves.add(new Move(row, col, row, 6, "castling"));
                }
            }
        }

        // QUEENSIDE CASTLING (0-0-0)
        // Rook at position 0, no pieces between, rook hasn't moved
        if (!board.hasRookMoved(isWhite, true)) {
            Tile rookTile = board.getTile(row, 0);
            if (rookTile.isOccupied() && rookTile.getPiece().getClass().getSimpleName().equals("Rook")
                    && rookTile.getPiece().isWhite() == isWhite) {
                // Check if squares between are empty
                if (!board.getTile(row, 1).isOccupied() && !board.getTile(row, 2).isOccupied()
                        && !board.getTile(row, 3).isOccupied()) {
                    moves.add(new Move(row, col, row, 2, "castling"));
                }
            }
        }
    }

    @Override
    public String getSymbol() {
        return "K";
    }
}
