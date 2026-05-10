package Pieces;

import Model.Board;
import Model.Move;
import Model.Tile;
import Util.ImageLoader;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    public Knight(boolean isWhite) {
        super(isWhite);
    }

    @Override
    protected ImageIcon loadIcon() {
        String path = isWhite ?
                "/Assets/Pieces/white_knight.png" :
                "/Assets/Pieces/black_knight.png";
        return ImageLoader.loadScaled(path, 60, 60);
    }

    public List<Move> getValidMoves(Board board, int row, int col) {

        List<Move> moves = new ArrayList<>();

        int[][] jumps = {
                {2,1}, {2,-1}, {-2,1}, {-2,-1},
                {1,2}, {1,-2}, {-1,2}, {-1,-2}
        };

        for (int[] j : jumps) {

            int r = row + j[0];
            int c = col + j[1];

            if (r >= 0 && r < 8 && c >= 0 && c < 8) {

                Tile tile = board.getTile(r, c);

                if (!tile.isOccupied() || tile.getPiece().isWhite() != isWhite) {
                    moves.add(new Move(row, col, r, c));
                }
            }
        }

        return moves;
    }

    @Override
    public String getSymbol() {
        return "N";
    }
}
