package controllers;

import static models.Constants.*;

import models.Arena;
import models.MyRobot;
import models.Result;
import utils.API;
import utils.ExplorationAlgorithm;
import utils.FastestPathAlgorithm;
import utils.FileReaderWriter;
import views.CenterPanel;
import views.EastPanel;
import views.WestPanel;

import javax.swing.*;

import conn.TCPConn;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Collections;

import static models.Constants.ARENA_DESCRIPTOR_PATH;

public class SimulatorController implements MouseListener {
    private static MyRobot myRobot;
    private int turningSpeedMs;
    private int fwdSpeedMs;
    private Timer timer;
    public static final int[] timeElapsed = new int[1];
    public static int numFwd;
    public static int numTurn;
    private JLabel[] statusLbls;
    private ExplorationAlgorithm explorationAlgo;
    private FastestPathAlgorithm fastestPathAlgo;
    private String instructionsWhenStartAtNorth;
    private String instructionsWhenStartAtEast;

    private static CenterPanel centerPanel;

    public static  boolean test = true;
    private TCPConn tcpConn;
    
    SwingWorker<Boolean, Void> worker;
    public SimulatorController(WestPanel westPanel) {
        westPanel.addTestMovementListener(e -> westPanel.arenaPanel.requestFocus());
    }
    
    public SimulatorController(CenterPanel centerPanel, MyRobot myRobot){
        this.centerPanel = centerPanel;
        this.myRobot = myRobot;

        centerPanel.addRPIBtnListener(e -> startRealRun(myRobot, centerPanel));
        
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
            setConfigurations(centerPanel, myRobot, false);
            centerPanel.setExplorationAndFastestPathBtns(true);
            centerPanel.getRestartBtn().setEnabled(true);
        }   );
        centerPanel.addRestartBtnListener(e -> restart(centerPanel, myRobot));
        centerPanel.addExplorationBtnListener(e -> {
            exploration(centerPanel, myRobot, ExplorationType.NORMAL);
        });
        centerPanel.addExplorationRightClickListener(this);
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
        fields[0].setText(Arena.getActualRowFromRow(myRobot.getCurRow()) + ", " + (myRobot.getCurCol()));
        fields[1].setText((Double.toString(myRobot.getForwardSpeed())));
        fields[2].setText((Double.toString(myRobot.getTurningSpeed())));
        fields[3].setText(Arena.getActualRowFromRow(myRobot.getWayPointRow()) + ", " + (myRobot.getWayPointCol()));
        fields[4].setText((Double.toString(myRobot.getExplorationCoverageLimit())));
        fields[5].setText((myRobot.getExplorationTimeLimitFormatted()));

    }

    private void setConfigurations(CenterPanel centerPanel, MyRobot myRobot, Boolean resetMap) {
        String[] rowCol = parseInputToRowColArr(centerPanel.getFields()[0].getText());
        String[] wayPointRowCol = parseInputToRowColArr(centerPanel.getFields()[3].getText());
        double forwardSpeed = Double.parseDouble(centerPanel.getFields()[1].getText());
        double turningSpeed = Double.parseDouble(centerPanel.getFields()[2].getText());

        Orientation selectedOrientation = orientationStringToEnum((String) centerPanel.getOrientationSelection().getSelectedItem());
        myRobot.setStartOrientation(selectedOrientation);

        myRobot.setStartRow(Arena.getRowFromActualRow(Integer.parseInt(rowCol[0], 10)));
        myRobot.setStartCol(Integer.parseInt(rowCol[1], 10));
        myRobot.setCurPositionToStart();
        myRobot.setForwardSpeed(forwardSpeed);
        myRobot.setTurningSpeed(turningSpeed);
        myRobot.setWayPointRow(Arena.getRowFromActualRow(Integer.parseInt(wayPointRowCol[0], 10)));
        myRobot.setWayPointCol(Integer.parseInt(wayPointRowCol[1], 10));
        myRobot.setExplorationCoverageLimit(Double.parseDouble(centerPanel.getFields()[4].getText()));
        myRobot.setExplorationTimeLimit(parseInputToSecs(centerPanel.getFields()[5].getText()));

        disableConfigurations(centerPanel);

        if (resetMap) {
            myRobot.getArena().reinitializeArena(myRobot);
        }
        myRobot.resetPathTaken();
        myRobot.pcs.firePropertyChange(MyRobot.UPDATE_GUI_BASED_ON_SENSOR, null, null);

        myRobot.getArena().setHasExploredBasedOnOccupiedGrid(myRobot);

    }

    private void restart(CenterPanel centerPanel, MyRobot myRobot) {
        if (timer != null) {
            timer.stop();
        }
        setConfigurations(centerPanel, myRobot, true);
        centerPanel.setExplorationAndFastestPathBtns(true);
        centerPanel.getFastestPathBtn().setEnabled(false);
        reinitStatusPanelVariables();
        centerPanel.reinitStatusPanelTxt();
        if (worker != null) {
            worker.cancel(true);
        }
    }
    private void reinitStatusPanelVariables() {
        timeElapsed[0] = 0;
        numTurn = 0;
        numFwd = 0;
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

    private void startRealRun(MyRobot myRobot, CenterPanel centerPanel) {
        MyRobot.isRealRun = true;
        tcpConn = TCPConn.getInstance();
        worker = new SwingWorker<Boolean, Void>(){

            protected Boolean doInBackground() throws Exception {
                String message;
                if (test){
                    // simulate start signal from android
                    message = "explore:13,1,E|1,18";
                    while(!message.contains(START_EXPLORATION)) {
                        System.out.println("something wrong");
                    }
                    API.processStartExplorationMsg(message, myRobot);
                } else {
                    System.out.println("Waiting for connection");
                    tcpConn.instantiateConnection(TCPConn.RPI_IP, TCPConn.RPI_PORT);
                    System.out.println("Successfully Connected!");
                    myRobot.getConnection(tcpConn);

                    message = tcpConn.readMessage();

                    while(!message.contains(START_EXPLORATION)) {
                        System.out.println("Expecting start exploration but received: " + message);
                        message = tcpConn.readMessage();
                    }
                    API.processStartExplorationMsg(message, myRobot);
                }
                exploration(centerPanel, myRobot, ExplorationType.NORMAL);
                return true;
            }
        };
        worker.execute();

    }
    private void exploration(CenterPanel centerPanel, MyRobot myRobot, ExplorationType explorationType){
        this.myRobot = myRobot;
        myRobot.setCurPositionToStart();
        myRobot.getArena().reinitializeArena(myRobot);

        myRobot.pcs.firePropertyChange(myRobot.REPAINT, null, null);
        if (myRobot.isRealRun) {
            myRobot.updateSensorsWithRealReadings();
        }
        myRobot.pcs.firePropertyChange(myRobot.UPDATE_GUI_BASED_ON_SENSOR, null, null);

        explorationAlgo = new ExplorationAlgorithm(myRobot, getInstance(), explorationType);

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

        worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                myRobot.setHasFoundGoalZoneFlag(false);
                numTurn = 0;
                numFwd = 0;
                timer.start();
                explorationAlgo.explorationLogic(false);
                timer.stop();
                centerPanel.getFastestPathBtn().setEnabled(true);

                fastestPathAlgo = new FastestPathAlgorithm(myRobot, getInstance());
                instructionsWhenStartAtEast = fastestPathAlgo.generateInstructionsForFastestPath(Orientation.E);

                myRobot.getArena().resetGridCostAndCameFrom();
                instructionsWhenStartAtNorth = fastestPathAlgo.generateInstructionsForFastestPath(Orientation.N);

                Orientation bestStartingPosition = fastestPathAlgo.getMostOptimalStartingPosition(
                        instructionsWhenStartAtNorth,
                        instructionsWhenStartAtEast
                );

                if (bestStartingPosition == Orientation.N) {
                    if (MyRobot.isRealRun) {
                        tcpConn.sendMessage("an" + "Start facing North");
                    }
                    System.out.println("start facing north");
                } else {
                    if (MyRobot.isRealRun) {
                        tcpConn.sendMessage("an" + "Start facing East");
                    }
                    System.out.println("start facing east");
                }
                return true;
            }

            @Override
            protected void done() {
                String message;
                if (test && MyRobot.isRealRun) {
                    message = "fastest:1,1,N";
                    while (!message.contains(START_FASTEST)) {
                        System.out.println("something wrong");
                    }
                    API.processStartFastestMsg(message, myRobot);
                    fastestPath(centerPanel, myRobot);
                }
                if (MyRobot.isRealRun) {
                    message = tcpConn.readMessage();
                    while(!message.contains(START_FASTEST)) {
                        System.out.println("Expecting start fastest path but received: " + message);
                        message = tcpConn.readMessage();
                    }
                    API.processStartFastestMsg(message, myRobot);
                    fastestPath(centerPanel, myRobot);
                }
            }
        };
        worker.execute();
    }


    private void fastestPath(CenterPanel centerPanel, MyRobot myRobot){
        this.myRobot = myRobot;
        myRobot.setStartRow(DEFAULT_START_ROW);
        myRobot.setStartCol(DEFAULT_START_COL);
        myRobot.setCurPositionToStart();
        myRobot.resetPathTaken();

        turningSpeedMs = (int)(myRobot.getTurningSpeed() * 1000);
        fwdSpeedMs = (int)(myRobot.getForwardSpeed() * 1000);

        timeElapsed[0] = 0;
        centerPanel.setExplorationAndFastestPathBtns(false);

        statusLbls = centerPanel.getStatusLbls();

        worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                numTurn = 0;
                numFwd = 0;

                timer.start();
                String instructionsToBeExecuted;
                if (myRobot.getStartOrientation() == Orientation.N) {
                    instructionsToBeExecuted = instructionsWhenStartAtNorth;
                } else if (myRobot.getStartOrientation() == Orientation.E) {
                    instructionsToBeExecuted = instructionsWhenStartAtEast;
                } else {
                    instructionsToBeExecuted = null;
                    System.out.println("Only start with East Or North");
                }
                executeInstructionInSimulator(instructionsToBeExecuted);
                timer.stop();
                return true;
            }
        };
        worker.execute();
    }


    public void executeInstructionInSimulator(String instructions) throws InterruptedException {
        for (int i = 0; i < instructions.length(); i ++) {
            if (instructions.charAt(i) == 'W') {
                forward();
            } else if (instructions.charAt(i) == 'A') {
                left();
            } else if (instructions.charAt(i) == 'D') {
                right();
            } else {
                System.out.println("Unknown Instruction");
            }
        }
    }

    public void forward() throws InterruptedException {
        Thread.sleep(fwdSpeedMs);
        myRobot.forward();
        myRobot.addCurGridToPathTaken();
    }

    public void right() throws InterruptedException {

        Thread.sleep(turningSpeedMs);
        myRobot.turnRight();
    }

    public void left() throws InterruptedException {
        Thread.sleep(turningSpeedMs);
        myRobot.turnLeft();
    }

    public SimulatorController getInstance() {
    	//if(_instance == null)
    	//	_instance = new SimulatorController(centerPanel, myRobot);
    	return this;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == 3) {
            bruteForceAllPossibleStartingPosition(centerPanel, myRobot, ExplorationType.NORMAL);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    // used for finding out the best starting position
    private void bruteForceAllPossibleStartingPosition(CenterPanel centerPanel, MyRobot myRobot, ExplorationType explorationType) {
        this.myRobot = myRobot;

        explorationAlgo = new ExplorationAlgorithm(myRobot, getInstance(), explorationType);

        turningSpeedMs = (int)(myRobot.getTurningSpeed() * 1000);
        fwdSpeedMs = (int)(myRobot.getForwardSpeed() * 1000);

        centerPanel.setExplorationAndFastestPathBtns(false);

        worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                ArrayList<Result> results = new ArrayList<>();
                Result result;
                for (int r = ARENA_HEIGHT/2 + 1; r <= ARENA_HEIGHT; r++) {
                    for (int c = 1; c < ARENA_WIDTH - 1; c++) {
                        for (int o = 0; o < 4; o++) {
                            if (myRobot.possibleStartingPosition(r, c)) {
                                myRobot.setHasFoundGoalZoneFlag(false);
                                numTurn = 0;
                                numFwd = 0;
                                myRobot.setStartRow(r);
                                myRobot.setStartCol(c);
                                myRobot.setStartOrientation(Orientation.values()[o]);
                                myRobot.setCurPositionToStart();
                                myRobot.getArena().reinitializeArena(myRobot);
                                myRobot.resetPathTaken();
                                myRobot.pcs.firePropertyChange(MyRobot.UPDATE_GUI_BASED_ON_SENSOR, null, null);
                                myRobot.getArena().setHasExploredBasedOnOccupiedGrid(myRobot);
                                explorationAlgo.explorationLogic(true);

                                if (numFwd < 200 && myRobot.getArena().getCoveragePercentage() == 100.0) {
                                    result = new Result(r, c, Orientation.values()[o], numFwd, numTurn, numFwd+numTurn);
                                    results.add(result);
                                }
                            }
                        }
                    }
                }
                Collections.sort(results);
                System.out.println("This is the top 5 best starting position");
                for (int i = 0; i < 5; i++) {
                    System.out.println(results.get(i).toString());
                }
                return true;
            }
        };
        worker.execute();
    }
}
