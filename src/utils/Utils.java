package utils;

import java.util.concurrent.TimeUnit;

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
}
