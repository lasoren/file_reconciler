import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


interface ServerFunctions {
	 void onFirstPacket2(String data);
	 void onSecondPacket(String data);
}

public class FRSocketServer extends SocketClient {
	
	private ServerFunctions serverListener;
	ServerSocket s;
	
	FRSocketServer (String ipaddr, int port, ServerFunctions mainThread) {
		super(ipaddr, port);
		this.serverListener = mainThread;
	}
	
	private boolean ListenForConnection() {
	    try {    
	    	this.s = new ServerSocket(this.port);
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
 	                 {
 	            	  if (streamIn.available() != 0) {
 	            	  	String line = streamIn.readUTF();
 	                    System.out.println(line);
 	                    if (line.equals("test2")) {
 	                    	this.serverListener.onFirstPacket2(line);
 	                    } else if (line.equals("test3")) {
 	                    	this.serverListener.onSecondPacket(line);
 	                    }
 	            	  }
 	                 }
 	                 catch(IOException ioe)
 	                
 	                 { System.out.println("Exception!");
 	                	 done = true;
 	                 }
 	             while (!this.sendQueue.isEmpty()) {

             		try {
             	        streamOut.writeUTF(this.sendQueue.poll());
             	        streamOut.flush();
             		} catch (IOException e) {
             			// TODO Auto-generated catch block
             			e.printStackTrace();
             		}
             	}
 	              }
 	           }
 	        } catch (Exception e) {
    	System.out.println("Error accepting");
    }
    }	
}

