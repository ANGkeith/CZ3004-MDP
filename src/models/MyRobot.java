package models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import static models.Constants.*;

public class MyRobot {
    public static final String UPDATEGUI = "updateGui";

    private int curRow;
    private int curCol;
    private Orientation curOrientation;
    private Arena referenceArena;
    private Sensor[] frontSensor;
    private Sensor[] rightSensor;
    private Sensor[] leftSensor;
    private Sensor[][] allSensor;
    private double forwardSpeed;
    private double turningSpeed;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public MyRobot(int curRow, int curCol, Orientation curOrientation, Arena referenceArena) {
        this.curRow = curRow;
        this.curCol = curCol;
        this.curOrientation = curOrientation;
        this.referenceArena = referenceArena;
        this.forwardSpeed = 1.0;
        this.turningSpeed = 0.5;
        initSensor();
    }

    public void move(My_Robot_Instruction myRobotInstruction) {
        int temp;
        if (myRobotInstruction == My_Robot_Instruction.FORWARD)
        {
            if (true) {
                if (!hasObstacleRightInFront()) {
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
        } else if (myRobotInstruction == My_Robot_Instruction.TURN_RIGHT) {
            if (curOrientation == Orientation.N) {
                setCurOrientation(Orientation.E);
            } else if (curOrientation == Orientation.E) {
                setCurOrientation(Orientation.S);
            } else if (curOrientation == Orientation.S) {
                setCurOrientation(Orientation.W);
            } else if (curOrientation == Orientation.W) {
                setCurOrientation(Orientation.N);
            }
        } else if (myRobotInstruction == My_Robot_Instruction.TURN_LEFT) {
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
    }


    private void initSensor()
    {
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

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    private boolean hasChangeInValue(int oldValue, int newValue) {
        return oldValue != newValue;
    }
}