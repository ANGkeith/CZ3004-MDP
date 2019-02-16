package views;

import utils.FileReaderWriter;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import javax.swing.JPanel;
import javax.swing.Timer;
import models.Arena;
import static models.Constants.*;
import models.Grid;
import models.MyRobot;
import models.Sensor;

public class ArenaPanel extends JPanel implements ActionListener, java.awt.event.KeyListener {
    Timer t = new Timer(500, this);
    int curRobotCol;
    int curRobotRow;
    Orientation curOrientation;
    private Grid grid;
    private MyRobot myRobot;
    private Arena liveArena;
    private Arena mockArena;
    private Grid curSensedGrid;

    public ArenaPanel(int curRobotRow, int curRobotCol, Orientation orientation) {
        mockArena = new Arena();
        try {
            FileReaderWriter fileReader = new FileReaderWriter(java.nio.file.FileSystems.getDefault().getPath(ARENA_DESCRIPTOR_PATH, new String[0]));
            mockArena.binStringToArena(fileReader.read());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        liveArena = new Arena();
        myRobot = new MyRobot(curRobotRow, curRobotCol, orientation, mockArena);
        setFocusable(true);
        requestFocus();
        addKeyListener(this);
        t.start();
    }

    public void paintComponent(Graphics g) {
        // the order of the elements being painted matters
        fillGrids(g);
        displaySensorRange(g);

        drawGridBorder(g);
        drawArenaDivider(g);
        displayRobot(g, myRobot.getCurRow(), myRobot.getCurCol());
    }

    private void fillGrids(Graphics g) {
        for (int row = 0; row < ARENA_HEIGHT; row++) {
            for (int col = 0; col < ARENA_WIDTH; col++) {
                grid = liveArena.getGrid(row, col);

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
        Sensor[][] allSensor = myRobot.getAllSensor();
        Graphics2D g2d = (Graphics2D)g;

        g2d.setColor(SENSOR_RANGE_COLOR);
        for (Sensor[] groupSensor: allSensor) {
            for (Sensor sensor: groupSensor) {
                g2d.fillRect(
                        sensor.getSensorCol() * GRID_SIZE,
                        sensor.getSensorRow() * GRID_SIZE,
                        GRID_SIZE,
                        GRID_SIZE);
            }
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

        getRobotSensorReadings(myRobot.getFrontSensor());
        getRobotSensorReadings(myRobot.getRightSensor());
        getRobotSensorReadings(myRobot.getLeftSensor());
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

    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public void keyReleased(KeyEvent e) {
        curRobotCol = myRobot.getCurCol();
        curRobotRow = myRobot.getCurRow();
        curOrientation = myRobot.getCurOrientation();
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            myRobot.move(My_Robot_Instruction.FORWARD);
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            myRobot.move(My_Robot_Instruction.TURN_RIGHT);
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            myRobot.move(My_Robot_Instruction.TURN_LEFT);
        }
        getRobotSensorReadings(myRobot.getFrontSensor());
        getRobotSensorReadings(myRobot.getRightSensor());
        getRobotSensorReadings(myRobot.getLeftSensor());
    }

    private void getRobotSensorReadings(Sensor[] sensors) {
        for (int i = 0; i < sensors.length; i++) {
            if (Arena.isValidRowCol(sensors[i].getSensorRow(), sensors[i].getSensorCol())) {
                curSensedGrid = liveArena.getGrid(sensors[i].getSensorRow(), sensors[i].getSensorCol());
                curSensedGrid.setHasBeenExplored(true);
                curSensedGrid.setHasObstacle(sensors[i].getReading());
            }
        }
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

}