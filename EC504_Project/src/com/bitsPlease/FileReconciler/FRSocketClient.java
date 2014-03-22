package com.bitsPlease.FileReconciler;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

interface ClientFunctions {
	 void clientOnTestPacket(String data);
	 void clientOnConnect();
	 void clientOnError(String err);
}

enum ClientOpcodes {
    test1
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
	    	this.streamIn = new DataInputStream(new BufferedInputStream(this.s.getInputStream()));
			this.streamOut = new DataOutputStream(this.s.getOutputStream());
	    	return true;
	    } catch (Exception e) {
	    	this.clientListener.clientOnError("Unable to connect");
	    	return false;
	    }
	}
	
    public void run() {
    	boolean connect = this.EstablishConnection();
    	
    	if (!connect) {
    		this.clientListener.clientOnError("Error establishing connection");
    	}
    	
    	clientListener.clientOnConnect();

		String line = "";
        while (true) {  
        	try {
				if (streamIn.available() != 0) {
						line = streamIn.readUTF();
						System.out.println(line);
						if (line.equals(ClientOpcodes.test1.name())) {
							clientListener.clientOnTestPacket(line);
						}
					}
			} catch (IOException e1) {
				this.clientListener.clientOnError("Error reading packet");
			}
    		if (!this.processSendQueue()) {
    			this.clientListener.clientOnError("Error sending packets");
    		}
        }
    }
}
