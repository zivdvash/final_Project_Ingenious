public class MoveData {
    private int score;
    private Piece piece;
    private Cell cell;
    private int orientation;

    public MoveData(int score, Piece piece, Cell cell, int orientation) {
        this.score = score;
        this.piece = piece;
        this.cell = cell;
        this.orientation = orientation;
    }

    public int getScore() {
        return score;
    }

    public Piece getPiece() {
        return piece;
    }

    public Cell getCell() {
        return cell;
    }

    public int getOrientation() {
        return orientation;
    }

}
