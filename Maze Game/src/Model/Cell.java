package Model;

public class Cell {

    public enum CellType {
        WALL,
        PATH,
        START,
        END
    }

    private final int row;
    private final int col;
    private CellType type;
    private boolean visited;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.type = CellType.WALL; // default: blocked until maze is carved
        this.visited = false;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isWall() {
        return type == CellType.WALL;
    }

    public boolean isStart() {
        return type == CellType.START;
    }

    public boolean isEnd() {
        return type == CellType.END;
    }

    public boolean isPathLike() {
        return type == CellType.PATH || type == CellType.START || type == CellType.END;
    }
}
