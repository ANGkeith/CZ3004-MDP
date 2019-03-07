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

    public static String constructMessageForAndroid(String... datas) {
        String message = "an";
        for (String d: datas) {
            message = message + d + ",";
        }
        return message;
    }
}
