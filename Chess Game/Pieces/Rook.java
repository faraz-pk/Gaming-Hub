package Pieces;

import Model.Board;
import Model.Move;
import Model.Tile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    public Rook(boolean isWhite) {
        super(isWhite);
    }

    @Override
    protected ImageIcon loadIcon() {
        String path = isWhite ?
                "/Assets/Pieces/white_rook.png" :
                "/Assets/Pieces/black_rook.png";

        ImageIcon original = new ImageIcon(getClass().getResource(path));
        Image img = original.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public List<Move> getValidMoves(Board board, int row, int col) {

        List<Move> moves = new ArrayList<>();

        int[][] dirs = {
                {1,0}, {-1,0}, {0,1}, {0,-1}
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
        return "R";
    }
}