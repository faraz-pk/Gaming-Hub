package Model;

import Pieces.*;

public class Board {

    private Tile[][] board;
    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private boolean whiteRookAMoved = false; // Queen-side rook
    private boolean whiteRookHMoved = false; // King-side rook
    private boolean blackRookAMoved = false;
    private boolean blackRookHMoved = false;
    private int lastPawnMoveFromRow = -1;
    private int lastPawnMoveFromCol = -1;
    private int lastPawnMoveToRow = -1;
    private int lastPawnMoveToCol = -1;

    public Board() {
        this(true);
    }

    private Board(boolean setupPieces) {
        board = new Tile[8][8];
        initializeBoard();
        if (setupPieces) {
            setupPieces();
        }
    }

    private void initializeBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = new Tile(row, col);
            }
        }
    }

    public Tile getTile(int row, int col) {
        return board[row][col];
    }

    public boolean isInside(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private void setupPieces() {
        // Pawns
        for (int col = 0; col < 8; col++) {
            board[1][col].setPiece(new Pawn(false));
            board[6][col].setPiece(new Pawn(true));
        }

        // Rooks
        board[0][0].setPiece(new Rook(false));
        board[0][7].setPiece(new Rook(false));
        board[7][0].setPiece(new Rook(true));
        board[7][7].setPiece(new Rook(true));

        // Knights
        board[0][1].setPiece(new Knight(false));
        board[0][6].setPiece(new Knight(false));
        board[7][1].setPiece(new Knight(true));
        board[7][6].setPiece(new Knight(true));

        // Bishops
        board[0][2].setPiece(new Bishop(false));
        board[0][5].setPiece(new Bishop(false));
        board[7][2].setPiece(new Bishop(true));
        board[7][5].setPiece(new Bishop(true));

        // Queens
        board[0][3].setPiece(new Queen(false));
        board[7][3].setPiece(new Queen(true));

        // Kings
        board[0][4].setPiece(new King(false));
        board[7][4].setPiece(new King(true));
    }

    // Castling state tracking
    public void setKingMoved(boolean isWhite) {
        if (isWhite) whiteKingMoved = true;
        else blackKingMoved = true;
    }

    public void setRookMoved(boolean isWhite, boolean isQueenSide) {
        if (isWhite) {
            if (isQueenSide) whiteRookAMoved = true;
            else whiteRookHMoved = true;
        } else {
            if (isQueenSide) blackRookAMoved = true;
            else blackRookHMoved = true;
        }
    }

    public boolean hasKingMoved(boolean isWhite) {
        return isWhite ? whiteKingMoved : blackKingMoved;
    }

    public boolean hasRookMoved(boolean isWhite, boolean isQueenSide) {
        if (isWhite) {
            return isQueenSide ? whiteRookAMoved : whiteRookHMoved;
        } else {
            return isQueenSide ? blackRookAMoved : blackRookHMoved;
        }
    }

    // En passant tracking
    public void setLastPawnMove(int fromRow, int fromCol, int toRow, int toCol) {
        this.lastPawnMoveFromRow = fromRow;
        this.lastPawnMoveFromCol = fromCol;
        this.lastPawnMoveToRow = toRow;
        this.lastPawnMoveToCol = toCol;
    }

    public void clearLastPawnMove() {
        lastPawnMoveFromRow = -1;
        lastPawnMoveFromCol = -1;
        lastPawnMoveToRow = -1;
        lastPawnMoveToCol = -1;
    }

    public int getLastPawnMoveFromRow() { return lastPawnMoveFromRow; }
    public int getLastPawnMoveFromCol() { return lastPawnMoveFromCol; }
    public int getLastPawnMoveToRow() { return lastPawnMoveToRow; }
    public int getLastPawnMoveToCol() { return lastPawnMoveToCol; }

    public Board copy() {
        Board copy = new Board(false);

        copy.whiteKingMoved = whiteKingMoved;
        copy.blackKingMoved = blackKingMoved;
        copy.whiteRookAMoved = whiteRookAMoved;
        copy.whiteRookHMoved = whiteRookHMoved;
        copy.blackRookAMoved = blackRookAMoved;
        copy.blackRookHMoved = blackRookHMoved;
        copy.lastPawnMoveFromRow = lastPawnMoveFromRow;
        copy.lastPawnMoveFromCol = lastPawnMoveFromCol;
        copy.lastPawnMoveToRow = lastPawnMoveToRow;
        copy.lastPawnMoveToCol = lastPawnMoveToCol;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Tile sourceTile = board[row][col];
                if (sourceTile.isOccupied()) {
                    copy.board[row][col].setPiece(clonePiece(sourceTile.getPiece()));
                }
            }
        }

        return copy;
    }

    private Pieces.Piece clonePiece(Pieces.Piece piece) {
        if (piece instanceof Pawn) return new Pawn(piece.isWhite());
        if (piece instanceof Rook) return new Rook(piece.isWhite());
        if (piece instanceof Knight) return new Knight(piece.isWhite());
        if (piece instanceof Bishop) return new Bishop(piece.isWhite());
        if (piece instanceof Queen) return new Queen(piece.isWhite());
        if (piece instanceof King) return new King(piece.isWhite());
        throw new IllegalArgumentException("Unsupported piece type: " + piece.getClass().getSimpleName());
    }
}
