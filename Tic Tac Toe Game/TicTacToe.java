import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TicTacToe.java  — 2-Player with Difficulty Levels
 *
 * EASY   → 3×3 grid, need 3 in a row to win
 * MEDIUM → 5×5 grid, need 4 in a row to win
 * HARD   → 7×7 grid, need 5 in a row to win
 *
 * OOP Concepts:
 *   - Encapsulation  : game state is private, changed via methods only
 *   - Inheritance    : CellButton extends JPanel
 *   - Inner class    : CellButton lives inside TicTacToe
 *   - Enum           : Difficulty carries grid size + win length + label
 */
public class TicTacToe extends JFrame {

    // ══════════════════════════════════════════════
    // Difficulty Enum
    // ══════════════════════════════════════════════
    enum Difficulty {
        EASY  ("Easy   — 3×3, 3 in a row", 3, 3),
        MEDIUM("Medium — 5×5, 4 in a row", 5, 4),
        HARD  ("Hard   — 7×7, 5 in a row", 7, 5);

        final String label;
        final int    gridSize;   // rows = cols = gridSize
        final int    winLength;  // consecutive marks needed

        Difficulty(String label, int gridSize, int winLength) {
            this.label      = label;
            this.gridSize   = gridSize;
            this.winLength  = winLength;
        }

        @Override public String toString() { return label; }
    }

    // ══════════════════════════════════════════════
    // Colours & Fonts
    // ══════════════════════════════════════════════
    private static final Color COL_BG      = new Color(22, 26, 42);
    private static final Color COL_HEADER  = new Color(16, 20, 36);
    private static final Color COL_GRID    = new Color(50, 62, 105);
    private static final Color COL_CELL    = new Color(28, 33, 55);
    private static final Color COL_HOVER   = new Color(40, 48, 78);
    private static final Color COL_X       = new Color(255,  85, 115);
    private static final Color COL_O       = new Color( 55, 205, 230);
    private static final Color COL_WIN_HL  = new Color( 80, 255, 145);
    private static final Color COL_TEXT    = new Color(215, 222, 255);
    private static final Color COL_MUTED   = new Color(120, 135, 195);

    // ══════════════════════════════════════════════
    // Game State
    // ══════════════════════════════════════════════
    private Difficulty  difficulty = Difficulty.EASY;
    private String[]    board;          // "" | "X" | "O"
    private boolean     xTurn;
    private boolean     gameOver;
    private List<int[]> winCells;       // list of [row,col] in the winning run

    private int xWins, oWins, draws;

    // ══════════════════════════════════════════════
    // UI
    // ══════════════════════════════════════════════
    private CellButton[][]      cells;
    private JPanel              gridPanel;
    private JLabel              statusLbl;
    private JLabel              scoreLbl;
    private JComboBox<Difficulty> diffBox;

    // ══════════════════════════════════════════════
    // Constructor
    // ══════════════════════════════════════════════
    public TicTacToe() {
        setTitle("Tic Tac Toe");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(COL_BG);
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);

        // Grid area — rebuilt each difficulty change
        gridPanel = new JPanel();
        gridPanel.setBackground(COL_BG);
        root.add(gridPanel, BorderLayout.CENTER);

        root.add(buildFooter(), BorderLayout.SOUTH);

        applyDifficulty();
        pack();
        setMinimumSize(new Dimension(420, 500));
        setLocationRelativeTo(null);
    }

    // ══════════════════════════════════════════════
    // UI Builders
    // ══════════════════════════════════════════════

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setBackground(COL_HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(16, 22, 14, 22));

        // Title
        JLabel title = new JLabel("TIC  TAC  TOE", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(COL_TEXT);

        // Difficulty row
        JPanel diffRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        diffRow.setOpaque(false);

        JLabel diffLabel = new JLabel("Difficulty:");
        diffLabel.setFont(new Font("Arial", Font.BOLD, 13));
        diffLabel.setForeground(COL_MUTED);

        diffBox = new JComboBox<>(Difficulty.values());
        diffBox.setSelectedItem(Difficulty.EASY);
        diffBox.setFont(new Font("Arial", Font.BOLD, 13));
        diffBox.setBackground(new Color(35, 42, 70));
        diffBox.setForeground(COL_TEXT);
        diffBox.setFocusable(false);
        diffBox.setPreferredSize(new Dimension(220, 30));
        diffBox.addActionListener(e -> {
            Difficulty selected = (Difficulty) diffBox.getSelectedItem();
            if (selected != difficulty) {
                difficulty = selected;
                xWins = 0; oWins = 0; draws = 0;
                applyDifficulty();
            }
        });

        diffRow.add(diffLabel);
        diffRow.add(diffBox);

        // Status
        statusLbl = new JLabel("Player X's Turn", SwingConstants.CENTER);
        statusLbl.setFont(new Font("Arial", Font.BOLD, 15));
        statusLbl.setForeground(COL_X);
        statusLbl.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        header.add(title,     BorderLayout.NORTH);
        header.add(diffRow,   BorderLayout.CENTER);
        header.add(statusLbl, BorderLayout.SOUTH);
        return header;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(0, 6));
        footer.setBackground(COL_HEADER);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 22, 16, 22));

        scoreLbl = new JLabel("X: 0   Draws: 0   O: 0", SwingConstants.CENTER);
        scoreLbl.setFont(new Font("Arial", Font.BOLD, 18));
        scoreLbl.setForeground(COL_TEXT);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setOpaque(false);
        btnRow.add(makeButton("New Game",    new Color(50, 68, 150), e -> startNewGame()));
        btnRow.add(makeButton("Reset Score", new Color(130, 40, 60),
                e -> { xWins=0; oWins=0; draws=0; updateScore(); startNewGame(); }));

        footer.add(scoreLbl, BorderLayout.CENTER);
        footer.add(btnRow,   BorderLayout.SOUTH);
        return footer;
    }

    // ══════════════════════════════════════════════
    // Grid Rebuild  (called on difficulty change)
    // ══════════════════════════════════════════════

    /**
     * Tears down the old grid and builds a fresh one
     * sized for the current difficulty.
     */
    private void applyDifficulty() {
        int n = difficulty.gridSize;

        // Cell pixel size scales down for larger grids
        int cellPx = (n == 3) ? 110 : (n == 5) ? 76 : 60;

        gridPanel.removeAll();
        gridPanel.setLayout(new BorderLayout());
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JPanel inner = new JPanel(new GridLayout(n, n, 5, 5));
        inner.setBackground(COL_GRID);
        inner.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        board = new String[n * n];
        cells = new CellButton[n][n];

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                board[r * n + c] = "";
                CellButton cell = new CellButton(r, c, cellPx);
                cells[r][c] = cell;
                inner.add(cell);
            }
        }

        gridPanel.add(inner, BorderLayout.CENTER);
        gridPanel.revalidate();
        gridPanel.repaint();

        // Reset game state
        xTurn    = true;
        gameOver = false;
        winCells = null;
        setStatus("Player X's Turn", COL_X);
        updateScore();

        pack();
        setLocationRelativeTo(null);
    }

    private void startNewGame() {
        int n = difficulty.gridSize;
        for (int r = 0; r < n; r++)
            for (int c = 0; c < n; c++) {
                board[r * n + c] = "";
                cells[r][c].repaint();
            }
        xTurn    = true;
        gameOver = false;
        winCells = null;
        setStatus("Player X's Turn", COL_X);
    }

    // ══════════════════════════════════════════════
    // Game Logic
    // ══════════════════════════════════════════════

    private void handleClick(int row, int col) {
        int n = difficulty.gridSize;
        if (gameOver || !board[row * n + col].isEmpty()) return;

        String mark = xTurn ? "X" : "O";
        board[row * n + col] = mark;
        cells[row][col].repaint();

        winCells = checkWin(row, col, mark);

        if (winCells != null) {
            gameOver = true;
            if (xTurn) xWins++; else oWins++;
            updateScore();
            repaintWinCells();
            setStatus("Player " + mark + " Wins! 🎉", xTurn ? COL_X : COL_O);
        } else if (isBoardFull()) {
            gameOver = true;
            draws++;
            updateScore();
            setStatus("It's a Draw! 🤝", COL_MUTED);
        } else {
            xTurn = !xTurn;
            setStatus("Player " + (xTurn ? "X" : "O") + "'s Turn", xTurn ? COL_X : COL_O);
        }
    }

    /**
     * Checks all 4 directions from the just-placed cell (row,col).
     * Returns list of winning [row,col] pairs if a run of winLength is found,
     * else returns null.
     *
     * Directions: horizontal, vertical, diagonal (\), anti-diagonal (/)
     */
    private List<int[]> checkWin(int row, int col, String mark) {
        int n   = difficulty.gridSize;
        int win = difficulty.winLength;

        int[][] dirs = {{0,1},{1,0},{1,1},{1,-1}};

        for (int[] d : dirs) {
            List<int[]> run = new ArrayList<>();
            run.add(new int[]{row, col});

            // Extend in positive direction
            for (int step = 1; step < win; step++) {
                int nr = row + d[0]*step, nc = col + d[1]*step;
                if (nr<0||nr>=n||nc<0||nc>=n) break;
                if (!board[nr*n+nc].equals(mark)) break;
                run.add(new int[]{nr, nc});
            }
            // Extend in negative direction
            for (int step = 1; step < win; step++) {
                int nr = row - d[0]*step, nc = col - d[1]*step;
                if (nr<0||nr>=n||nc<0||nc>=n) break;
                if (!board[nr*n+nc].equals(mark)) break;
                run.add(new int[]{nr, nc});
            }

            if (run.size() >= win) return run;
        }
        return null;
    }

    private boolean isBoardFull() {
        for (String s : board) if (s.isEmpty()) return false;
        return true;
    }

    private boolean isWinCell(int row, int col) {
        if (winCells == null) return false;
        for (int[] wc : winCells) if (wc[0]==row && wc[1]==col) return true;
        return false;
    }

    private void repaintWinCells() {
        if (winCells == null) return;
        for (int[] wc : winCells) cells[wc[0]][wc[1]].repaint();
    }

    // ══════════════════════════════════════════════
    // UI Helpers
    // ══════════════════════════════════════════════

    private void setStatus(String msg, Color color) {
        statusLbl.setText(msg);
        statusLbl.setForeground(color);
    }

    private void updateScore() {
        scoreLbl.setText("X: " + xWins + "   Draws: " + draws + "   O: " + oWins);
    }

    private JButton makeButton(String text, Color bg, ActionListener al) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isRollover() ? bg.brighter() : bg;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COL_TEXT);
                g2.setFont(new Font("Arial", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()-fm.getHeight())/2 + fm.getAscent());
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        btn.setPreferredSize(new Dimension(140, 36));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(al);
        return btn;
    }

    // ══════════════════════════════════════════════
    // Inner Class: CellButton
    // OOP: Inheritance — extends JPanel
    // ══════════════════════════════════════════════
    private class CellButton extends JPanel {
        private final int row, col, px;
        private boolean hovered = false;

        CellButton(int row, int col, int px) {
            this.row = row;
            this.col = col;
            this.px  = px;
            setBackground(COL_CELL);
            setPreferredSize(new Dimension(px, px));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e)  { handleClick(row, col); }
                @Override public void mouseEntered(MouseEvent e)  {
                    if (board[row*difficulty.gridSize+col].isEmpty() && !gameOver) {
                        hovered = true; repaint();
                    }
                }
                @Override public void mouseExited(MouseEvent e)   { hovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            // Background
            boolean winning = isWinCell(row, col);
            Color bg = winning ? new Color(40, 80, 50)
                     : hovered ? COL_HOVER
                     : COL_CELL;
            g2.setColor(bg);
            g2.fillRoundRect(2, 2, w-4, h-4, 10, 10);

            // Symbol
            String val = board[row * difficulty.gridSize + col];
            if (val.isEmpty()) return;

            Color col = val.equals("X") ? COL_X : COL_O;
            Color drawColor = winning ? COL_WIN_HL : col;

            // Glow layers
            int fontSize = (int)(px * 0.55);
            Font glowFont = new Font("Arial", Font.BOLD, fontSize + 8);
            g2.setFont(glowFont);
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(val)) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(new Color(drawColor.getRed(), drawColor.getGreen(), drawColor.getBlue(), 45));
            for (int d = 1; d <= 3; d++) {
                g2.drawString(val, tx+d, ty+d);
                g2.drawString(val, tx-d, ty-d);
            }

            // Main symbol
            Font mainFont = new Font("Arial", Font.BOLD, fontSize);
            g2.setFont(mainFont);
            fm = g2.getFontMetrics();
            tx = (w - fm.stringWidth(val)) / 2;
            ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(drawColor);
            g2.drawString(val, tx, ty);
        }
    }

    // ══════════════════════════════════════════════
    // Main
    // ══════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicTacToe().setVisible(true));
    }
}