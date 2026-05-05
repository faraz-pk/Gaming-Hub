package Logic;

import Model.Maze;
import Model.Player;

import java.awt.Point;
import java.util.*;

public class GameEngine {

    private Maze maze;
    private Player player;
    private MazeGenerator.Difficulty difficulty;
    private final MazeGenerator mazeGenerator;

    public GameEngine() {
        this.mazeGenerator = new MazeGenerator();
    }

    public void startNewGame(MazeGenerator.Difficulty difficulty) {
        this.difficulty = difficulty;
        this.maze = mazeGenerator.generateMaze(difficulty);
        this.player = new Player(maze.getStartRow(), maze.getStartCol());
    }

    public Maze getMaze() {
        return maze;
    }

    public Player getPlayer() {
        return player;
    }

    public MazeGenerator.Difficulty getDifficulty() {
        return difficulty;
    }

    public boolean isGameStarted() {
        return maze != null && player != null;
    }

    public boolean moveUp() {
        return movePlayerTo(player.getRow() - 1, player.getCol());
    }

    public boolean moveDown() {
        return movePlayerTo(player.getRow() + 1, player.getCol());
    }

    public boolean moveLeft() {
        return movePlayerTo(player.getRow(), player.getCol() - 1);
    }

    public boolean moveRight() {
        return movePlayerTo(player.getRow(), player.getCol() + 1);
    }

    private boolean movePlayerTo(int newRow, int newCol) {
        if (!isGameStarted()) return false;
        if (!maze.isWalkable(newRow, newCol)) return false;

        player.setPosition(newRow, newCol);
        player.incrementSteps();
        return true;
    }

    public boolean hasPlayerWon() {
        if (!isGameStarted()) return false;
        return player.isAt(maze.getEndRow(), maze.getEndCol());
    }

    public void restartSameDifficulty() {
        if (difficulty == null) {
            difficulty = MazeGenerator.Difficulty.MEDIUM;
        }
        startNewGame(difficulty);
    }

    // -------- NEW: shortest solution path from current player position to END --------
    public java.util.List<Point> findSolutionPathFromPlayer() {
        if (!isGameStarted()) return Collections.emptyList();

        int rows = maze.getRows();
        int cols = maze.getCols();

        boolean[][] visited = new boolean[rows][cols];
        Point[][] parent = new Point[rows][cols];
        Queue<Point> queue = new LinkedList<>();

        Point start = new Point(player.getRow(), player.getCol());
        Point end = new Point(maze.getEndRow(), maze.getEndCol());

        queue.offer(start);
        visited[start.x][start.y] = true;

        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};

        while (!queue.isEmpty()) {
            Point cur = queue.poll();

            if (cur.x == end.x && cur.y == end.y) {
                return buildPath(parent, end, start);
            }

            for (int[] d : dirs) {
                int nr = cur.x + d[0];
                int nc = cur.y + d[1];

                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                if (visited[nr][nc]) continue;
                if (!maze.isWalkable(nr, nc)) continue;

                visited[nr][nc] = true;
                parent[nr][nc] = cur;
                queue.offer(new Point(nr, nc));
            }
        }

        return Collections.emptyList();
    }

    private java.util.List<Point> buildPath(Point[][] parent, Point end, Point start) {
        java.util.List<Point> path = new ArrayList<>();
        Point cur = end;

        while (cur != null) {
            path.add(cur);
            if (cur.x == start.x && cur.y == start.y) break;
            cur = parent[cur.x][cur.y];
        }

        Collections.reverse(path);
        return path;
    }
}