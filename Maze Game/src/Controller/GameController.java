package Controller;

import Logic.GameEngine;
import Logic.MazeGenerator;
import UI.MainFrame;

public class GameController {

    private final GameEngine gameEngine;
    private MainFrame mainFrame;

    public GameController() {
        this.gameEngine = new GameEngine();
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    // ---------- Navigation ----------
    public void showStartScreen() {
        if (mainFrame != null) {
            mainFrame.showStartPanel();
        }
    }

    public void showDifficultyScreen() {
        if (mainFrame != null) {
            mainFrame.showDifficultyPanel();
        }
    }

    public void startGame(MazeGenerator.Difficulty difficulty) {
        gameEngine.startNewGame(difficulty);

        if (mainFrame != null) {
            mainFrame.getGamePanel().requestGameFocus();
            mainFrame.showGamePanel();
            mainFrame.refreshGamePanel();
        }
    }

    public void restartGame() {
        gameEngine.restartSameDifficulty();

        if (mainFrame != null) {
            mainFrame.getGamePanel().requestGameFocus();
            mainFrame.refreshGamePanel();
        }
    }

    // ---------- Player Movement ----------
    public void moveUp() {
        handleMove(gameEngine.moveUp());
    }

    public void moveDown() {
        handleMove(gameEngine.moveDown());
    }

    public void moveLeft() {
        handleMove(gameEngine.moveLeft());
    }

    public void moveRight() {
        handleMove(gameEngine.moveRight());
    }

    private void handleMove(boolean moved) {
        if (!moved || mainFrame == null) return;

        mainFrame.refreshGamePanel();

        if (gameEngine.hasPlayerWon()) {
            // Play win sound immediately BEFORE modal dialog appears
            mainFrame.getGamePanel().playWinSoundNow();

            mainFrame.showWinMessage(
                    "You won! Steps: " + gameEngine.getPlayer().getSteps()
            );
        }
    }

    // ---------- Getters for UI ----------
    public GameEngine getGameEngine() {
        return gameEngine;
    }
}