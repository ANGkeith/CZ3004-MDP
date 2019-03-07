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

import static models.Constants.*;

public class MyRobot {
	public static final String REPAINT = "repaint";
	public static final String UPDATE_GUI_BASED_ON_SENSOR = "updateGuiBasedOnSensor";

	private int curRow;
	private int curCol;
	private int startRow = DEFAULT_START_ROW;
	private int startCol = DEFAULT_START_COL;
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
	public boolean isRealRun = false;
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
		this.isRealRun = true;
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

	public String constructMessageForRpi(String instruction) {
		// TODO clean up
		int nextRow;
		int nextCol;
		Orientation nextOrientation;
		nextRow = getCurRow();
		nextCol = getCurCol();
		nextOrientation = getCurOrientation();
		if (instruction.equals(FORWARD_INSTRUCTION_TO_ARDUINO)) {
			if (curOrientation == Orientation.N) {
				nextRow = curRow - 1;
			} else if (curOrientation == Orientation.E) {
				nextCol = curCol + 1;
			} else if (curOrientation == Orientation.S) {
				nextRow = curRow + 1;
			} else if (curOrientation == Orientation.W) {
				nextCol = curCol - 1;
			}
		} else if (instruction.equals(TURN_RIGHT_INSTRUCTION_TO_ARDUINO)) {
			if (curOrientation == Orientation.N) {
				nextOrientation = Orientation.E;
			} else if (curOrientation == Orientation.E) {
				nextOrientation = Orientation.S;
			} else if (curOrientation == Orientation.S) {
				nextOrientation = Orientation.W;
			} else if (curOrientation == Orientation.W) {
				nextOrientation = Orientation.N;
			}
		} else if (instruction.equals(TURN_LEFT_INSTRUCTION_TO_ARDUINO)) {
			if (curOrientation == Orientation.N) {
				nextOrientation = Orientation.W;
			} else if (curOrientation == Orientation.E) {
				nextOrientation = Orientation.N;
			} else if (curOrientation == Orientation.S) {
				nextOrientation = Orientation.E;
			} else if (curOrientation == Orientation.W) {
				nextOrientation = Orientation.S;
			}
		}
		String myPosition = nextCol + "," + Arena.getRowFromActualRow(nextRow) + "," + nextOrientation.toString();
		return RPI_IDENTIFIER + myPosition;
	}

	public void forward() {

		if (!hasObstacleRightInFront()) {
			if (isRealRun) {
				try {
					if (!SimulatorController.test) {
						tcpConn.sendMessage(FORWARD_INSTRUCTION_TO_ARDUINO);
						tcpConn.sendMessage(constructMessageForAndroid());
						tcpConn.sendMessage(constructMessageForRpi(FORWARD_INSTRUCTION_TO_ARDUINO));
					} else {
						System.out.println("F");
						System.out.println(constructMessageForRpi(FORWARD_INSTRUCTION_TO_ARDUINO));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			SimulatorController.numFwd++;

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
				updateSensorsWithRealReadings();
			}
			pcs.firePropertyChange(UPDATE_GUI_BASED_ON_SENSOR, null, null);

		}
	}

	public void turnRight() {

		if (isRealRun) {
			try {
				if (!SimulatorController.test) {
					tcpConn.sendMessage(TURN_RIGHT_INSTRUCTION_TO_ARDUINO);
					tcpConn.sendMessage(constructMessageForAndroid());
					tcpConn.sendMessage(constructMessageForRpi(TURN_RIGHT_INSTRUCTION_TO_ARDUINO));
				} else {
					System.out.println("R");
					System.out.println(constructMessageForRpi(TURN_RIGHT_INSTRUCTION_TO_ARDUINO));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		SimulatorController.numTurn++;
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
			updateSensorsWithRealReadings();
		}
		pcs.firePropertyChange(UPDATE_GUI_BASED_ON_SENSOR, null, null);
	}

	public void turnLeft() {

		if (isRealRun) {
			try {
				if (!SimulatorController.test) {
					tcpConn.sendMessage(TURN_LEFT_INSTRUCTION_TO_ARDUINO);
					tcpConn.sendMessage(constructMessageForAndroid());
					tcpConn.sendMessage(constructMessageForRpi(TURN_LEFT_INSTRUCTION_TO_ARDUINO));
				} else {
					System.out.println("L");
					System.out.println(constructMessageForRpi(TURN_LEFT_INSTRUCTION_TO_ARDUINO));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		SimulatorController.numTurn++;
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
		if (isRealRun) {
			updateSensorsWithRealReadings();
		}
		pcs.firePropertyChange(UPDATE_GUI_BASED_ON_SENSOR, null, null);
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

	public boolean hasObstacleRightInFront() {
		for (int i = 0; i < frontSensor.length; i++) {
			if (frontSensor[i].getSensorReading() == 1) {
				return true;
			}
		}
		return false;
	}

	public boolean hasObstacleToItsImmediateLeft() {
		for (int i = 0; i < leftSensor.length; i++) {
			if (leftSensor[i].getSensorReading() == 1) {
				return true;
			}
		}
		return false;
	}

	public void updateSensorsWithRealReadings() {
		boolean messageFound;
		String realReadings;
		try {
			if (SimulatorController.test) {
				System.out.println("Enter a sensor reading at " + Arena.getActualRowFromRow(getCurRow()) + ", "
						+ getCurCol() + ", "  + getCurOrientation() + ": ");
				Scanner sc = new Scanner(System.in);
				realReadings =  sc.nextLine();


				m = Pattern.compile(SENSOR_READING_PATTERN).matcher(realReadings);
			} else {
				realReadings = tcpConn.readMessage();
				do {
					m = Pattern.compile(SENSOR_READING_PATTERN).matcher(realReadings);
					messageFound = m.find();
				} while (!messageFound);
			}

			realReadings = realReadings.substring(2);


			leftSensor[0].setRealReading(Character.getNumericValue(realReadings.charAt(0)));
			frontSensor[0].setRealReading(Character.getNumericValue(realReadings.charAt(1)));
			frontSensor[1].setRealReading(Character.getNumericValue(realReadings.charAt(2)));
			frontSensor[2].setRealReading(Character.getNumericValue(realReadings.charAt(3)));
			rightSensor[0].setRealReading(Character.getNumericValue(realReadings.charAt(4)));
			rightSensor[1].setRealReading(Character.getNumericValue(realReadings.charAt(5)));

			System.out.println("<<<<");
			System.out.print("Readings at " + Arena.getActualRowFromRow(getCurRow()) + "," + getCurCol() + ": ");
			System.out.print("\t L: " + Character.getNumericValue(realReadings.charAt(0)));
			System.out.print("   F: " + Character.getNumericValue(realReadings.charAt(1))
					+ Character.getNumericValue(realReadings.charAt(2))
					+ Character.getNumericValue(realReadings.charAt(3))
			);
			System.out.print("   R: " + Character.getNumericValue(realReadings.charAt(4))
					+ Character.getNumericValue(realReadings.charAt(5))
					+ "\n"
			);
			System.out.println(">>>>");

			pcs.firePropertyChange(MyRobot.UPDATE_GUI_BASED_ON_SENSOR, null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	}

	public int getStartCol() {
		return startCol;
	}

	public void setStartCol(int startCol) {
		this.startCol = startCol;
	}

	public Orientation getStartOrientation() {
		return startOrientation;
	}

	public void setStartOrientation(Orientation startOrientation) {
		this.startOrientation = startOrientation;
	}

	public boolean isRealRun() {
		return isRealRun;
	}

	public void setStartRowColOri(int row, int col, Orientation orientation) {
		setStartRow(row);
		setStartCol(col);
		setStartOrientation(orientation);
	}
}