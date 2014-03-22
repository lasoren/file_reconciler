import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
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


public class FRSocketServer implements Runnable {
	
	ServerSocket s;
	boolean connected = false;
	boolean listening = false;
	String host;
	int port;
	
	FRSocketServer (String ipaddr, int port) {
		this.host = ipaddr;
		this.port = port;
	}
	
	private boolean ListenForConnection() {
	    try {    
	    	this.s = new ServerSocket(this.port);
	    	this.listening = true;
	    	return true;
	    } catch (Exception e) {
	    	System.out.println("Unable to connect");
	    	return false;
	    }
	}
	
    public void run() {
    	boolean listen = this.ListenForConnection();
    	
    	if (!listen) {
    		System.out.println("Error listening");
    	} else {
    		System.out.println("Listening");
    	}
    	try {
    	
 	        while (true) {
 	            Socket socket = this.s.accept();
 	            System.out.println("Accepted conn");
 				DataOutputStream streamOut = new DataOutputStream(socket.getOutputStream());
 		        
 	             DataInputStream streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

 	              boolean done = false;
 	              while (!done)
 	              {  try
 	                 {  //String line = streamIn.readUTF();
 	                    //System.out.println(line);
 	                    //done = line.equals("{end}");
 	  		        streamOut.writeUTF("testing");
 	 		        streamOut.flush();
 	 		        done=true;
 	                 }
 	                 catch(IOException ioe)
 	                 {  done = true;
 	                 }
 	              }
 	           }
 	        } catch (Exception e) {
    	System.out.println("Error accepting");
    }
    }	
}

