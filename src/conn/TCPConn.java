package conn;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import models.Constants;

public class TCPConn {
	
	public static final String RPI_IP = "192.168.2.1";
	public static final int RPI_PORT = 45000;
	
	private static TCPConn mInstance;
	private Socket mSocket;
	private PrintWriter mWriterToRPI;
	private Scanner mScannerFromRPI;
	
	private TCPConn() {}
	
	public static TCPConn getInstance() {
		if (mInstance == null)
			mInstance = new TCPConn();
		return mInstance;
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		TCPConn conn = TCPConn.getInstance();
		conn.instantiateConnection(RPI_IP, RPI_PORT);
		System.out.println("Connection Successful!");
		while (true)
		{
			conn.sendMessage(Constants.READ_SENSOR_VALUES);
			String msgReceived = conn.readMessage();
			System.out.println("Message received: "+ msgReceived);
		}
	}
	
	public void instantiateConnection (String ip, int port) throws UnknownHostException, IOException {
		mSocket = new Socket(RPI_IP, RPI_PORT);
		mWriterToRPI = new PrintWriter(mSocket.getOutputStream());
		mScannerFromRPI = new Scanner(mSocket.getInputStream());
	}
	
	public void endConnection () throws IOException {
		if (mSocket.isClosed() != true)
			mSocket.close();
	}
	
	public void sendMessage(String msg) throws IOException {
		mWriterToRPI.print(msg);
		mWriterToRPI.flush();
		System.out.println("Message sent: " + msg);
	}

	public String readMessage() throws IOException {
		String msgRecieved = mScannerFromRPI.nextLine();
		System.out.println("Message received: " + msgRecieved);
		return msgRecieved;
}
	
}
