// מחלקה פנימית המייצגת צובע-ציון
public class ColorScore implements Comparable<ColorScore> {
    private int color;
    private int score;
    public void setScore(int score) {
        this.score = score;
    }

    public ColorScore(int color, int score) {
        this.color = color;
        this.score = score;
    }

    public int getColor() {
        return color;
    }

    public int getScore() {
        return score;
    }
    @Override
    public int compareTo(ColorScore other) {
        // Compare ColorScore objects based on their scores
        return Integer.compare(this.score, other.score);
    }
}