package utils;

import controllers.SimulatorController;
import models.Arena;
import models.Grid;
import models.MyRobot;

import java.util.ArrayList;
import java.util.Stack;

import static models.Constants.*;

public class FastestPathAlgorithm {

    private MyRobot myRobot;
    private SimulatorController sim;

    // Grids that still need to be evaluated
    private ArrayList<Grid> openSet;
    // Grids that are already evaluated
    private ArrayList<Grid> closedSet;
    Stack<Grid> path = new Stack();
    private int i;

    public FastestPathAlgorithm(MyRobot myRobot, SimulatorController sim) {
        this.myRobot = myRobot;
        this.sim = sim;
    }

    public String goTo(int fromRow, int fromCol, Orientation fromOri, int toRow, int toCol) {
        String instructions = "";


        // Path from starting zone to waypoint
        myRobot.getArena().getGrid(fromRow, fromCol).setO(fromOri);
        buildAStarTree(fromRow, fromCol, toRow, toCol);
        path = reconstruct_path(toRow, toCol);
        instructions += getRobotInstructions(path);
        myRobot.getArena().resetGridCostAndCameFrom();
        return instructions;
    }



    public String generateInstructionsForFastestPath(Orientation startingOrientation) {
        String instructions = "";


        // Path from starting zone to waypoint
        myRobot.getArena().getGrid(DEFAULT_START_ROW, DEFAULT_START_COL).setO(startingOrientation);
    	buildAStarTree(DEFAULT_START_ROW, DEFAULT_START_COL, myRobot.getWayPointRow(), myRobot.getWayPointCol());
        path = reconstruct_path(myRobot.getWayPointRow(), myRobot.getWayPointCol());
        instructions += getRobotInstructions(path);

        myRobot.getArena().resetGridCostAndCameFrom();

        // Path from waypoint to goal zone
        buildAStarTree(myRobot.getWayPointRow(), myRobot.getWayPointCol(), GOAL_ZONE_ROW, GOAL_ZONE_COL);
        path = reconstruct_path(GOAL_ZONE_ROW, GOAL_ZONE_COL);
        instructions += getRobotInstructions(path);

        return instructions;
    }

    public String getRobotInstructions(Stack<Grid> s) {
        Stack<Grid> path = (Stack)s.clone();
        Grid targetGrid;
        Orientation orientationNeeded;

        String instructions = "";
        Grid prevGrid = path.pop();
        while (!path.empty()) {
            targetGrid = path.pop();
            orientationNeeded = getRespectiveOrientationToTarget(prevGrid.getRow(), prevGrid.getCol(), targetGrid.getRow(), targetGrid.getCol());
            instructions += instructionToTurnToTarget(prevGrid.getO(), orientationNeeded);
            instructions += "W";
            prevGrid = targetGrid;
        }
        return instructions;
    }


    private void buildAStarTree(int startingRow, int startingCol, int targetRow, int targetCol) {
        Grid startingGrid = myRobot.getArena().getGrid(startingRow, startingCol);
        Grid targetGrid = myRobot.getArena().getGrid(targetRow, targetCol);

        closedSet = new ArrayList<>();
        openSet = new ArrayList<>();
        openSet.add(startingGrid);

        startingGrid.setG(0);
        startingGrid.setH(calculateHeuristic(startingGrid, targetGrid ));

        boolean searchCompletedFlag = false;
        Grid curGrid;
        ArrayList<Grid> curNeighbouringGridArrList;
        
        while (openSet.size() > 0 && !searchCompletedFlag) {
            int indexOfNodeWithLowestF;

            indexOfNodeWithLowestF = pickLowestF(openSet);

            curGrid = openSet.get(indexOfNodeWithLowestF);
            
            openSet.remove(curGrid);
            closedSet.add(curGrid);

            curNeighbouringGridArrList = getNeighbouringGrids(curGrid.getRow(), curGrid.getCol());

            Grid curNeighbour;
            int tempG;
            Orientation tempO;
            for (i = 0; i < curNeighbouringGridArrList.size(); i++) {
                curNeighbour = curNeighbouringGridArrList.get(i);
                if (!closedSet.contains(curNeighbour)) {
                    tempO = getRespectiveOrientationToTarget(curGrid.getRow(), curGrid.getCol(),
                            curNeighbour.getRow(), curNeighbour.getCol());
                    tempG = curGrid.getG() + calculateG(curGrid.getO(), tempO);
                    if (openSet.contains(curNeighbour)) {
                        if (tempG < curNeighbour.getG()) {
                            curNeighbour.setG(tempG);
                            curNeighbour.setO(tempO);
                            curNeighbour.setH(calculateHeuristic(curNeighbour, targetGrid));
                            curNeighbour.setCameFrom(curGrid);
                        }
                    } else {
                        curNeighbour.setG(tempG);
                        curNeighbour.setO(tempO);
                        curNeighbour.setH(calculateHeuristic(curNeighbour, targetGrid));
                        curNeighbour.setCameFrom(curGrid);
                        openSet.add(curNeighbour);
                    }
                }
            }
        }
    }
    
    private int pickLowestF(ArrayList<Grid> openSet) {
        int curLowest = 0;
        for (i = 0; i < openSet.size(); i++) {
            if (openSet.get(i).getF() < openSet.get(curLowest).getF()) {
                curLowest = i;
            }
        }
        return curLowest;
    }

    private int calculateG(Orientation curOrientation, Orientation respectiveOrientation) {
            return MOVE_COST + getNumberOfTurnRequired(curOrientation, respectiveOrientation) * TURN_COST;
    }

    private int getNumberOfTurnRequired(Orientation curOrientation, Orientation respectiveOrientation) {
        int numOfTurn = Math.abs(curOrientation.ordinal() - respectiveOrientation.ordinal());
        return numOfTurn % 2;
    }

    private ArrayList<Grid> getNeighbouringGrids(int row, int col) {
        ArrayList<Grid> neighbourArrList = new ArrayList<>();
        // northNeighbour
        if (canBeVisited(row - 1, col)) {
            neighbourArrList.add(myRobot.getArena().getGrid(row - 1, col));
        }
        // SouthNeighbour
        if (canBeVisited(row + 1, col)) {
            neighbourArrList.add(myRobot.getArena().getGrid(row + 1, col));
        }
        // WestNeighbour
        if (canBeVisited(row, col - 1)) {
            neighbourArrList.add(myRobot.getArena().getGrid(row, col - 1));
        }
        // EastNeighbour
        if (canBeVisited(row, col + 1)) {
            neighbourArrList.add(myRobot.getArena().getGrid(row, col + 1));
        }
        return neighbourArrList;
    }

    /*
     * Used for checking whether the surround 3 by 3 grid can be visited.
     * It can be visited when all of the grid does not have obstacle and has been explored
    */
    private boolean canBeVisited(int centerR, int centerC) {
        Grid curGrid;
        for (int r = -1; r < 2; r++) {
            for (int c = -1; c < 2; c++) {
                curGrid = myRobot.getArena().getGrid(centerR + r, centerC + c);
                if (curGrid != null) {
                    if (!curGrid.hasBeenExplored() || curGrid.hasObstacle()) {
                        return false;
                    }
                    if (r == 0 && c == 0) {
                        if (curGrid.isVirtualWall()) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private int calculateHeuristic(Grid startingGrid, Grid targetGrid) {
    	int minNumOfGridAwayFromGoal;

    	minNumOfGridAwayFromGoal = Math.abs(targetGrid.getCol() - startingGrid.getCol()) +
                Math.abs(targetGrid.getRow() - startingGrid.getRow());

        if (gridNotInSameAxisAsGoal(startingGrid, targetGrid)) {
            return minNumOfGridAwayFromGoal * MOVE_COST + TURN_COST;
        }
        return minNumOfGridAwayFromGoal * MOVE_COST;
    }

    private boolean gridNotInSameAxisAsGoal(Grid startingGrid, Grid targetGrid) {
    		return targetGrid.getCol() - startingGrid.getCol() != 0 || targetGrid.getRow() - startingGrid.getRow() != 0;
    }

    private Stack<Grid> reconstruct_path(int targetRow, int targetCol) {
        Grid targetGrid = myRobot.getArena().getGrid(targetRow, targetCol);
        Grid prevGrid;

        Stack<Grid> path = new Stack();
        
        path.push(targetGrid);
        while (targetGrid.getCameFrom() != null) {
            prevGrid = targetGrid.getCameFrom();
            path.push(prevGrid);
            targetGrid = targetGrid.getCameFrom();
        }
        return path;
    }

    private Orientation getRespectiveOrientationToTarget(int curR, int curC, int targetR, int targetC) {
        if (curR == targetR && targetC > curC) {
            return Orientation.E;
        } else if (curR == targetR && targetC < curC) {
            return Orientation.W;
        } else if (curC == targetC && targetR > curR) {
            return Orientation.S;
        } else if (curC == targetC && targetR < curR) {
            return Orientation.N;
        }
        return null;
    }

    // turn myRobot to targetOrientation
    public void turnRobot(Orientation targetOrientation) throws Exception {
        Orientation curOrientation = myRobot.getCurOrientation();

        int modulus;
        modulus = (targetOrientation.ordinal() - curOrientation.ordinal()) % 4;
        if (modulus < 0) {
            modulus += 4;
        }

        if (targetOrientation == curOrientation) {
            return;
        } else if (modulus == 1) {
            sim.right();
        } else if (modulus == 3) {
            sim.left();
        } else {
            sim.right();
            sim.right();
        }
        myRobot.pcs.firePropertyChange(MyRobot.UPDATE_GUI_BASED_ON_SENSOR, null, null);
    }

    private String instructionToTurnToTarget(Orientation curOrientation, Orientation targetOrientation) {

        int modulus;
        modulus = (targetOrientation.ordinal() - curOrientation.ordinal()) % 4;
        if (modulus < 0) {
            modulus += 4;
        }

        if (targetOrientation == curOrientation) {
            return "";
        } else if (modulus == 1) {
            return "D";
        } else if (modulus == 3) {
            return "A";
        } else {
            return "DD";
        }
    }

    private String[] parseInputToRowColArr(String s) {
        return s.split("\\s*,\\s*");
    }

    public Orientation getMostOptimalStartingPosition(String instructionN, String instructionE) {
        int nCost = calculateCostFromInstructions(instructionN);
        int eCost = calculateCostFromInstructions(instructionE);

        if (nCost < eCost) {
            return Orientation.N;
        }
        return Orientation.E;
    }

    private int calculateCostFromInstructions(String instructions) {
        int cost = 0;
        for (int i = 0; i < instructions.length(); i ++) {
            if (instructions.charAt(i) == 'W') {
                cost += MOVE_COST;
            } else if (instructions.charAt(i) == 'A') {
                cost += TURN_COST;
            } else if (instructions.charAt(i) == 'D') {
                cost += TURN_COST;
            } else {
                System.out.println("Something wrong with calculateCostFromInstructions");
            }
        }
        return cost;
    }
}
