public enum Difficulty {

    EASY("Easy", 36),
    MEDIUM("Medium", 27),
    HARD("Hard", 17);

    private final String label;
    private final int clues; // number of cells pre-filled

    Difficulty(String label, int clues) {
        this.label = label;
        this.clues = clues;
    }

    public String getLabel() {
        return label;
    }

    public int getClues() {
        return clues;
    }

    @Override
    public String toString() {
        return label;
    }
}