package Models;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * SudokuCell.java
 * Represents a single cell in the 9×9 Sudoku grid.
 * Responsibilities:
 *  - Display a number or remain empty
 *  - Show different visual states (fixed, selected, conflict, hint)
 *  - Accept keyboard input when selected
 * OOP Concepts: Inheritance (extends JPanel), Encapsulation
 */
public class SudokuCell extends JPanel {

    // ── Visual State ──────────────────────────────
    public enum CellState {
        NORMAL, SELECTED, RELATED
    }

    // ── Colours ───────────────────────────────────
    private static final Color COL_BG_NORMAL   = new Color(248, 248, 252);
    private static final Color COL_BG_FIXED    = new Color(220, 225, 240);
    private static final Color COL_BG_SELECTED = new Color(180, 210, 255);
    private static final Color COL_BG_RELATED  = new Color(210, 230, 255);
    private static final Color COL_BG_CONFLICT = new Color(255, 200, 200);
    private static final Color COL_BG_HINT     = new Color(200, 255, 215);

    private static final Color COL_TEXT_FIXED  = new Color(30,  40,  90);
    private static final Color COL_TEXT_USER   = new Color(30, 110, 200);
    private static final Color COL_TEXT_CONFLICT = new Color(200, 30, 30);
    private static final Color COL_TEXT_HINT   = new Color(0, 150, 80);

    private static final Font FONT_FIXED = new Font("Segue UI", Font.BOLD,  22);
    private static final Font FONT_USER  = new Font("Segue UI", Font.PLAIN, 22);

    // ── State ─────────────────────────────────────
    private final int row;
    private final int col;
    private int value;
    private boolean fixed;
    private CellState state = CellState.NORMAL;
    private boolean conflict = false;
    private boolean hint = false;

    private CellClickListener clickListener;

    // ──────────────────────────────────────────────
    // Constructor
    // ──────────────────────────────────────────────

    public SudokuCell(int row, int col) {
        this.row = row;
        this.col = col;
        setPreferredSize(new Dimension(56, 56));
        setOpaque(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (clickListener != null) {
                    clickListener.onCellClicked(SudokuCell.this);
                }
            }
        });
    }

    // ──────────────────────────────────────────────
    // Painting
    // ──────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        // Background
        Color bg;
        if (conflict) bg = COL_BG_CONFLICT;
        else if (hint) bg = COL_BG_HINT;
        else if (state == CellState.SELECTED) bg = COL_BG_SELECTED;
        else if (state == CellState.RELATED)  bg = COL_BG_RELATED;
        else if (fixed) bg = COL_BG_FIXED;
        else bg = COL_BG_NORMAL;

        g2.setColor(bg);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Number
        if (value != 0) {
            Color textColor;
            if (conflict)   textColor = COL_TEXT_CONFLICT;
            else if (hint)  textColor = COL_TEXT_HINT;
            else if (fixed) textColor = COL_TEXT_FIXED;
            else            textColor = COL_TEXT_USER;

            g2.setColor(textColor);
            g2.setFont(fixed ? FONT_FIXED : FONT_USER);

            String text = String.valueOf(value);
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth()  - fm.stringWidth(text)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, x, y);
        }

        // Selection ring
        if (state == CellState.SELECTED) {
            g2.setColor(new Color(60, 130, 255));
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
        }
    }

    // ──────────────────────────────────────────────
    // State Management
    // ──────────────────────────────────────────────

    public void setValue(int v, boolean isFixed) {
        this.value = v;
        this.fixed = isFixed;
        repaint();
    }

    public void setState(CellState state) {
        this.state = state;
        repaint();
    }

    public void setConflict(boolean conflict) {
        this.conflict = conflict;
        repaint();
    }

    public void setHint(boolean hint) {
        this.hint = hint;
        repaint();
    }

    public int  getRow()   { return row; }
    public int  getCol()   { return col; }

    // ──────────────────────────────────────────────
    // Listener Interface
    // ──────────────────────────────────────────────

    public interface CellClickListener {
        void onCellClicked(SudokuCell cell);
    }

    public void setCellClickListener(CellClickListener listener) {
        this.clickListener = listener;
    }
}
