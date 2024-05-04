import java.util.*;

public class Cell {
    private int x, y; // Coordinates on the board
    private int Color; // Color Of Cell
    private HashSet<Cell> neighbors;


    public Cell(int x, int y, int color, HashSet<Cell> neighbors) {
        this.x = x;
        this.y = y;
        Color = color;
        this.neighbors = neighbors;
    }

    private boolean isBorder() {
        if (x == 0 || x == 8) {
            return true;
        } else {
            if (y == 0) {
                return true;
            } else {
                return getRowLength(x) == y + 1;
            }
        }
    }

    public static int getRowLength(int x) {
        if (x < 4) {
            return 5 + x; // Rows 0 to 3 increase in length
        } else if (x < 5) {
            return 9; // Middle row has the maximum length
        } else {
            return 13 - x; // Rows 5 to 8 decrease in length
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getColor() {
        return Color;
    }

    public void setColor(int Color) {
        this.Color = Color;

    }

    public HashSet<Cell> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(HashSet<Cell> neighbors) {
        this.neighbors = neighbors;
    }

    public String formatCoordinate() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Cell cell = (Cell) obj;
        return x == cell.x && y == cell.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cell: ").append(formatCoordinate());
        sb.append(", Color: ").append(getColor());
        sb.append(" ");
        return sb.toString();
    }

}