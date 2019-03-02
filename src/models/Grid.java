package models;

import static models.Constants.INFINITY;

public class Grid {
    private boolean hasObstacle;
    private boolean hasBeenExplored;
    private boolean isVirtualWall;
    private int row;
    private int col;
    // Cost for astar
    private int f;
    private int g;
    private int h;
    private Grid cameFrom;

    public Grid(int row, int col) {
        hasObstacle = false;
        hasBeenExplored = false;
        setIsVirtualWall(false);
        this.row = row;
        this.col = col;

        f = INFINITY;
        g = INFINITY;
        h = INFINITY;
        cameFrom = null;
    }

    public void reinitizalizeGridCost() {
        f = INFINITY;
        g = INFINITY;
        h = INFINITY;
        cameFrom = null;
    }

    public Grid getCameFrom() {
        return cameFrom;
    }

    public void setCameFrom(Grid grid) {
        this.cameFrom = grid;
    }

    public int getF() {
        return f;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
        f = this.g + h;
    }

    public void setH(int h) {
        this.h = h;
        f = this.h + g;
    }

    public boolean isVirtualWall() {
        return isVirtualWall;
    }

    public void setIsVirtualWall(boolean virtualWall) {
        isVirtualWall = virtualWall;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void toggleObstacle() {
        hasObstacle = (!hasObstacle);
    }

    public boolean hasObstacle() {
        return hasObstacle;
    }

    public void setHasObstacle(boolean hasObstacle)
    {
        this.hasObstacle = hasObstacle;
    }

    public boolean hasBeenExplored() {
        return hasBeenExplored;
    }

    public void setHasBeenExplored(boolean hasBeenExplored) {
        this.hasBeenExplored = hasBeenExplored;
    }

}
