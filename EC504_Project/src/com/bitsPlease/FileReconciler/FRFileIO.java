package com.bitsPlease.FileReconciler;

import java.util.*;
import java.io.*;


public class FRFileIO {
	
    public static void main(String[] args) {
    	//byte [] f = readIn("FIRST.txt");
    	//writeOut(f, "final.txt");
    }
    
    public static byte[] readIn(String textFile){
    	/*
    	 * FileChannel fc = new FileInputStream(file).getChannel();
         MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, (int) fc.size());
    	 */
    	
        File file = new File(textFile);
        // List<byte[]> bArray = new ArrayList<byte[]>();
        byte[] b = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(b);
            //for (int i = 0; i < b.length; i++) {
            // System.out.print((char)b[i]);
            
            
            //}
            fileInputStream.close();
            
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
            e.printStackTrace();
        }
        catch (IOException e1) {
            System.out.println("Error Reading The File.");
            e1.printStackTrace();
        }
        return b;
    }
    
    public static void writeOut(byte[] b, String textFile){
        //try {
        
		//String decoded = new String(b, "ASCII");
		try {
	        File file = new File(textFile);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			try {
				fileOutputStream.write(b);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//PrintWriter writer = new PrintWriter("final.txt", "ASCII");
			//for(int i = 0; i < b.length; i++){
            //System.out.println(new String(b, "ASCII"));
            //writer.print(new String(b, "ASCII"));
			//}
			//writer.close();
			float p = 100;
			StringBuilder ps = new StringBuilder();
			for (int i=0; i<20; i++) {
				if (i <= p/5) {
					ps.append("#");
				} else {
					ps.append(" ");
				}
			}
			System.out.print("[" + ps + "] " + p + "%\r");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
    }
}
