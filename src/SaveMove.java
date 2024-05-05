public class SaveMove {
    int highestScore;
    int highestX;
    int highestY;
    int highestOrientation;
    int highestPieceIndex;

    public SaveMove(int highestScore, int highestX, int highestY, int highestOrientation, int highestPieceIndex) {
        this.highestScore = highestScore;
        this.highestX = highestX;
        this.highestY = highestY;
        this.highestOrientation = highestOrientation;
        this.highestPieceIndex = highestPieceIndex;
    }
    public int getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }

    public int getHighestX() {
        return highestX;
    }

    public void setHighestX(int highestX) {
        this.highestX = highestX;
    }

    public int getHighestY() {
        return highestY;
    }

    public void setHighestY(int highestY) {
        this.highestY = highestY;
    }

    public int getHighestOrientation() {
        return highestOrientation;
    }

    public void setHighestOrientation(int highestOrientation) {
        this.highestOrientation = highestOrientation;
    }

    public int getHighestPieceIndex() {
        return highestPieceIndex;
    }

    public void setHighestPieceIndex(int highestPieceIndex) {
        this.highestPieceIndex = highestPieceIndex;
    }
}
