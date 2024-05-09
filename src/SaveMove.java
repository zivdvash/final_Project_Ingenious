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

    public int getHighestOrientation() {
        return highestOrientation;
    }


}
