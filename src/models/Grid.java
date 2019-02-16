package models;

public class Grid {
    private boolean hasObstacle;
    private boolean hasBeenExplored;

    public Grid() {
        hasObstacle = false;
        hasBeenExplored = false;
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

}
