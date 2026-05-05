import javax.swing.*;
import java.awt.*;

public class SudokuGridPanel extends JPanel {

    private static final Color BORDER_THICK = new Color(30, 40, 90);
    private static final Color BORDER_THIN  = new Color(180, 190, 210);
    private static final int THICK = 3;
    private static final int THIN  = 1;

    private final SudokuCell[][] cells = new SudokuCell[9][9];

    public SudokuGridPanel() {
        // Null layout so we can position cells precisely
        setLayout(null);
        setBackground(BORDER_THICK);
        // Will be sized in doLayout
    }

    // Build or rebuild the grid with a new board
    public void initGrid(SudokuBoard board, SudokuCell.CellClickListener listener) {
        removeAll();

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                SudokuCell cell = new SudokuCell(r, c);
                cell.setValue(board.getUserValue(r, c), board.isFixed(r, c));
                cell.setCellClickListener(listener);
                cells[r][c] = cell;
                add(cell);
            }
        }
        revalidate();
        repaint();
    }

    // Recompute each cell's bounds based on current panel size
    @Override
    public void doLayout() {
        int totalW = getWidth();
        int totalH = getHeight();

        // Total border pixels: 4 thick (outer + 2 inner box) + 6 thin = 4*3 + 6*1 = 18
        int availW = totalW - (4 * THICK + 6 * THIN);
        int availH = totalH - (4 * THICK + 6 * THIN);
        int cellW  = availW / 9;
        int cellH  = availH / 9;

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (cells[r][c] == null) continue;
                int x = bordersBefore(c) + c * cellW;
                int y = bordersBefore(r) + r * cellH;
                cells[r][c].setBounds(x, y, cellW, cellH);
            }
        }
    }

    // Cumulative border pixels before index i
    private int bordersBefore(int i) {
        int px = THICK; // outer border
        for (int k = 0; k < i; k++) {
            px += ((k + 1) % 3 == 0) ? THICK : THIN;
        }
        return px;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(504, 504);
    }

    public SudokuCell getCell(int r, int c) { return cells[r][c]; }

    // Refresh a single cell's display value
    public void updateCell(int r, int c, int value, boolean isFixed) {
        if (cells[r][c] != null) {
            cells[r][c].setValue(value, isFixed);
        }
    }
}