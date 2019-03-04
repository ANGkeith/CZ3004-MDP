package models;

public class Result implements Comparable<Result>{
    private int row;
    private int col;
    private Constants.Orientation orientation;
    private int numFwd;
    private int numTurn;
    private int totalMove;

    public Result(int row, int col, Constants.Orientation orientation, int numFwd, int numTurn, int totalMove) {
        this.row = row;
        this.col = col;
        this.orientation = orientation;
        this.numFwd = numFwd;
        this.numTurn = numTurn;
        this.totalMove = totalMove;
    }

    public String toString() {
        return row + ", " + col + " " + orientation + " Forward: " + numFwd + " Turn: " + numTurn + " Total: " + totalMove;
    }

    @Override
    public int compareTo(Result r) {
        if (this.totalMove > r.totalMove) {
            return 1;
        }
        return -1;
    }
}
