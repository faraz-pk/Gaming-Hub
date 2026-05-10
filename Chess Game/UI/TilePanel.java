package UI;

import javax.swing.*;

public class TilePanel extends JPanel {

    private int row;
    private int col;

    public TilePanel(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
}