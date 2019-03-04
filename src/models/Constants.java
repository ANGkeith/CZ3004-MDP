package models;

import java.awt.Color;

public class Constants {
    // Arena Settings
    public static final int ARENA_WIDTH = 15;
    public static final int ARENA_HEIGHT = 20;
    public static final int ZONE_SIZE = 3;
    public static final int ARENA_DIVIDER_LINE_THICKNESS = 4;
    public static final int GRID_SIZE = 25;
    public static final int GOAL_ZONE_ROW = 1;
    public static final int GOAL_ZONE_COL = 13;
    public static final int START_ZONE_ROW = 18;
    public static final int START_ZONE_COL = 1;

    public static final int DEFAULT_START_ROW = 18;
    public static final int DEFAULT_START_COL = 1;
    public static final Orientation DEFAULT_START_ORIENTATION = Orientation.E;

    public static final int MOVE_COST = 1;
    public static final int TURN_COST = 1;
    public static final int INFINITY = 999;

    public static final int DEFAULT_COVERAGE_LIMIT = 100;
    public static final int DEFAULT_TIME_LIMIT = 120;

    // Color
    public static final Color ARENA_LABEL_COLOR = Color.darkGray;
    public static final Color EXPLORED_COLOR = Color.white;
    public static final Color UNEXPLORED_COLOR = Color.black;
    public static final Color OBSTACLE_COLOR = Color.RED;
    public static final Color ROBOT_COLOR = Color.black;
    public static final Color ARENA_DIVIDER_LINE_COLOR = Color.BLUE;
    public static final Color ARENA_GRID_LINE_COLOR = Color.darkGray;
    public static final Color GOAL_ZONE_COLOR = Color.MAGENTA;
    public static final Color START_ZONE_COLOR = new Color (50, 226, 36);
    public static final Color ORIENTATION_MARKER_COLOR = Color.WHITE;
    public static final Color SENSOR_RANGE_COLOR = new Color(212, 255, 46, 100);
    public static final Color PATH_TAKEN_COLOR = new Color(80, 65, 255, 132);

    public static final String ARENA_DESCRIPTOR_PATH = System.getProperty("user.dir") + "/local_storage/mock_arena.txt";

    public static final String[] orientationList = new String[]{"North", "East", "South", "West"};

    public static final String FORWARD = "arW";
    public static final String TURN_LEFT = "arA";
    public static final String TURN_RIGHT = "arD";
	public static final String EXPLORE_DONE = "F";
	public static final String SEPARATOR = "|";
	public static final String READ_SENSOR_VALUES = "E";
	public static final String DONE = "Y";
	public static final String START_EXPLORATION = "explore";
	public static final String START_SHORTEST = "short";
	public static final String CALIBRATE = "C";
	public static final String SENSOR_READING_PATTERN = "\\d{6}";
	public static final String CALIBRATE_PATTERN = ":1:[0-9]:1:[0-9]:[0-9][|]";

    public enum Orientation {
        N,
        E,
        S,
        W,
    }

    public enum ExplorationType {
        NORMAL,
        TIME_LIMITED,
        COVERAGE_LIMITED,
    }

    // this position is relative to the orientation of the robot
    public enum Sensor_Position {
        FRONT,
        RIGHT,
        LEFT,
    }
}