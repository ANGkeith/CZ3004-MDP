package utils;

import java.util.concurrent.TimeUnit;
import static models.Constants.*;

public class Utils {
    public static void delay() {
        try{
            TimeUnit.MILLISECONDS.sleep(350);
        } catch	(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void longDelay() {
        try{
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch	(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void longDelay2() {
        try{
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch	(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Object[] parsePositionToTakePic(String positionToTakePic) {
        String[] arg = positionToTakePic.split(",");
        Object[] result = new Object[3];
        result[0] = Integer.parseInt(arg[0], 10);
        result[1] = Integer.parseInt(arg[1], 10);

        if (arg[2].equals("N")) {
            result[2] = Orientation.N;
        } else if (arg[2].equals("E")) {
            result[2] = Orientation.E;
        } else if (arg[2].equals("S")) {
            result[2] = Orientation.S;
        } else if (arg[2].equals("W")) {
            result[2] = Orientation.W;
        }

        return result;
    }
}
