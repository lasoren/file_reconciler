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
				payload.put("is_raw_text", true);
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
				
				jsonData.put(new String(halfOne));
				jsonData.put(new String(halfTwo));
			}
		}
		
		try {
			payload.put("indices", jsonIndices);
			payload.put("data", jsonData);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		System.out.println(payload);
		return payload;
	}
	String byteArrayToString(byte[] in) {
		char out[] = new char[in.length * 2];
		for (int i = 0; i < in.length; i++) {
			out[i * 2] = "0123456789ABCDEF".charAt((in[i] >> 4) & 15);
			out[i * 2 + 1] = "0123456789ABCDEF".charAt(in[i] & 15);
		}
		return new String(out);
	}
	
	public JSONObject compareParts(int recurrence, int indices[], byte data[][], boolean is_raw_text) {
		double divisor = (long) Math.pow(2, recurrence);
		double partLength = (fileArraySize/divisor);
		
		
		if (!is_raw_text) {
			JSONObject response = new JSONObject();
			
			try {
				response.put("recurrence", recurrence);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			JSONArray jsonIndices = new JSONArray();
			
			for (int i = 0; i < indices.length; i++) {
				byte oldData[] = Arrays.copyOfRange(fileArray, (int) (indices[i]*partLength), (int) (indices[i]*partLength + partLength));
				
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
			return response;
		}
		//save data into file and were done!
		else { 
			//there should be 5 or less indices at this point
			for (int i = 0; i < indices.length; i++) {
				
				int offset = (int) (indices[i]*partLength);
				
				for (int j = 0; j < data[i].length; j++) {
					fileArray[offset] = data[i][j];
					offset++;
				}
			}
			return null;
		}
	}
	
}
