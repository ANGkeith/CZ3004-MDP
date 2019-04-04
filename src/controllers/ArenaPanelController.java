package controllers;

import models.*;
import utils.ExplorationAlgorithm;
import views.ArenaPanel;
import views.CenterPanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static models.Constants.INFINITY;

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

                        int timesNotCalibrated = ExplorationAlgorithm.timesNotCalibratedVertical + ExplorationAlgorithm.timesNotCalibratedHorizontal;

                        if (i == sensor.getSensorReading()) {
                            if (curGrid.isHasBeenExplored()) {
                                if (!curGrid.isHasObstacle()) {
                                    System.out.println("Previously no obstacle, but now has obstacle at : "
                                            + Arena.getActualRowFromRow(curRow) + ", " + curCol);
                                    curGrid.addErrorCount();

                                    if (curGrid.getErrorCount() > 1) {
                                        curGrid.setTimesNotCalibrated(INFINITY);
                                        curGrid.setErrorCount(0);
                                    }
                                    if (curGrid.getTimesNotCalibrated() > timesNotCalibrated) {
                                        System.out.println("old " + curGrid.getTimesNotCalibrated());
                                        System.out.println("new" + timesNotCalibrated);
                                        curGrid.setHasObstacle(true);
                                        setNeightbourFace(curRow, curCol);
                                        curGrid.setTimesNotCalibrated(timesNotCalibrated);
                                    }
                                } else {
                                    curGrid.updateTimesNotCalibrated(timesNotCalibrated);
                                }
                            } else {
                                curGrid.setHasObstacle(true);
                                curGrid.setHasBeenExplored(true);
                                setNeightbourFace(curRow, curCol);
                                curGrid.setTimesNotCalibrated(timesNotCalibrated);
                            }
                        } else {
                            if (curGrid.isHasBeenExplored()) {
                                if (curGrid.isHasObstacle()) {
                                    System.out.println("Previously has obstacle, but now no obstacle at : "
                                            + Arena.getActualRowFromRow(curRow) + ", " + curCol);
                                    curGrid.addErrorCount();

                                    if (curGrid.getErrorCount() > 1) {
                                        curGrid.setTimesNotCalibrated(INFINITY);
                                        curGrid.setErrorCount(0);
                                    }

                                    if (curGrid.getTimesNotCalibrated() > timesNotCalibrated) {
                                        System.out.println("old " + curGrid.getTimesNotCalibrated());
                                        System.out.println("new" + timesNotCalibrated);
                                        curGrid.setHasObstacle(false);
                                        curGrid.setTimesNotCalibrated(timesNotCalibrated);
                                    }
                                } else {
                                    curGrid.updateTimesNotCalibrated(timesNotCalibrated);
                                }
                            } else {
                                curGrid.setHasObstacle(false);
                                curGrid.setHasBeenExplored(true);
                                curGrid.setTimesNotCalibrated(timesNotCalibrated);
                            }
                        }

                        // prevent obstacle from being added to path taken
                        for (Grid q: myRobot.getPathTaken()) {
                            for (int k = -1; k < 2; k++) {
                                for (int l = -1; l < 2; l++) {
                                    myRobot.getArena().getGrid(q.getRow() + k, q.getCol() + l).setHasObstacle(false);
                                    myRobot.getArena().getGrid(q.getRow() + k, q.getCol() + l).setTimesNotCalibrated(0);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void setNeightbourFace(int r, int c) {
        Grid n = myRobot.getArena().getGrid(r - 1, c);
        Grid s = myRobot.getArena().getGrid(r + 1, c);
        Grid e = myRobot.getArena().getGrid(r, c + 1);
        Grid w = myRobot.getArena().getGrid(r, c - 1);

        Grid curGrid = myRobot.getArena().getGrid(r, c);
        if (n == null || (n.hasBeenExplored() && n.hasObstacle())) {
            curGrid.setU(true);
            if (n != null) {
                n.setD(true);
            }
        }
        if (s == null || (s.hasBeenExplored() && s.hasObstacle())) {
            curGrid.setD(true);
            if (s != null) {
                s.setU(true);
            }
        }
        if (e == null || (e.hasBeenExplored() && e.hasObstacle())) {
            curGrid.setR(true);
            if (e != null) {
                e.setL(true);
            }
        }
        if (w == null || (w.hasBeenExplored() && w.hasObstacle())) {
            curGrid.setL(true);
            if (w != null) {
                w.setR(true);
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
            myRobot.addCurGridToPathTaken();
        } else if (e.getKeyCode() == KeyEvent.VK_O) {
            System.out.println(myRobot.isInDeadEnd(myRobot.getCurRow(), myRobot.getCurCol(), 2));
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            myRobot.takePicture();
        } else if (e.getKeyCode() == KeyEvent.VK_T) {
            System.out.printf("r,c,o %d, %d, %s", 19-myRobot.getCurRow(), myRobot.getCurCol(), myRobot.getCurOrientation());
        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            myRobot.calibrateRight();
        } else if (e.getKeyCode() == KeyEvent.VK_F) {
            myRobot.calibrateFront();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
