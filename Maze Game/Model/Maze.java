package Model;

public class Maze {

    private final int rows;
    private final int cols;
    private final Cell[][] grid;

    private int startRow;
    private int startCol;
    private int endRow;
    private int endCol;

    public Maze(int rows, int cols) {
        // Keep dimensions odd for cleaner maze generation with wall carving
        if (rows < 5 || cols < 5) {
            throw new IllegalArgumentException("Maze dimensions must be at least 5x5.");
        }

        this.rows = (rows % 2 == 0) ? rows + 1 : rows;
        this.cols = (cols % 2 == 0) ? cols + 1 : cols;

        this.grid = new Cell[this.rows][this.cols];
        initializeGridAsWalls();

        // default start/end (can be overwritten by generator)
        this.startRow = 1;
        this.startCol = 1;
        this.endRow = this.rows - 2;
        this.endCol = this.cols - 2;

        setCellType(startRow, startCol, Cell.CellType.START);
        setCellType(endRow, endCol, Cell.CellType.END);
    }

    private void initializeGridAsWalls() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Cell(r, c); // default type = WALL
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Cell[][] getGrid() {
        return grid;
    }

    public Cell getCell(int row, int col) {
        if (!isInBounds(row, col)) {
            return null;
        }
        return grid[row][col];
    }

    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public boolean isWalkable(int row, int col) {
        if (!isInBounds(row, col)) {
            return false;
        }
        return grid[row][col].isPathLike();
    }

    public void setCellType(int row, int col, Cell.CellType type) {
        if (!isInBounds(row, col)) {
            return;
        }
        grid[row][col].setType(type);
    }

    public void resetVisitedFlags() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c].setVisited(false);
            }
        }
    }

    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getEndRow() {
        return endRow;
    }

    public int getEndCol() {
        return endCol;
    }

    public void setStart(int row, int col) {
        if (!isInBounds(row, col)) return;

        // Clear old start if still marked START
        if (isInBounds(startRow, startCol) && grid[startRow][startCol].isStart()) {
            grid[startRow][startCol].setType(Cell.CellType.PATH);
        }

        startRow = row;
        startCol = col;
        grid[startRow][startCol].setType(Cell.CellType.START);
    }

    public void setEnd(int row, int col) {
        if (!isInBounds(row, col)) return;

        // Clear old end if still marked END
        if (isInBounds(endRow, endCol) && grid[endRow][endCol].isEnd()) {
            grid[endRow][endCol].setType(Cell.CellType.PATH);
        }

        endRow = row;
        endCol = col;
        grid[endRow][endCol].setType(Cell.CellType.END);
    }

    public void fillAllWalls() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c].setType(Cell.CellType.WALL);
                grid[r][c].setVisited(false);
            }
        }
    }
}