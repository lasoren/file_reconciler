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
	 void serverOnHashResponse(JSONObject payload);
	 void serverOnConnect();
	 void serverOnError(String msg, ServerErrors code);
	 void serverOnDirectoryData(JSONObject payload);
}

enum ServerOpcodes {
   hashResponse, clientDone, directoryData
}

enum ServerErrors {
	   errRead, errSend, errParse, errClose, errListen
	}

public class FRSocketServer extends SocketClient {
	
	private ServerFunctions serverListener;
	private ServerSocket serv = null;
	private Socket socket;
	public static int clientNum = 1;
	
	FRSocketServer (String ipaddr, int port, ServerFunctions mainThread) {
		super(ipaddr, port);
		this.serverListener = mainThread;
	}
	
	private boolean ListenForConnection() {
	    try {
	    	if (this.serv == null) {
	    		this.serv = new ServerSocket(this.port);
	    	}
	    	this.socket = this.serv.accept();
			this.streamOut = new DataOutputStream(socket.getOutputStream());
	        this.streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream())); //new
	    	return true;
	    } catch (Exception e) {
	    	return false;
	    }
	}
	
    public void run() {
    	boolean connect = this.ListenForConnection();
    	
    	if (!connect) {
    		this.serverListener.serverOnError("Error listening", ServerErrors.errListen);
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
						if (response.optString("opcode").equals(ServerOpcodes.hashResponse.name())) {
							serverListener.serverOnHashResponse(response.optJSONObject("payload"));
						} else if (response.optString("opcode").equals(ServerOpcodes.clientDone.name())) {
					    	connect = this.ListenForConnection();
					    	
					    	if (!connect) {
					    		this.serverListener.serverOnError("Error listening", ServerErrors.errListen);
					    		return;
					    	}
					    	clientNum++;
					    	serverListener.serverOnConnect();
						} else if (response.optString("opcode").equals(ServerOpcodes.rawData.name())) {
							serverListener.serverOnDirectoryData(response.optJSONObject("payload"));
  						}
					} catch (JSONException e) {
						this.serverListener.serverOnError("Error parsing response", ServerErrors.errParse);
					}
    			}
    		} catch(IOException e) { 
    			this.serverListener.serverOnError("Error reading packet", ServerErrors.errRead);
    		}
    		if (!this.processSendQueue()) {
    			this.serverListener.serverOnError("Error sending packets", ServerErrors.errSend);
    		}
    	}
    }

	@Override
	public void close() {
		try {
			this.socket.close();
		} catch (IOException e) {
			this.serverListener.serverOnError("Error closing socket", ServerErrors.errClose);
		}
	}	
}

