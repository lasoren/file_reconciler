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
	long fileArraySize;
	
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
	public void hashParts(int recurrence, int indices[]) {
		recurrence++;
		double divisor = (long) Math.pow(2, recurrence);
		int partLength = (int) (fileArraySize/divisor);
		
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
				payload.put("is_raw_text", false);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			for (int i = 0; i < indices.length; i++) {
				byte halfOne[] = Arrays.copyOfRange(fileArray, indices[i]*partLength, indices[i]*partLength + partLength);
				byte halfTwo[] = Arrays.copyOfRange(fileArray, indices[i]*partLength + partLength, indices[i]*partLength + 2*partLength);
				//hash each part
				byte hashedOne[] = md.digest(halfOne);
				byte hashedTwo[] = md.digest(halfTwo);
				
				jsonIndices.put(i);
				jsonIndices.put(i+1);
				
				jsonData.put(hashedOne.toString());
				jsonData.put(hashedTwo.toString());
			}
		}
		else {
			try {
				payload.put("is_raw_text", true);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			for (int i = 0; i < indices.length; i++) {
				byte halfOne[] = Arrays.copyOfRange(fileArray, indices[i]*partLength, indices[i]*partLength + partLength);
				byte halfTwo[] = Arrays.copyOfRange(fileArray, indices[i]*partLength + partLength, indices[i]*partLength + 2*partLength);
				
				jsonIndices.put(i);
				jsonIndices.put(i+1);
				
				jsonData.put(halfOne.toString());
				jsonData.put(halfTwo.toString());
			}
		}
		
		try {
			payload.put("indices", jsonIndices);
			payload.put("data", jsonData);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		System.out.println(payload);
		
	}
	
	public void compareParts(int recurrence, int indices[], byte data[][], boolean is_raw_text) {
		double divisor = (long) Math.pow(2, recurrence);
		int partLength = (int) (fileArraySize/divisor);
		
		
		if (!is_raw_text) {
			JSONObject response = new JSONObject();
			
			try {
				response.put("recurrence", recurrence);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			JSONArray jsonIndices = new JSONArray();
			
			for (int i = 0; i < indices.length; i++) {
				byte oldData[] = Arrays.copyOfRange(fileArray, indices[i]*partLength, indices[i]*partLength + partLength);
				
				byte oldHashed[] = md.digest(oldData);
				byte newHashed[] = md.digest(data[i]);
				
				if (!oldHashed.equals(newHashed)) {
					jsonIndices.put(i);
				}
			}
			
			try {
				response.put("indices", jsonIndices);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			System.out.println(response);
		}
		//save data into file and were done!
		else { 
			//there should be 5 or less indices at this point
			for (int i = 0; i < indices.length; i++) {
				
				int offset = indices[i]*partLength;
				
				for (int j = 0; j < data[i].length; j++) {
					fileArray[offset] = data[i][j];
					offset++;
				}
			}
		}	
	}
	
}
