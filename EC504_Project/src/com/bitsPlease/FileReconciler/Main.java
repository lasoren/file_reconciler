package com.bitsPlease.FileReconciler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Main implements ClientFunctions, ServerFunctions {

	static SocketClient r;
	byte fileArray[];
	RecurrentHasher rh;
	boolean isDirectory;
	File file;
	
	public static void main(String args[]) {
		try {
			CommandLine.check(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean thisIsDirectory = false;
		File folder = new File(CommandLine.getName());
		
		if(folder.isDirectory()){
			thisIsDirectory = true;
		}

		if (args[0].equals("client")) {
			Main.r = new FRSocketClient(CommandLine.getIP(), 42069, new Main(CommandLine.getName(), true, thisIsDirectory));
			Main.r.start();
		} else if (args[0].equals("server")){
			Main.r = new FRSocketServer(CommandLine.getIP(), 42069, new Main(CommandLine.getName(), false, thisIsDirectory));
			Main.r.start();
		} else {
			System.out.println("Error");
		}
	}
	
	Main(String fileName, boolean client, boolean isDirectory) {
		this.fileArray = FRFileIO.readIn(fileName);
		File folder = new File(fileName);
		this.file = folder;

		if (client) {
//			fileArray[fileArray.length-10] = 'Q';
//			fileArray[53223423] = 'Q';
//			fileArray[23423] = 'Q';
//			fileArray[80000000] = 'Q';
//			fileArray[2232344] = 'Q';
//			fileArray[1337] = 'F';
			
			//testing deletions fileArray
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
			try {
				outputStream.write(Arrays.copyOfRange(fileArray, 0, 2));
				outputStream.write(new byte[] {'Q'});
				outputStream.write(Arrays.copyOfRange(fileArray, 4, fileArray.length));
			} catch (IOException e) {
				e.printStackTrace();
			}
			fileArray = outputStream.toByteArray();
		}
		this.rh = new RecurrentHasher(this.fileArray, this.fileArray.length);
		this.isDirectory = isDirectory;
	}

	@Override
	public void clientOnHashResponse(JSONObject payload) {
		//System.out.println("Client got server's response, sending final response to server");
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
		//System.out.println("Server got packet from client. Responding with next packet");
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
		//System.out.println("Server received raw data packet from client!");
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
			//System.out.println("Reconciled!");		
			FRFileIO.writeOut(this.fileArray, "out.txt");
		} else {
			Main.r.send(packet.toString());
		}
		
	}
	
	@Override
	 public void serverOnDirectoryData(JSONObject payload){
		
		JSONArray fileArray = payload.optJSONArray("data");
		List<String> finalFileList = new ArrayList<String>();
		Collection<File> inputFiles = FileUtils.listFiles(this.file,
                new String[] {"txt" }, true);
		
	    for (File elem : inputFiles) {
	    	for (int i = 0; i < fileArray.length(); i++) {
	    		String jsonFile = null;
	    		  try {
					jsonFile = fileArray.getString(i);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		if(jsonFile.equals(elem.getName())){
	    			finalFileList.add(jsonFile);
	    		}  
	    	}
	    }
	    
	    
	    JSONObject load = new JSONObject();
		JSONObject innerLoad = new JSONObject();
		JSONArray jsonData = new JSONArray();
		
		try {
			load.put("opcode", ServerOpcodes.directoryData.name());
			innerLoad.put("data", jsonData);
			load.put("payload", innerLoad);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	    Main.r.send(load.toString());
	    
	}
	
	public JSONObject directoryList(){
		
		JSONObject load = new JSONObject();
		JSONObject payload = new JSONObject();
		JSONArray jsonData = new JSONArray();
		Collection<File> inputFiles = FileUtils.listFiles(this.file,
                new String[] {"txt" }, true);
		
		
		for (File elem : inputFiles) {
			jsonData.put(elem.getName());
		}
		
		try {
			load.put("opcode", ServerOpcodes.directoryData.name());
			payload.put("data", jsonData);
			load.put("payload", payload);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return load;

	}
	
	 public void findFile(String name,File file)
	    {
	        File[] list = file.listFiles();
	        if(list!=null)
	        for (File fil : list)
	        {
	            if (fil.isDirectory())
	            {
	                findFile(name,fil);
	            }
	            else if (name.equalsIgnoreCase(fil.getName()))
	            {
	                System.out.println(fil.getParentFile());
	            }
	        }
	    }
	
	
	@Override
	public void clientOnDirectoryData(JSONObject payload){
		JSONArray fileArray = payload.optJSONArray("data");
		
	}
	
	@Override
	public void clientOnConnect() {
		
		if(this.isDirectory){
			
			JSONObject packet = directoryList();
			Main.r.send(packet.toString());
			
		}else{
		//System.out.println("Client connected to server! Sending initial packet to server");
		JSONObject packet = rh.hashParts(0, new int[] {0});
		Main.r.send(packet.toString());
		}
	}
	
	@Override
	public void serverOnConnect() {
		System.out.println("Connected to client!");
	}

	@Override
	public void serverOnError(String err) {
		//System.out.println("Error: " + err);
	}

	@Override
	public void clientOnError(String err) {
		//System.out.println("Error: " + err);	
	}
}
