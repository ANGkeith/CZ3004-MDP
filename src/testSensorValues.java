import conn.TCPConn;
import utils.API;
import utils.FileReaderWriter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.concurrent.TimeUnit;

import static models.Constants.LOG_FOR_CALIBRATION_PATH;

public class testSensorValues {

    public final static int COUNT = 4;


    String fixedMapDescriptor = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,";

    public static void main(String[] args) {
        TCPConn tcpConn;
        String sensorValues;

        String instructions = "WWWAWWWWWWWWWWWWWWDWWAWWWDWWWWWWW";
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



        tcpConn.sendMessage("an1,1,E,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");

        tcpConn.sendMessage("ar3A95D2A3D7");


        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an2,1,E,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an3,1,E,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an4,1,E,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(turnDelay);
        tcpConn.sendMessage("an4,1,N,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an4,2,N,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an4,3,N,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an4,4,N,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an4,5,N,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an4,6,N,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an4,7,N,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an4,8,N,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an4,9,N,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an4,10,N,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an4,11,N,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an4,12,N,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an4,13,N,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an4,14,N,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an4,15,N,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(turnDelay);
        tcpConn.sendMessage("an4,15,E,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an5,15,E,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an6,15,E,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(turnDelay);
        tcpConn.sendMessage("an6,15,N,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an6,16,N,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an6,17,N,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an6,18,N,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(turnDelay);
        tcpConn.sendMessage("an6,18,E,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an7,18,E,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an8,18,E,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an9,18,E,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an10,18,E,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an11,18,E,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an12,18,E,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");
        hardcodedelay(fwdDelay);
        tcpConn.sendMessage("an13,18,E,ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,001000200040078000000038001000000004000820107e30040000000061008020004000800,");

        /*
        for (int i = 0; i<100; i++ ) {
            tcpConn.sendMessage("anD");
        }
        /*

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
