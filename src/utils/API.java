package utils;

import models.Arena;
import models.MyRobot;

import java.util.ArrayList;

import static models.Constants.*;
public class API {

    public static String constructMessageForAndroid(MyRobot myRobot) {
        String p0 = myRobot.getCurCol() + "," + Arena.getRowFromActualRow(myRobot.getCurRow()) + "," + myRobot.getCurOrientation().toString();
        String p1 = myRobot.getArena().generateMapDescriptorP1();
        String p2 = myRobot.getArena().generateMapDescriptorP2();

        String toAndroid = "an"+ p0 + "," + p1 + "," + p2 + ",";
        return toAndroid;
    }

	public static String constructP0ForAndroid(MyRobot myRobot) {
		String p0 = myRobot.getCurCol() + "," + Arena.getRowFromActualRow(myRobot.getCurRow()) + "," + myRobot.getCurOrientation().toString();
		return "an" + p0;
	}

	public static String constructMessageForRpi(MyRobot myRobot) {
		String myPosition = myRobot.getCurCol() + "," + Arena.getRowFromActualRow(myRobot.getCurRow()) + "," + myRobot.getCurOrientation().toString();
		return RPI_IDENTIFIER + myPosition.toLowerCase();
	}

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

    public static String constructSwervePathForArduino(String instructions) {
        ArrayList arrayList = new ArrayList();

        int count = 0;
        for (int i = 0; i < instructions.length(); i++) {
            if (instructions.charAt(i) == 'W') {
                count++;
            } else {
                if (count != 0) {
                    arrayList.add(count);
                }
                arrayList.add(instructions.charAt(i));
                count = 0;
            }
        }
        if (count != 0) {
            arrayList.add(count);
        }

        int prev;
        int next;
        for (int j = 0; j < arrayList.size(); j++) {
            if (arrayList.get(j) instanceof Character) {
                prev = (int) arrayList.get(j - 1) - 1;
                arrayList.add(j - 1, prev);
                arrayList.remove(j);
                next = (int) arrayList.get(j + 1) - 1;
                arrayList.add(j + 1, next);
                arrayList.remove(j + 2);
            }
        }

        int lastElement = (int) arrayList.get(arrayList.size() - 1);


        if (lastElement == 0) {
            arrayList.remove(arrayList.size() - 1);
            arrayList.add('g');
        } else {
            arrayList.remove(arrayList.size() - 1);
            arrayList.add(lastElement - 1);
            arrayList.add('H');
        }

        int part1;
        int part2;
        for (int k = 0; k < arrayList.size(); k++) {
            if (arrayList.get(k) instanceof Integer) {
                if ((int) arrayList.get(k) >= 10) {
                    part1 = 9;
                    part2 = (int) arrayList.get(k) - 9;
                    arrayList.remove(k);
                    arrayList.add(k, part1);
                    arrayList.add(k + 1, part2);
                }
            }
        }
        String formattedInstructions = "";
        for (Object o: arrayList) {
            formattedInstructions += o;
        }
        return ARD_IDENTIFIER + formattedInstructions;
    }

    public static String constructPathForArduino(String instructions) {

        String formattedInstructions = "";
        int count = 0;
        for (int i = 0; i < instructions.length()-1; i++) {
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
        formattedInstructions += "H";
        return ARD_IDENTIFIER + formattedInstructions;
    }
}
