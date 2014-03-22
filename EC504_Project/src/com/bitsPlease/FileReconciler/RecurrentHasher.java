package com.bitsPlease.FileReconciler;

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
		
		JSONObject load = new JSONObject();
		JSONObject payload = new JSONObject();
		
		try {
			payload.put("recurrence", recurrence);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		JSONArray jsonIndices = new JSONArray();
		JSONArray jsonData = new JSONArray();
		
		if (partLength > HASH_LENGTH) {
			try {
				load.put("opcode", ServerOpcodes.hashData.name());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			for (int i = 0; i < indices.length; i++) {
				byte halfOne[] = Arrays.copyOfRange(fileArray, (int) (indices[i]*partLength), (int) (indices[i]*partLength + partLength));
				byte halfTwo[] = Arrays.copyOfRange(fileArray, (int) (indices[i]*partLength + partLength), (int) (indices[i]*partLength + 2*partLength));
				//DEBUG output
				System.out.println(halfOne.length);
				System.out.println(halfTwo.length);
				
				//hash each part
				byte hashedOne[] = md.digest(halfOne);
				byte hashedTwo[] = md.digest(halfTwo);
				
				jsonIndices.put(i*2);
				jsonIndices.put(i*2+1);
				
				jsonData.put(byteArrayToString(hashedOne));
				jsonData.put(byteArrayToString(hashedTwo));
			}
		}
		else {
			try {
				load.put("opcode", ServerOpcodes.rawData.name());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			for (int i = 0; i < indices.length; i++) {
				byte halfOne[] = Arrays.copyOfRange(fileArray, (int) (indices[i]*partLength), (int) (indices[i]*partLength + partLength));
				byte halfTwo[] = Arrays.copyOfRange(fileArray, (int) (indices[i]*partLength + partLength), (int) (indices[i]*partLength + 2*partLength));
				//DEBUG output
				System.out.println(halfOne.length);
				System.out.println(halfTwo.length);
				
				jsonIndices.put(i*2);
				jsonIndices.put(i*2+1);
				
				JSONArray half1 = new JSONArray();
				JSONArray half2 = new JSONArray();
				for (int j = 0; j < halfOne.length; j++) {
					half1.put(halfOne[j]);
				}
				for (int j = 0; j < halfTwo.length; j++) {
					half2.put(halfTwo[j]);
				}
				
				jsonData.put(half1);
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
		
		System.out.println(load);
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
		
		JSONObject load = new JSONObject();
		JSONObject response = new JSONObject();
		
		try {
			load.put("opcode", ClientOpcodes.hashResponse.name());
			response.put("recurrence", recurrence);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		JSONArray jsonIndices = new JSONArray();
		
		for (int i = 0; i < indices.length; i++) {
			byte oldData[] = Arrays.copyOfRange(fileArray, (int) (indices[i]*partLength), (int) (indices[i]*partLength + partLength));
			
			byte oldHashed[] = md.digest(oldData);
			String hash = byteArrayToString(oldHashed);
			
			if (!hash.equals(data[i])) {
				jsonIndices.put(i);
			}
		}
		
		try {
			response.put("indices", jsonIndices);
			load.put("payload", response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		System.out.println(load);
		return load;
	}
	
	
	public JSONObject compareParts(int recurrence, int indices[], JSONArray data[]) {
		double divisor = (long) Math.pow(2, recurrence);
		double partLength = (fileArraySize/divisor);
		
		//there should be 5 or less indices at this point
		for (int i = 0; i < indices.length; i++) {
			
			int offset = (int) (indices[i]*partLength);
			
			for (int j = 0; j < data[i].length(); j++) {
				fileArray[offset] = (byte) data[i].optInt(j);
				offset++;
			}
		}
		return null;
	}
}
