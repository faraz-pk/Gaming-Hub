package Model;

public class Player {

    private int row;
    private int col;
    private int steps;

    public Player(int startRow, int startCol) {
        this.row = startRow;
        this.col = startCol;
        this.steps = 0;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getSteps() {
        return steps;
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void reset(int startRow, int startCol) {
        this.row = startRow;
        this.col = startCol;
        this.steps = 0;
    }

    public void incrementSteps() {
        steps++;
    }

    public boolean isAt(int targetRow, int targetCol) {
        return this.row == targetRow && this.col == targetCol;
    }
}