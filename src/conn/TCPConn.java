package conn;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TCPConn {
	
	public static final String RPI_IP = "192.168.2.1";
	public static final int RPI_PORT = 45000;
	
	private static TCPConn mInstance;
	private Socket mSocket;
	private PrintWriter mWriterToRPI;
	private Scanner mScannerFromRPI;
	
	public TCPConn() {}
	
	public static TCPConn getInstance() {
		if (mInstance == null)
			mInstance = new TCPConn();
		return mInstance;
	}
	
	public static void main(String[] args) throws IOException {
		TCPConn tcpConn = new TCPConn();
		tcpConn.readMessageArduino();

		/*
		TCPConn conn = TCPConn.getInstance();
		conn.instantiateConnection(RPI_IP, RPI_PORT);
		System.out.println("Connection Successful!");
		while (true)
		{
			conn.sendMessage(Constants.READ_SENSOR_VALUES);
			String msgReceived = conn.readMessageArduino();
			System.out.println("Message received: "+ msgReceived);
		}
		*/
	}
	
	public void instantiateConnection (String ip, int port) throws IOException {
		mSocket = new Socket(RPI_IP, RPI_PORT);
		mWriterToRPI = new PrintWriter(mSocket.getOutputStream());
		mScannerFromRPI = new Scanner(mSocket.getInputStream());
	}
	
	public void endConnection () throws IOException {
		if (mSocket.isClosed() != true)
			mSocket.close();
	}
	
	public void sendMessage(String msg) {
		mWriterToRPI.print(msg);
		mWriterToRPI.flush();
		System.out.println("Message sent: " + msg);
	}

	public String readMessageArduino() {
		String msgReceived = mScannerFromRPI.nextLine().trim();
		mScannerFromRPI.nextLine();
		System.out.println("Message received: " + msgReceived);
		return msgReceived;
	}

	public String readMessage() {
		String msgReceived = mScannerFromRPI.nextLine().trim();
		System.out.println("Message received: " + msgReceived);
		return msgReceived;
	}

}
