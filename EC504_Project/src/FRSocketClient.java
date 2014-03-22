import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;

public class FRSocketClient implements Runnable {
	
	Socket s;
	boolean connected = false;
	String server;
	int port;
	
	FRSocketClient (String ipaddr, int port) {
		this.server = ipaddr;
		this.port = port;
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
	
    public void run() {
    	try {
    		
    	
    	boolean connect = this.EstablishConnection();
    	if (!connect) {
    		System.out.println("Error connecting");
    	}
    	DataInputStream console;
    	DataOutputStream streamOut = new DataOutputStream(this.s.getOutputStream());

    	console = new DataInputStream(System.in); //new BufferedReader(new InputStreamReader(s.getInputStream()));

    	
			String line = "";
            while (!line.equals("{end}"))
            {  
            	
				line = console.readLine();

                  System.out.println(line);
                  streamOut.writeUTF(line);
                  streamOut.flush();
            }
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
    }
   
   
	
}
