package Controller;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;
import Models.SudokuBoard;
import UI.SudokuGridPanel;
import UI.SudokuFrame;
import Models.SudokuCell;

/**
 * SudokuController.java
 * Acts as the Controller in the MVC pattern.
 * Coordinates between SudokuBoard (Model) and SudokuFrame (View).
 * Responsibilities:
 *  - Handles cell selection and keyboard input
 *  - Tracks hints used and mistakes
 *  - Manages the game timer
 *  - Highlights related cells and conflicts
 */
public class SudokuController {

    private static final int MAX_HINTS = 3;
    private static final int MAX_MISTAKES = 3;

    private SudokuBoard board;
    private final SudokuGridPanel gridPanel;
    private final SudokuFrame frame;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private int hintsUsed    = 0;
    private int mistakes     = 0;
    private int elapsedSecs  = 0;
    private boolean gameOver = false;

    private Timer timer;

    public SudokuController(SudokuGridPanel gridPanel, SudokuFrame frame) {
        this.gridPanel = gridPanel;
        this.frame     = frame;
    }

    // ──────────────────────────────────────────────
    // Game lifecycle
    // ──────────────────────────────────────────────

    public void startGame(Difficulty difficulty) {
        stopTimer();
        board        = new SudokuBoard(difficulty);
        selectedRow  = -1;
        selectedCol  = -1;
        hintsUsed    = 0;
        mistakes     = 0;
        elapsedSecs  = 0;
        gameOver     = false;

        gridPanel.initGrid(board, this::onCellClicked);
        frame.updateStats(hintsUsed, MAX_HINTS, mistakes, MAX_MISTAKES);
        frame.updateTimer("00:00");
        startTimer();
    }

    // ──────────────────────────────────────────────
    // Cell interaction
    // ──────────────────────────────────────────────

    private void onCellClicked(SudokuCell cell) {
        if (gameOver) return;
        selectCell(cell.getRow(), cell.getCol());
    }

    public void selectCell(int row, int col) {
        selectedRow = row;
        selectedCol = col;
        refreshHighlights();
    }

    /**
     * Called by the frame when a digit key is pressed.
     * Validates the value and updates board + UI.
     */
    public void enterDigit(int digit) {
        if (gameOver || selectedRow < 0) return;
        if (board.isFixed(selectedRow, selectedCol)) return;

        board.setUserValue(selectedRow, selectedCol, digit);

        SudokuCell cell = gridPanel.getCell(selectedRow, selectedCol);

        if (digit != 0) {
            boolean conflict = board.conflictsInUserGrid(selectedRow, selectedCol, digit);
            cell.setConflict(conflict);

            // Count mistake only when a wrong (non-conflicting) answer is entered
            if (!conflict && digit != board.getSolutionValue(selectedRow, selectedCol)) {
                mistakes++;
                frame.updateStats(hintsUsed, MAX_HINTS, mistakes, MAX_MISTAKES);
                frame.flashMistake();
                if (mistakes >= MAX_MISTAKES) {
                    endGame(false);
                    return;
                }
            }
        } else {
            cell.setConflict(false);
        }

        cell.setValue(digit, false);
        refreshHighlights();

        if (board.isSolved()) {
            endGame(true);
        }
    }

    public void eraseCell() {
        enterDigit(0);
    }

    // ──────────────────────────────────────────────
    // Hints
    // ──────────────────────────────────────────────

    public void giveHint() {
        if (gameOver || hintsUsed >= MAX_HINTS) return;

        int[] hinted = board.giveHint();
        if (hinted == null) return;

        hintsUsed++;
        int r = hinted[0], c = hinted[1];
        SudokuCell cell = gridPanel.getCell(r, c);
        cell.setValue(board.getUserValue(r, c), true);
        cell.setHint(true);
        cell.setConflict(false);

        frame.updateStats(hintsUsed, MAX_HINTS, mistakes, MAX_MISTAKES);
        refreshHighlights();

        if (board.isSolved()) {
            endGame(true);
        }
    }

    // ──────────────────────────────────────────────
    // Highlight logic
    // ──────────────────────────────────────────────

    private void refreshHighlights() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                SudokuCell cell = gridPanel.getCell(r, c);
                if (cell == null) continue;

                if (r == selectedRow && c == selectedCol) {
                    cell.setState(SudokuCell.CellState.SELECTED);
                } else if (selectedRow >= 0 && isRelated(r, c, selectedRow, selectedCol)) {
                    cell.setState(SudokuCell.CellState.RELATED);
                } else {
                    cell.setState(SudokuCell.CellState.NORMAL);
                }

                // Refresh conflict state for every editable cell
                if (!board.isFixed(r, c) && board.getUserValue(r, c) != 0) {
                    boolean conflict = board.conflictsInUserGrid(r, c, board.getUserValue(r, c));
                    cell.setConflict(conflict);
                }
            }
        }
    }

    /** Returns true if (r,c) is in the same row, col, or 3×3 box as (sr,sc). */
    private boolean isRelated(int r, int c, int sr, int sc) {
        if (r == sr || c == sc) return true;
        return (r / 3 == sr / 3) && (c / 3 == sc / 3);
    }

    // ──────────────────────────────────────────────
    // Timer
    // ──────────────────────────────────────────────

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                elapsedSecs++;
                int m = elapsedSecs / 60;
                int s = elapsedSecs % 60;
                String time = String.format("%02d:%02d", m, s);
                SwingUtilities.invokeLater(() -> frame.updateTimer(time));
            }
        }, 1000, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    // ──────────────────────────────────────────────
    // Game end
    // ──────────────────────────────────────────────

    private void endGame(boolean won) {
        gameOver = true;
        stopTimer();
        int m = elapsedSecs / 60;
        int s = elapsedSecs % 60;
        String timeStr = String.format("%02d:%02d", m, s);
        SwingUtilities.invokeLater(() -> frame.showGameOverDialog(won, timeStr, mistakes, hintsUsed));
    }

    // ──────────────────────────────────────────────
    // Getters
    // ──────────────────────────────────────────────

    public int getSelectedRow()  { return selectedRow; }
    public int getSelectedCol()  { return selectedCol; }
}
