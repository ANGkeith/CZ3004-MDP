package models;

import static models.Constants.*;
public class Sensor
{
    private MyRobot myRobot;
    private Arena mockArena;
    Orientation robotOrientation;
    private int[] sensorPosition = new int[2];

    private int relativeRow;
    private int relativeCol;

    public Sensor(MyRobot myRobot, Arena mockArena, int relativeRow, int relativeCol)
    {
        this.myRobot = myRobot;
        this.relativeRow = relativeRow;
        this.relativeCol = relativeCol;
        this.mockArena = mockArena;
    }

    public int getSensorRow() {
        return getSensorPosition()[0];
    }

    public int getSensorCol() {
        return getSensorPosition()[1];
    }

    public int[] getSensorPosition() {
        robotOrientation = myRobot.getCurOrientation();
        switch (robotOrientation) {
            case N:
                sensorPosition[0] = (myRobot.getCurRow() + relativeRow);
                sensorPosition[1] = (myRobot.getCurCol() + relativeCol);
                break;
            case E:
                sensorPosition[0] = (myRobot.getCurRow() + relativeCol);
                sensorPosition[1] = (myRobot.getCurCol() - relativeRow);
                break;
            case S:
                sensorPosition[0] = (myRobot.getCurRow() - relativeRow);
                sensorPosition[1] = (myRobot.getCurCol() - relativeCol);
                break;
            case W:
                sensorPosition[0] = (myRobot.getCurRow() - relativeCol);
                sensorPosition[1] = (myRobot.getCurCol() + relativeRow);
        }

        return sensorPosition;
    }

    public boolean getReading() {
        if (Arena.isValidRowCol(getSensorRow(), getSensorCol())) {
            return mockArena.getGrid(getSensorPosition()[0], getSensorPosition()[1]).hasObstacle();
        }
        return true;
    }
}