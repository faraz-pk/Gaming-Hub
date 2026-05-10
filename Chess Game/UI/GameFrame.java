package UI;

import Game.GameMode;
import Game.GameState;
import UI.Panels.*;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    private CardLayout layout;
    private JPanel container;

    private StartPanel startPanel;
    private ModeSelectPanel modePanel;
    private GamePanel gamePanel;

    private GameMode selectedMode;

    public GameFrame() {
        setTitle("Chess Game");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        layout = new CardLayout();
        container = new JPanel(layout);

        // Initialize panels
        startPanel = new StartPanel(this);
        modePanel = new ModeSelectPanel(this);
        gamePanel = new GamePanel(this);

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
        gamePanel.startGame(mode);
    }

    public GameMode getGameMode() {
        return selectedMode;
    }
}