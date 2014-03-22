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

interface ClientFunctions {
	 void onFirstPacket(String data);
}

public class FRSocketClient implements Runnable {
	
	Socket s;
	boolean connected = false;
	String server;
	int port;
	private ClientFunctions clientListener;
	
	FRSocketClient (String ipaddr, int port, ClientFunctions mainThread) {
		this.server = ipaddr;
		this.port = port;
		this.clientListener = mainThread;
	}
	
	private boolean EstablishConnection() {
	    try {    
	    	this.s = new Socket(this.server, this.port);
	    	this.connected = true;
	    	return true;
	    } catch (Exception e) {
	    	System.out.println("Unable to connect");
	    	return false;
	    }
	}
	
	public void send(String line) {
    	DataOutputStream streamOut;
		try {
			streamOut = new DataOutputStream(this.s.getOutputStream());
	        streamOut.writeUTF(line);
	        streamOut.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
    public void run() {
    	try {
    		
    	
    	boolean connect = this.EstablishConnection();
    	if (!connect) {
    		System.out.println("Error connecting");
    	}

    	DataInputStream streamIn = new DataInputStream(new BufferedInputStream(this.s.getInputStream()));
    	
			String line = "";
            while (true)
            {  
            	
				line = streamIn.readUTF();
				System.out.println(line);
				if (line.equals("testing")) {
					clientListener.onFirstPacket(line);
				}
                

            }
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
    }
   
   
	
}
