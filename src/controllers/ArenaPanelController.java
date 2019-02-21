package controllers;

import models.Grid;
import models.MyRobot;
import models.Sensor;
import views.ArenaPanel;
import models.Arena;
import views.CenterPanel;

import static models.Constants.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ArenaPanelController  implements PropertyChangeListener, KeyListener {

    private ArenaPanel arenaPanel;
    private CenterPanel centerPanel;
    private MyRobot myRobot;
    private Arena arena;
    private Grid grid;
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
        if (MyRobot.UPDATEGUI.equals(evt.getPropertyName())) {
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
                numOfSensibleGrid = sensor.getSimulatedSensorReading();
                if (sensor.getSimulatedSensorReading() == sensor.NO_OBSTACLE) {
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
                    curGrid = arena.getGrid(curRow, curCol);
                    if (curGrid != null) {
                        curGrid.setHasBeenExplored(true);
                        if (i == sensor.getSimulatedSensorReading()) {
                            curGrid.setHasObstacle(true);
                        }
                    }
                }
            }
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            myRobot.move(My_Robot_Instruction.FORWARD);
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            myRobot.move(My_Robot_Instruction.TURN_RIGHT);
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            myRobot.move(My_Robot_Instruction.TURN_LEFT);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
