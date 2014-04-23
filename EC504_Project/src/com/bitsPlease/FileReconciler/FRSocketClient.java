package com.bitsPlease.FileReconciler;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

interface ClientFunctions {
	 void clientOnHashResponse(JSONObject payload);
	 void clientOnConnect();
	 void clientOnDirectoryData(JSONObject payload);
	 void clientOnError(String err);
}

enum ClientOpcodes {
    hashResponse
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
	    	this.streamIn = new DataInputStream(new BufferedInputStream(this.s.getInputStream())); //new
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
    		return;
    	}
    	
    	clientListener.clientOnConnect();

		String line = "";
        while (true) {  
        	try {
				if (streamIn.available() != 0) {
						line = streamIn.readUTF();
						JSONObject response;
						try {
							response = new JSONObject(line);
							//System.out.println(line);
							if (response.optString("opcode").equals(ClientOpcodes.hashResponse.name())) {
								clientListener.clientOnHashResponse(response.optJSONObject("payload"));
							}
						} catch (JSONException e) {
							this.clientListener.clientOnError("Error parsing response");
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
