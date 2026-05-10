import UI.SudokuFrame;

/**
 * Main.java
 * Entry point for the Sudoku Game.
 * Part of the OOP Gaming Hub project.
 */
void main() {
    javax.swing.SwingUtilities.invokeLater(() -> {
        SudokuFrame frame = new SudokuFrame();
        frame.setVisible(true);
    });
}