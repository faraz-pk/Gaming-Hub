package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import Controller.SudokuController;
import Controller.Difficulty;
/**
 * SudokuFrame.java
 * The main JFrame (View in MVC).
 * Builds the complete UI and delegates logic to SudokuController.
 * Layout:
 *  ┌──────────────────────────────────┐
 *  │         HEADER (title/stats)     │
 *  ├──────────────────────────────────┤
 *  │         9×9 GRID                 │
 *  ├──────────────────────────────────┤
 *  │         NUMBER PAD               │
 *  ├──────────────────────────────────┤
 *  │   [Hint] [New Game] [Difficulty] │
 *  └──────────────────────────────────┘
 */
public class SudokuFrame extends JFrame {

    // ── Colors / Fonts ───────────────────────────
    private static final Color COL_BG        = new Color(240, 244, 255);
    private static final Color COL_HEADER_BG = new Color(25,  40, 100);
    private static final Color COL_ACCENT    = new Color(60, 130, 255);
    private static final Font  FONT_TITLE    = new Font("Segue UI", Font.BOLD, 28);
    private static final Font  FONT_STAT     = new Font("Segue UI", Font.PLAIN, 14);
    private static final Font  FONT_BTN      = new Font("Segue UI", Font.BOLD, 14);

    // ── Sub-components ────────────────────────────
    private SudokuGridPanel gridPanel;
    private SudokuController controller;

    // ── Stat labels ───────────────────────────────
    private JLabel timerLabel;
    private JLabel hintsLabel;
    private JLabel mistakesLabel;

    // ── Difficulty ────────────────────────────────
    private JComboBox<Difficulty> difficultyBox;

    // ── Mistake flash ─────────────────────────────
    private Timer mistakeFlashTimer;

    public SudokuFrame() {
        setTitle("Sudoku — Gaming Hub");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        buildUI();
        pack();
        setLocationRelativeTo(null);

        // Start with Easy by default
        startNewGame(Difficulty.EASY);
    }

    // ──────────────────────────────────────────────
    // UI Construction
    // ──────────────────────────────────────────────

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(COL_BG);
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        // Keyboard listener on the frame
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKey(e);
            }
        });
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COL_HEADER_BG);
        header.setBorder(new EmptyBorder(16, 24, 16, 24));

        // Title
        JLabel title = new JLabel("SUDOKU");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        // Stats panel (timer, hints, mistakes)
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        statsPanel.setOpaque(false);

        timerLabel    = statLabel("⏱  00:00");
        hintsLabel    = statLabel("💡 Hints: 0/3");
        mistakesLabel = statLabel("✗  Mistakes: 0/3");

        statsPanel.add(timerLabel);
        statsPanel.add(hintsLabel);
        statsPanel.add(mistakesLabel);

        header.add(statsPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(COL_BG);
        center.setBorder(new EmptyBorder(20, 28, 10, 28));

        // Grid
        gridPanel = new SudokuGridPanel();
        gridPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(gridPanel);

        center.add(Box.createVerticalStrut(16));

        // Number pad
        NumberPadPanel numPad = new NumberPadPanel();
        numPad.setAlignmentX(Component.CENTER_ALIGNMENT);
        numPad.setNumberPadListener(n -> {
            if (n == 0) controller.eraseCell();
            else        controller.enterDigit(n);
            requestFocus();
        });
        center.add(numPad);

        return center;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 14));
        footer.setBackground(COL_BG);

        // Difficulty selector
        difficultyBox = new JComboBox<>(Difficulty.values());
        difficultyBox.setFont(FONT_BTN);
        difficultyBox.setPreferredSize(new Dimension(120, 38));
        difficultyBox.setFocusable(false);
        footer.add(difficultyBox);

        // New Game
        JButton newGameBtn = createButton("New Game", COL_ACCENT);
        newGameBtn.addActionListener(_ -> {
            Difficulty d = (Difficulty) difficultyBox.getSelectedItem();
            startNewGame(d);
            requestFocus();
        });
        footer.add(newGameBtn);

        // Hint
        JButton hintBtn = createButton("Hint 💡", new Color(80, 180, 120));
        hintBtn.addActionListener(_ -> {
            controller.giveHint();
            requestFocus();
        });
        footer.add(hintBtn);

        return footer;
    }

    // ──────────────────────────────────────────────
    // Game Control
    // ──────────────────────────────────────────────

    private void startNewGame(Difficulty difficulty) {
        if (controller == null) {
            controller = new SudokuController(gridPanel, this);
        }
        controller.startGame(difficulty);
        requestFocus();
    }

    // ──────────────────────────────────────────────
    // Keyboard Input
    // ──────────────────────────────────────────────

    private void handleKey(KeyEvent e) {
        int code = e.getKeyCode();

        // Digit keys 1–9
        if (code >= KeyEvent.VK_1 && code <= KeyEvent.VK_9) {
            controller.enterDigit(code - KeyEvent.VK_0);
        } else if (code >= KeyEvent.VK_NUMPAD1 && code <= KeyEvent.VK_NUMPAD9) {
            controller.enterDigit(code - KeyEvent.VK_NUMPAD0);
        }
        // Delete / Backspace = erase
        else if (code == KeyEvent.VK_DELETE || code == KeyEvent.VK_BACK_SPACE || code == KeyEvent.VK_0) {
            controller.eraseCell();
        }
        // Arrow navigation
        else if (code == KeyEvent.VK_UP    || code == KeyEvent.VK_KP_UP) {
            navigate(-1, 0);
        } else if (code == KeyEvent.VK_DOWN  || code == KeyEvent.VK_KP_DOWN) {
            navigate(1, 0);
        } else if (code == KeyEvent.VK_LEFT  || code == KeyEvent.VK_KP_LEFT) {
            navigate(0, -1);
        } else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_KP_RIGHT) {
            navigate(0, 1);
        }
        // H = hint shortcut
        else if (code == KeyEvent.VK_H) {
            controller.giveHint();
        }
    }

    private void navigate(int dr, int dc) {
        int r = controller.getSelectedRow();
        int c = controller.getSelectedCol();
        if (r < 0) { controller.selectCell(0, 0); return; }
        int nr = Math.clamp(r + dr, 0, 8);
        int nc = Math.clamp(c + dc, 0, 8);
        controller.selectCell(nr, nc);
    }

    // ──────────────────────────────────────────────
    // View Updates (called by Controller)
    // ──────────────────────────────────────────────

    public void updateStats(int hintsUsed, int maxHints, int mistakes, int maxMistakes) {
        hintsLabel.setText("💡 Hints: " + hintsUsed + "/" + maxHints);
        mistakesLabel.setText("✗  Mistakes: " + mistakes + "/" + maxMistakes);
    }

    public void updateTimer(String time) {
        timerLabel.setText("⏱  " + time);
    }

    /** Brief red flash on the mistakes label when an error is made. */
    public void flashMistake() {
        if (mistakeFlashTimer != null) mistakeFlashTimer.stop();
        mistakesLabel.setForeground(new Color(255, 100, 100));
        mistakeFlashTimer = new Timer(600, _ -> mistakesLabel.setForeground(Color.WHITE));
        mistakeFlashTimer.setRepeats(false);
        mistakeFlashTimer.start();
    }

    /** Show win/lose dialog. */
    public void showGameOverDialog(boolean won, String time, int mistakes, int hints) {
        String title, msg;
        if (won) {
            title = "🎉 Congratulations!";
            msg   = "<html><center><b>Puzzle Solved!</b><br>"
                    + "Time: " + time + "<br>"
                    + "Mistakes: " + mistakes + "&nbsp;&nbsp;Hints used: " + hints
                    + "</center></html>";
        } else {
            title = "😢 Game Over";
            msg   = "<html><center><b>Too many mistakes!</b><br>"
                    + "Better luck next time.</center></html>";
        }

        Object[] options = {"New Game", "Close"};
        int choice = JOptionPane.showOptionDialog(
                this, msg, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);

        if (choice == 0) {
            Difficulty d = (Difficulty) difficultyBox.getSelectedItem();
            startNewGame(d);
        }
    }

    // ──────────────────────────────────────────────
    // Helper builders
    // ──────────────────────────────────────────────

    private JLabel statLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_STAT);
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed()
                        ? bg.darker()
                        : getModel().isRollover()
                          ? bg.brighter()
                          : bg;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(130, 38));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusable(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
