package com.bitsPlease.UnitTests;

import com.bitsPlease.FileReconciler.RecurrentHasher;

public class TestRecurrentHasher {
	
	static byte fileArray[] = {'a','b','c','d','e','f','g','h','i','c','d','e','f','g','h','i','c','d','e','f','g','h','i','c','d','e','f','g','h','i','c','d','e','f','g','h','i','c','d','e','f','g','h','i','c','d','e','f','g','h','i','c','d','e','f','g','h','i','c','d','e','f','g','h','i','c','d','e','f','g','h','i','r','r'};
	
	public static void main(String args[]) {
		RecurrentHasher rh = new RecurrentHasher(fileArray, fileArray.length);
		
		System.out.println(rh.fileArraySize);
		rh.hashParts(0, new int[] {0});
		
		//byte[] b = new BigInteger("6DEE9784222610515CD372CF9F7E2B72",16).toByteArray();
		//System.out.println(rh.byteArrayToString(b));
		
	}
}
