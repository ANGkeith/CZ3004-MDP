package controllers;

import static models.Constants.*;

import models.Arena;
import models.MyRobot;
import utils.FileReaderWriter;
import views.CenterPanel;
import views.EastPanel;
import views.WestPanel;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.FileSystems;

import static models.Constants.ARENA_DESCRIPTOR_PATH;

public class SimulatorController {
    SwingWorker<Void, Void> explorationWorker;
    public SimulatorController(WestPanel westPanel) {
        westPanel.addTestMovementListener(e -> westPanel.arenaPanel.requestFocus());
    }

    public SimulatorController(CenterPanel centerPanel, MyRobot myRobot, Arena arena){
        centerPanel.addModifyBtnListener(e -> enableConfigurations(centerPanel));
        centerPanel.addCancelBtnListener(e -> disableConfigurations(centerPanel));
        centerPanel.addOkBtnListener(e -> saveConfigurations(centerPanel, myRobot, arena));
        centerPanel.addRestartBtnListener(e -> restart(centerPanel, myRobot, arena));
        centerPanel.addExplorationBtnListener(e -> exploration(myRobot));

    }

    public SimulatorController(EastPanel eastPanel) {
        eastPanel.addSaveBtnListener(e -> saveMap(eastPanel));
        eastPanel.addClearBtnListener(e -> clearObstacle(eastPanel));
    }

    private void enableConfigurations(CenterPanel centerPanel) {
        for (int i = 0; i < centerPanel.getLbls().length; i++) {
            centerPanel.getFields()[i].setEnabled(true);
        }
        centerPanel.getOkBtn().setEnabled(true);
        centerPanel.getCancelBtn().setEnabled(true);
        centerPanel.getModifyBtn().setEnabled(false);
        centerPanel.getOrientationSelection().setEnabled(true);
    }

    private void disableConfigurations(CenterPanel centerPanel) {
        for (int i = 0; i < centerPanel.getLbls().length; i++) {
            centerPanel.getFields()[i].setEnabled(false);
        }
        centerPanel.getOkBtn().setEnabled(false);
        centerPanel.getCancelBtn().setEnabled(false);
        centerPanel.getModifyBtn().setEnabled(true);
        centerPanel.getOrientationSelection().setEnabled(false);
    }

    private void saveConfigurations(CenterPanel centerPanel, MyRobot myRobot, Arena arena) {
        String[] rowCol = parseInputToRowColArr(centerPanel.getFields()[0].getText());
        double forwardSpeed = Double.parseDouble(centerPanel.getFields()[1].getText());
        double turningSpeed = Double.parseDouble(centerPanel.getFields()[2].getText());

        // Have to plus 1 because the row and col starts from 0;
        myRobot.setCurRow(Integer.parseInt(rowCol[0], 10) - 1);
        myRobot.setCurCol(Integer.parseInt(rowCol[1], 10) - 1);
        myRobot.setForwardSpeed(forwardSpeed);
        myRobot.setTurningSpeed(turningSpeed);

        Orientation selectedOrientation = orientationStringToEnum((String) centerPanel.getOrientationSelection().getSelectedItem());
        myRobot.setCurOrientation(selectedOrientation);

        disableConfigurations(centerPanel);

        arena.reinitializeArena();
        myRobot.pcs.firePropertyChange(MyRobot.UPDATEGUI, null, null);

        arena.setHasExploredBasedOnOccupiedGrid(myRobot);

        // TODO stop thread
    }

    private void restart(CenterPanel centerPanel, MyRobot myRobot, Arena arena) {
       saveConfigurations(centerPanel, myRobot, arena);
       if (explorationWorker != null) {
           explorationWorker.cancel(true);
       }
    }

    private void saveMap(EastPanel eastPanel) {
        try {
            FileReaderWriter fileWriter = new FileReaderWriter(FileSystems.getDefault().getPath(ARENA_DESCRIPTOR_PATH, new String[0]));
            fileWriter.write(eastPanel.getReferenceArena().obstacleToString());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void clearObstacle(EastPanel eastPanel) {
        for (int r = 0; r < ARENA_HEIGHT; r++) {
            for (int c = 0; c < ARENA_WIDTH; c++) {
                untoggleObstacle(eastPanel.getButtonArenaPanel().arenaGrids[r][c]);
                eastPanel.getReferenceArena().clearObstacle();
            }
        }
    }

    // utils
    private String[] parseInputToRowColArr(String s) {
        return s.split(",\\s*");
    }

    public Orientation orientationStringToEnum(String s) {
        if (s == "North") {
            return Orientation.N;
        } else if (s == "East") {
            return Orientation.E;
        } else if (s == "South") {
            return Orientation.S;
        }
        return Orientation.W;
    }

    private void untoggleObstacle(JButton arenaGrids) {
        if (arenaGrids.getBackground() == OBSTACLE_COLOR) {
            arenaGrids.setBackground(MAP_COLOR);
        }
    }


    private void exploration(MyRobot myRobot){
        explorationWorker = new SwingWorker<Void, Void>() {
            int turningSpeedMs = (int)(myRobot.getTurningSpeed() * 1000);
            int fwdSpeedMs = (int)(myRobot.getForwardSpeed() * 1000);

            @Override
            protected Void doInBackground() throws Exception {
                while (true) {
                    if (myRobot.hasObstacleToItsImmediateRight()) {
                        if (!myRobot.hasObstacleRightInFront()) {
                            forward();
                        } else if (!myRobot.hasObstacleToItsImmediateLeft()) {
                            left();
                        } else if (myRobot.hasObstacleToItsImmediateLeft()) {
                            right();
                            right();
                            while(myRobot.hasObstacleToItsImmediateRight()) {
                                forward();
                            }
                            right();
                            forward();
                            forward();
                            forward();
                        }
                    } else {
                        right();
                        forward();
                        if (myRobot.hasObstacleRightInFront()) {
                            left();
                            forward();
                        }
                    }
                }

            }
            private void forward() throws InterruptedException {
                myRobot.move(My_Robot_Instruction.FORWARD);
                Thread.sleep(fwdSpeedMs);
            }
            private void right() throws InterruptedException {
                myRobot.move(My_Robot_Instruction.TURN_RIGHT);
                Thread.sleep(turningSpeedMs);
            }
            private void left() throws InterruptedException {
                myRobot.move(My_Robot_Instruction.TURN_LEFT);
                Thread.sleep(turningSpeedMs);
            }
        };

        explorationWorker.execute();

    }



}
