package views;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.JPanel;
import static models.Constants.*;
import java.util.Queue;

import models.Arena;
import models.Grid;
import models.MyRobot;
import models.Sensor;

public class ArenaPanel extends JPanel {

    private MyRobot myRobot;
    private Arena arena;
    private Grid grid;

    Sensor[][] frontRightLeftSensors;

    public ArenaPanel(MyRobot myRobot, Arena arena) {
        this.arena = arena;
        this.myRobot = myRobot;

        // sense the environment based on the initialized location
        updateArenaBasedOnSensorReadings();

        setFocusable(true);
        requestFocus();
    }

    public void paintComponent(Graphics g) {
        // the order of the elements being painted matters
        fillGrids(g);
        displayPathTaken(g);
        displaySensorRange(g);

        drawGridBorder(g);
        drawArenaDivider(g);
        displayRobot(g, myRobot.getCurRow(), myRobot.getCurCol());
    }

    private void fillGrids(Graphics g) {
        for (int row = 0; row < ARENA_HEIGHT; row++) {
            for (int col = 0; col < ARENA_WIDTH; col++) {
                grid = arena.getGrid(row, col);

                if (Arena.isStartZone(row, col)) {
                    g.setColor(START_ZONE_COLOR);
                } else if (Arena.isGoalZone(row, col)) {
                    g.setColor(GOAL_ZONE_COLOR);
                } else if (!grid.hasBeenExplored()) {
                    g.setColor(UNEXPLORED_COLOR);
                } else if (grid.hasObstacle()) {
                    g.setColor(OBSTACLE_COLOR);
                } else {
                    g.setColor(EXPLORED_COLOR);
                }
                g.fillRect(col * GRID_SIZE, row * GRID_SIZE, GRID_SIZE, GRID_SIZE);
            }
        }
    }

    private void displaySensorRange(Graphics g) {
        int numOfSensibleGrid;
        int curRow;
        int curCol;
        frontRightLeftSensors = myRobot.getAllSensor();

        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(SENSOR_RANGE_COLOR);

        for (Sensor[] sensors: frontRightLeftSensors) {
            for (Sensor sensor: sensors) {
                numOfSensibleGrid = sensor.getSensorReading();
                if (sensor.getSensorReading() == 0) {
                    numOfSensibleGrid = sensor.getSensorRange();
                }
                for (int i = 1; i <= numOfSensibleGrid; i++) {
                    switch(sensor.getSensorOrientation()) {
                        case N:
                            curRow = sensor.getSensorAbsoluteRow() - i;
                            curCol = sensor.getSensorAbsoluteCol();
                            break;
                        case E:
                            curRow = sensor.getSensorAbsoluteRow();
                            curCol = sensor.getSensorAbsoluteCol() + i;
                            break;
                        case S:
                            curRow = sensor.getSensorAbsoluteRow() + i;
                            curCol = sensor.getSensorAbsoluteCol();
                            break;
                        default:
                            curRow = sensor.getSensorAbsoluteRow();
                            curCol = sensor.getSensorAbsoluteCol() - i;
                            break;
                    }
                    g2d.fillRect(
                            curCol * GRID_SIZE,
                            curRow * GRID_SIZE,
                            GRID_SIZE,
                            GRID_SIZE);
                }
            }
        }
    }

    private void displayPathTaken(Graphics g) {
        Queue<Grid> pathTaken = myRobot.getPathTaken();
        int curR;
        int curC;
        Graphics2D g2d = (Graphics2D)g;
        for(Grid q : pathTaken) {
            curR = q.getRow();
            curC = q.getCol();
            g2d.setColor(PATH_TAKEN_COLOR);
            g2d.fillRect(curC * GRID_SIZE, curR * GRID_SIZE, GRID_SIZE, GRID_SIZE);
        }
    }
    private void drawGridBorder(Graphics g) {
        for (int row = 0; row < ARENA_HEIGHT; row++) {
            for (int col = 0; col < ARENA_WIDTH; col++) {
                g.setColor(ARENA_GRID_LINE_COLOR);
                ((Graphics2D) g).setStroke(new java.awt.BasicStroke(2.0F));
                g.drawRect(col * GRID_SIZE, row * GRID_SIZE, GRID_SIZE, GRID_SIZE);
            }
        }
    }

    private void drawArenaDivider(Graphics g) {
        g.setColor(ARENA_DIVIDER_LINE_COLOR);
        g.fillRect(0, (ARENA_HEIGHT/2 - 1 ) * GRID_SIZE + GRID_SIZE - 2, ARENA_WIDTH * GRID_SIZE, ARENA_DIVIDER_LINE_THICKNESS);
    }

    private void displayRobot(Graphics g, int row, int col) {
        g.setColor(ROBOT_COLOR);
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        Ellipse2D robotImg = new Ellipse2D.Double((col - 1) * GRID_SIZE, (row - 1) * GRID_SIZE, 75.0D, 75.0D);
        g2.fill(robotImg);
        paintOrientationMarker(g2, myRobot);
    }

    private void paintOrientationMarker(Graphics2D g2, MyRobot myRobot) {
        int orientationMarkerRow = myRobot.getCurRow();
        int orientationMarkerCol = myRobot.getCurCol();
        if (myRobot.getCurOrientation() == Orientation.N) {
            orientationMarkerRow -= 1;
        } else if (myRobot.getCurOrientation() == Orientation.E) {
            orientationMarkerCol += 1;
        } else if (myRobot.getCurOrientation() == Orientation.S) {
            orientationMarkerRow += 1;
        } else {
            orientationMarkerCol -= 1;
        }
        Ellipse2D orientationMarker = new Ellipse2D.Double(
                orientationMarkerCol * GRID_SIZE + 5,
                orientationMarkerRow * GRID_SIZE + 5,
                15.0D,
                15.0D);

        g2.setColor(ORIENTATION_MARKER_COLOR);
        g2.fill(orientationMarker);
    }

    private void updateArenaBasedOnSensorReadings() {
        int numOfSensibleGrid;
        int curRow;
        int curCol;

        frontRightLeftSensors = myRobot.getAllSensor();

        for (Sensor[] sensors: frontRightLeftSensors) {
            for (Sensor sensor: sensors) {
                numOfSensibleGrid = sensor.getSensorReading();
                if (sensor.getSensorReading() == sensor.NO_OBSTACLE) {
                    numOfSensibleGrid = sensor.getSensorRange();
                }
                for (int i = 1; i <= numOfSensibleGrid; i++) {
                    switch(sensor.getSensorOrientation()) {
                        case N:
                            curRow = sensor.getSensorAbsoluteRow() - i;
                            curCol = sensor.getSensorAbsoluteCol();
                            break;
                        case E:
                            curRow = sensor.getSensorAbsoluteRow();
                            curCol = sensor.getSensorAbsoluteCol() + i;
                            break;
                        case S:
                            curRow = sensor.getSensorAbsoluteRow() + i;
                            curCol = sensor.getSensorAbsoluteCol();
                            break;
                        default:
                            curRow = sensor.getSensorAbsoluteRow();
                            curCol = sensor.getSensorAbsoluteCol() - i;
                            break;
                    }
                    if (Arena.isValidRowCol(curRow, curCol)) {
                        grid = arena.getGrid(curRow, curCol);
                        grid.setHasBeenExplored(true);
                        if (i == sensor.getSensorReading()) {
                            grid.setHasObstacle(true);
                        }
                    }
                }
            }
        }
    }
}