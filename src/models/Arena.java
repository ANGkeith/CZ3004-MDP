package models;


public class Arena {
    private Grid[][] grids = new Grid[20][15];
    private MyRobot robot;

    public Arena() {
        for (int r = 0; r < 20; ++r) {
            for (int c = 0; c < 15; ++c) {
                this.grids[r][c] = new Grid();
                if (!this.isVirtualWall(r, c)) continue;
                this.grids[r][c].setIsVirtualWall(true);
            }
        }
    }

    private boolean isVirtualWall(int row, int col) {
        return row == 0 || row == 19 || col == 0 || col == 14;
    }

    public Grid getGrid(int row, int col) {
        return this.grids[row][col];
    }

    public String obstacleToString() {
        String descriptor = "";
        for (int r = 19; r > -1; --r) {
            for (int c = 0; c < 15; ++c) {
                if (this.grids[r][c].hasObstacle()) {
                    descriptor += "1";
                    continue;
                }
                descriptor += "0";
            }
        }
        return descriptor;
    }

    public void binStringToArena(String arenaDescriptor) {
        int curIndex = 0;
        for (int r = 19; r > -1; --r) {
            for (int c = 0; c < 15; ++c) {
                if (arenaDescriptor.charAt(curIndex) == '0') {
                    this.grids[r][c].setHasObstacle(false);
                } else if (arenaDescriptor.charAt(curIndex) == '1') {
                    this.grids[r][c].setHasObstacle(true);
                }
                ++curIndex;
            }
        }
    }

    public void resetObstacle() {
        for (int r = 0; r < 20; ++r) {
            for (int c = 0; c < 15; ++c) {
                this.grids[r][c].setHasObstacle(false);
            }
        }
    }

    public static boolean isValidRowCol(int row, int col) {
        return row < 20 && row >= 0 && col < 15 && col >= 0;
    }
}