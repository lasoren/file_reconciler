package com.bitsPlease.FileReconciler;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


interface ServerFunctions {
	 void serverOnTestPacket(String data);
	 void serverOnSecondTestPacket(String data);
	 void serverOnError(String err);
	 void serverOnConnect();
}

enum ServerOpcodes {
    test1, test2
}

public class FRSocketServer extends SocketClient {
	
	private ServerFunctions serverListener;
	private ServerSocket serv;
	private Socket socket;
	
	FRSocketServer (String ipaddr, int port, ServerFunctions mainThread) {
		super(ipaddr, port);
		this.serverListener = mainThread;
	}
	
	private boolean ListenForConnection() {
	    try {    
	    	this.serv = new ServerSocket(this.port);
	    	this.socket = this.serv.accept();
			this.streamOut = new DataOutputStream(socket.getOutputStream());
	        this.streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
	    	return true;
	    } catch (Exception e) {
	    	this.serverListener.serverOnError("Unable to listen for connection");
	    	return false;
	    }
	}
	
    public void run() {
    	boolean connect = this.ListenForConnection();
    	
    	if (!connect) {
    		this.serverListener.serverOnError("Error listening");
    	}
    	
    	serverListener.serverOnConnect();
 	    
    	while (true) {
    		try {
    			if (streamIn.available() != 0) {
    				String line = streamIn.readUTF();
    				System.out.println(line);
    				if (line.equals(ServerOpcodes.test1.name())) {
    					this.serverListener.serverOnTestPacket(line);
    				} else if (line.equals(ServerOpcodes.test2.name())) {
    					this.serverListener.serverOnSecondTestPacket(line);
    				}
    			}
    		} catch(IOException e) { 
    			this.serverListener.serverOnError("Error reading packet");
    		}
    		if (!this.processSendQueue()) {
    			this.serverListener.serverOnError("Error sending packets");
    		}
    	}
    }	
}

