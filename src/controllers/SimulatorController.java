package controllers;

import static models.Constants.*;

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



    public SimulatorController(WestPanel westPanel) {
        westPanel.addTestMovementListener(e -> westPanel.arenaPanel.requestFocus());

    }

    public SimulatorController(CenterPanel centerPanel, MyRobot myRobot){
        centerPanel.addModifyBtnListener(e -> enableConfigurations(centerPanel));
        centerPanel.addCancelBtnListener(e -> disableConfigurations(centerPanel));
        centerPanel.addOkBtnListener(e -> saveConfigurations(centerPanel, myRobot));
        centerPanel.addRestartBtnListener(e -> restart(centerPanel));

    }

    public SimulatorController(EastPanel eastPanel) {
        eastPanel.addLoadBtnListener(e -> loadMap(eastPanel));
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

    private void saveConfigurations(CenterPanel centerPanel, MyRobot myRobot) {
        String[] rowCol = parseInputToRowColArr(centerPanel.getFields()[0].getText());
        myRobot.setCurRow(Integer.parseInt(rowCol[0], 10));
        myRobot.setCurCol(Integer.parseInt(rowCol[1], 10));

        Orientation selectedOrientation = orientationStringToEnum((String) centerPanel.getOrientationSelection().getSelectedItem());
        myRobot.setCurOrientation(selectedOrientation);

        disableConfigurations(centerPanel);
    }

    private void restart(CenterPanel centerPanel) {

    }

    private void loadMap(EastPanel eastPanel) {
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
                eastPanel.getReferenceArena().resetObstacle();
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
}
