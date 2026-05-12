package UI;

import AI.ChessAi;
import Game.GameMode;
import Game.GameState;
import UI.Panels.ModeSelectPanel;
import UI.Panels.StartPanel;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    private CardLayout layout;
    private JPanel container;

    private StartPanel startPanel;
    private ModeSelectPanel modePanel;
    private UI.Panels.GamePanel gamePanel;

    private GameMode selectedMode;
    private ChessAi.Difficulty selectedDifficulty = ChessAi.Difficulty.MEDIUM;

    public GameFrame() {
        setTitle("Chess Game");
        setSize(1120, 820);
        setMinimumSize(new Dimension(980, 760));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        layout = new CardLayout();
        container = new JPanel(layout);

        // Initialize panels
        startPanel = new StartPanel(this);
        modePanel = new ModeSelectPanel(this);
        gamePanel = new UI.Panels.GamePanel(this);

        container.add(startPanel, "START");
        container.add(modePanel, "MODE");
        container.add(gamePanel, "GAME");

        add(container);
        setVisible(true);
    }

    public void switchState(GameState state) {
        switch (state) {
            case START -> layout.show(container, "START");
            case MODE_SELECT -> layout.show(container, "MODE");
            case PLAYING -> {
                gamePanel.startGame(selectedMode);
                layout.show(container, "GAME");
            }
        }
    }

    public void setGameMode(GameMode mode) {
        this.selectedMode = mode;
    }

    public GameMode getGameMode() {
        return selectedMode;
    }

    public ChessAi.Difficulty getSelectedDifficulty() {
        return selectedDifficulty;
    }

    public void setSelectedDifficulty(ChessAi.Difficulty selectedDifficulty) {
        this.selectedDifficulty = selectedDifficulty;
    }
}
