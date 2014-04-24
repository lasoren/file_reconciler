package com.bitsPlease.FileReconciler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Main implements ClientFunctions, ServerFunctions {

	static SocketClient r;
	byte fileArray[];
	RecurrentHasher rh;
	boolean isDirectory;
	File file;
	boolean client;
	Queue<String> q = new LinkedList<String>();
	String currentFileName;
	int numfiles = 1;
	int currentfile = 0;
	
	public static int FINAL_SEND_LEN = 46;
	
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
		try {
			CommandLine.check(args);
		} catch (Exception e) {
			System.exit(0);
		}
		Main.r = new FRSocketClient(CommandLine.getIP(), 42069, new Main(CommandLine.getName(), true, thisIsDirectory));
		Main.r.start();
	}
	
	Main(String fileName, boolean client, boolean isDirectory) {
		this.client = client;
		this.isDirectory = isDirectory;
		if (!isDirectory) {
			this.currentfile++;
		}
		File folder = new File(fileName);
		if (!folder.exists()) {
			System.out.println("File or folder not found!");
			System.exit(0);
		}
		this.file = folder;
		if (!isDirectory) {
			StartRecurrentHashing(fileName, client, isDirectory);
		}
	}

	private void StartRecurrentHashing(String fileName, boolean client, boolean isDirectory) {
		//this.isDirectory = isDirectory;
		this.currentFileName = fileName;
		String fn;
		if (file.isDirectory()) {
			fn = file.getAbsolutePath() + "/" + this.currentFileName;
		} else {
			fn = file.getAbsolutePath();
		}
		this.fileArray = FRFileIO.readIn(fn);
		this.client = client;

		//introducing test errors
//		if (!client) {
//			//fileArray[fileArray.length-10] = 'Q';
//			//fileArray[53223423] = 'Q';
//			//fileArray[100] = 'Q';
//			//fileArray[80000000] = 'Q';
//			//fileArray[2232344] = 'Q';
//			fileArray[2] = (byte)(80+this.currentfile);
//			//}
//			
//			//testing deletions fileArray
//			ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
//			try {
//				outputStream.write(Arrays.copyOfRange(fileArray, 0, 23423+5));
//				outputStream.write(Arrays.copyOfRange(fileArray, 23423, fileArray.length));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			fileArray = outputStream.toByteArray();
//			fileArray[80000000] = 'Q';
//		}
		String printname;
		if (file.isDirectory()) {
			printname = this.currentFileName;
		} else {
			printname = file.getName();
		}
		this.rh = new RecurrentHasher(this.fileArray, this.fileArray.length, this.numfiles, this.currentfile, printname);
	}

	@Override
	public void serverOnHashResponse(JSONObject payload) {
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
	public void clientOnHashData(JSONObject payload) {
		//System.out.println("Server got packet from client. Responding with next packet");
		int recurrence = payload.optInt("recurrence");
		if (recurrence == 1) {
			//System.out.println(payload);
			int fileSize = payload.optInt("arraysize");
			rh.fileArraySize = fileSize;
		}
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
	public void clientOnRawData(JSONObject payload) {
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
			String fn;
			if (isDirectory) {
				fn = file.getAbsolutePath() + "/" + this.currentFileName;
			} else {
				fn = file.getAbsolutePath();
			}
			FRFileIO.writeOut(rh.fileArray, fn);
			this.rh.finishFileProgress();
			try {
				JSONObject packet2 = new JSONObject();
				packet2.put("opcode", ServerOpcodes.fileDone.name());
				Main.r.send(packet2.toString());
			} catch (Exception e) {
				clientOnError("Error forming packet", ClientErrors.errForm);
			}
		} else {
			Main.r.send(packet.toString());
		}
	}
	
	@Override
	 public void clientOnDirectoryData(JSONObject payload){
		
		JSONArray fileArray = payload.optJSONArray("data");
		List<String> finalFileList = new ArrayList<String>();
		List<String> candidates = new ArrayList<String>();
		Collection<File> inputFiles = FileUtils.listFiles(this.file,
				TrueFileFilter.INSTANCE, DirectoryFileFilter.DIRECTORY);
		
		String fn;
		for (File elem : inputFiles) {
			fn = elem.getAbsolutePath();
			fn = fn.replace(file.getPath() + "/", "");
			candidates.add(fn);
		}
		
	    	for (int i = 0; i < fileArray.length(); i++) {
	    		String jsonFile = null;
	    		  try {
					jsonFile = fileArray.getString(i);

				} catch (JSONException e) {
					e.printStackTrace();
				}
	    		if(candidates.contains(jsonFile)){
	    			finalFileList.add(jsonFile);
	    		}  
	    	}
	    
	    
	    JSONObject load = new JSONObject();
		JSONObject innerLoad = new JSONObject();
		JSONArray jsonData = new JSONArray();
		
		try {
			load.put("opcode", ServerOpcodes.directoryResponse.name());
			innerLoad.put("data", jsonData);
			for (int i=0; i<finalFileList.size(); i++) {
				jsonData.put(finalFileList.get(i));
			}
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
				 TrueFileFilter.INSTANCE, DirectoryFileFilter.DIRECTORY);
		
		String fn;
		for (File elem : inputFiles) {
			fn = elem.getAbsolutePath();
			fn = fn.replace(file.getPath() + "/", "");
			jsonData.put(fn);
		}
		
		try {
			load.put("opcode", ClientOpcodes.directoryData.name());
			payload.put("data", jsonData);
			load.put("payload", payload);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return load;

	}
	
	@Override
	public void serverOnDirectoryResponse(JSONObject payload){
		this.currentfile = 0;
		JSONArray fileArray = payload.optJSONArray("data");
		for(int i = 0; i < fileArray.length(); i++){
			String fileName = null;
			try {
				fileName = fileArray.getString(i);
				q.add(fileName);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		this.numfiles = q.size();
		String cfn = q.poll();
		this.currentfile++;
		StartRecurrentHashing(cfn, false, false);
		
		JSONObject packet = new JSONObject();
		JSONObject newpayload = new JSONObject();
		try {
			newpayload.put("filename", cfn);
			newpayload.put("numfiles", numfiles);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		try {
			packet.put("opcode", ClientOpcodes.startFile);
			packet.put("payload", newpayload);
			Main.r.send(packet.toString());
			JSONObject senddata = rh.hashParts(0, new int[] {0});
			Main.r.send(senddata.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}


	}
	
	@Override
	public void clientOnStartFile(JSONObject payload) {
		String fn;
		fn = payload.optString("filename");
		if (payload.has("numfiles")) {
			this.numfiles = payload.optInt("numfiles");
		}
		this.currentfile++;
		StartRecurrentHashing(fn, true, false);
	}

	@Override
	public void serverOnConnect() {	
		System.out.println("Connected to client!");
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
	public void clientOnConnect() {
		System.out.println("Connected to server!");
	}

	@Override
	public void serverOnError(String msg, ServerErrors code) {
		System.out.println("Server error: " + msg);
	}

	@Override
	public void clientOnError(String msg, ClientErrors code) {
		if (code == ClientErrors.errNoHost) {
			System.out.println("Listening for connection since no open host found");
			if (!isDirectory) {
				StartRecurrentHashing(CommandLine.getName(), false, this.isDirectory);
			}
			this.client = false;
			Main.r = new FRSocketServer(CommandLine.getIP(), 42069, this);
			Main.r.start();
		} else {
			System.out.println("Client error: " + msg);
		}
	}

	@Override
	public void serverOnFileDone() {
		if (!q.isEmpty()) {
			System.out.println("\nTotal bytes transmitted (sent and recieved): "+Main.r.numberFormat.format((Main.r.bytes/1024.0))+" kB");
			this.currentfile++;
			String fn = q.poll();
			StartRecurrentHashing(fn, false, false);
			JSONObject packet = new JSONObject();
			JSONObject newpayload = new JSONObject();
			try {
				newpayload.put("filename", fn);
				packet.put("opcode", ClientOpcodes.startFile);
				packet.put("payload", newpayload);
			} catch (Exception e) {
				
			}
			Main.r.send(packet.toString());
			JSONObject packet2 = rh.hashParts(0, new int[] {0});
			Main.r.send(packet2.toString());
		} else {
			System.out.println("\nTotal bytes transmitted (sent and recieved): "+Main.r.numberFormat.format(((Main.r.bytes+FINAL_SEND_LEN)/1024.0))+" kB");
			try {
				JSONObject packet = new JSONObject();
				packet.put("opcode", ClientOpcodes.clientDone.name());
				Main.r.send(packet.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
