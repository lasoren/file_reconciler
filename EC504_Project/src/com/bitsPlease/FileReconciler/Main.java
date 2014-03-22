package com.bitsPlease.FileReconciler;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main implements ClientFunctions, ServerFunctions {

	static SocketClient r;
	byte fileArray[] = {'1','2','3','4','5','6','7','8','9','0','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','1','2','3','4','5','6','7','8','9','0','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','2','3','4','5','6','7','8','9','0','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','1','2','3','4','5','6','7','8','9','0','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','2','3','4','5','6','7','8','9','0','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','1','2','3','4','5','6','7','8','9','0','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','Z'};
	RecurrentHasher rh;
	
	public static void main(String args[]) {
		CommandLine.check(args);
		if (args[0].equals("client")) {
			Main.r = new FRSocketClient(CommandLine.getIP(), 42069, new Main(true));
			Main.r.start();
		} else if (args[0].equals("server")){
			Main.r = new FRSocketServer(CommandLine.getIP(), 42069, new Main(false));
			Main.r.start();
		} else {
			System.out.println("Error");
		}
	}
	
	Main(boolean client) {
		if (client) {
			fileArray[fileArray.length-10] = 'Q';
			fileArray[10] = 'F';
		}
		this.rh = new RecurrentHasher(this.fileArray, this.fileArray.length);
	}

	@Override
	public void clientOnHashResponse(JSONObject payload) {
		System.out.println("Client got server's response, sending final response to server");
		int recurrence = payload.optInt("recurrence");
		JSONArray indices = payload.optJSONArray("indices");
		int indicesarray[] = new int[indices.length()];
		for (int i = 0; i < indices.length(); i++) {
		    indicesarray[i] = indices.optInt(i);
		}
		JSONObject packet = rh.hashParts(recurrence, indicesarray);
		Main.r.send(packet.toString());
	}

	@Override
	public void serverOnHashData(JSONObject payload) {
		System.out.println("Server got packet from client. Responding with next packet");
		int recurrence = payload.optInt("recurrence");
		JSONArray indices = payload.optJSONArray("indices");
		JSONArray data = payload.optJSONArray("data");

		int indicesarray[] = new int[indices.length()];
		for (int i = 0; i < indices.length(); i++) {
		    indicesarray[i] = indices.optInt(i);
		}
		
		String dataarray[] = new String[data.length()];
		for (int i = 0; i < data.length(); i++) {
		    dataarray[i] = data.optString(i);
		}
		
		JSONObject packet = rh.compareParts(recurrence, indicesarray, dataarray);
		Main.r.send(packet.toString());
		
	}

	@Override
	public void serverOnRawData(JSONObject payload) {
		System.out.println("Server received raw data packet from client!");
		int recurrence = payload.optInt("recurrence");
		JSONArray indices = payload.optJSONArray("indices");
		JSONArray data = payload.optJSONArray("data");

		int indicesarray[] = new int[indices.length()];
		for (int i = 0; i < indices.length(); i++) {
		    indicesarray[i] = indices.optInt(i);
		}
		
		JSONArray dataarray[] = new JSONArray[data.length()];
		for (int i = 0; i < data.length(); i++) {
		    dataarray[i] = data.optJSONArray(i);
		}
		
		JSONObject packet = rh.compareParts(recurrence, indicesarray, dataarray);
		if (packet == null) {
			System.out.println("Reconciled!");		
			System.out.println(new String(this.fileArray));
		} else {
			Main.r.send(packet.toString());
		}
		
	}
	@Override
	public void clientOnConnect() {
		System.out.println("Client connected to server! Sending initial packet to server");
		JSONObject packet = rh.hashParts(0, new int[] {0});
		Main.r.send(packet.toString());
	}
	
	@Override
	public void serverOnConnect() {
		System.out.println("Connected to client!");
	}

	@Override
	public void serverOnError(String err) {
		System.out.println("Error: " + err);
	}

	@Override
	public void clientOnError(String err) {
		System.out.println("Error: " + err);	
	}
}
