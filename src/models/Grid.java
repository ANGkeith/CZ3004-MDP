package models;

import static models.Constants.*;

public class Grid {
    //  JunYaw changes start
//    private Grid sensedFrom;
    private int timesNotCalibrated;
    //  JunYaw changes end

    private boolean hasObstacle;
    private boolean hasBeenExplored;
    private boolean isVirtualWall;
    private int row;
    private int col;
    // Cost for astar
    private int f;
    private int g;
    private int h;
    private Orientation o;
    private Grid cameFrom;

    private boolean u;
    private boolean d;
    private boolean l;
    private boolean r;

    public Grid(int row, int col) {
        //  JunYaw changes start
        timesNotCalibrated = 100000;
        //  JunYaw changes end

        hasObstacle = false;
        hasBeenExplored = false;
        setIsVirtualWall(false);
        this.row = row;
        this.col = col;

        f = INFINITY;
        g = INFINITY;
        h = INFINITY;
        cameFrom = null;
        o = null;

        u = false;
        d = false;
        l = false;
        r = false;

    }

    public void resetGridCostAndCameFrom() {
        f = INFINITY;
        g = INFINITY;
        h = INFINITY;
        cameFrom = null;
    }

    //  JunYaw changes start
//    public Grid getSensedFrom() {
//        return this.sensedFrom;
//    }
//
//    public void setSensedFrom(Grid grid) {
//        this.sensedFrom = sensedFrom;
//    }

    public int getTimesNotCalibrated() {
        return this.timesNotCalibrated;
    }

    public void setTimesNotCalibrated(int timesNotCalibrated) {
        this.timesNotCalibrated = timesNotCalibrated;
    }
    //  JunYaw changes end

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

    public Orientation getO() {
        return o;
    }

    public void setO(Orientation o) {
        this.o = o;
    }

    public boolean isHasObstacle() {
        return hasObstacle;
    }

    public boolean isHasBeenExplored() {
        return hasBeenExplored;
    }

    public void setVirtualWall(boolean virtualWall) {
        isVirtualWall = virtualWall;
    }

    public boolean isU() {
        return u;
    }

    public void setU(boolean u) {
        this.u = u;
    }

    public boolean isD() {
        return d;
    }

    public void setD(boolean d) {
        this.d = d;
    }

    public boolean isL() {
        return l;
    }

    public void setL(boolean l) {
        this.l = l;
    }

    public boolean isR() {
        return r;
    }

    public void setR(boolean r) {
        this.r = r;
    }
}
