package Logic;

import Model.Cell;
import Model.Maze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MazeGenerator {

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    private final Random random = new Random();

    public Maze generateMaze(Difficulty difficulty) {
        int rows;
        int cols;

        switch (difficulty) {
            case EASY:
                rows = 15;
                cols = 15;
                break;
            case MEDIUM:
                rows = 25;
                cols = 25;
                break;
            case HARD:
                rows = 35;
                cols = 35;
                break;
            default:
                rows = 25;
                cols = 25;
        }

        Maze maze = new Maze(rows, cols);
        maze.fillAllWalls();

        // Start carving from (1,1)
        carvePassagesDFS(maze, 1, 1);

        // Define start and end
        maze.setStart(1, 1);
        maze.setEnd(maze.getRows() - 2, maze.getCols() - 2);

        // Ensure start/end are reachable path-like cells
        maze.setCellType(1, 1, Cell.CellType.START);
        maze.setCellType(maze.getRows() - 2, maze.getCols() - 2, Cell.CellType.END);

        // Optional complexity tuning by difficulty
        addExtraOpenings(maze, difficulty);

        return maze;
    }

    private void carvePassagesDFS(Maze maze, int row, int col) {
        Cell current = maze.getCell(row, col);
        if (current == null) return;

        current.setVisited(true);
        current.setType(Cell.CellType.PATH);

        List<int[]> directions = new ArrayList<>();
        // Move in 2-step jumps: up, down, left, right
        directions.add(new int[]{-2, 0});
        directions.add(new int[]{2, 0});
        directions.add(new int[]{0, -2});
        directions.add(new int[]{0, 2});

        Collections.shuffle(directions, random);

        for (int[] dir : directions) {
            int nextRow = row + dir[0];
            int nextCol = col + dir[1];

            // Keep border as walls -> only carve inside [1..rows-2], [1..cols-2]
            if (!isInsideInnerBounds(maze, nextRow, nextCol)) {
                continue;
            }

            Cell nextCell = maze.getCell(nextRow, nextCol);
            if (nextCell != null && !nextCell.isVisited()) {
                // Break wall between current and next
                int wallRow = row + dir[0] / 2;
                int wallCol = col + dir[1] / 2;
                maze.setCellType(wallRow, wallCol, Cell.CellType.PATH);

                carvePassagesDFS(maze, nextRow, nextCol);
            }
        }
    }

    private boolean isInsideInnerBounds(Maze maze, int row, int col) {
        return row > 0 && row < maze.getRows() - 1 && col > 0 && col < maze.getCols() - 1;
    }

    private void addExtraOpenings(Maze maze, Difficulty difficulty) {
        int openings;
        switch (difficulty) {
            case EASY:
                openings = (maze.getRows() * maze.getCols()) / 30; // more openings -> easier
                break;
            case MEDIUM:
                openings = (maze.getRows() * maze.getCols()) / 50;
                break;
            case HARD:
                openings = (maze.getRows() * maze.getCols()) / 80; // fewer openings -> harder
                break;
            default:
                openings = (maze.getRows() * maze.getCols()) / 50;
        }

        for (int i = 0; i < openings; i++) {
            int r = 1 + random.nextInt(maze.getRows() - 2);
            int c = 1 + random.nextInt(maze.getCols() - 2);

            Cell cell = maze.getCell(r, c);
            if (cell == null) continue;

            // Don't overwrite start/end markers
            if (cell.isStart() || cell.isEnd()) continue;

            // Randomly open some walls for alternative routes
            if (cell.isWall()) {
                maze.setCellType(r, c, Cell.CellType.PATH);
            }
        }
    }
}