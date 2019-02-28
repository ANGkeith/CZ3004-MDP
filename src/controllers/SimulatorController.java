package controllers;

import static models.Constants.*;

import models.MyRobot;
import utils.Algorithm;
import utils.FileReaderWriter;
import views.CenterPanel;
import views.EastPanel;
import views.WestPanel;

import javax.swing.*;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.FileSystems;

import static models.Constants.ARENA_DESCRIPTOR_PATH;

public class SimulatorController {
    private MyRobot myRobot;
    private int turningSpeedMs;
    private int fwdSpeedMs;
    private Timer timer;
    public final int[] timeElapsed = new int[1];
    public static int numFwd;
    public static int numTurn;
    private JLabel[] statusLbls;
    private Algorithm algo;


    SwingWorker<Boolean, Void> explorationWorker;
    public SimulatorController(WestPanel westPanel) {
        westPanel.addTestMovementListener(e -> westPanel.arenaPanel.requestFocus());
    }

    public SimulatorController(CenterPanel centerPanel, MyRobot myRobot){
        centerPanel.addModifyBtnListener(e -> {
            enableConfigurations(centerPanel);
            centerPanel.setExplorationAndFastestPathBtns(false);
            centerPanel.getRestartBtn().setEnabled(false);
        });
        centerPanel.addCancelBtnListener(e -> {
            enableConfigurations(centerPanel);
            disableConfigurations(centerPanel);
            resetConfigurations(centerPanel, myRobot);
            centerPanel.setExplorationAndFastestPathBtns(true);
            centerPanel.getRestartBtn().setEnabled(true);

        });
        centerPanel.addOkBtnListener(e -> {
            saveConfigurations(centerPanel, myRobot);
            centerPanel.setExplorationAndFastestPathBtns(true);
            centerPanel.getRestartBtn().setEnabled(true);
        }   );
        centerPanel.addRestartBtnListener(e -> restart(centerPanel, myRobot));
        centerPanel.addExplorationBtnListener(e -> exploration(centerPanel, myRobot, ExplorationType.NORMAL));
        centerPanel.addFastestPathBtnListener(e -> fastestPath(centerPanel, myRobot));
        centerPanel.addCoverageLimitedExplorationBtnListener(e -> exploration(centerPanel, myRobot, ExplorationType.COVERAGE_LIMITED));
        centerPanel.addTimeLimitedExplorationBtnListener(e -> exploration(centerPanel, myRobot, ExplorationType.TIME_LIMITED));
        centerPanel.addMapDescriptorP1Listener(e -> copyP1ToClipBoard(centerPanel, myRobot));
        centerPanel.addMapDescriptorP2Listener(e -> copyP2ToClipBoard(centerPanel, myRobot));

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
    private void resetConfigurations(CenterPanel centerPanel, MyRobot myRobot) {
        JTextField[] fields = centerPanel.getFields();
        fields[0].setText((myRobot.getCurRow()) + ", " + (myRobot.getCurCol()));
        fields[1].setText((Double.toString(myRobot.getForwardSpeed())));
        fields[2].setText((Double.toString(myRobot.getTurningSpeed())));
        fields[4].setText((Double.toString(myRobot.getExplorationCoverageLimit())));
        fields[5].setText((myRobot.getExplorationTimeLimitFormatted()));

    }

    private void saveConfigurations(CenterPanel centerPanel, MyRobot myRobot) {
        String[] rowCol = parseInputToRowColArr(centerPanel.getFields()[0].getText());
        double forwardSpeed = Double.parseDouble(centerPanel.getFields()[1].getText());
        double turningSpeed = Double.parseDouble(centerPanel.getFields()[2].getText());

        // Have to plus 1 because the row and col starts from 0;
        myRobot.setCurRow(Integer.parseInt(rowCol[0], 10));
        myRobot.setCurCol(Integer.parseInt(rowCol[1], 10));
        myRobot.setForwardSpeed(forwardSpeed);
        myRobot.setTurningSpeed(turningSpeed);
        myRobot.setExplorationCoverageLimit(Double.parseDouble(centerPanel.getFields()[4].getText()));
        myRobot.setExplorationTimeLimit(parseInputToSecs(centerPanel.getFields()[5].getText()));

        Orientation selectedOrientation = orientationStringToEnum((String) centerPanel.getOrientationSelection().getSelectedItem());
        myRobot.setCurOrientation(selectedOrientation);

        disableConfigurations(centerPanel);

        myRobot.getArena().reinitializeArena();
        myRobot.pcs.firePropertyChange(MyRobot.UPDATEGUI, null, null);

        myRobot.getArena().setHasExploredBasedOnOccupiedGrid(myRobot);

    }

    private void restart(CenterPanel centerPanel, MyRobot myRobot) {
        if (timer != null) {
            timer.stop();
        }
        saveConfigurations(centerPanel, myRobot);
        centerPanel.setExplorationAndFastestPathBtns(true);
        centerPanel.reinitStatusPanelTxt();
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
        return s.split("\\s*,\\s*");
    }

    private int parseInputToSecs(String s) {
        String[] stringArr = s.split("\\s*:\\s*");
        int min = Integer.parseInt(stringArr[0], 10);
        int sec = Integer.parseInt(stringArr[1], 10);
        return min * 60 + sec;
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
            arenaGrids.setBackground(EXPLORED_COLOR);
        }
    }

    private void copyP1ToClipBoard(CenterPanel centerPanel, MyRobot myRobot) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection contentToBeCopied = new StringSelection(myRobot.getArena().generateMapDescriptorP1());
        clipboard.setContents(contentToBeCopied, contentToBeCopied);
    }

    private void copyP2ToClipBoard(CenterPanel centerPanel, MyRobot myRobot) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection contentToBeCopied = new StringSelection(myRobot.getArena().generateMapDescriptorP2());
        clipboard.setContents(contentToBeCopied, contentToBeCopied);
    }

    private void exploration(CenterPanel centerPanel, MyRobot myRobot, ExplorationType explorationType){
        this.myRobot = myRobot;
        algo = new Algorithm(myRobot, getInstance(), explorationType);

        turningSpeedMs = (int)(myRobot.getTurningSpeed() * 1000);
        fwdSpeedMs = (int)(myRobot.getForwardSpeed() * 1000);

        timeElapsed[0] = 0;
        centerPanel.setExplorationAndFastestPathBtns(false);

        statusLbls = centerPanel.getStatusLbls();
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                timeElapsed[0]++;
                statusLbls[0].setText(centerPanel.statusPrefixedLbls[0] + timeElapsed[0]);
            }
        });

        explorationWorker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                myRobot.setHasFoundGoalZoneFlag(false);
                numTurn = 0;
                numFwd = 0;
                timer.start();
                algo.explorationLogic();
                timer.stop();
                System.out.println("P1: " + myRobot.getArena().generateMapDescriptorP1());
                System.out.println("P2: " + myRobot.getArena().generateMapDescriptorP2());
                return true;
            }
        };
        explorationWorker.execute();
    }

    // ToDO
    private void fastestPath(CenterPanel centerPanel, MyRobot myRobot){
        this.myRobot = myRobot;
        algo = new Algorithm(myRobot, getInstance());

        timeElapsed[0] = 0;
        centerPanel.setExplorationAndFastestPathBtns(false);

        statusLbls = centerPanel.getStatusLbls();

        explorationWorker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                timer.start();
                algo.fastestPathLogic();
                timer.stop();
                return true;
            }
        };
        explorationWorker.execute();
    }


    public void forward() throws InterruptedException {
        Thread.sleep(fwdSpeedMs);
        numFwd++;
        myRobot.forward();
    }

    public void right() throws InterruptedException {
        Thread.sleep(turningSpeedMs);
        numTurn++;
        myRobot.turnRight();
    }

    public void left() throws InterruptedException {
        Thread.sleep(turningSpeedMs);
        numTurn++;
        myRobot.turnLeft();
    }

    public SimulatorController getInstance() {
        return this;
    }
}
