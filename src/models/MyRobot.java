package models;

import controllers.SimulatorController;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static models.Constants.*;

import conn.TCPConn;


import static utils.API.constructMessageForAndroid;
import static utils.API.constructMessageForRpi;
import static utils.ExplorationAlgorithm.*;

public class MyRobot {
	public static final String REPAINT = "repaint";
	public static final String UPDATE_GUI_BASED_ON_SENSOR = "updateGuiBasedOnSensor";
	public static final String WAYPOINT_UPDATE = "wayPointUpdate";
	public static final String START_POSITION_UPDATE = "startPositionUpdate";


	private int curRow;
	private int curCol;
	private int startRow = DEFAULT_START_ROW;
	private int startCol = DEFAULT_START_COL;
	private int wayPointRow = DEFAULT_WAY_POINT_ROW;
	private int wayPointCol = DEFAULT_WAY_POINT_COL;
	private Orientation startOrientation = DEFAULT_START_ORIENTATION;
	private int temp;
	private Orientation curOrientation;
	private Arena referenceArena;
	private Arena arena;
	private double explorationCoverageLimit = DEFAULT_COVERAGE_LIMIT;
	private int explorationTimeLimit = DEFAULT_TIME_LIMIT;
	private Sensor[] frontSensor;
	private Sensor[] rightSensor;
	private Sensor[] leftSensor;
	private Sensor[][] allSensor;
	private double forwardSpeed;
	private double turningSpeed;
	private boolean hasFoundGoalZoneFlag;
	private Queue<Grid> pathTaken;
	public PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private TCPConn tcpConn;
	public static boolean isRealRun = false;
	public Matcher m;

	public MyRobot(Arena arena, Arena referenceArena) {
		this.arena = arena;
		this.curRow = startRow;
		this.curCol = startCol;
		this.curOrientation = startOrientation;
		this.referenceArena = referenceArena;
		this.forwardSpeed = 0;
		this.turningSpeed = 0;
		this.hasFoundGoalZoneFlag = false;
		this.pathTaken = new ConcurrentLinkedQueue<>();
		initSensor();
	}

	public void resetPathTaken() {
		pathTaken.clear();
	}

	public void getConnection(TCPConn tcpConn) {
		this.tcpConn = tcpConn;
	}

	public void calibrateRight() {
		if (detectObstacleAtBothRightSensor())  {
			if (curOrientation == Orientation.N || curOrientation == Orientation.S) {
				if (timesNotCalibratedHorizontal != 0) {
					if (isRealRun()) {
						tcpConn.sendMessage(CALIBRATE_RIGHT_INSTRUCTION_TO_ARDUINO);
					}
					System.out.println("Calibrating right at " + Arena.getActualRowFromRow(curRow) + "," + curCol + " " + curOrientation + " (h,v) " + timesNotCalibratedHorizontal + ":" + timesNotCalibratedVertical);
					timesNotCalibratedHorizontal = 0;
				}
			} else {
				if (timesNotCalibratedVertical != 0) {
					if (isRealRun()) {
						tcpConn.sendMessage(CALIBRATE_RIGHT_INSTRUCTION_TO_ARDUINO);
					}
					System.out.println("Calibrating right at " + Arena.getActualRowFromRow(curRow) + "," + curCol + " " + curOrientation + " (h,v) " + timesNotCalibratedHorizontal + ":" + timesNotCalibratedVertical);
					timesNotCalibratedVertical = 0;
				}
			}
		}
		pcs.firePropertyChange(UPDATE_GUI_BASED_ON_SENSOR, null, null);
	}

	public void calibrateFront() {
	    if (detectObstacleAtBothFrontCalibratingSensor()) {
			if (curOrientation == Orientation.N || curOrientation == Orientation.S) {
			    if (timesNotCalibratedVertical != 0) {
					if (isRealRun()) {
						tcpConn.sendMessage(CALIBRATE_FRONT_INSTRUCTION_TO_ARDUINO);
					}
					System.out.println("Calibrating front at " + Arena.getActualRowFromRow(curRow) + "," + curCol + " " + curOrientation + " (h,v) " + timesNotCalibratedHorizontal + ":" + timesNotCalibratedVertical);
					timesNotCalibratedVertical = 0;
				}
			} else {
				if (timesNotCalibratedHorizontal != 0) {
					if (isRealRun()) {
						tcpConn.sendMessage(CALIBRATE_FRONT_INSTRUCTION_TO_ARDUINO);
					}
					System.out.println("Calibrating front at " + Arena.getActualRowFromRow(curRow) + "," + curCol + " " + curOrientation + " (h,v) " + timesNotCalibratedHorizontal + ":" + timesNotCalibratedVertical);
					timesNotCalibratedHorizontal = 0;
				}
			}
		}
		pcs.firePropertyChange(UPDATE_GUI_BASED_ON_SENSOR, null, null);
	}

	public void forwardFP() {
		if (curOrientation == Orientation.N) {
			temp = curRow - 1;
			setCurRow(temp);
		} else if (curOrientation == Orientation.E) {
			temp = curCol + 1;
			setCurCol(temp);
		} else if (curOrientation == Orientation.S) {
			temp = curRow + 1;
			setCurRow(temp);
		} else if (curOrientation == Orientation.W) {
			temp = curCol - 1;
			setCurCol(temp);
		}
		pcs.firePropertyChange(REPAINT, null, null);
	}

	public void rightFP() {
		if (curOrientation == Orientation.N) {
			setCurOrientation(Orientation.E);
		} else if (curOrientation == Orientation.E) {
			setCurOrientation(Orientation.S);
		} else if (curOrientation == Orientation.S) {
			setCurOrientation(Orientation.W);
		} else if (curOrientation == Orientation.W) {
			setCurOrientation(Orientation.N);
		}
		pcs.firePropertyChange(REPAINT, null, null);

	}

	public void leftFP() {
		if (curOrientation == Orientation.N) {
			setCurOrientation(Orientation.W);
		} else if (curOrientation == Orientation.E) {
			setCurOrientation(Orientation.N);
		} else if (curOrientation == Orientation.S) {
			setCurOrientation(Orientation.E);
		} else if (curOrientation == Orientation.W) {
			setCurOrientation(Orientation.S);
		}
		pcs.firePropertyChange(REPAINT, null, null);
	}



	public void takePicture() {
	    int i;
		Grid grid2;
		Grid grid1;
	    Grid grid0;
	    picTaken++;

		if (curOrientation == Orientation.N) {
			for (i = 0; i < 5; i++) {
				grid2 = getArena().getGrid((curRow - 1), (curCol - 2) - i);
				if (grid2 != null && grid2.hasObstacle()) {
					if (i < 2) {
						break;
					} else {
						grid2.setR(true);
						break;
					}
				}
			}
			for (i = 0; i < 5; i++) {
				grid1 = getArena().getGrid((curRow), (curCol - 2) - i);
				if (grid1 != null && grid1.hasObstacle()) {
					grid1.setR(true);
					break;
				}
			}
			for (i = 0; i < 5; i++) {
				grid0 = getArena().getGrid((curRow + 1), (curCol - 2) - i);
				if (grid0 != null && grid0.hasObstacle()) {
					if (i < 2) {
						break;
					} else {
						grid0.setR(true);
						break;
					}
				}
			}
		} else if (curOrientation == Orientation.S) {
			for (i = 0; i < 5; i++) {
				grid2 = getArena().getGrid((curRow + 1), (curCol + 2) + i);
				if (grid2 != null && grid2.hasObstacle()) {
					if (i < 2) {
						break;
					} else {
						grid2.setL(true);
						break;
					}
				}
			}
			for (i = 0; i < 5; i++) {
				grid1 = getArena().getGrid((curRow), (curCol + 2) + i);
				if (grid1 != null && grid1.hasObstacle()) {
					grid1.setL(true);
					break;
				}
			}
			for (i = 0; i < 5; i++) {
				grid0 = getArena().getGrid((curRow - 1), (curCol + 2) + i);
				if (grid0 != null && grid0.hasObstacle()) {
					if (i < 2) {
						break;
					} else {
						grid0.setL(true);
						break;
					}
				}
			}
		} else if (curOrientation == Orientation.E) {
			for (i = 0; i < 5; i++) {
				grid2 = getArena().getGrid((curRow - 2) - i, (curCol + 1));
				if (grid2 != null && grid2.hasObstacle()) {
					if (i < 2) {
						break;
					} else {
						grid2.setD(true);
						break;
					}
				}
			}
			for (i = 0; i < 5; i++) {
				grid1 = getArena().getGrid((curRow - 2) - i, (curCol));
				if (grid1 != null && grid1.hasObstacle()) {
					grid1.setD(true);
					break;
				}
			}
			for (i = 0; i < 5; i++) {
				grid0 = getArena().getGrid((curRow - 2) - i, (curCol - 1));
				if (grid0 != null && grid0.hasObstacle()) {
					if (i < 2) {
						break;
					} else {
						grid0.setD(true);
						break;
					}
				}
			}
		} else if (curOrientation == Orientation.W) {
			for (i = 0; i < 5; i++) {
				grid2 = getArena().getGrid((curRow + 2) + i, (curCol - 1));
				if (grid2 != null && grid2.hasObstacle()) {
					if (i < 2) {
						break;
					} else {
						grid2.setU(true);
						break;
					}
				}
			}
			for (i = 0; i < 5; i++) {
				grid1 = getArena().getGrid((curRow + 2) + i, (curCol));
				if (grid1 != null && grid1.hasObstacle()) {
					grid1.setU(true);
					break;
				}
			}
			for (i = 0; i < 5; i++) {
				grid0 = getArena().getGrid((curRow + 2) + i, (curCol + 1));
				if (grid0 != null && grid0.hasObstacle()) {
					if (i < 2) {
						break;
					} else {
						grid0.setU(true);
						break;
					}
				}
			}
		}
		pcs.firePropertyChange(REPAINT, null, null);

		if (isRealRun()) {
		    sendCommandToRpiToTakePicture();
		}

	}

	public void reverse() {
		if (timesNotCalibratedHorizontal > TIMES_NOT_CALIBRATED_R_THRESHOLD) {
			if (curOrientation == Orientation.N || curOrientation == Orientation.S )  {
				calibrateRight();
			} else if (curOrientation == Orientation.W || curOrientation == Orientation.E) {
				calibrateFront();
			}
		}

		if (timesNotCalibratedVertical > TIMES_NOT_CALIBRATED_F_THRESHOLD) {
			if (curOrientation == Orientation.E || curOrientation == Orientation.W)  {
				calibrateRight();
			} else if (curOrientation == Orientation.N || curOrientation == Orientation.S) {
				calibrateFront();
			}
		}

		timesNotCalibratedVertical++;
		timesNotCalibratedHorizontal++;
		SimulatorController.numFwd++;
		if (curOrientation == Orientation.N) {
			temp = curRow + 1;
			setCurRow(temp);
		} else if (curOrientation == Orientation.E) {
			temp = curCol - 1;
			setCurCol(temp);
		} else if (curOrientation == Orientation.S) {
			temp = curRow - 1;
			setCurRow(temp);
		} else if (curOrientation == Orientation.W) {
			temp = curCol + 1;
			setCurCol(temp);
		}

		pcs.firePropertyChange(REPAINT, null, null);

		if (isRealRun) {
			if (SimulatorController.manualSensorReading) {
				System.out.println("FORWARD");
			} else {
				tcpConn.sendMessage(REVERSE_INSTRUCTION_TO_ARDUINO);
			}
		}
		if (isRealRun) {
			updateArenaBasedOnRealReadings();
			sendPositionToAndroid();
		} else {
			pcs.firePropertyChange(UPDATE_GUI_BASED_ON_SENSOR, null, null);
		}
	}

	public void ultraInstinct() {
		if (timesNotCalibratedHorizontal > TIMES_NOT_CALIBRATED_R_THRESHOLD) {
			if (curOrientation == Orientation.N || curOrientation == Orientation.S )  {
				calibrateRight();
			} else if (curOrientation == Orientation.W || curOrientation == Orientation.E) {
				calibrateFront();
			}
		}

		if (timesNotCalibratedVertical > TIMES_NOT_CALIBRATED_F_THRESHOLD) {
			if (curOrientation == Orientation.E || curOrientation == Orientation.W)  {
				calibrateRight();
			} else if (curOrientation == Orientation.N || curOrientation == Orientation.S) {
				calibrateFront();
			}
		}

		if (!hasObstacleRightInFront()) {
			System.out.println("here " + curRow + ", " + curCol + " "  + ULTRA_INSTINCT_INSTRUCTION_TO_ARDUINO);
			SimulatorController.numFwd++;
			timesNotCalibratedHorizontal++;
			timesNotCalibratedVertical++;
			SimulatorController.numFwd++;
			timesNotCalibratedHorizontal++;
			timesNotCalibratedVertical++;

			if (curOrientation == Orientation.N) {
				temp = curRow - 2;
				setCurRow(temp);
			} else if (curOrientation == Orientation.E) {
				temp = curCol + 2;
				setCurCol(temp);
			} else if (curOrientation == Orientation.S) {
				temp = curRow + 2;
				setCurRow(temp);
			} else if (curOrientation == Orientation.W) {
				temp = curCol - 2;
				setCurCol(temp);
			}

			pcs.firePropertyChange(REPAINT, null, null);

			if (isRealRun) {
				if (SimulatorController.manualSensorReading) {
					System.out.println("FORWARD");
				} else {
					tcpConn.sendMessage(ULTRA_INSTINCT_INSTRUCTION_TO_ARDUINO);
				}
			}

			if (isRealRun) {
				updateArenaBasedOnRealReadings();
				sendPositionToAndroid();
			} else {
				pcs.firePropertyChange(UPDATE_GUI_BASED_ON_SENSOR, null, null);
			}
		} else {
			System.out.println(curRow + " " + curCol + " " + curOrientation);
			System.out.println("WARNING: COLLIDING");
		}
	}
	public void forward() {
		if (timesNotCalibratedHorizontal > TIMES_NOT_CALIBRATED_R_THRESHOLD) {
			if (curOrientation == Orientation.N || curOrientation == Orientation.S )  {
				calibrateRight();
			} else if (curOrientation == Orientation.W || curOrientation == Orientation.E) {
				calibrateFront();
			}
		}

		if (timesNotCalibratedVertical > TIMES_NOT_CALIBRATED_F_THRESHOLD) {
			if (curOrientation == Orientation.E || curOrientation == Orientation.W)  {
				calibrateRight();
			} else if (curOrientation == Orientation.N || curOrientation == Orientation.S) {
				calibrateFront();
			}
		}

		if (!hasObstacleRightInFront()) {
			SimulatorController.numFwd++;
			timesNotCalibratedHorizontal++;
			timesNotCalibratedVertical++;

			if (curOrientation == Orientation.N) {
				temp = curRow - 1;
				setCurRow(temp);
			} else if (curOrientation == Orientation.E) {
				temp = curCol + 1;
				setCurCol(temp);
			} else if (curOrientation == Orientation.S) {
				temp = curRow + 1;
				setCurRow(temp);
			} else if (curOrientation == Orientation.W) {
				temp = curCol - 1;
				setCurCol(temp);
			}

			pcs.firePropertyChange(REPAINT, null, null);

			if (isRealRun) {
				if (SimulatorController.manualSensorReading) {
					System.out.println("FORWARD");
				} else {
					tcpConn.sendMessage(FORWARD_INSTRUCTION_TO_ARDUINO);
				}
			}

			if (isRealRun) {
				updateArenaBasedOnRealReadings();
				sendPositionToAndroid();
			} else {
				pcs.firePropertyChange(UPDATE_GUI_BASED_ON_SENSOR, null, null);
			}
		} else {
			System.out.println(curRow + " " + curCol + " " + curOrientation);
			System.out.println("WARNING: COLLIDING");
		}
	}

	public void turnRight() {
		if (timesNotCalibratedHorizontal > TIMES_NOT_CALIBRATED_R_THRESHOLD) {
			if (curOrientation == Orientation.N || curOrientation == Orientation.S )  {
				calibrateRight();
			} else if (curOrientation == Orientation.W || curOrientation == Orientation.E) {
				calibrateFront();
			}
		}

		if (timesNotCalibratedVertical > TIMES_NOT_CALIBRATED_F_THRESHOLD) {
			if (curOrientation == Orientation.E || curOrientation == Orientation.W)  {
				calibrateRight();
			} else if (curOrientation == Orientation.N || curOrientation == Orientation.S) {
				calibrateFront();
			}
		}

		SimulatorController.numTurn++;
		timesNotCalibratedHorizontal++;
		timesNotCalibratedVertical++;

		if (curOrientation == Orientation.N) {
			setCurOrientation(Orientation.E);
		} else if (curOrientation == Orientation.E) {
			setCurOrientation(Orientation.S);
		} else if (curOrientation == Orientation.S) {
			setCurOrientation(Orientation.W);
		} else if (curOrientation == Orientation.W) {
			setCurOrientation(Orientation.N);
		}

		pcs.firePropertyChange(REPAINT, null, null);

		if (isRealRun) {
			if (SimulatorController.manualSensorReading) {
				System.out.println("RIGHT");
			} else {
				tcpConn.sendMessage(TURN_RIGHT_INSTRUCTION_TO_ARDUINO);
			}
		}

		if (isRealRun) {
			updateArenaBasedOnRealReadings();
			sendPositionToAndroid();
		} else {
			pcs.firePropertyChange(UPDATE_GUI_BASED_ON_SENSOR, null, null);
		}
	}

	public void turnLeft() {
		if (timesNotCalibratedHorizontal > TIMES_NOT_CALIBRATED_R_THRESHOLD) {
			if (curOrientation == Orientation.N || curOrientation == Orientation.S )  {
				calibrateRight();
			} else if (curOrientation == Orientation.W || curOrientation == Orientation.E) {
				calibrateFront();
			}
		}

		if (timesNotCalibratedVertical > TIMES_NOT_CALIBRATED_F_THRESHOLD) {
			if (curOrientation == Orientation.E || curOrientation == Orientation.W)  {
				calibrateRight();
			} else if (curOrientation == Orientation.N || curOrientation == Orientation.S) {
				calibrateFront();
			}
		}

		SimulatorController.numTurn++;
		timesNotCalibratedHorizontal++;
		timesNotCalibratedVertical++;

		if (curOrientation == Orientation.N) {
			setCurOrientation(Orientation.W);
		} else if (curOrientation == Orientation.E) {
			setCurOrientation(Orientation.N);
		} else if (curOrientation == Orientation.S) {
			setCurOrientation(Orientation.E);
		} else if (curOrientation == Orientation.W) {
			setCurOrientation(Orientation.S);
		}
		if (isRealRun) {
			if (SimulatorController.manualSensorReading) {
				System.out.println("LEFT");
			} else {
				tcpConn.sendMessage(TURN_LEFT_INSTRUCTION_TO_ARDUINO);
			}
		}
		pcs.firePropertyChange(REPAINT, null, null);

		if (isRealRun) {
			updateArenaBasedOnRealReadings();
			sendPositionToAndroid();
		} else {
			pcs.firePropertyChange(UPDATE_GUI_BASED_ON_SENSOR, null, null);
		}
	}

	public void sendPositionToAndroid() {
		if (SimulatorController.manualSensorReading) {
			System.out.println(constructMessageForAndroid(this));
		} else {
			tcpConn.sendMessage(constructMessageForAndroid(this));
		}
	}

	public void sendCommandToRpiToTakePicture() {
		if (SimulatorController.manualSensorReading) {
			System.out.println(constructMessageForRpi(this));
		} else {
			System.out.println(constructMessageForRpi(this));
			tcpConn.sendMessage(constructMessageForRpi(this));
		}
	}

	public boolean hasObstacleRightInFront() {
		int curRow = getCurRow();
		int curCol = getCurCol();
		Orientation curOrientation = getCurOrientation();
		Grid[] rightGrid = new Grid[3];

		int[] row = {0, 0, 0};
		int[] col = {0, 0, 0};

		switch (curOrientation) {
			case N:
				col[0] = curCol - 1;
				row[0] = curRow - 2;
				col[1] = curCol;
				row[1] = curRow - 2;
				col[2] = curCol + 1;
				row[2] = curRow - 2;
				break;
			case E:
				col[0] = curCol + 2;
				row[0] = curRow - 1;
				col[1] = curCol + 2;
				row[1] = curRow;
				col[2] = curCol + 2;
				row[2] = curRow + 1;
				break;
			case S:
				col[0] = curCol + 1;
				row[0] = curRow + 2;
				col[1] = curCol;
				row[1] = curRow + 2;
				col[2] = curCol - 1;
				row[2] = curRow + 2;
				break;
			default:
				col[0] = curCol - 2;
				row[0] = curRow + 1;
				col[1] = curCol - 2;
				row[1] = curRow;
				col[2] = curCol - 2;
				row[2] = curRow - 1;
				break;
		}

		rightGrid[0] = arena.getGrid(row[0], col[0]);
		rightGrid[1] = arena.getGrid(row[1], col[1]);
		rightGrid[2] = arena.getGrid(row[2], col[2]);

		for (int i = 0; i < frontSensor.length; i++) {
			if (rightGrid[i] != null) {
				if (arena.isStartZone(row[i], col[i]) || arena.isGoalZone(row[i], col[i])) {
					continue;
				}
			}
			if (frontSensor[i].getSensorReading() == 1) {
				return true;
			}
		}
		return false;
	}

	public boolean inGoalZone() {
		return getArena().isGoalZone(curRow, curCol);
	}

	public boolean inStartZone() {
		return getArena().isStartZone(curRow, curCol);
	}

	public void setCurPositionToStart() {
		setCurRow(startRow);
		setCurCol(startCol);
		setCurOrientation(startOrientation);
	}

    private void initSensor() {
        /*
            Front Sensor:
                         x  x  x
                         x  x  x
                        [0][1][2]
                        [ ][ ][ ]
                        [ ][ ][ ]

            Right Sensor:
                        [ ][ ][0]x x
                        [ ][ ][ ]
                        [ ][ ][1]x x

            Left Sensor:
               x x x x x[0][ ][ ]
                        [ ][ ][ ]
                        [ ][ ][ ]


         Sensor Value Format =
         */


		frontSensor = new Sensor[3];
		rightSensor = new Sensor[2];
		leftSensor = new Sensor[1];

		frontSensor[0] = new Sensor(this, referenceArena, -1, -1, Sensor_Position.FRONT, 2);
		frontSensor[1] = new Sensor(this, referenceArena, -1, 0, Sensor_Position.FRONT, 2);
		frontSensor[2] = new Sensor(this, referenceArena, -1, 1, Sensor_Position.FRONT, 2);

		rightSensor[0] = new Sensor(this, referenceArena, -1, 1, Sensor_Position.RIGHT, 2);
		rightSensor[1] = new Sensor(this, referenceArena, 1, 1, Sensor_Position.RIGHT, 2);

		leftSensor[0] = new Sensor(this, referenceArena, -1, -1, Sensor_Position.LEFT, 5);

		allSensor = new Sensor[3][3];
	}

	public boolean hasObstacleToImmediateRight() {

		int curRow = getCurRow();
		int curCol = getCurCol();
		Orientation curOrientation = getCurOrientation();
		Grid[] rightGrid = new Grid[2];

		int[] row = {0, 0};
		int[] col = {0, 0};

		switch (curOrientation) {
			case N:
				col[0] = curCol + 2;
				row[0] = curRow - 1;
				col[1] = curCol + 2;
				row[1] = curRow + 1;
				break;
			case E:
				col[0] = curCol + 1;
				row[0] = curRow + 2;
				col[1] = curCol - 1;
				row[1] = curRow + 2;
				break;
			case S:
				col[0] = curCol - 2;
				row[0] = curRow + 1;
				col[1] = curCol - 2;
				row[1] = curRow - 1;
				break;
			default:
				col[0] = curCol - 1;
				row[0] = curRow - 2;
				col[1] = curCol + 1;
				row[1] = curRow - 2;
				break;
		}

		rightGrid[0] = arena.getGrid(row[0], col[0]);
		rightGrid[1] = arena.getGrid(row[1], col[1]);

		for (int i = 0; i < rightSensor.length; i++) {
			if (rightGrid[i] != null) {
				if (arena.isStartZone(row[i], col[i]) || arena.isGoalZone(row[i], col[i])) {
					continue;
				}
			}
			if (rightSensor[i].getSensorReading() == 1) {
				return true;
			}
		}
		return false;
	}

	public boolean hasObstacleOneGridFromTheRight() {
		for (int i = 0; i < rightSensor.length; i++) {
			if (rightSensor[i].getSensorReading() == 2) {
				return true;
			}
		}
		return false;
	}

	public boolean frontSensorReadingGives(int reading0, int reading1, int reading2) {
	    return (frontSensor[0].getSensorReading() == reading0 && frontSensor[1].getSensorReading() == reading1 && frontSensor[2].getSensorReading() == reading2);
	}

	public int fastFwdMovement() {
		if (curOrientation == Orientation.N) {

        }
		return 0;

	}

	public boolean nextFwdRightSideAlreadyExplored(int num) {
		if (curOrientation == Orientation.E) {
			int row = curRow + 2;
			int col = curCol + 2 + num;
			Grid grid;

			for (int range = 0; range < 2; range++) {
				grid = getArena().getGrid(row - range, col);
				if (grid == null || !grid.hasBeenExplored()) {
					return false;
				}
				if (grid.hasObstacle()) {
					break;
				}
			}

			return true;
		} else if (curOrientation == Orientation.W) {
			int row = curRow + 2;
			int col = curCol - 2 - num;
			Grid grid;

			for (int range = 0; range < 5; range++) {
				grid = getArena().getGrid(row + range, col);
				if (grid == null || !grid.hasBeenExplored()) {
					return false;
				}
				if (grid.hasObstacle()) {
					break;
				}
			}

			return true;
		} else if (curOrientation == Orientation.N) {
			int row = curRow - 2 - num;
			int col = curCol - 2;
			Grid grid;

			for (int range = 0; range < 5; range++) {
				grid = getArena().getGrid(row, col-range);
				if (grid == null || !grid.hasBeenExplored()) {
					return false;
				}
				if (grid.hasObstacle()) {
					break;
				}
			}

			return true;
		} else if (curOrientation == Orientation.S) {
			int row = curRow + 2 + num;
			int col = curCol + 2;
			Grid grid;

			for (int range = 0; range < 5; range++) {
				grid = getArena().getGrid(row, col+range);
				if (grid == null || !grid.hasBeenExplored()) {
					return false;
				}
				if (grid.hasObstacle()) {
					break;
				}
			}
			return true;
		}
		System.out.println("UNexpectedValue: asd");
		return false;
	}

	public boolean nextFwdLeftSideAlreadyExplored(int num) {
		if (curOrientation == Orientation.E) {
			int row = curRow - 2;
			int col = curCol + 2 + num;
			Grid grid;

			for (int range = 0; range < 5; range++) {
				grid = getArena().getGrid(row - range, col);
				if (grid == null || !grid.hasBeenExplored()) {
					return false;
				}
				if (grid.hasObstacle()) {
					break;
				}
			}

			return true;
		} else if (curOrientation == Orientation.W) {
            int row = curRow + 2;
            int col = curCol - 2 - num;
            Grid grid;

            for (int range = 0; range < 5; range++) {
                grid = getArena().getGrid(row + range, col);
				if (grid == null || !grid.hasBeenExplored()) {
					return false;
				}
				if (grid.hasObstacle()) {
					break;
				}
            }

            return true;
		} else if (curOrientation == Orientation.N) {
			int row = curRow - 2 - num;
			int col = curCol - 2;
			Grid grid;

			for (int range = 0; range < 5; range++) {
				grid = getArena().getGrid(row, col-range);
				if (grid == null || !grid.hasBeenExplored()) {
					return false;
				}
				if (grid.hasObstacle()) {
					break;
				}
			}

			return true;
		} else if (curOrientation == Orientation.S) {
			int row = curRow + 2 + num;
			int col = curCol + 2;
			Grid grid;

			for (int range = 0; range < 5; range++) {
				grid = getArena().getGrid(row, col+range);
				if (grid == null || !grid.hasBeenExplored()) {
					return false;
				}
				if (grid.hasObstacle()) {
					break;
				}
			}
			return true;
        }
		System.out.println("UNexpectedValue: asd");
		return false;
	}

	public boolean rightSensorReadingGives(int frontReading, int backReading) {
		return (rightSensor[0].getSensorReading() == frontReading && rightSensor[1].getSensorReading() == backReading);
	}

	// does not return true if arena wall is detected
	public boolean leftSensorDetectedObstacle() {
		if (leftSensor[0].getSensorReading() > 0) {
			return !seeIfObstacleIsArenaWall(leftSensor[0].getSensorReading());
		}
		return false;
	}

	private boolean seeIfObstacleIsArenaWall(int reading) {
		if (curOrientation == Orientation.N) {
			return (getArena().getGrid(curRow - 1, (curCol - 1) - reading) == null);
		} else if (curOrientation == Orientation.S) {
			return (getArena().getGrid(curRow + 1, (curCol + 1) + reading) == null);
		} else if (curOrientation == Orientation.E) {
			return (getArena().getGrid((curRow - 1) - reading, (curCol + 1)) == null);
		} else if (curOrientation == Orientation.W) {
			return (getArena().getGrid((curRow + 1) + reading, (curCol - 1)) == null);
		}
		System.out.println("Unexpected value at seeIfObstacleIsArenaWall");
		return false;
	}

	public boolean frontSensorDetectedObstacle2GridAway() {
		for (int i = 0; i < frontSensor.length; i++) {
			if (frontSensor[i].getSensorReading() == 2 && !robotFront2GridAwayFromArenaWall()) {
				return true;
			}
		}
		return false;
	}

	public boolean robotFront2GridAwayFromArenaWall() {
		return (curOrientation == Orientation.S && curRow == 17) ||
				(curOrientation == Orientation.E && curCol == 12) ||
				(curOrientation == Orientation.N && curRow == 2) ||
				(curOrientation == Orientation.W && curCol == 2);
	}


	public boolean detectObstacleAtBothRightSensor() {
		return (rightSensor[0].getSensorReading()  == 1) && (rightSensor[1].getSensorReading() == 1);
	}

	public boolean detectObstacleAtBothFrontCalibratingSensor() {
		return (frontSensor[0].getSensorReading()  == 1) && (frontSensor[2].getSensorReading() == 1);
	}

	//TODO fix
	public boolean rightSideFrontSensorThirdGridNeedsToBeExplored() {
		int row;
		int col;
		Grid grid;
		if (curOrientation == Orientation.S) {
			row = curRow + 1;
			col = curCol - 4;
		} else if (curOrientation == Orientation.N) {
			row = curRow - 1;
			col = curCol + 4;
		} else if (curOrientation == Orientation.E) {
			row = curRow + 4;
			col = curCol + 1;
		} else if (curOrientation == Orientation.W) {
			row = curRow - 4;
			col = curCol - 1;
		} else {
			System.out.println("Unexpected Orientation at rightSideFrontSernsorThirdGridNeedsToBeExplored");
			row = -1;
			col = -1;
		}
		grid = getArena().getGrid(row, col);
		if (grid != null && !grid.hasBeenExplored()) {
			return true;
		}
		return false;


	}

	public boolean hasObstacleToImmediateLeft() {

		int curRow = getCurRow();
		int curCol = getCurCol();
		Orientation curOrientation = getCurOrientation();
		Grid leftGrid = null;

		int row = 0;
		int col = 0;

		switch (curOrientation) {
			case N:
				col = curCol - 2;
				row = curRow - 1;
				break;
			case E:
				col = curCol + 1;
				row = curRow - 2;
				break;
			case S:
				col = curCol + 2;
				row = curRow + 1;
				break;
			default:
				col = curCol - 1;
				row = curRow + 2;
				break;
		}

		leftGrid = arena.getGrid(row, col);

		for (int i = 0; i < leftSensor.length; i++) {
			if (leftGrid != null) {
				if (arena.isStartZone(row, col) || arena.isGoalZone(row, col)) {
					continue;
				}
			}
			if (leftSensor[i].getSensorReading() == 1) {
				return true;
			}
		}
		return false;
	}

	public boolean hasSomeObstacleFaceNotCapturedOnTheLeft() {
		//TODO
		return false;
	}

	public boolean canReverseByOne() {
	    int i;
	    Grid grid;
		switch(curOrientation) {
			case N:
				for (i = -1; i < 2; i ++) {
					grid = getArena().getGrid(curRow + 2, curCol + i);
					if (grid == null || grid.hasObstacle() || !grid.hasBeenExplored()) {
						return false;
					}
				}
				break;
			case E:
				for (i = -1; i < 2; i ++) {
					grid = getArena().getGrid(curRow + i, curCol - 2);
					if (grid == null || grid.hasObstacle() || !grid.hasBeenExplored()) {
						return false;
					}
				}
				break;
			case S:
				for (i = -1; i < 2; i ++) {
					grid = getArena().getGrid(curRow - 2, curCol + i);
					if (grid == null || grid.hasObstacle() || !grid.hasBeenExplored()) {
						return false;
					}
				}
				break;
			case W:
				for (i = -1; i < 2; i ++) {
					grid = getArena().getGrid(curRow + i, curCol + 2);
					if (grid == null || grid.hasObstacle() || !grid.hasBeenExplored()) {
						return false;
					}
				}
				break;
			default:
				System.out.println("Unexpected value at canReverseByOne()");
		}
		return true;
	}

	public void updateArenaBasedOnRealReadings() {
		boolean messageFound;
		String realReadings;
		try {
			if (SimulatorController.manualSensorReading) {
				//System.out.println("Enter a sensor reading at " + Arena.getActualRowFromRow(getCurRow()) + ", "
				//		+ getCurCol() + ", "  + getCurOrientation() + ": ");
				Scanner sc = new Scanner(System.in);
				realReadings =  sc.nextLine();


				m = Pattern.compile(SENSOR_READING_PATTERN).matcher(realReadings);
			} else {
				// System.out.println("<<<< \n Sensor reading at " + Arena.getActualRowFromRow(getCurRow()) + ", "
				//		+ getCurCol() + ", "  + getCurOrientation() + ": ");
				realReadings = tcpConn.readMessageArduino();
				do {
					m = Pattern.compile(SENSOR_READING_PATTERN).matcher(realReadings);
					messageFound = m.find();
				} while (!messageFound);
			}

			String[] readingsArr = new String[2];
			if (realReadings.contains(",")) {
				readingsArr = realReadings.split(",");
			}
			realReadings = realReadings.substring(2);

			if (SimulatorController.manualSensorReading) {
				leftSensor[0].setRealReading(Character.getNumericValue(realReadings.charAt(0)));
			} else {
				int leftSensorReading;
				int frontLSensorReading;
				int frontMSensorReading;
				int frontRSensorReading;
				int rightFSensorReading;
				int rightBSensorReading;
				try {
					leftSensorReading = getRealLeftSensorReadings(Double.parseDouble(readingsArr[1]));
				} catch (NumberFormatException e) {
					leftSensorReading = getRealLeftSensorReadings(80.0);
				}
				try {
					frontLSensorReading = getRealShortSensorRreadings(Double.parseDouble(readingsArr[2]), FRONT_L_SENSOR_THRESHOLD);
				} catch (NumberFormatException e) {
					frontLSensorReading = getRealShortSensorRreadings(80.0, FRONT_R_SENSOR_THRESHOLD);
				}

				try {
					frontMSensorReading = getRealShortSensorRreadings(Double.parseDouble(readingsArr[3]), FRONT_M_SENSOR_THRESHOLD);
				} catch (NumberFormatException e) {
					frontMSensorReading = getRealShortSensorRreadings(80.0, FRONT_M_SENSOR_THRESHOLD);
				}

				try {
					frontRSensorReading = getRealShortSensorRreadings(Double.parseDouble(readingsArr[4]), FRONT_R_SENSOR_THRESHOLD);
				} catch (NumberFormatException e) {
					frontRSensorReading = getRealShortSensorRreadings(80.0, FRONT_R_SENSOR_THRESHOLD);
				}

				try{
					rightFSensorReading = getRealShortSensorRreadings(Double.parseDouble(readingsArr[5]), RIGHT_F_SENSOR_THRESHOLD);
				} catch (NumberFormatException e) {
					rightFSensorReading = getRealShortSensorRreadings(80.0, RIGHT_F_SENSOR_THRESHOLD);
				}

				try {
					readingsArr[6] = readingsArr[6].split(";")[0];
					rightBSensorReading = getRealShortSensorRreadings(Double.parseDouble(readingsArr[6]), RIGHT_B_SENSOR_THRESHOLD);
				} catch (NumberFormatException e) {
					rightBSensorReading = getRealShortSensorRreadings(80.0, RIGHT_B_SENSOR_THRESHOLD);
				}

				leftSensor[0].setRealReading(leftSensorReading);
				frontSensor[0].setRealReading(frontLSensorReading);
				frontSensor[1].setRealReading(frontMSensorReading);
				frontSensor[2].setRealReading(frontRSensorReading);
				rightSensor[0].setRealReading(rightFSensorReading);
				rightSensor[1].setRealReading(rightBSensorReading);

				System.out.println(leftSensorReading + " " + frontLSensorReading + frontMSensorReading + frontRSensorReading + " " + rightFSensorReading + rightBSensorReading);
				System.out.println(">>>");
			}

			pcs.firePropertyChange(MyRobot.UPDATE_GUI_BASED_ON_SENSOR, null, null);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int getRealLeftSensorReadings(double rawValue) {
		for (int i = 0; i < 5; i++) {
			if (rawValue < LEFT_SENSOR_THRESHOLD[i]) {
				return i + 1;
			}
		}
		return 0;
	}

	private int getRealShortSensorRreadings(double rawValue, double[] threshold) {
		for (int i = 0; i < 2; i++) {
			if (rawValue < threshold[i]) {
				return i + 1;
			}
		}
		return 0;
	}

	public boolean isAtCenterOfGoalZone() {
		return (getCurRow() == GOAL_ZONE_ROW && getCurCol() == GOAL_ZONE_COL);
	}

	public boolean isAtBtmRight() {
		return (getCurRow() == 18 && getCurCol() == 13);
	}

	public boolean isAtTopLeft() {
		return (getCurRow() == 1 && getCurCol() == 1);
	}

	public boolean isAtStartZone() {
		return (getCurRow() == START_ZONE_ROW && getCurCol() == START_ZONE_COL);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	private boolean hasChangeInValue(int oldValue, int newValue) {
		return oldValue != newValue;
	}

	public boolean getHasFoundGoalZoneFlag() {
		return hasFoundGoalZoneFlag;
	}

	public void setHasFoundGoalZoneFlag(boolean hasFoundGoalZoneFlag) {
		this.hasFoundGoalZoneFlag = hasFoundGoalZoneFlag;
	}

	public Sensor[] getFrontSensor() {
		return frontSensor;
	}

	public Sensor[] getRightSensor() {
		return rightSensor;
	}

	public Sensor[] getLeftSensor() {
		return leftSensor;
	}

	public Sensor[][] getAllSensor() {
		allSensor[0] = getFrontSensor();
		allSensor[1] = getRightSensor();
		allSensor[2] = getLeftSensor();
		return allSensor;
	}

	public boolean ifNeedToTakePictureOfBlindSpotGrid1() {
		int curRow = getCurRow();
		int curCol = getCurCol();
		Orientation curOrientation = getCurOrientation();

		int blindSpotRow;
		int blindSpotCol;
		Grid blindSpotGrid;

		switch (curOrientation) {
			case N:
				blindSpotCol = curCol + 2;
				blindSpotRow = curRow;
				blindSpotGrid = arena.getGrid(blindSpotRow, blindSpotCol);
				if (blindSpotGrid != null && blindSpotGrid.hasObstacle()) {
					return !blindSpotGrid.isL();
				} else {
					return false;
				}
			case E:
				blindSpotCol = curCol;
				blindSpotRow = curRow + 2;
				blindSpotGrid = arena.getGrid(blindSpotRow, blindSpotCol);
				if (blindSpotGrid != null && blindSpotGrid.hasObstacle()) {
					return !blindSpotGrid.isU();
				} else {
					return false;
				}
			case S:
				blindSpotCol = curCol - 2;
				blindSpotRow = curRow;
				blindSpotGrid = arena.getGrid(blindSpotRow, blindSpotCol);
				if (blindSpotGrid != null && blindSpotGrid.hasObstacle()) {
					return !blindSpotGrid.isR();
				} else {
					return false;
				}
			case W:
				blindSpotCol = curCol;
				blindSpotRow = curRow - 2;
				blindSpotGrid = arena.getGrid(blindSpotRow, blindSpotCol);
				if (blindSpotGrid != null && blindSpotGrid.hasObstacle()) {
					return !blindSpotGrid.isD();
				} else {
					return false;
				}
			default:
				System.out.println("Unexpected value at ifNeedToTakePictureOfBlindSpot");
				return false;
		}
	}

	public boolean ifNeedToTakePictureOfBlindSpotGrid2() {
		int curRow = getCurRow();
		int curCol = getCurCol();
		Orientation curOrientation = getCurOrientation();

		int blindSpotRow;
		int blindSpotCol;
		Grid blindSpotGrid;

		switch (curOrientation) {
			case N:
				blindSpotCol = curCol + 3;
				blindSpotRow = curRow;
				blindSpotGrid = arena.getGrid(blindSpotRow, blindSpotCol);
				if (blindSpotGrid != null && blindSpotGrid.hasObstacle()) {
					return !blindSpotGrid.isL();
				} else {
					return false;
				}
			case E:
				blindSpotCol = curCol;
				blindSpotRow = curRow + 3;
				blindSpotGrid = arena.getGrid(blindSpotRow, blindSpotCol);
				if (blindSpotGrid != null && blindSpotGrid.hasObstacle()) {
					return !blindSpotGrid.isU();
				} else {
					return false;
				}
			case S:
				blindSpotCol = curCol - 3;
				blindSpotRow = curRow;
				blindSpotGrid = arena.getGrid(blindSpotRow, blindSpotCol);
				if (blindSpotGrid != null && blindSpotGrid.hasObstacle()) {
					return !blindSpotGrid.isR();
				} else {
					return false;
				}
			case W:
				blindSpotCol = curCol;
				blindSpotRow = curRow - 3;
				blindSpotGrid = arena.getGrid(blindSpotRow, blindSpotCol);
				if (blindSpotGrid != null && blindSpotGrid.hasObstacle()) {
					return !blindSpotGrid.isD();
				} else {
					return false;
				}
			default:
				System.out.println("Unexpected value at ifNeedToTakePictureOfBlindSpot");
				return false;
		}
	}

	public boolean rightBlindSpotHasObstacle() {
		int curRow = getCurRow();
		int curCol = getCurCol();
		Orientation curOrientation = getCurOrientation();

		int blindSpotRow;
		int blindSpotCol;
		Grid blindSpotGrid;

		switch (curOrientation) {
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
			if (arena.isStartZone(blindSpotRow, blindSpotCol) || arena.isGoalZone(blindSpotRow, blindSpotCol)) {
				return false;
			}
			if (blindSpotGrid.hasBeenExplored()) {
				return blindSpotGrid.hasObstacle();
			} else {
				return false;
			}
		}
		return true;
	}

	public boolean immediateLeftSideHasObstacle() {
		Grid grid00 = null;
		Grid grid01 = null;
		Grid grid02 = null;

		switch (curOrientation) {
			case N:
				grid00 = arena.getGrid(curRow-1, curCol-2);
				grid01 = arena.getGrid(curRow, curCol-2);
				grid02 = arena.getGrid(curRow+1, curCol-2);

				if (grid00.hasObstacle() || grid01.hasObstacle() || grid02.hasObstacle()) {
					return true;
				}

				break;
			case S:
				if (isAtCenterOfGoalZone() || isAtBtmRight() || seeIfObstacleIsArenaWall(1)) {
					return false;
				}
				grid00 = arena.getGrid(curRow+1, curCol+2);
				grid01 = arena.getGrid(curRow, curCol+2);
				grid02 = arena.getGrid(curRow-1, curCol+2);

				if (grid00.hasObstacle() || grid01.hasObstacle() || grid02.hasObstacle()) {
				    return true;
				}

				break;
			case E:
				grid00 = arena.getGrid(curRow-2, curCol+1);
				grid01 = arena.getGrid(curRow-2, curCol);
				grid02 = arena.getGrid(curRow-2, curCol-1);

				if (grid00.hasObstacle() || grid01.hasObstacle() || grid02.hasObstacle()) {
					return true;
				}

				break;
			case W:
				if (isAtStartZone() || isAtBtmRight() || seeIfObstacleIsArenaWall(1)) {
					return false;
				}
				grid00 = arena.getGrid(curRow+2, curCol-1);
				grid01 = arena.getGrid(curRow+2, curCol);
				grid02 = arena.getGrid(curRow+2, curCol+1);

				if (grid00.hasObstacle() || grid01.hasObstacle() || grid02.hasObstacle()) {
				    return true;
				}
				break;
		}
		System.out.println("UNEXPECTED VALUE AT HASDASDAS");
		return false;
	}

	public boolean rightBlindSpotHasObstacle2() {
		int curRow = getCurRow();
		int curCol = getCurCol();
		Orientation curOrientation = getCurOrientation();

		int blindSpotRow;
		int blindSpotCol;
		Grid blindSpotGrid;

		switch (curOrientation) {
			case N:
				blindSpotCol = curCol + 3;
				blindSpotRow = curRow;
				break;
			case E:
				blindSpotCol = curCol;
				blindSpotRow = curRow + 3;
				break;
			case S:
				blindSpotCol = curCol - 3;
				blindSpotRow = curRow;
				break;
			default:
				blindSpotCol = curCol;
				blindSpotRow = curRow - 3;
				break;
		}

		blindSpotGrid = arena.getGrid(blindSpotRow, blindSpotCol);
		if (blindSpotGrid != null) {
			if (blindSpotGrid.hasBeenExplored()) {
				return blindSpotGrid.hasObstacle();
			} else {
				return false;
			}
		}
		return true;
	}

	public boolean rightAndFrontFacingArenaWall () {
		if (curRow == 1 && curCol == 13 && curOrientation == Orientation.N) {
			return true;
		}
		if (curRow == 1 && curCol == 1 && curOrientation == Orientation.W) {
			return true;
		}
		if (curRow == 18 && curCol == 13 && curOrientation == Orientation.E) {
			return true;
        }
		return false;
	}

	public boolean frontFacingArenaWall() {
		if (curRow == 18 && curOrientation == Orientation.S) {
			return true;
		}
		if (curRow == 1 && curOrientation == Orientation.N) {
			return true;
		}
		if (curCol == 1 && curOrientation == Orientation.W) {
			return true;
		}
		if (curCol == 13 && curOrientation == Orientation.E) {
			return true;
		}
		return false;
	}

	public boolean rightSideFacingArenaWall() {
		if (curRow == 18 && curOrientation == Orientation.E) {
			return true;
		}
		if (curRow == 1 && curOrientation == Orientation.W) {
			return true;
		}
		if (curCol == 1 && curOrientation == Orientation.S) {
			return true;
		}
		if (curCol == 13 && curOrientation == Orientation.N) {
			return true;
		}
		return false;
	}

	public int getCurCol() {
		return curCol;
	}

	public void setCurCol(int curCol) {
		if ((curCol != 0) && (curCol != 14) && hasChangeInValue(this.curCol, curCol)) {
			this.curCol = curCol;
		}
	}

	public int getCurRow() {
		return curRow;
	}

	public void setCurRow(int curRow) {
		if ((curRow != 0) && (curRow != 19) && hasChangeInValue(this.curRow, curRow)) {
			this.curRow = curRow;
		}
	}

	public Orientation getCurOrientation() {
		return curOrientation;
	}

	public void setCurOrientation(Orientation curOrientation) {
		this.curOrientation = curOrientation;
	}

	public double getForwardSpeed() {
		return forwardSpeed;
	}

	public void setForwardSpeed(double forwardSpeed) {
		this.forwardSpeed = forwardSpeed;
	}

	public double getTurningSpeed() {
		return turningSpeed;
	}

	public void setTurningSpeed(double turningSpeed) {
		this.turningSpeed = turningSpeed;
	}

	public Arena getArena() {
		return arena;
	}

	public double getExplorationCoverageLimit() {
		return explorationCoverageLimit;
	}

	public void setExplorationCoverageLimit(double explorationCoverageLimit) {
		this.explorationCoverageLimit = explorationCoverageLimit;
	}

	public int getExplorationTimeLimitInSeconds() {
		return explorationTimeLimit;
	}

	public String getExplorationTimeLimitFormatted() {
		int secs = explorationTimeLimit % 60;
		int mins = explorationTimeLimit / 60;
		return mins + " : " + secs;
	}

	public void setExplorationTimeLimit(int explorationTimeLimit) {
		this.explorationTimeLimit = explorationTimeLimit;
	}

	public void add2GridToPathTaken() {
		if (curOrientation == Orientation.N) {
			pathTaken.add(getArena().getGrid(curRow + 1, curCol));
		} else if (curOrientation == Orientation.S) {
			pathTaken.add(getArena().getGrid(curRow - 1, curCol));
		} else if (curOrientation == Orientation.E) {
			pathTaken.add(getArena().getGrid(curRow, curCol - 1));
		} else if (curOrientation == Orientation.W) {
			pathTaken.add(getArena().getGrid(curRow, curCol + 1));
		}
		pathTaken.add(getArena().getGrid(curRow, curCol));
	}

	public void addCurGridToPathTaken() {
		pathTaken.add(getArena().getGrid(curRow, curCol));
	}

	public Queue<Grid> getPathTaken() {
		return pathTaken;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
		pcs.firePropertyChange(MyRobot.START_POSITION_UPDATE, null, null);
	}

	public int getStartCol() {
		return startCol;
	}

	public void setStartCol(int startCol) {
		this.startCol = startCol;
		pcs.firePropertyChange(MyRobot.START_POSITION_UPDATE, null, null);
	}

	public Orientation getStartOrientation() {
		return startOrientation;
	}

	public void setStartOrientation(Orientation startOrientation) {
		this.startOrientation = startOrientation;
		pcs.firePropertyChange(MyRobot.START_POSITION_UPDATE, null, null);
	}

	public boolean isRealRun() {
		return isRealRun;
	}

	public int getWayPointRow() {
		return wayPointRow;
	}

	public void setWayPointRow(int wayPointRow) {
		this.wayPointRow = wayPointRow;
		pcs.firePropertyChange(MyRobot.WAYPOINT_UPDATE, null, null);
	}

	public int getWayPointCol() {
		return wayPointCol;
	}

	public void setWayPointCol(int wayPointCol) {
		this.wayPointCol = wayPointCol;
		pcs.firePropertyChange(MyRobot.WAYPOINT_UPDATE, null, null);
	}

	public Grid[] get5GridToLeftOfRobot(int row, int col) {
		int i;

		Grid[] leftFront5Grids = new Grid[5];
		if (curOrientation == Orientation.N) {
			for (i = 0; i < 5; i ++) {
				leftFront5Grids[i] = getArena().getGrid(row, col);
			}
		}
		return leftFront5Grids;
	}

	public boolean hasObstacleDiagonalLeftBehindRobot() {
	    Grid grid;
	    int row;
	    int col;
		if (curOrientation == Orientation.N) {
			row = curRow + 2;
			col = curCol - 2;
		} else if (curOrientation == Orientation.S) {
			row = curRow - 2;
			col = curCol + 2;
		} else if (curOrientation == Orientation.E) {
			row = curRow - 2;
			col = curCol - 2;
		} else if (curOrientation == Orientation.W) {
			row = curRow + 2;
			col = curCol + 2;
		} else {
			System.out.println("Unexpected value at hasObstacleDiagonalLeftBehindRobot");
			row = -1;
			col = -1;
		}

		grid = getArena().getGrid(row, col);
		return (grid != null && grid.hasBeenExplored() && grid.hasObstacle());
	}

	public boolean isInDeadEnd() {

		Grid grid00 = null;
		Grid grid01 = null;
		Grid grid02 = null;
		Grid grid10 = null;
		Grid grid11 = null;
		Grid grid12 = null;

		switch (curOrientation) {
			case N:
				if (isAtStartZone() || isAtTopLeft() || seeIfObstacleIsArenaWall(1)) {
					return false;
				}
				grid00 = arena.getGrid(curRow-1, curCol-2);
				grid01 = arena.getGrid(curRow, curCol-2);
				grid02 = arena.getGrid(curRow+1, curCol-2);
				grid10 = arena.getGrid(curRow-1, curCol-3);
				grid11 = arena.getGrid(curRow, curCol-3);
				grid12 = arena.getGrid(curRow+1, curCol-3);

				if (grid00.hasObstacle() || grid01.hasObstacle() || grid02.hasObstacle()) {
				    return true;
				}

				break;
			case S:
				if (isAtCenterOfGoalZone() || isAtBtmRight() || seeIfObstacleIsArenaWall(1)) {
					return false;
				}
				grid00 = arena.getGrid(curRow+1, curCol+2);
				grid01 = arena.getGrid(curRow, curCol+2);
				grid02 = arena.getGrid(curRow-1, curCol+2);
				grid10 = arena.getGrid(curRow+1, curCol+3);
				grid11 = arena.getGrid(curRow, curCol+3);
				grid12 = arena.getGrid(curRow-1, curCol+3);

				if (grid00.hasObstacle() || grid01.hasObstacle() || grid02.hasObstacle()) {
					if (arena.getGrid(curRow-2, curCol+2).hasObstacle()) {
						return true;
					}
				}

				break;
			case E:
				if (isAtCenterOfGoalZone() || isAtTopLeft() || seeIfObstacleIsArenaWall(1)) {
					return false;
				}
				grid00 = arena.getGrid(curRow-2, curCol+1);
				grid01 = arena.getGrid(curRow-2, curCol);
				grid02 = arena.getGrid(curRow-2, curCol-1);
				grid10 = arena.getGrid(curRow-3, curCol+1);
				grid11 = arena.getGrid(curRow-3, curCol);
				grid12 = arena.getGrid(curRow-3, curCol-1);

				if (grid00.hasObstacle() || grid01.hasObstacle() || grid02.hasObstacle()) {
				    return true;
				}

				break;
			case W:
				if (isAtStartZone() || isAtBtmRight() || seeIfObstacleIsArenaWall(1)) {
					return false;
				}

				grid00 = arena.getGrid(curRow+2, curCol-1);
				grid01 = arena.getGrid(curRow+2, curCol);
				grid02 = arena.getGrid(curRow+2, curCol+1);
				grid10 = arena.getGrid(curRow+3, curCol-1);
				grid11 = arena.getGrid(curRow+3, curCol);
				grid12 = arena.getGrid(curRow+3, curCol+1);

				if (grid00.hasObstacle() || grid01.hasObstacle() || grid02.hasObstacle()) {
					if (arena.getGrid(curRow+2, curCol+2).hasObstacle()) {
					    return true;
					}
				}
				break;
		}

		System.out.println("UNEXPECTED Asdasdasd");
		return false;
	}

	public boolean isInDeadEnd(int row, int col, int numOfFwd) {


		Grid grid00 = null;
		Grid grid01 = null;
		Grid grid02 = null;
		Grid gridf0 = null;
		Grid gridf1 = null;
		Grid gridf2 = null;
		Grid gridr0 = null;

		switch (curOrientation) {
			case N:
				row = row - numOfFwd;
				if (isAtStartZone() || isAtTopLeft()  || seeIfObstacleIsArenaWall(1)) {
					return false;
				}
				grid00 = arena.getGrid(row-1, col-2);
				grid01 = arena.getGrid(row, col-2);
				grid02 = arena.getGrid(row+1, col-2);
				gridf0 = arena.getGrid(row-2, col-1);
				gridf1 = arena.getGrid(row-2, col);
				gridf2 = arena.getGrid(row-2, col+1);
				gridr0 = arena.getGrid(row-1, col+2);
				System.out.println(grid00.hasBeenExplored());

				if ((grid00.hasBeenExplored() && grid01.hasBeenExplored() && grid02.hasBeenExplored())
						&& gridf0.hasBeenExplored() && gridf1.hasBeenExplored() && gridf2.hasBeenExplored()
						&& (gridr0 == null || gridr0.hasBeenExplored())) {

					if ((grid00.hasObstacle() || grid01.hasObstacle() || grid02.hasObstacle()) && (gridf0.hasObstacle() || gridf1.hasObstacle() || gridf2.hasObstacle())) {
						return true;
					}
				}
				break;
			case S:
				row = row + numOfFwd;
				if (isAtStartZone() || isAtTopLeft() || seeIfObstacleIsArenaWall(1)) {
					return false;
				}
				grid00 = arena.getGrid(row-1, col-2);
				grid01 = arena.getGrid(row, col-2);
				grid02 = arena.getGrid(row+1, col-2);
				gridf0 = arena.getGrid(row-2, col-1);
				gridf1 = arena.getGrid(row-2, col);
				gridf2 = arena.getGrid(row-2, col+1);
				gridr0 = arena.getGrid(row-1, col+2);

				if ((grid00.hasBeenExplored() && grid01.hasBeenExplored() && grid02.hasBeenExplored())
						&& gridf0.hasBeenExplored() && gridf1.hasBeenExplored() && gridf2.hasBeenExplored()
						&& (gridr0 == null || gridr0.hasBeenExplored())) {

					if ((grid00.hasObstacle() || grid01.hasObstacle() || grid02.hasObstacle()) && (gridf0.hasObstacle() || gridf1.hasObstacle() || gridf2.hasObstacle())) {
						return true;
					}
				}
				break;
			case E:

				break;
			case W:
				break;
		}

		return false;
	}
}