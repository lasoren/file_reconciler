import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

interface ClientFunctions {
	 void onFirstPacket(String data);
	 void connected();
}

public class FRSocketClient extends SocketClient {
	
	private ClientFunctions clientListener;
	private Socket s;

	FRSocketClient (String ipaddr, int port, ClientFunctions mainThread) {
		super(ipaddr, port);
		this.clientListener = mainThread;
	}
	
	private boolean EstablishConnection() {
	    try {    
	    	this.s = new Socket(this.server, this.port);
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
    	clientListener.connected();

    	DataInputStream streamIn = new DataInputStream(new BufferedInputStream(this.s.getInputStream()));
		DataOutputStream streamOut = new DataOutputStream(this.s.getOutputStream());
			String line = "";
            while (true)
            {  
            	if (streamIn.available() != 0) {
					line = streamIn.readUTF();
					System.out.println(line);
					if (line.equals("test1")) {
						clientListener.onFirstPacket(line);
					}
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
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
    }
   
   
	
}
