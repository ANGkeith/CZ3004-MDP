import conn.TCPConn;
import utils.API;
import utils.FileReaderWriter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.concurrent.TimeUnit;

import static models.Constants.LOG_FOR_CALIBRATION_PATH;
import static models.Constants.START_FASTEST;
import static utils.Utils.delay;
import static utils.Utils.longDelay;

public class testSensorValues {

    public final static int COUNT = 4;


    String fixedMapDescriptor = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,";

    public static void main(String[] args) {
        TCPConn tcpConn;
        String sensorValues;

        String instructions = "WWWWWWWWWWWWWWWDWWWWWWWDWWWWAWWWWWAWWWWWW";
        System.out.println(API.constructPathForArduino(instructions));

        tcpConn = TCPConn.getInstance();
        clearLogFile();

        int fwdDelay = 300;
        int turnDelay = 300;

        System.out.println("Waiting for connection");
        try {
            tcpConn.instantiateConnection(TCPConn.RPI_IP, TCPConn.RPI_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Successfully Connected!");


        String message = tcpConn.readMessage();
        while(!message.contains(START_FASTEST)) {
            System.out.println("Expecting start fastest path but received: " + message);
            message = tcpConn.readMessage();
        }

        tcpConn.sendMessage("an1,1,N,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");

        longDelay();

        tcpConn.sendMessage(API.constructPathForArduino(instructions));

        tcpConn.sendMessage("an1,2,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an1,3,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an1,4,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an1,5,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an1,6,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an1,7,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an1,8,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an1,9,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an1,10,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an1,11,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an1,12,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an1,13,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an1,14,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an1,15,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an1,16,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an1,16,E,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an2,16,E,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an3,16,E,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an4,16,E,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an5,16,E,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an6,16,E,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an7,16,E,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an8,16,E,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an8,16,S,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an8,15,S,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an8,14,S,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an8,13,S,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an8,12,S,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an8,12,E,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an9,12,E,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an10,12,E,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an11,12,E,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an12,12,E,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an13,12,E,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an13,12,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an13,13,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an13,14,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an13,15,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an13,16,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an13,17,N,");
		hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an13,18,N,");
		hardcodedelay(fwdDelay);


       /* for (int i = 0; i<100; i++ ) {
            tcpConn.sendMessage("arD;");
        }*/


/*
        tcpConn.readMessage();
        tcpConn.readMessageArduino();
        for (int i = 0; i < COUNT - 1; i++) {
            sensorValues = tcpConn.readMessageArduino();
            String[] readingsArr;
            readingsArr = sensorValues.split(",");
            // remove header
            sensorValues = sensorValues.substring(2);

            try {
                FileReaderWriter fileWriter = new FileReaderWriter(FileSystems.getDefault().getPath(LOG_FOR_CALIBRATION_PATH));
                fileWriter.logMsg("(" + i + ") " +
                        sensorValues.charAt(0) + " " + sensorValues.substring(1, 4) + " " + sensorValues.substring(4, 6) +
                        " " + readingsArr[1] + "\n", true);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            tcpConn.sendMessage("arD");

        }
        tcpConn.readMessageArduino();
        */
    }


    public static void clearLogFile() {
        try {
            FileReaderWriter fileWriter = new FileReaderWriter(FileSystems.getDefault().getPath(LOG_FOR_CALIBRATION_PATH));
            fileWriter.logMsg("", false);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    public static void hardcodedelay(int milliSeconds) {
        try{
            TimeUnit.MILLISECONDS.sleep(milliSeconds);
        } catch	(InterruptedException e) {
            e.printStackTrace();
        }
    }

}
