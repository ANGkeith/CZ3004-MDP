package models;

import static models.Constants.*;
public class Sensor
{
    private MyRobot myRobot;
    private Arena referenceArena;

    private int sensorRange;

    private int relativeRow;
    private int relativeCol;
    private Sensor_Position sensor_position;


    // The relative row and col is the relative position of the sensor w.r.t the center of the north-oriented robot
    public Sensor(MyRobot myRobot, Arena referenceArena, int relativeRow, int relativeCol, Sensor_Position sensor_position, int sensorRange) {
        this.myRobot = myRobot;
        this.relativeRow = relativeRow;
        this.relativeCol = relativeCol;
        this.referenceArena = referenceArena;
        this.sensor_position = sensor_position;
        this.sensorRange = sensorRange;

    }

    public Orientation getSensorOrientation() {

        switch (sensor_position) {
            case FRONT:
                return myRobot.getCurOrientation();
            case LEFT:
                switch (myRobot.getCurOrientation()) {
                    case N:
                        return Orientation.W;
                    case E:
                        return Orientation.N;
                    case S:
                        return Orientation.E;
                    case W:
                        return Orientation.S;
                }
            default:
                switch (myRobot.getCurOrientation()) {
                    case N:
                        return Orientation.E;
                    case E:
                        return Orientation.S;
                    case S:
                        return Orientation.W;
                    default:
                        return Orientation.N;
                }
        }
    }

    /*
        Method for simulating the readings by the sensor it returns the distance (in number of grids) of the obstacle
        from the sensor.

        If there are no obstacle detected within the range, a value of 0 is returned
    */
    public int getSimulatedSensorReading() {
        switch(getSensorOrientation()) {
            case N:
                for (int i = 1; i <= sensorRange; i++) {
                    if (checkForObstacleAgainstReferenceArena(getSensorAbsoluteRow() - i, getSensorAbsoluteCol())) {
                        return i;
                    }
                }
                break;
            case E:
                for (int i = 1; i <= sensorRange; i++) {
                    if (checkForObstacleAgainstReferenceArena(getSensorAbsoluteRow(), getSensorAbsoluteCol() + i)) {
                        return i;
                    }
                }
                break;
            case S:
                for (int i = 1; i <= sensorRange; i++) {
                    if (checkForObstacleAgainstReferenceArena(getSensorAbsoluteRow() + i, getSensorAbsoluteCol())) {
                        return i;
                    }
                }
                break;
            case W:
                for (int i = 1; i <= sensorRange; i++) {
                    if (checkForObstacleAgainstReferenceArena(getSensorAbsoluteRow(), getSensorAbsoluteCol() - 1)) {
                        return i;
                    }
                }
                break;
        }
        //
        return 0;
    }

    public boolean checkForObstacleAgainstReferenceArena(int row, int col) {
        if (Arena.isValidRowCol(row, col)) {
            return referenceArena.getGrid(row, col).hasObstacle();
        }
        return true;
    }

    public int getSensorAbsoluteRow() {
        switch(myRobot.getCurOrientation()) {
            case N:
                return myRobot.getCurRow() + relativeRow;
            case E:
                return myRobot.getCurRow() + relativeCol;
            case S:
                return myRobot.getCurRow() - relativeRow;
            case W:
                return myRobot.getCurRow() - relativeCol;
            default:
                return -1;
        }
    }

    public int getSensorAbsoluteCol() {
        switch(myRobot.getCurOrientation()) {
            case N:
                return myRobot.getCurCol() + relativeCol;
            case E:
                return myRobot.getCurCol() - relativeRow;
            case S:
                return myRobot.getCurCol() - relativeCol;
            case W:
                return myRobot.getCurCol() + relativeRow;
            default:
                return -1;
        }
    }

    public int getSensorRange() {
        return sensorRange;
    }
}