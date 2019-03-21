package controllers;

import models.*;
import views.ArenaPanel;
import views.CenterPanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ArenaPanelController  implements PropertyChangeListener, KeyListener {

    private ArenaPanel arenaPanel;
    private CenterPanel centerPanel;
    private MyRobot myRobot;
    private Arena arena;
    private String coverage;

    public ArenaPanelController(ArenaPanel arenaPanel, CenterPanel centerPanel, MyRobot myRobot) {
        this.myRobot = myRobot;
        this.arenaPanel = arenaPanel;
        this.arena = myRobot.getArena();
        this.centerPanel = centerPanel;
        myRobot.addPropertyChangeListener(this);

        arenaPanel.addKeyListener(this);

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (MyRobot.REPAINT.equals(evt.getPropertyName())) {
            arenaPanel.repaint();
        }
        if (MyRobot.UPDATE_GUI_BASED_ON_SENSOR.equals(evt.getPropertyName())) {
            updateArenaBasedOnSensorReadings();

            coverage = String.format("%.2f", arena.getCoveragePercentage());
            centerPanel.getStatusLbls()[1].setText(centerPanel.statusPrefixedLbls[1] + coverage);
            centerPanel.getStatusLbls()[2].setText(centerPanel.statusPrefixedLbls[2] + SimulatorController.numFwd);
            centerPanel.getStatusLbls()[3].setText(centerPanel.statusPrefixedLbls[3] + SimulatorController.numTurn);

            arenaPanel.revalidate();
            arenaPanel.repaint();
        }
    }

    private void updateArenaBasedOnSensorReadings() {
        int numOfSensibleGrid;
        int curRow;
        int curCol;
        Grid curGrid;
        Sensor[][] frontRightLeftSensors;
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
                        case W:
                            curRow = sensor.getSensorAbsoluteRow();
                            curCol = sensor.getSensorAbsoluteCol() - i;
                            break;
                        default:
                            curCol = 1;
                            curRow = 1;
                            System.out.println("error at updateArenaBasedOnSensorReadings");
                            break;
                    }
                    curGrid = arena.getGrid(curRow, curCol);
                    if (curGrid != null && !Arena.isStartZone(curRow, curCol) && !Arena.isGoalZone(curRow, curCol)) {
                        if (i == sensor.getSensorReading()) {
                            if (curGrid.isHasBeenExplored() && !curGrid.isHasObstacle()) {
                                // TODO handle this case
                                System.out.println("Previously no obstacle, but now has obstacle at : "
                                        + Arena.getActualRowFromRow(curRow) + ", " + curCol);
                            }
                            curGrid.setHasObstacle(true);
                        } else {
                            if (curGrid.isHasObstacle() && curGrid.isHasBeenExplored()) {
                                // TODO handle this case
                                System.out.println("Previously has obstacle, but now no obstacle at : "
                                        + Arena.getActualRowFromRow(curRow) + ", " + curCol);
                                //curGrid.setHasBeenExplored(false);
                            }
                        }

                        // prevent obstacle from being added to path taken
                        for (Grid q: myRobot.getPathTaken()) {
                            for (int k = -1; k < 2; k++) {
                                for (int l = -1; l < 2; l++) {
                                    myRobot.getArena().getGrid(q.getRow() + k, q.getCol() + l).setHasObstacle(false);
                                }
                            }
                        }
                        curGrid.setHasBeenExplored(true);
                    }
                }
            }
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            myRobot.forward();
            myRobot.addCurGridToPathTaken();
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            myRobot.turnRight();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            myRobot.turnLeft();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            myRobot.reverse();
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            myRobot.takePicture();
        } else if (e.getKeyCode() == KeyEvent.VK_T) {
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
