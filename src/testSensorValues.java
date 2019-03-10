import conn.TCPConn;
import utils.FileReaderWriter;

import java.io.IOException;
import java.nio.file.FileSystems;

import static models.Constants.LOG_FOR_CALIBRATION_PATH;

public class testSensorValues {

    public final static int COUNT = 8;

    public static void main(String[] args) {
        TCPConn tcpConn;
        String sensorValues;

        tcpConn = TCPConn.getInstance();
        clearLogFile();

        System.out.println("Waiting for connection");
        try {
            tcpConn.instantiateConnection(TCPConn.RPI_IP, TCPConn.RPI_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Successfully Connected!");

        String msg = tcpConn.readMessageArduino();
        System.out.println(msg);

        tcpConn.sendMessage("arW");

        for (int i = 0; i < COUNT - 1; i++) {
            sensorValues = tcpConn.readMessage();
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
            tcpConn.sendMessage("arW");

        }
    }


    public static void clearLogFile() {
        try {
            FileReaderWriter fileWriter = new FileReaderWriter(FileSystems.getDefault().getPath(LOG_FOR_CALIBRATION_PATH));
            fileWriter.logMsg("", false);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
