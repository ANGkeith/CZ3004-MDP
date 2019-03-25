import conn.TCPConn;
import utils.API;
import utils.FileReaderWriter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.concurrent.TimeUnit;

import static models.Constants.*;

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



        tcpConn.sendMessage("arE;");
        tcpConn.sendMessage(CALIBRATE_FRONT_INSTRUCTION_TO_ARDUINO);
        tcpConn.sendMessage(TURN_LEFT_INSTRUCTION_TO_ARDUINO);
        tcpConn.sendMessage(CALIBRATE_FRONT_INSTRUCTION_TO_ARDUINO);
        tcpConn.sendMessage(TURN_LEFT_INSTRUCTION_TO_ARDUINO);
        tcpConn.sendMessage(CALIBRATE_RIGHT_INSTRUCTION_TO_ARDUINO);

        for (int i = 0; i<10; i++ ) {
            tcpConn.sendMessage(FORWARD_INSTRUCTION_TO_ARDUINO);
            tcpConn.sendMessage(FORWARD_INSTRUCTION_TO_ARDUINO);

            tcpConn.sendMessage(TURN_RIGHT_INSTRUCTION_TO_ARDUINO);
            tcpConn.sendMessage(FORWARD_INSTRUCTION_TO_ARDUINO);
            tcpConn.sendMessage(FORWARD_INSTRUCTION_TO_ARDUINO);
            tcpConn.sendMessage(FORWARD_INSTRUCTION_TO_ARDUINO);
            tcpConn.sendMessage(FORWARD_INSTRUCTION_TO_ARDUINO);
            tcpConn.sendMessage(FORWARD_INSTRUCTION_TO_ARDUINO);
            tcpConn.sendMessage(TURN_RIGHT_INSTRUCTION_TO_ARDUINO);
        }


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
