package Pieces;

import Model.Board;
import Model.Move;
import Model.Tile;
import Util.ImageLoader;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    public Bishop(boolean isWhite) {
        super(isWhite);
    }

    @Override
    protected ImageIcon loadIcon() {
        String path = isWhite ?
                "/Assets/Pieces/white_bishop.png" :
                "/Assets/Pieces/black_bishop.png";
        return ImageLoader.loadScaled(path, 60, 60);
    }

    public List<Move> getValidMoves(Board board, int row, int col) {

        List<Move> moves = new ArrayList<>();

        int[][] dirs = {
                {1,1}, {1,-1}, {-1,1}, {-1,-1}
        };

        for (int[] d : dirs) {

            int r = row + d[0];
            int c = col + d[1];

            while (r >= 0 && r < 8 && c >= 0 && c < 8) {

                Tile tile = board.getTile(r, c);

                if (!tile.isOccupied()) {
                    moves.add(new Move(row, col, r, c));
                } else {
                    if (tile.getPiece().isWhite() != isWhite) {
                        moves.add(new Move(row, col, r, c));
                    }
                    break;
                }

                r += d[0];
                c += d[1];
            }
        }

        return moves;
    }

    @Override
    public String getSymbol() {
        return "B";
    }
}
