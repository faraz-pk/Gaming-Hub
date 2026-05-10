import UI.SudokuFrame;

import javax.swing.SwingUtilities;

/**
 * Entry point for the Sudoku game.
 */
public class SudokuGameMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SudokuFrame frame = new SudokuFrame();
            frame.setVisible(true);
        });
    }
}
