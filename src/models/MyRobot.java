package models;

import static models.Constants.*;

public class MyRobot
{
    private int curRow;

    private int curCol;
    private Orientation curOrientation;
    private Arena mockArena;
    private Sensor[] frontSensor;
    private Sensor[] rightSensor;
    private Sensor[] leftSensor;
    private Sensor[][] allSensor = new Sensor[3][15];

    public MyRobot(int curRow, int curCol, Orientation curOrientation, Arena mockArena)
    {
        this.curRow = curRow;
        this.curCol = curCol;
        this.curOrientation = curOrientation;
        this.mockArena = mockArena;
        initSensor();
    }

    public void move(My_Robot_Instruction myRobotInstruction) {
        int temp = 0;
        if (myRobotInstruction == My_Robot_Instruction.FORWARD)
        {
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
                         0  1  2
                         3  4  5
         0  1  2  3  4  [ ][ ][ ] 0  1
                        [ ][ ][ ]
                        [ ][ ][ ] 2  3

        This shows the sensor's representation;
         */
        frontSensor = new Sensor[6];
        frontSensor[0] = new Sensor(this, mockArena, -3, -1);
        frontSensor[1] = new Sensor(this, mockArena, -3, 0);
        frontSensor[2] = new Sensor(this, mockArena, -3, 1);
        frontSensor[3] = new Sensor(this, mockArena, -2, -1);
        frontSensor[4] = new Sensor(this, mockArena, -2, 0);
        frontSensor[5] = new Sensor(this, mockArena, -2, 1);

        rightSensor = new Sensor[4];
        rightSensor[0] = new Sensor(this, mockArena, -1, 2);
        rightSensor[1] = new Sensor(this, mockArena, -1, 3);
        rightSensor[2] = new Sensor(this, mockArena, 1, 2);
        rightSensor[3] = new Sensor(this, mockArena, 1, 3);

        leftSensor = new Sensor[5];
        leftSensor[0] = new Sensor(this, mockArena, -1, -6);
        leftSensor[1] = new Sensor(this, mockArena, -1, -5);
        leftSensor[2] = new Sensor(this, mockArena, -1, -4);
        leftSensor[3] = new Sensor(this, mockArena, -1, -3);
        leftSensor[4] = new Sensor(this, mockArena, -1, -2);
    }

    public boolean hasObstacleRightInFront() {
        for (int i = 3; i < 6; i++) {
            if (frontSensor[i].getReading()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasObstacleRightOnTheRight() {
        return frontSensor[0].getReading() || frontSensor[1].getReading();
    }

    public int getCurCol() {
        return curCol;
    }

    public void setCurCol(int curCol) {
        if ((curCol != 0) && (curCol != 14)) {
            this.curCol = curCol;
        }
    }

    public int getCurRow() {
        return curRow;
    }

    public void setCurRow(int curRow) {
        if ((curRow != 0) && (curRow != 19)) {
            this.curRow = curRow;
        }
    }

    public Orientation getCurOrientation() {
        return curOrientation;
    }

    public void setCurOrientation(Orientation curOrientation) {
        this.curOrientation = curOrientation;
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
}