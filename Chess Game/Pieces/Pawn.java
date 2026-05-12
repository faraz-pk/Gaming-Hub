package Pieces;

import Model.Board;
import Model.Move;
import Model.Tile;
import Util.ImageLoader;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(boolean isWhite) {
        super(isWhite);
    }

    @Override
    protected ImageIcon loadIcon() {
        String path = isWhite ?
                "/Assets/Pieces/white_pawn.png" :
                "/Assets/Pieces/black_pawn.png";
        return ImageLoader.loadScaled(path, 60, 60);
    }

    @Override
    public List<Move> getValidMoves(Board board, int row, int col) {
        List<Move> moves = new ArrayList<>();
        int dir = isWhite ? -1 : 1;
        int startRow = isWhite ? 6 : 1;

        // One square forward
        int nextRow = row + dir;
        if (nextRow >= 0 && nextRow < 8 && !board.getTile(nextRow, col).isOccupied()) {
            // Check if pawn reaches promotion rank
            if ((isWhite && nextRow == 0) || (!isWhite && nextRow == 7)) {
                // Add promotion moves
                moves.add(new Move(row, col, nextRow, col, "promotion", "Q")); // Queen
                moves.add(new Move(row, col, nextRow, col, "promotion", "R")); // Rook
                moves.add(new Move(row, col, nextRow, col, "promotion", "B")); // Bishop
                moves.add(new Move(row, col, nextRow, col, "promotion", "N")); // Knight
            } else {
                moves.add(new Move(row, col, nextRow, col));

                // Two squares forward from start
                int doubleNextRow = row + (2 * dir);
                if (row == startRow && !board.getTile(doubleNextRow, col).isOccupied()) {
                    moves.add(new Move(row, col, doubleNextRow, col));
                }
            }
        }

        // Diagonal Captures
        int[] cols = {col - 1, col + 1};
        for (int c : cols) {
            if (c >= 0 && c < 8 && nextRow >= 0 && nextRow < 8) {
                Tile target = board.getTile(nextRow, c);
                if (target.isOccupied() && target.getPiece().isWhite() != isWhite) {
                    if ((isWhite && nextRow == 0) || (!isWhite && nextRow == 7)) {
                        // Promotion by capture
                        moves.add(new Move(row, col, nextRow, c, "promotion", "Q"));
                        moves.add(new Move(row, col, nextRow, c, "promotion", "R"));
                        moves.add(new Move(row, col, nextRow, c, "promotion", "B"));
                        moves.add(new Move(row, col, nextRow, c, "promotion", "N"));
                    } else {
                        moves.add(new Move(row, col, nextRow, c));
                    }
                }
            }
        }

        // EN PASSANT
        addEnPassantMoves(board, row, col, nextRow, moves);

        return moves;
    }

    private void addEnPassantMoves(Board board, int row, int col, int nextRow, List<Move> moves) {
        int lastFromRow = board.getLastPawnMoveFromRow();
        int lastToRow = board.getLastPawnMoveToRow();
        int lastToCol = board.getLastPawnMoveToCol();
        int lastFromCol = board.getLastPawnMoveFromCol();

        if (lastFromRow != -1 && Math.abs(lastFromRow - lastToRow) == 2) {
            if (lastToRow == row && Math.abs(lastToCol - col) == 1) {
                Tile capturedPawnTile = board.getTile(row, lastToCol);
                Tile targetTile = board.getTile(nextRow, lastToCol);
                if (lastFromCol == lastToCol
                        && capturedPawnTile.isOccupied()
                        && capturedPawnTile.getPiece() instanceof Pawn
                        && capturedPawnTile.getPiece().isWhite() != isWhite
                        && !targetTile.isOccupied()) {
                    moves.add(new Move(row, col, nextRow, lastToCol, "en_passant"));
                }
            }
        }
    }

    @Override
    public String getSymbol() {
        return "P";
    }
}
