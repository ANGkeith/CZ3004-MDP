package models;

import controllers.SimulatorController;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import conn.TCPConn;

import javax.swing.*;
import static models.Constants.*;
import static utils.ExplorationAlgorithm.timesNotCalibratedF;
import static utils.ExplorationAlgorithm.timesNotCalibratedR;
import static utils.Utils.delay;

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

	public boolean possibleStartingPosition(int row, int col) {
		Grid curGrid;
		for (int r = -1; r < 2; r++) {
			for (int c = -1; c < 2; c++) {
				curGrid = referenceArena.getGrid(row + r, col + c);
				if (curGrid == null) {
					return false;
				} else if (curGrid.hasObstacle()) {
					return false;
				}
			}
		}
		return true;
	}

	public String constructMessageForAndroid() {
		String p0 = getCurCol() + "," + Arena.getRowFromActualRow(getCurRow()) + "," + getCurOrientation().toString();
		String p1 = arena.generateMapDescriptorP1();
		String p2 = arena.generateMapDescriptorP2();

		String toAndroid = "an"+ p0 + "," + p1 + "," + p2 + ",";
		return toAndroid;
	}

	public String constructP0ForAndroid() {
		String p0 = getCurCol() + "," + Arena.getRowFromActualRow(getCurRow()) + "," + getCurOrientation().toString();
		return "an" + p0;
	}

	public String constructMessageForRpi() {
		String myPosition = getCurCol() + "," + Arena.getRowFromActualRow(getCurRow()) + "," + getCurOrientation().toString();
		return RPI_IDENTIFIER + myPosition;
	}

	public void calibrate() {
		if (isRealRun()) {
			tcpConn.sendMessage(CALIBRATE_INSTRUCTION_TO_ARDUINO);
		}
		System.out.println("Calibrating right at " + Arena.getActualRowFromRow(curRow) + "," + curCol + " " + timesNotCalibratedR);
		timesNotCalibratedR = 0;
	}

	public void calibrateFront() {
		if (isRealRun()) {
			tcpConn.sendMessage(CALIBRATE_FRONT_INSTRUCTION_TO_ARDUINO);
		}
		System.out.println("Calibrating front at " + Arena.getActualRowFromRow(curRow) + "," + curCol + " " + timesNotCalibratedR);
		timesNotCalibratedF = 0;
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

	public void forward() {

		if (timesNotCalibratedR > TIMES_NOT_CALIBRATED_R_THRESHOLD && detectObstacleAtBothRightSensor()) {
			calibrate();
		}

		if (timesNotCalibratedF > TIMES_NOT_CALIBRATED_F_THRESHOLD && frontFacingArenaWall()) {
			calibrateFront();
		}

		if (!hasObstacleRightInFront()) {
			SimulatorController.numFwd++;
			timesNotCalibratedR++;
			timesNotCalibratedF++;

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
					//delay();
				}
			}

			if (isRealRun) {
				updateArenaBasedOnRealReadings("F");
				sendPositionToAndroidAndRpi();
			} else {
				pcs.firePropertyChange(UPDATE_GUI_BASED_ON_SENSOR, null, null);
			}
		}
	}

	public void turnRight() {
		if (timesNotCalibratedR > TIMES_NOT_CALIBRATED_R_THRESHOLD && detectObstacleAtBothRightSensor()) {
			calibrate();
		}
		if (timesNotCalibratedF > TIMES_NOT_CALIBRATED_F_THRESHOLD && frontFacingArenaWall()) {
			calibrateFront();
		}

		SimulatorController.numTurn++;
		timesNotCalibratedR++;
		timesNotCalibratedF++;

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
				//delay();
			}
		}

		if (isRealRun) {
			updateArenaBasedOnRealReadings("R");
			sendPositionToAndroidAndRpi();
		} else {
			pcs.firePropertyChange(UPDATE_GUI_BASED_ON_SENSOR, null, null);
		}
	}

	public void turnLeft() {
		if (timesNotCalibratedR > TIMES_NOT_CALIBRATED_R_THRESHOLD && detectObstacleAtBothRightSensor()) {
			calibrate();
		}

		if (timesNotCalibratedF > TIMES_NOT_CALIBRATED_F_THRESHOLD && frontFacingArenaWall()) {
			calibrateFront();
		}

		SimulatorController.numTurn++;
		timesNotCalibratedR++;
		timesNotCalibratedF++;

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
				//delay();
			}
		}
		pcs.firePropertyChange(REPAINT, null, null);

		if (isRealRun) {
			updateArenaBasedOnRealReadings("L");
			sendPositionToAndroidAndRpi();
		} else {
			pcs.firePropertyChange(UPDATE_GUI_BASED_ON_SENSOR, null, null);
		}
	}

	public void sendPositionToAndroidAndRpi() {
		if (SimulatorController.manualSensorReading) {
			System.out.println(constructMessageForAndroid());
			System.out.println(constructMessageForRpi());
		} else {
			SwingWorker worker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					delay();
					tcpConn.sendMessage(constructMessageForAndroid());
					//tcpConn.sendMessage(constructMessageForRpi());
					return null;
				}
			};
			worker.execute();
		}
	}

	public boolean hasObstacleRightInFront() {
		for (int i = 0; i < frontSensor.length; i++) {
			if (frontSensor[i].getSensorReading() == 1) {
				return true;
			}
		}
		return false;
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

	public boolean hasObstacleToItsImmediateRight() {
		for (int i = 0; i < rightSensor.length; i++) {
			if (rightSensor[i].getSensorReading() == 1) {
				return true;
			}
		}
		return false;
	}

	public boolean detectObstacleAtBothRightSensor() {
		return (rightSensor[0].getSensorReading()  == 1) && (rightSensor[1].getSensorReading() == 1);
	}

	public boolean hasObstacleToItsImmediateLeft() {
		for (int i = 0; i < leftSensor.length; i++) {
			if (leftSensor[i].getSensorReading() == 1) {
				return true;
			}
		}
		return false;
	}

	public void updateArenaBasedOnRealReadings(String instructions) {
		boolean messageFound;
		String realReadings;
		try {
			if (SimulatorController.manualSensorReading) {
				System.out.println("Enter a sensor reading at " + Arena.getActualRowFromRow(getCurRow()) + ", "
						+ getCurCol() + ", "  + getCurOrientation() + ": ");
				Scanner sc = new Scanner(System.in);
				realReadings =  sc.nextLine();


				m = Pattern.compile(SENSOR_READING_PATTERN).matcher(realReadings);
			} else {
				System.out.println("<<<");
				System.out.println("Sensor reading at " + Arena.getActualRowFromRow(getCurRow()) + ", "
						+ getCurCol() + ", "  + getCurOrientation() + ": ");
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
					rightBSensorReading = getRealShortSensorRreadings(Double.parseDouble(readingsArr[6]), RIGHT_B_SENSOR_THRESHOLD);
				} catch (NumberFormatException e) {
					rightBSensorReading = getRealShortSensorRreadings(80.0, RIGHT_B_SENSOR_THRESHOLD);
				}

				/*
				int leftSensorReading = getRealLeftSensorReadings(Double.parseDouble(readingsArr[1]));
				int frontLSensorReading = getRealShortSensorRreadings(Double.parseDouble(readingsArr[2]), FRONT_L_SENSOR_THRESHOLD);
				int frontMSensorReading = getRealShortSensorRreadings(Double.parseDouble(readingsArr[3]), FRONT_M_SENSOR_THRESHOLD);
				int frontMSensorReading = getRealShortSensorRreadings(Double.parseDouble(readingsArr[3]), FRONT_M_SENSOR_THRESHOLD);
				int frontRSensorReading = getRealShortSensorRreadings(Double.parseDouble(readingsArr[4]), FRONT_R_SENSOR_THRESHOLD);
				int rightFSensorReading = getRealShortSensorRreadings(Double.parseDouble(readingsArr[5]), RIGHT_F_SENSOR_THRESHOLD);
				int rightBSensorReading = getRealShortSensorRreadings(Double.parseDouble(readingsArr[6]), RIGHT_B_SENSOR_THRESHOLD);
				*/

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

	public boolean isAtGoalZone() {
		return (getCurRow() == GOAL_ZONE_ROW && getCurCol() == GOAL_ZONE_COL);
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
}