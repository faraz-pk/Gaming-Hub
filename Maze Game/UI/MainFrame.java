package UI;

import Controller.GameController;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public static final String START_PANEL = "START_PANEL";
    public static final String DIFFICULTY_PANEL = "DIFFICULTY_PANEL";
    public static final String GAME_PANEL = "GAME_PANEL";

    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    private final StartPanel startPanel;
    private final DifficultyPanel difficultyPanel;
    private final GamePanel gamePanel;

    private final GameController controller;

    public MainFrame(GameController controller) {
        this.controller = controller;

        setTitle("Maze Game");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        startPanel = new StartPanel(controller);
        difficultyPanel = new DifficultyPanel(controller);
        gamePanel = new GamePanel(controller);

        cardPanel.add(startPanel, START_PANEL);
        cardPanel.add(difficultyPanel, DIFFICULTY_PANEL);
        cardPanel.add(gamePanel, GAME_PANEL);

        setContentPane(cardPanel);

        showStartPanel();
    }

    public void showStartPanel() {
        cardLayout.show(cardPanel, START_PANEL);
    }

    public void showDifficultyPanel() {
        cardLayout.show(cardPanel, DIFFICULTY_PANEL);
    }

    public void showGamePanel() {
        cardLayout.show(cardPanel, GAME_PANEL);
    }

    public void refreshGamePanel() {
        gamePanel.repaint();
    }

    public void showWinMessage(String message) {
        int option = JOptionPane.showOptionDialog(
                this,
                message + "\n\nWhat do you want to do next?",
                "Maze Completed",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Play Again", "Change Difficulty", "Exit"},
                "Play Again"
        );

        if (option == 0) {
            controller.restartGame();
            showGamePanel();
            gamePanel.requestGameFocus();
            refreshGamePanel();
        } else if (option == 1) {
            showDifficultyPanel();
        } else {
            dispose();
        }
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }
}
