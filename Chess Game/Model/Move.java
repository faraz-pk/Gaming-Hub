package Model;

public class Move {

    private int fromRow, fromCol;
    private int toRow, toCol;
    private String moveType; // "normal", "castling", "en_passant", "promotion"
    private String promotionPiece; // "Q", "R", "B", "N" for pawn promotion

    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.moveType = "normal";
    }

    public Move(int fromRow, int fromCol, int toRow, int toCol, String moveType) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.moveType = moveType;
    }

    public Move(int fromRow, int fromCol, int toRow, int toCol, String moveType, String promotionPiece) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.moveType = moveType;
        this.promotionPiece = promotionPiece;
    }

    public int getFromRow() { return fromRow; }
    public int getFromCol() { return fromCol; }
    public int getToRow() { return toRow; }
    public int getToCol() { return toCol; }
    public String getMoveType() { return moveType; }
    public String getPromotionPiece() { return promotionPiece; }
}