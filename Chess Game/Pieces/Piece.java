package Pieces;

import Model.Board;
import Model.Move;

import javax.swing.*;
import java.util.List;

public abstract class Piece {

    protected boolean isWhite;
    protected ImageIcon icon;

    public Piece(boolean isWhite) {
        this.isWhite = isWhite;
        this.icon = loadIcon();
    }

    public boolean isWhite() {
        return isWhite;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    protected abstract ImageIcon loadIcon();

    public abstract List<Move> getValidMoves(Board board, int row, int col);

    public abstract String getSymbol();
}