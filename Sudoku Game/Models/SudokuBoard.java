package Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import Controller.Difficulty;

/**
 * SudokuBoard.java
 * Core model class responsible for:
 *  - Generating a valid, fully solved Sudoku grid
 *  - Removing cells to create a puzzle based on difficulty
 *  - Validating user input
 *  - Checking win conditions
 *  - Providing hint functionality
 * Demonstrates OOP principles:
 *  - Encapsulation: grid data is private
 *  - Single Responsibility: handles only board logic
 */
public class SudokuBoard {

    public static final int SIZE = 9;
    public static final int BOX_SIZE = 3;

    private final int[][] solution;   // the complete solved board
    private final int[][] puzzle;     // board with cells removed (0 = empty)
    private final int[][] userGrid;   // the user's current state
    private final boolean[][] fixed;  // true = pre-filled cell (cannot be edited)

    public SudokuBoard(Difficulty difficulty) {
        solution  = new int[SIZE][SIZE];
        puzzle    = new int[SIZE][SIZE];
        userGrid  = new int[SIZE][SIZE];
        fixed     = new boolean[SIZE][SIZE];

        generate(difficulty);
    }

    // ──────────────────────────────────────────────
    // Generation
    // ──────────────────────────────────────────────

    /** Fills solution, builds puzzle, copies puzzle to userGrid. */
    private void generate(Difficulty difficulty) {
        fillBoard(solution);
        copyGrid(solution, puzzle);
        removeCells(puzzle, difficulty.getClues());

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                userGrid[r][c] = puzzle[r][c];
                fixed[r][c]    = puzzle[r][c] != 0;
            }
        }
    }

    /**
     * Recursive backtracking solver used both to generate
     * a complete board and to solve puzzles for hints.
     */
    private boolean fillBoard(int[][] grid) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] == 0) {
                    List<Integer> nums = shuffled();
                    for (int num : nums) {
                        if (isValidPlacement(grid, row, col, num)) {
                            grid[row][col] = num;
                            if (fillBoard(grid)) return true;
                            grid[row][col] = 0;
                        }
                    }
                    return false; // trigger backtrack
                }
            }
        }
        return true; // all cells filled
    }

    /** Removes cells from a fully-filled grid, leaving `clues` cells intact. */
    private void removeCells(int[][] grid, int clues) {
        int toRemove = SIZE * SIZE - clues;
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < SIZE * SIZE; i++) positions.add(i);
        Collections.shuffle(positions);

        int removed = 0;
        for (int pos : positions) {
            if (removed == toRemove) break;
            int r = pos / SIZE;
            int c = pos % SIZE;
            if (grid[r][c] != 0) {
                grid[r][c] = 0;
                removed++;
            }
        }
    }

    // ──────────────────────────────────────────────
    // Validation
    // ──────────────────────────────────────────────

    /**
     * Checks if placing `num` at (row, col) is valid in `grid`
     * per standard Sudoku rules (row, column, 3×3 box).
     */
    public boolean isValidPlacement(int[][] grid, int row, int col, int num) {
        // Check row
        for (int c = 0; c < SIZE; c++) {
            if (grid[row][c] == num) return false;
        }
        // Check column
        for (int r = 0; r < SIZE; r++) {
            if (grid[r][col] == num) return false;
        }
        // Check 3×3 box
        int boxRow = (row / BOX_SIZE) * BOX_SIZE;
        int boxCol = (col / BOX_SIZE) * BOX_SIZE;
        for (int r = boxRow; r < boxRow + BOX_SIZE; r++) {
            for (int c = boxCol; c < boxCol + BOX_SIZE; c++) {
                if (grid[r][c] == num) return false;
            }
        }
        return true;
    }

    /**
     * Returns true if placing num at (row, col) in userGrid conflicts
     * with existing user-entered values (used to highlight conflicts live).
     */
    public boolean conflictsInUserGrid(int row, int col, int num) {
        // Row
        for (int c = 0; c < SIZE; c++) {
            if (c != col && userGrid[row][c] == num) return true;
        }
        // Column
        for (int r = 0; r < SIZE; r++) {
            if (r != row && userGrid[r][col] == num) return true;
        }
        // Box
        int boxRow = (row / BOX_SIZE) * BOX_SIZE;
        int boxCol = (col / BOX_SIZE) * BOX_SIZE;
        for (int r = boxRow; r < boxRow + BOX_SIZE; r++) {
            for (int c = boxCol; c < boxCol + BOX_SIZE; c++) {
                if ((r != row || c != col) && userGrid[r][c] == num) return true;
            }
        }
        return false;
    }

    /** Returns true when the user has correctly filled every cell. */
    public boolean isSolved() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (userGrid[r][c] != solution[r][c]) return false;
            }
        }
        return true;
    }

    // ──────────────────────────────────────────────
    // Hint
    // ──────────────────────────────────────────────

    /**
     * Fills one incorrect or empty cell with the correct solution value.
     * Returns int[]{row, col} of the hinted cell, or null if none found.
     */
    public int[] giveHint() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (!fixed[r][c] && userGrid[r][c] != solution[r][c]) {
                    emptyCells.add(new int[]{r, c});
                }
            }
        }
        if (emptyCells.isEmpty()) return null;
        Collections.shuffle(emptyCells);
        int[] cell = emptyCells.getFirst();
        userGrid[cell[0]][cell[1]] = solution[cell[0]][cell[1]];
        fixed[cell[0]][cell[1]] = true; // treat hint cells as fixed
        return cell;
    }

    // ──────────────────────────────────────────────
    // Setters / Getters
    // ──────────────────────────────────────────────

    /**
     * Place a user's number; 0 means erase. Returns false if cell is fixed.
     */
    public void setUserValue(int row, int col, int value) {
        if (fixed[row][col]) return;
        userGrid[row][col] = value;
    }

    public int getUserValue(int row, int col)    { return userGrid[row][col]; }
    public int getSolutionValue(int row, int col){ return solution[row][col]; }
    public boolean isFixed(int row, int col)     { return fixed[row][col]; }

    // ──────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────

    private List<Integer> shuffled() {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= SIZE; i++) list.add(i);
        Collections.shuffle(list);
        return list;
    }

    private void copyGrid(int[][] src, int[][] dst) {
        for (int r = 0; r < SIZE; r++) {
            System.arraycopy(src[r], 0, dst[r], 0, SIZE);
        }
    }
}
