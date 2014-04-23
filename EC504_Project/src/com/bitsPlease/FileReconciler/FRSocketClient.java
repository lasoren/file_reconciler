package com.bitsPlease.FileReconciler;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;


interface ClientFunctions {
	 void clientOnHashData(JSONObject payload);
	 void clientOnRawData(JSONObject payload);
	 void clientOnError(String msg, ClientErrors code);
	 void clientOnConnect();
	 void clientOnDirectoryData(JSONObject payload);
	 void clientOnStartFile(JSONObject payload);
}

enum ClientOpcodes {
	hashData, rawData, directoryData, startFile, clientDone
}
enum ClientErrors {
	errNoHost, errSend, errParse, errRead, errForm, errClose
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
	    	return false;
	    }
	}
	
    public void run() {
    	boolean connect = this.EstablishConnection();
    	
    	if (!connect) {
    		this.clientListener.clientOnError("Error establishing connection", ClientErrors.errNoHost);
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
							if (response.optString("opcode").equals(ClientOpcodes.hashData.name())) {
								clientListener.clientOnHashData(response.optJSONObject("payload"));
							} else if (response.optString("opcode").equals(ClientOpcodes.rawData.name())) {
								clientListener.clientOnRawData(response.optJSONObject("payload"));
							} else if (response.optString("opcode").equals(ClientOpcodes.directoryData.name())) {
								clientListener.clientOnDirectoryData(response.optJSONObject("payload"));
							} else if (response.optString("opcode").equals(ClientOpcodes.startFile.name())) {
								clientListener.clientOnStartFile(response.optJSONObject("payload"));
							} else if (response.optString("opcode").equals(ClientOpcodes.clientDone.name())) {
								JSONObject packet = new JSONObject();
								packet.put("opcode", ServerOpcodes.clientDone.name());
								Main.r.send(packet.toString());
							}
						} catch (JSONException e) {
							this.clientListener.clientOnError("Error parsing response", ClientErrors.errParse);
						}
					}
			} catch (IOException e1) {
				this.clientListener.clientOnError("Error reading packet", ClientErrors.errRead);
			}
    		if (!this.processSendQueue()) {
    			this.clientListener.clientOnError("Error sending packets", ClientErrors.errSend);
    		}
        }
    }
    
	@Override
	public void close() {
		try {
			this.s.close();
		} catch (IOException e) {
			this.clientListener.clientOnError("Error closing socket", ClientErrors.errClose);
		}
	}	
}
