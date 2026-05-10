package UI;

import Controller.GameController;
import Logic.GameEngine;
import Model.Cell;
import Model.Maze;
import Model.Player;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.Point;
import java.io.File;
import java.util.Collections;
import java.util.List;

public class GamePanel extends JPanel {

    private final GameController controller;

    private static final int TOP_HUD_HEIGHT = 60;
    private static final int PADDING = 20;
    // NEW: solution path toggle/cache
    private boolean showHintPath = false;
    private List<Point> cachedPath = Collections.emptyList();

    private static final String MOVE_SOUND = "sounds/move.wav";
    private static final String WIN_SOUND = "sounds/win.wav";

    public GamePanel(GameController controller) {
        this.controller = controller;

        setBackground(new Color(18, 18, 28));
        setFocusable(true);
        setLayout(new BorderLayout());

        setupKeyBindings();
    }

    private void setupKeyBindings() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        bindKey(inputMap, actionMap, "UP", "moveUp", () -> attemptMove(controller::moveUp));
        bindKey(inputMap, actionMap, "W", "moveUpW", () -> attemptMove(controller::moveUp));

        bindKey(inputMap, actionMap, "DOWN", "moveDown", () -> attemptMove(controller::moveDown));
        bindKey(inputMap, actionMap, "S", "moveDownS", () -> attemptMove(controller::moveDown));

        bindKey(inputMap, actionMap, "LEFT", "moveLeft", () -> attemptMove(controller::moveLeft));
        bindKey(inputMap, actionMap, "A", "moveLeftA", () -> attemptMove(controller::moveLeft));

        bindKey(inputMap, actionMap, "RIGHT", "moveRight", () -> attemptMove(controller::moveRight));
        bindKey(inputMap, actionMap, "D", "moveRightD", () -> attemptMove(controller::moveRight));

        bindKey(inputMap, actionMap, "R", "restart", () -> {
            controller.restartGame();
            resetHint();
        });

        bindKey(inputMap, actionMap, "ESCAPE", "backDifficulty", controller::showDifficultyScreen);

        // NEW: Hint toggle key
        bindKey(inputMap, actionMap, "H", "toggleHint", this::toggleHintPath);
    }

    private void attemptMove(Runnable moveAction) {
        GameEngine engine = controller.getGameEngine();
        if (engine == null || !engine.isGameStarted()) return;

        int oldRow = engine.getPlayer().getRow();
        int oldCol = engine.getPlayer().getCol();

        moveAction.run();

        int newRow = engine.getPlayer().getRow();
        int newCol = engine.getPlayer().getCol();

        if (oldRow != newRow || oldCol != newCol) {
            playSound(MOVE_SOUND);
            if (showHintPath) {
                cachedPath = engine.findSolutionPathFromPlayer();
            }
        }

        if (engine.hasPlayerWon()) {
            resetHint();
        }
    }

    private void toggleHintPath() {
        GameEngine engine = controller.getGameEngine();
        if (engine == null || !engine.isGameStarted()) return;

        showHintPath = !showHintPath;
        if (showHintPath) {
            cachedPath = engine.findSolutionPathFromPlayer();
        } else {
            cachedPath = Collections.emptyList();
        }
        repaint();
    }

    private void resetHint() {
        showHintPath = false;
        cachedPath = Collections.emptyList();
        repaint();
    }

    private void bindKey(InputMap inputMap, ActionMap actionMap, String keyStroke, String actionKey, Runnable action) {
        inputMap.put(KeyStroke.getKeyStroke(keyStroke), actionKey);
        actionMap.put(actionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameEngine engine = controller.getGameEngine();
                if (engine == null || !engine.isGameStarted()) return;
                action.run();
            }
        });
    }

    public void requestGameFocus() {
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GameEngine engine = controller.getGameEngine();
        if (engine == null || !engine.isGameStarted()) {
            drawNoGameMessage(g2);
            g2.dispose();
            return;
        }

        Maze maze = engine.getMaze();
        Player player = engine.getPlayer();

        drawHud(g2, engine);
        drawMaze(g2, maze, player);

        g2.dispose();
    }

    private void drawNoGameMessage(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 24));
        String text = "No game running. Choose difficulty to start.";
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = getHeight() / 2;
        g2.drawString(text, x, y);
    }

    private void drawHud(Graphics2D g2, GameEngine engine) {
        g2.setColor(new Color(35, 35, 50));
        g2.fillRect(0, 0, getWidth(), TOP_HUD_HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));

        String difficultyText = "Difficulty: " + engine.getDifficulty();
        String stepsText = "Steps: " + engine.getPlayer().getSteps();
        String helpText = "Move: Arrow/WASD | R: Restart | H: Hint | ESC: Difficulty";

        g2.drawString(difficultyText, 20, 25);
        g2.drawString(stepsText, 20, 45);

        FontMetrics fm = g2.getFontMetrics();
        int helpX = getWidth() - fm.stringWidth(helpText) - 20;
        g2.drawString(helpText, Math.max(helpX, 220), 35);
    }

    private void drawMaze(Graphics2D g2, Maze maze, Player player) {
        int availableWidth = getWidth() - (2 * PADDING);
        int availableHeight = getHeight() - TOP_HUD_HEIGHT - (2 * PADDING);

        int rows = maze.getRows();
        int cols = maze.getCols();

        int cellSize = Math.min(availableWidth / cols, availableHeight / rows);
        if (cellSize <= 0) return;

        int mazePixelWidth = cols * cellSize;
        int mazePixelHeight = rows * cellSize;

        int startX = (getWidth() - mazePixelWidth) / 2;
        int startY = TOP_HUD_HEIGHT + ((availableHeight - mazePixelHeight) / 2) + PADDING;

        // base maze
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = maze.getCell(r, c);

                int x = startX + c * cellSize;
                int y = startY + r * cellSize;

                if (cell.isWall()) g2.setColor(new Color(40, 44, 52));
                else if (cell.isStart()) g2.setColor(new Color(76, 175, 80));
                else if (cell.isEnd()) g2.setColor(new Color(244, 67, 54));
                else g2.setColor(new Color(230, 230, 230));

                g2.fillRect(x, y, cellSize, cellSize);
            }
        }

        // NEW: draw hint path overlay
        if (showHintPath && cachedPath != null) {
            g2.setColor(new Color(255, 235, 59, 170)); // yellow transparent
            for (Point p : cachedPath) {
                int x = startX + p.y * cellSize;
                int y = startY + p.x * cellSize;
                g2.fillRect(x, y, cellSize, cellSize);
            }
        }

        // player
        int px = startX + player.getCol() * cellSize;
        int py = startY + player.getRow() * cellSize;

        g2.setColor(new Color(33, 150, 243));
        int margin = Math.max(2, cellSize / 8);
        g2.fillOval(px + margin, py + margin, cellSize - 2 * margin, cellSize - 2 * margin);
    }

    // NEW: simple WAV player
    private void playSound(String path) {
        try {
            System.out.println(new File(MOVE_SOUND).getAbsolutePath());
            System.out.println(new File(MOVE_SOUND).exists());
            File soundFile = new File(path);
            if (!soundFile.exists()) return;

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ignored) {
            // keep game running even if sound fails
        }
    }

    public void playWinSoundNow() {
        playSound(WIN_SOUND);
    }
}