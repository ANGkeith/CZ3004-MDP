package models;

public class Grid {
    private boolean hasObstacle;
    private boolean hasBeenExplored;
    private boolean isVirtualWall;

    public Grid() {
        hasObstacle = false;
        hasBeenExplored = false;
        setIsVirtualWall(false);
    }

    public void toggleObstacle() {
        hasObstacle = (!hasObstacle);
    }

    public boolean hasObstacle() { return hasObstacle; }

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

    public boolean isVirtualWall() {
        return isVirtualWall;
    }

    public void setIsVirtualWall(boolean isVirtualWall) {
        this.isVirtualWall = isVirtualWall;
    }
}