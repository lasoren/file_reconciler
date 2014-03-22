import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class SocketClient extends Thread {
	
	protected String server;
	protected int port;
	protected BlockingQueue<String> sendQueue;
	protected DataInputStream streamIn;
	protected DataOutputStream streamOut;
	
	SocketClient (String ipaddr, int port) {
		this.server = ipaddr;
		this.port = port;
		this.sendQueue = new LinkedBlockingQueue<String>();
	}
	
	public synchronized void send(String line) {
    	sendQueue.add(line);
	}
	
	protected boolean processSendQueue() {
        while (!this.sendQueue.isEmpty()) {
        	try {
                streamOut.writeUTF(this.sendQueue.poll());
                streamOut.flush();
        	} catch (IOException e) {
        		return false;
        	}
        }
        return true;
	}
	
    public abstract void run();
	
}
