package com.bitsPlease.FileReconciler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecurrentHasher {
	
	public static int HASH_LENGTH = 32;
	
	byte fileArray[];
	public long fileArraySize;
	
	MessageDigest md;
	
	boolean allDone = false;
	public double p = 0;
	
	public RecurrentHasher(byte fileArray[], long fileArraySize) {
		this.fileArray = fileArray;
		this.fileArraySize = fileArraySize;
		
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	//computer 1 hashes the appropriate parts
	//start should be recurrence 0, indices [0]
	public JSONObject hashParts(int recurrence, int indices[]) {	
		recurrence++;
		double divisor = (long) Math.pow(2, recurrence);
		double partLength = (fileArraySize/divisor);
		
		if (indices.length > 0) {
			double passProgress = Math.round((2*indices[0]*partLength/(double) fileArraySize)*100);
			if (passProgress > p) {
				p = passProgress;
			}
			StringBuilder ps = new StringBuilder();
			for (int i=0; i<20; i++) {
				if (i <= p/5) {
					ps.append("#");
				} else {
					ps.append(" ");
				}
			}
			System.out.print("Server: [" + ps + "] " + p + "%\r");
		}
		else {
			p = 100;
			StringBuilder ps = new StringBuilder();
			for (int i=0; i<20; i++) {
				if (i <= p/5) {
					ps.append("#");
				} else {
					ps.append(" ");
				}
			}
			System.out.print("Server: [" + ps + "] " + p + "%\r");
		}
		
		JSONObject load = new JSONObject();
		JSONObject payload = new JSONObject();
		
		try {
			payload.put("recurrence", recurrence);
			if (recurrence == 1) {
				payload.put("arraysize", (int) fileArraySize);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		JSONArray jsonIndices = new JSONArray();
		JSONArray jsonData = new JSONArray();
		
		if (partLength > HASH_LENGTH) {
			try {
				load.put("opcode", ClientOpcodes.hashData.name());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			
			for (int i = 0; i < indices.length; i++) {
				for (int k = (int) (2*indices[i]*partLength); k < (int) (2*indices[i]*partLength + partLength); k++) {
					md.update(fileArray[k]);
				}
				byte hashedOne[] = md.digest();
				md.reset();
				
				
				for (int k = (int) (2*indices[i]*partLength + partLength); k < (int) (2*indices[i]*partLength + 2*partLength); k++) {
					md.update(fileArray[k]);
				}
				byte hashedTwo[] = md.digest();
				md.reset();
				
				jsonIndices.put(indices[i]*2);
				jsonIndices.put(indices[i]*2+1);
				
				jsonData.put(byteArrayToString(hashedOne));
				jsonData.put(byteArrayToString(hashedTwo));
			}
		}
		else {
			try {
				load.put("opcode", ClientOpcodes.rawData.name());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			for (int i = 0; i < indices.length; i++) {
				int end = (int) (2*indices[i]*partLength + 2*partLength);
				byte halfOne[] = Arrays.copyOfRange(fileArray, (int) (2*indices[i]*partLength), end);
				end = (int) (2*indices[i]*partLength + 3*partLength);
				byte halfTwo[] = null;
				if (end >= fileArraySize) {
					halfTwo = Arrays.copyOfRange(fileArray, (int) (2*indices[i]*partLength + partLength), (int) fileArraySize);
				}
				else {
					halfTwo = Arrays.copyOfRange(fileArray, (int) (2*indices[i]*partLength + partLength), end);
				}
				
				jsonIndices.put(indices[i]*2);
				jsonIndices.put(indices[i]*2+1);
				
				JSONArray half1 = new JSONArray();
				for (int j = 0; j < halfOne.length; j++) {
					half1.put(halfOne[j]);
				}
				jsonData.put(half1);
				
				JSONArray half2 = new JSONArray();
				for (int k = 0; k < halfTwo.length; k++) {
					half2.put(halfTwo[k]);
				}
				jsonData.put(half2);
			}
		}
		
		try {
			payload.put("indices", jsonIndices);
			payload.put("data", jsonData);
			load.put("payload", payload);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
//		System.out.println(load);
		return load;
	}
	
	String byteArrayToString(byte[] in) {
		char out[] = new char[in.length * 2];
		for (int i = 0; i < in.length; i++) {
			out[i * 2] = "0123456789ABCDEF".charAt((in[i] >> 4) & 15);
			out[i * 2 + 1] = "0123456789ABCDEF".charAt(in[i] & 15);
		}
		return new String(out);
	}
	
	public JSONObject compareParts(int recurrence, int indices[], String data[]) {
		
		double divisor = (long) Math.pow(2, recurrence);
		double partLength = (fileArraySize/divisor);
		
		if (indices.length > 0) {
			double passProgress = Math.round((indices[0]*partLength/(double) fileArraySize)*100);
			if (passProgress > p) {
				p = passProgress;
			}
			StringBuilder ps = new StringBuilder();
			for (int i=0; i<20; i++) {
				if (i <= p/5) {
					ps.append("#");
				} else {
					ps.append(" ");
				}
			}
			System.out.print("Client "+ FRSocketServer.clientNum+": [" + ps + "] " + p + "%\r");
		}
		else {
			p = 100;
			StringBuilder ps = new StringBuilder();
			for (int i=0; i<20; i++) {
				if (i <= p/5) {
					ps.append("#");
				} else {
					ps.append(" ");
				}
			}
			System.out.print("Client "+ FRSocketServer.clientNum+": [" + ps + "] " + p + "%\r");
		}
		
		JSONObject load = new JSONObject();
		JSONObject response = new JSONObject();
		
		try {
			load.put("opcode", ServerOpcodes.hashResponse.name());
			response.put("recurrence", recurrence);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		JSONArray jsonIndices = new JSONArray();
		
		for (int i = 0; i < indices.length; i++) {
			for (int k = (int) (indices[i]*partLength); k < (int) (indices[i]*partLength + partLength) && k < fileArray.length; k++) {
				md.update(fileArray[k]);
			}
			byte oldHashed[] = md.digest();
			md.reset();
			
			String hash = byteArrayToString(oldHashed);
//			System.out.println("Data: "+data[i]);
			
			if (!hash.equals(data[i])) {
				jsonIndices.put(indices[i]);
				break;
			}
		}
		
		try {
			response.put("indices", jsonIndices);
			load.put("payload", response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
//		System.out.println(load);
		return load;
	}
	
	
	public JSONObject compareParts(int recurrence, int indices[], JSONArray data[]) {
		double divisor = (long) Math.pow(2, recurrence);
		double partLength = (fileArraySize/divisor);
		
		if (indices.length > 0) {	
			double passProgress = Math.round((indices[0]*partLength/(double) fileArraySize)*100);
			if (passProgress > p) {
				p = passProgress;
			}
			StringBuilder ps = new StringBuilder();
			for (int i=0; i<20; i++) {
				if (i <= p/5) {
					ps.append("#");
				} else {
					ps.append(" ");
				}
			}
			System.out.print("Client "+ FRSocketServer.clientNum+": [" + ps + "] " + p + "%\r");
		}
		else {
			p = 100;
			StringBuilder ps = new StringBuilder();
			for (int i=0; i<20; i++) {
				if (i <= p/5) {
					ps.append("#");
				} else {
					ps.append(" ");
				}
			}
			System.out.print("Client "+ FRSocketServer.clientNum+": [" + ps + "] " + p + "%\r");
		}
		
		//there should be 2 or less indices at this point
		//System.out.println("Number of indices: " + indices.length);
		for (int i = 0; i < indices.length; i++) {
			int length = data[i].length();
			byte updated[] = new byte[length];
			for (int j = 0; j < length; j++) {
				updated[j] = (byte) data[i].optInt(j);
			}
			
			String update = byteArrayToString(updated);
			
			int start = (int) (indices[i]*partLength);
			int end = (int) (indices[i]*partLength + 2*partLength);
			if (end >= fileArraySize) {
				end = (int) fileArraySize;
			}
			byte olded[] = Arrays.copyOfRange(fileArray, start, end);
			String old = byteArrayToString(olded);
			
			String longest = longestCommonSubstring(update, old);
			//System.out.println("Longest: "+longest);
			int longestLen = longest.length();
			
			int updateIdx = update.indexOf(longest);
			//System.out.println("Update Idx: " + updateIdx);
			if (longestLen < update.length() && (updateIdx != 0 || i == indices.length-1)) {
				//insertion, deletion, or modification exists here!
				int oldIdx = old.indexOf(longest);
				
				int difference = (updateIdx - oldIdx)/2; //how much we need to shift by
				
				//if difference > 0, update has insertions
				//if difference < 0, update has deletions
				
				//replace fileArray
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
				try {
					outputStream.write(Arrays.copyOfRange(fileArray, 0, start));
					outputStream.write(updated);
					if (end-difference < fileArray.length)
						outputStream.write(Arrays.copyOfRange(fileArray, end-difference, fileArray.length));
				} catch (IOException e) {
					e.printStackTrace();
				}
				fileArray = outputStream.toByteArray();
				//this.fileArraySize += difference;
//				System.out.println("File Index: "+start);
//				System.out.println("Update: "+update);
//				System.out.println("Old:    "+old);
//				System.out.println("Shift: "+difference);
				allDone = false;
				//need to start over now
				JSONObject load = new JSONObject();
				JSONObject response = new JSONObject();
				
				try {
					load.put("opcode", ServerOpcodes.hashResponse.name());
					response.put("recurrence", 0);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				JSONArray jsonIndices = new JSONArray();
				jsonIndices.put(0);
				try {
					response.put("indices", jsonIndices);
					load.put("payload", response);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return load;
			}
		}
		allDone = true;
		return null;
	}
	
	private static String longestCommonSubstring(String S1, String S2)
	{
	    int Start = 0;
	    int Max = 0;
	    for (int i = 0; i < S1.length(); i++)
	    {
	        for (int j = 0; j < S2.length(); j++)
	        {
	            int x = 0;
	            while (S1.charAt(i + x) == S2.charAt(j + x))
	            {
	                x++;
	                if (((i + x) >= S1.length()) || ((j + x) >= S2.length())) break;
	            }
	            if (x > Max)
	            {
	                Max = x;
	                Start = i;
	            }
	         }
	    }
	    return S1.substring(Start, (Start + Max));
	}
}
