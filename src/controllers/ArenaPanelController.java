package controllers;

import models.Grid;
import models.MyRobot;
import models.Sensor;
import views.ArenaPanel;
import models.Arena;
import static models.Constants.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ArenaPanelController  implements PropertyChangeListener, KeyListener {

    private ArenaPanel arenaPanel;
    private MyRobot myRobot;
    private Arena arena;
    private Grid grid;

    public ArenaPanelController(ArenaPanel arenaPanel, MyRobot myRobot, Arena arena) {
        this.myRobot = myRobot;
        this.arenaPanel = arenaPanel;
        this.arena = arena;
        myRobot.addPropertyChangeListener(this);

        arenaPanel.addKeyListener(this);

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (MyRobot.UPDATEGUI.equals(evt.getPropertyName())) {
            updateArenaBasedOnSensorReadings();
            arenaPanel.revalidate();
            arenaPanel.repaint();
        }
    }

    private void updateArenaBasedOnSensorReadings() {
        int numOfSensibleGrid;
        int curRow;
        int curCol;
        Sensor[][] frontRightLeftSensors;

        frontRightLeftSensors = myRobot.getAllSensor();

        for (Sensor[] sensors: frontRightLeftSensors) {
            for (Sensor sensor: sensors) {
                numOfSensibleGrid = sensor.getSimulatedSensorReading();
                if (sensor.getSimulatedSensorReading() == 0) {
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
                        if (i == sensor.getSimulatedSensorReading()) {
                            grid.setHasObstacle(true);
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
