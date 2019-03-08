package utils;

import models.Arena;

import static models.Constants.*;
public class AndroidApi {
    private static String[] getPayload(String message) {
        return message.substring(8).split(",");
    }

    public static int getRow(String androidMessage) {
        String[] payload =  getPayload(androidMessage);
        int actualRow = Integer.parseInt(payload[1], 10);
        return Arena.getRowFromActualRow(actualRow);
    }

    public static int getCol(String androidMessage) {
        String[] payload =  getPayload(androidMessage);
        return Integer.parseInt(payload[0], 10);
    }

    public static Orientation getOrientation(String androidMessage) {
        String[] payload =  getPayload(androidMessage);
        return Orientation.valueOf(payload[2]);
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
