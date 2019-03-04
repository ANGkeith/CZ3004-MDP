package models;

import controllers.SimulatorController;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import conn.TCPConn;

import static models.Constants.*;

public class MyRobot {
    public static final String UPDATEGUI = "updateGui";

    private int curRow;
    private int curCol;
    private int startRow = DEFAULT_START_ROW;
    private int startCol = DEFAULT_START_COL;
    private Orientation startOrientation = DEFAULT_START_ORIENTATION;
    private int temp;
    private Orientation curOrientation;
    private Arena referenceArena;
    private Arena arena;
    private double explorationCoverageLimit = DEFAULT_COVERAGE_LIMIT;
    private int explorationTimeLimit = DEFAULT_TIME_LIMIT;
    private Sensor[] frontSensor;
    private Sensor[] rightSensor;
    private Sensor[] leftSensor;
    private Sensor[][] allSensor;
    private double forwardSpeed;
    private double turningSpeed;
    private boolean hasFoundGoalZoneFlag;
    private Queue<Grid> pathTaken;
    public PropertyChangeSupport pcs = new PropertyChangeSupport(this);
   public SimulatorController controller;

    public MyRobot(Arena arena, Arena referenceArena) {
        this.arena = arena;
        this.curRow = startRow;
        this.curCol = startCol;
        this.curOrientation = startOrientation;
        this.referenceArena = referenceArena;
        this.forwardSpeed = 0;
        this.turningSpeed = 0;
        this.hasFoundGoalZoneFlag = false;
        this.pathTaken = new ConcurrentLinkedQueue<>();
        initSensor();
    }

    public void resetPathTaken() {
        pathTaken.clear();
    }

    public boolean possibleStartingPosition(int row, int col) {
        Grid curGrid;
        for (int r = -1; r < 2; r++) {
            for (int c = -1; c < 2; c++) {
                curGrid = referenceArena.getGrid(row + r, col + c);
                if (curGrid == null) {
                    return false;
                } else if(curGrid.hasObstacle()) {
                    return false;
                }

            }
        }
        return true;
    }

    public void forward() {

    	
    	if (!hasObstacleRightInFront()) {
        	
    		SimulatorController.numFwd++;                   
                
            if (curOrientation == Orientation.N) {
                temp = curRow - 1;
                setCurRow(temp);
            } else if (curOrientation == Orientation.E) {
                temp = curCol + 1;
                setCurCol(temp);
            } else if (curOrientation == Orientation.S) {
                temp = curRow + 1;
                setCurRow(temp);
            } else if (curOrientation == Orientation.W) {
                temp = curCol - 1;
                setCurCol(temp);
            }
        }
    }

    public void turnRight() {
    
    
        SimulatorController.numTurn++;
        if (curOrientation == Orientation.N) {
            setCurOrientation(Orientation.E);
        } else if (curOrientation == Orientation.E) {
            setCurOrientation(Orientation.S);
        } else if (curOrientation == Orientation.S) {
            setCurOrientation(Orientation.W);
        } else if (curOrientation == Orientation.W) {
            setCurOrientation(Orientation.N);
        }
    }

    public void turnLeft() {
    	
//    	SimulatorController controller = SimulatorController.getInstance();
    	
        SimulatorController.numTurn++;
        if (curOrientation == Orientation.N) {
            setCurOrientation(Orientation.W);
        } else if (curOrientation == Orientation.E) {
            setCurOrientation(Orientation.N);
        } else if (curOrientation == Orientation.S) {
            setCurOrientation(Orientation.E);
        } else if (curOrientation == Orientation.W) {
            setCurOrientation(Orientation.S);
        }
    }

    public void setToStart() {
        setCurRow(startRow);
        setCurCol(startCol);
        setCurOrientation(startOrientation);
    }

    private void initSensor() {
        /*
            Front Sensor:
                         x  x  x
                         x  x  x
                        [0][1][2]
                        [ ][ ][ ]
                        [ ][ ][ ]

            Right Sensor:
                        [ ][ ][0]x x
                        [ ][ ][ ]
                        [ ][ ][1]x x

            Left Sensor:
               x x x x x[0][ ][ ]
                        [ ][ ][ ]
                        [ ][ ][ ]

         */

        frontSensor = new Sensor[3];
        rightSensor = new Sensor[2];
        leftSensor = new Sensor[1];

        frontSensor[0] = new Sensor(this, referenceArena, -1, -1, Sensor_Position.FRONT, 2);
        frontSensor[1] = new Sensor(this, referenceArena, -1, 0, Sensor_Position.FRONT, 2);
        frontSensor[2] = new Sensor(this, referenceArena, -1, 1, Sensor_Position.FRONT, 2);

        rightSensor[0] = new Sensor(this, referenceArena, -1, 1, Sensor_Position.RIGHT, 2);
        rightSensor[1] = new Sensor(this, referenceArena, 1, 1, Sensor_Position.RIGHT, 2);

        leftSensor[0] = new Sensor(this, referenceArena, -1, -1, Sensor_Position.LEFT, 5);

        allSensor = new Sensor[3][3];
    }

    public boolean hasObstacleToItsImmediateRight() {
        for (int i = 0; i < rightSensor.length; i++) {
            if (rightSensor[i].getSimulatedSensorReading() == 1) {
                return true;
            }
        }
        return false;
    }

    public boolean hasObstacleRightInFront() {
        for (int i = 0; i < frontSensor.length; i++) {
            if (frontSensor[i].getSimulatedSensorReading() == 1) {
                return true;
            }
        }
        return false;
    }

    public boolean hasObstacleToItsImmediateLeft() {
        for (int i = 0; i < leftSensor.length; i++) {
            if (leftSensor[i].getSimulatedSensorReading() == 1) {
                return true;
            }
        }
        return false;
    }

    public boolean isAtGoalZone() {
        return (getCurRow() == GOAL_ZONE_ROW && getCurCol() == GOAL_ZONE_COL);
    }

    public boolean isAtStartZone() {
        return (getCurRow() == START_ZONE_ROW && getCurCol() == START_ZONE_COL);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    private boolean hasChangeInValue(int oldValue, int newValue) {
        return oldValue != newValue;
    }

    public boolean getHasFoundGoalZoneFlag() {
        return hasFoundGoalZoneFlag;
    }

    public void setHasFoundGoalZoneFlag(boolean hasFoundGoalZoneFlag) {
        this.hasFoundGoalZoneFlag = hasFoundGoalZoneFlag;
    }

    public Sensor[] getFrontSensor() {
        return frontSensor;
    }

    public Sensor[] getRightSensor() {
        return rightSensor;
    }
    public Sensor[] getLeftSensor() {
        return leftSensor;
    }

    public Sensor[][] getAllSensor() {
        allSensor[0] = getFrontSensor();
        allSensor[1] = getRightSensor();
        allSensor[2] = getLeftSensor();
        return allSensor;
    }


    public boolean rightBlindSpotHasObstacle() {
        int curRow = getCurRow();
        int curCol = getCurCol();
        Orientation curOrientation = getCurOrientation();

        int blindSpotRow;
        int blindSpotCol;
        Grid blindSpotGrid;

        switch(curOrientation) {
            case N:
                blindSpotCol = curCol + 2;
                blindSpotRow = curRow;
                break;
            case E:
                blindSpotCol = curCol;
                blindSpotRow = curRow + 2;
                break;
            case S:
                blindSpotCol = curCol - 2;
                blindSpotRow = curRow;
                break;
            default:
                blindSpotCol = curCol;
                blindSpotRow = curRow - 2;
                break;
        }

        blindSpotGrid = arena.getGrid(blindSpotRow, blindSpotCol);
        if (blindSpotGrid != null) {
            if (blindSpotGrid.hasBeenExplored()) {
                return blindSpotGrid.hasObstacle();
            } else {
                return false;
            }
        }
        return true;
    }

    public int getCurCol() {
        return curCol;
    }

    public void setCurCol(int curCol) {
        int oldValue = this.curCol;
        if ((curCol != 0) && (curCol != 14) && hasChangeInValue(this.curCol, curCol)) {
            this.curCol = curCol;
            pcs.firePropertyChange(UPDATEGUI, oldValue, curCol);
        }
    }

    public int getCurRow() {
        return curRow;
    }

    public void setCurRow(int curRow) {
        int oldValue = this.curRow;
        if ((curRow != 0) && (curRow != 19) && hasChangeInValue(this.curRow, curRow)) {
            this.curRow = curRow;
            pcs.firePropertyChange(UPDATEGUI, oldValue, curRow);
        }
    }

    public Orientation getCurOrientation() {
        return curOrientation;
    }

    public void setCurOrientation(Orientation curOrientation) {
        Orientation oldValue = this.curOrientation;
        this.curOrientation = curOrientation;
        pcs.firePropertyChange(UPDATEGUI, oldValue, curOrientation);
    }

    public double getForwardSpeed() {
        return forwardSpeed;
    }

    public void setForwardSpeed(double forwardSpeed) {
        this.forwardSpeed = forwardSpeed;
    }

    public double getTurningSpeed() {
        return turningSpeed;
    }

    public void setTurningSpeed(double turningSpeed) {
        this.turningSpeed = turningSpeed;
    }

    public Arena getArena() {
        return arena;
    }

    public double getExplorationCoverageLimit() {
        return explorationCoverageLimit;
    }

    public void setExplorationCoverageLimit(double explorationCoverageLimit) {
        this.explorationCoverageLimit = explorationCoverageLimit;
    }

    public int getExplorationTimeLimitInSeconds() {
        return explorationTimeLimit;
    }

    public String getExplorationTimeLimitFormatted() {
        int secs = explorationTimeLimit % 60;
        int mins = explorationTimeLimit / 60;
        return mins + " : " + secs;
    }

    public void setExplorationTimeLimit(int explorationTimeLimit) {
        this.explorationTimeLimit = explorationTimeLimit;
    }

    public void addCurGridToPathTaken() {
        pathTaken.add(getArena().getGrid(curRow, curCol));
    }

    public Queue<Grid> getPathTaken() {
        return pathTaken;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }

    public Orientation getStartOrientation() {
        return startOrientation;
    }

    public void setStartOrientation(Orientation startOrientation) {
        this.startOrientation = startOrientation;
    }
}