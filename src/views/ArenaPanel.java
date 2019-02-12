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

public class ArenaPanel extends JPanel implements ActionListener, java.awt.event.KeyListener
{
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
        /*
        try {
            FileReaderWriter fileReader = new FileReaderWriter(java.nio.file.FileSystems.getDefault().getPath(ARENA_DESCRIPTOR_PATH, new String[0]));
            mockArena.binStringToArena(fileReader.read());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        */
        liveArena = new Arena();
        myRobot = new MyRobot(curRobotRow, curRobotCol, orientation, mockArena);
        setFocusable(true);
        requestFocus();
        addKeyListener(this);
        t.start();
    }

    public void paintComponent(Graphics g)
    {
        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < 15; col++) {
                fillGrid(g, row, col);
                drawGridBorder(g, row, col);
            }
        }
        drawArenaDivider(g);
        displayRobot(g, myRobot.getCurRow(), myRobot.getCurCol());
    }


    private void drawGridBorder(Graphics g, int row, int col)
    {
        g.setColor(ARENA_GRID_LINE_COLOR);
        ((Graphics2D)g).setStroke(new java.awt.BasicStroke(2.0F));
        g.drawRect(col * 25, row * 25, 25, 25);
    }



    private void drawArenaDivider(Graphics g)
    {
        g.setColor(ARENA_DIVIDER_LINE_COLOR);
        g.fillRect(0, 248, 375, 4);
    }



    private void displayRobot(Graphics g, int row, int col)
    {
        g.setColor(Color.black);

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;


        Ellipse2D robotImg = new Ellipse2D.Double((col - 1) * 25, (row - 1) * 25, 75.0D, 75.0D);



        g2.fill(robotImg);

        paintOrientationMarker(g2, myRobot);

        updateLiveArenaGrids(myRobot.getFrontSensor());
        updateLiveArenaGrids(myRobot.getRightSensor());
        updateLiveArenaGrids(myRobot.getLeftSensor());
    }

    private void fillGrid(Graphics g, int row, int col)
    {
        grid = liveArena.getGrid(row, col);

        if (isStartZone(row, col)) {
            g.setColor(Color.GREEN);
        } else if (isGoalZone(row, col)) {
            g.setColor(Color.BLUE);
        }
        else if (!grid.hasBeenExplored()) {
            g.setColor(Color.BLACK);
        }
        else if (grid.hasObstacle()) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.white);
        }


        g.fillRect(col * 25, row * 25, 25, 25);






        Sensor[] frontSensors = myRobot.getFrontSensor();
        Sensor[] leftSensor = myRobot.getLeftSensor();
        Sensor[] rightSensor = myRobot.getRightSensor();

        Graphics2D g2d = (Graphics2D)g;
        paintSensor(g2d, frontSensors);
        paintSensor(g2d, leftSensor);
        paintSensor(g2d, rightSensor);
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

    private void paintSensor(Graphics2D g2d, Sensor[] sensors) {
        g2d.setColor(new Color(212, 255, 46, 1));
        for (int i = 0; i < sensors.length; i++) {
            g2d.fillRect(
                    sensors[i].getSensorCol() * GRID_SIZE,
                    sensors[i].getSensorRow() * GRID_SIZE,
                    25,
                    25);
        }
    }


    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    private boolean isGoalZone(int row, int col) {
        return (row < 3) && (col > 11);
    }

    private boolean isStartZone(int row, int col) {
        return (row > 16) && (col < 3);
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {
        curRobotCol = myRobot.getCurCol();
        curRobotRow = myRobot.getCurRow();
        curOrientation = myRobot.getCurOrientation();
        if (e.getKeyCode() == 38) {
            myRobot.move(My_Robot_Instruction.FORWARD);
        } else if (e.getKeyCode() == 39) {
            myRobot.move(My_Robot_Instruction.TURN_RIGHT);
        } else if (e.getKeyCode() == 37) {
            myRobot.move(My_Robot_Instruction.TURN_LEFT);
        }
        updateLiveArenaGrids(myRobot.getFrontSensor());
        updateLiveArenaGrids(myRobot.getRightSensor());
        updateLiveArenaGrids(myRobot.getLeftSensor());
    }

    public void keyPressed(KeyEvent e) {

    }

    private void updateLiveArenaGrids(Sensor[] sensors) {
        for (int i = 0; i < sensors.length; i++) {
            if (Arena.isValidRowCol(sensors[i].getSensorRow(), sensors[i].getSensorCol())) {
                curSensedGrid = liveArena.getGrid(sensors[i].getSensorRow(), sensors[i].getSensorCol());
                curSensedGrid.setHasBeenExplored(true);
                curSensedGrid.setHasObstacle(sensors[i].sense());
            }
        }
    }
}