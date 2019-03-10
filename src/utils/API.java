package utils;

import models.Arena;
import models.MyRobot;

import static models.Constants.*;
public class API {

    public static final String ANDROID = "ANDROID";

    public static void processStartExplorationMsg(String message, MyRobot myRobot) {
        // remove Header
        // sample input: "explore:10,5,N|1,18";
        message = message.substring(8);
        String[] startPosition  =  message.split("\\|")[0].split(",");
        String[] wayPointPosition = message.split("\\|")[1].split(",");

        myRobot.setStartCol(Integer.parseInt(startPosition[0], 10));
        myRobot.setStartRow(Arena.getRowFromActualRow(Integer.parseInt(startPosition[1], 10)));
        myRobot.setStartOrientation(Orientation.valueOf(startPosition[2]));
        myRobot.setWayPointCol(Integer.parseInt(wayPointPosition[0]));
        myRobot.setWayPointRow(Arena.getRowFromActualRow(Integer.parseInt(wayPointPosition[1], 10)));
    }

    public static void processStartFastestMsg(String message, MyRobot myRobot) {
        // remove Header
        // sample input: "fastest:10,5,N";
        message = message.substring(8);
        String[] startPosition  =  message.split(",");

        myRobot.setStartCol(Integer.parseInt(startPosition[0], 10));
        myRobot.setStartRow(Arena.getRowFromActualRow(Integer.parseInt(startPosition[1], 10)));
        myRobot.setStartOrientation(Orientation.valueOf(startPosition[2]));
    }


    public static String constructPathForArduino(String instructions) {

        String formattedInstructions = "";
        int count = 0;
        for (int i = 0; i < instructions.length(); i++) {
            if (count == 9) {
                formattedInstructions += 9;
                count = 0;
            }
            if (instructions.charAt(i) == 'W') {
                count++;
            } else {
                if (count != 0) {
                    formattedInstructions += count;
                }
                formattedInstructions += instructions.charAt(i);
                count = 0;
            }
        }
        if (count != 0) {
            formattedInstructions += count;
        }
        return "ad" + formattedInstructions;
    }
}
