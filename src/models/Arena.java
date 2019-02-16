package models;



import static models.Constants.*;

public class Arena {
    private Grid[][] grids = new Grid[ARENA_HEIGHT][ARENA_WIDTH];

    public Arena() {
        for (int r = 0; r < ARENA_HEIGHT; ++r) {
            for (int c = 0; c < ARENA_WIDTH; ++c) {
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
            for (int c = 0; c < ARENA_WIDTH; ++c) {
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
        for (int r = ARENA_HEIGHT - 1; r > -1; --r) {
            for (int c = 0; c < ARENA_WIDTH; ++c) {
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
        for (int r = 0; r < ARENA_HEIGHT; ++r) {
            for (int c = 0; c < ARENA_WIDTH; ++c) {
                this.grids[r][c].setHasObstacle(false);
            }
        }
    }


    public static boolean isGoalZone(int row, int col) {
        return (row < ZONE_SIZE) && (col > ARENA_WIDTH - 1 - ZONE_SIZE);
    }

    public static boolean isStartZone(int row, int col) {
        return (row > ARENA_HEIGHT - 1 - ZONE_SIZE) && (col < ZONE_SIZE);
    }

    public static boolean isValidRowCol(int row, int col) {
        return row < ARENA_HEIGHT && row >= 0 && col < ARENA_WIDTH && col >= 0;
    }
}