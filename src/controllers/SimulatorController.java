package controllers;

import static models.Constants.*;

import models.Arena;
import models.Grid;
import models.MyRobot;
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
    private Arena arena;
    private MyRobot myRobot;
    private int turningSpeedMs;
    private int fwdSpeedMs;
    private Timer timer;
    private final int[] timeElapsed = new int[1];
    public static int numFwd;
    public static int numTurn;


    SwingWorker<Boolean, Void> explorationWorker;
    public SimulatorController(WestPanel westPanel) {
        westPanel.addTestMovementListener(e -> westPanel.arenaPanel.requestFocus());
    }

    public SimulatorController(CenterPanel centerPanel, MyRobot myRobot){
        centerPanel.addModifyBtnListener(e -> enableConfigurations(centerPanel));
        centerPanel.addCancelBtnListener(e -> disableConfigurations(centerPanel));
        centerPanel.addOkBtnListener(e -> saveConfigurations(centerPanel, myRobot));
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

    private void saveConfigurations(CenterPanel centerPanel, MyRobot myRobot) {
        String[] rowCol = parseInputToRowColArr(centerPanel.getFields()[0].getText());
        double forwardSpeed = Double.parseDouble(centerPanel.getFields()[1].getText());
        double turningSpeed = Double.parseDouble(centerPanel.getFields()[2].getText());

        // Have to plus 1 because the row and col starts from 0;
        myRobot.setCurRow(Integer.parseInt(rowCol[0], 10) - 1);
        myRobot.setCurCol(Integer.parseInt(rowCol[1], 10) - 1);
        myRobot.setForwardSpeed(forwardSpeed);
        myRobot.setTurningSpeed(turningSpeed);
        String curUserInput = centerPanel.getFields()[4].getText();
        myRobot.setExplorationCoverageLimit(Double.parseDouble(centerPanel.getFields()[4].getText()));
        myRobot.setExplorationTimeLimit(parseInputToSecs(centerPanel.getFields()[5].getText()));

        Orientation selectedOrientation = orientationStringToEnum((String) centerPanel.getOrientationSelection().getSelectedItem());
        myRobot.setCurOrientation(selectedOrientation);

        disableConfigurations(centerPanel);

        myRobot.getArena().reinitializeArena();
        myRobot.pcs.firePropertyChange(MyRobot.UPDATEGUI, null, null);

        myRobot.getArena().setHasExploredBasedOnOccupiedGrid(myRobot);

        // TODO stop thread
    }

    private void restart(CenterPanel centerPanel, MyRobot myRobot) {
        timer.stop();
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
        turningSpeedMs = (int)(myRobot.getTurningSpeed() * 1000);
        fwdSpeedMs = (int)(myRobot.getForwardSpeed() * 1000);
        this.myRobot = myRobot;
        this.arena = myRobot.getArena();

        timeElapsed[0] = 0;
        centerPanel.setExplorationAndFastestPathBtns(false);

        JLabel[] statusLbls = centerPanel.getStatusLbls();

        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                timeElapsed[0]++;
                statusLbls[0].setText(centerPanel.statusPrefixedLbls[0] + timeElapsed[0]);
            }
        });


        explorationWorker = new SwingWorker<Boolean, Void>() {

            boolean explorationCompletedFlag;
            boolean hasFoundGoalZoneFlag;
            @Override
            protected Boolean doInBackground() throws Exception {
                hasFoundGoalZoneFlag = false;
                explorationCompletedFlag = false;
                numTurn = 0;
                numFwd = 0;
                timer.start();
                while (!explorationCompletedFlag && stoppingConditions(explorationType)) {
                    if (myRobot.hasObstacleToItsImmediateRight() || rightBlindSpotHasObstacle()) {
                        if (!myRobot.hasObstacleRightInFront()) {
                            forward();
                        } else if (!myRobot.hasObstacleToItsImmediateLeft()) {
                            left();
                        } else if (myRobot.hasObstacleToItsImmediateLeft()) {
                            right();
                            right();
                        }
                    } else {
                        right();
                        forward();
                    }

                    if (robotIsAtGoalZone()) {
                        hasFoundGoalZoneFlag = true;
                    }

                    if (hasFoundGoalZoneFlag && robotIsAtStartZone()) {
                        explorationCompletedFlag = true;
                    }
                }
                timer.stop();
                centerPanel.setExplorationAndFastestPathBtns(true);
                System.out.println(myRobot.getArena().generateMapDescriptorP1());
                System.out.println(myRobot.getArena().generateMapDescriptorP2());

                return true;
            }

            private boolean robotIsAtGoalZone() {
                return (myRobot.getCurRow() == 1 && myRobot.getCurCol() == 13);
            }

            private boolean robotIsAtStartZone() {
                return (myRobot.getCurRow() == 18 && myRobot.getCurCol() == 1);
            }

            private boolean stoppingConditions(ExplorationType explorationType) {
                if (explorationType == ExplorationType.NORMAL) {
                    return true;
                } else if (explorationType == ExplorationType.COVERAGE_LIMITED) {
                    if (myRobot.getExplorationCoverageLimit() > 0) {
                        return (myRobot.getExplorationCoverageLimit() > myRobot.getArena().getCoveragePercentage());
                    }

                } else if (explorationType == ExplorationType.TIME_LIMITED) {
                    if (myRobot.getExplorationTimeLimitInSeconds() > 0) {
                        return (myRobot.getExplorationTimeLimitInSeconds() > timeElapsed[0]);
                    }
                }
                return true;
            }
        };
        explorationWorker.execute();
    }

    // ToDO
    private void fastestPath(CenterPanel centerPanel, MyRobot myRobot){
    }


    private void forward() throws InterruptedException {
        Thread.sleep(fwdSpeedMs);
        numFwd++;
        myRobot.move(My_Robot_Instruction.FORWARD);
    }

    private void right() throws InterruptedException {
        Thread.sleep(turningSpeedMs);
        numTurn++;
        myRobot.move(My_Robot_Instruction.TURN_RIGHT);
    }

    private void left() throws InterruptedException {
        Thread.sleep(turningSpeedMs);
        numTurn++;
        myRobot.move(My_Robot_Instruction.TURN_LEFT);
    }

    private boolean rightBlindSpotHasObstacle() {
        int curRow = myRobot.getCurRow();
        int curCol = myRobot.getCurCol();
        Orientation curOrientation = myRobot.getCurOrientation();

        int blindSpotRow;
        int blindSpotCol;
        Grid blindSpotGrid;

        switch(curOrientation) {
            case N:
                blindSpotCol = curCol + 2;
                blindSpotRow = curRow;
                break;
            case E:
                blindSpotCol = curCol;
                blindSpotRow = curRow + 2;
                break;
            case S:
                blindSpotCol = curCol - 2;
                blindSpotRow = curRow;
                break;
            default:
                blindSpotCol = curCol;
                blindSpotRow = curRow - 2;
                break;
        }

        blindSpotGrid = arena.getGrid(blindSpotRow, blindSpotCol);
        if (blindSpotGrid != null) {
            if (blindSpotGrid.hasBeenExplored()) {
                return blindSpotGrid.hasObstacle();
            } else {
                System.out.println("BLIND SPOT NOT EXPLORED");
                return false;
            }
        }
        return true;
    }
}
