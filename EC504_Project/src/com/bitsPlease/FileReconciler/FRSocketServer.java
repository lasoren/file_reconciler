package com.bitsPlease.FileReconciler;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;


interface ServerFunctions {
	 void serverOnHashData(JSONObject payload);
	 void serverOnRawData(JSONObject payload);
	 void serverOnDirectoryData(JSONObject payload);
	 void serverOnError(String err);
	 void serverOnConnect();
}

enum ServerOpcodes {
	hashData, rawData, directoryData
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
	        this.streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream())); //new
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
    		return;
    	}
    	
    	serverListener.serverOnConnect();
 	    
    	while (true) {
    		try {
    			if (streamIn.available() != 0) {
    				String line = streamIn.readUTF();
    				//System.out.println(line);
    				JSONObject response;
					try {
						response = new JSONObject(line);
						if (response.optString("opcode").equals(ServerOpcodes.hashData.name())) {
							serverListener.serverOnHashData(response.optJSONObject("payload"));
						} else if (response.optString("opcode").equals(ServerOpcodes.rawData.name())) {
							serverListener.serverOnRawData(response.optJSONObject("payload"));
						} else if (response.optString("opcode").equals(ServerOpcodes.rawData.name())) {
							serverListener.serverOnDirectoryData(response.optJSONObject("payload"));
						}
					} catch (JSONException e) {
						this.serverListener.serverOnError("Error parsing response");
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

