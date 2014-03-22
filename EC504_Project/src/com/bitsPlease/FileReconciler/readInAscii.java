package com.bitsPlease.FileReconciler;
import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class readInAscii {

	public static void main(String[] args) {
	/*
		File file = new File("FIRST.txt");
		
		BufferedInputStream bin = null;
		FileInputStream fin = null;

		try {
			
			// create FileInputStream object
			fin = new FileInputStream(file);

			// create object of BufferedInputStream
			bin = new BufferedInputStream(fin);
	        List<byte[]> bArray = new ArrayList<byte[]>();

			// read file using BufferedInputStream
			while (bin.available() > 0) {
				//System.out.println((char) bin.read());
				String s = Character.toString((char) bin.read());
            	//System.out.println(s);
            	byte[] b = s.getBytes("ASCII");
            	bArray.add(b);
			}
			System.out.println("Done");

		}
		catch (FileNotFoundException e) {
			System.out.println("File not found" + e);
		}
		catch (IOException ioe) {
			System.out.println("Exception while reading file " + ioe);
		}
		finally {
			// close the streams using close method
			try {
				if (fin != null) {
					fin.close();
				}
				if (bin != null) {
					bin.close();
				}
			}
			catch (IOException ioe) {
				System.out.println("Error while closing stream : " + ioe);
			}
		}
		
		}
		*/
		
	/*
	   public static void main(String args[]) throws Exception
	    {
		   
		   
		   	int BUFSIZE = 256;
	        String inputFile = "FIRST.txt";
	        FileInputStream in = new FileInputStream(inputFile);
	        FileChannel ch = in.getChannel();
	        ByteBuffer buf = ByteBuffer.allocateDirect(BUFSIZE);  // BUFSIZE = 256

	        Charset cs = Charset.forName("ASCII"); // Or whatever encoding you want

	        
	        int rd;
	        List<byte[]> bArray = new ArrayList<byte[]>(13107200);
	        while ( (rd = ch.read( buf )) != -1 ) {
	            buf.rewind();
	            CharBuffer chbuf = cs.decode(buf);
	            for ( int i = 0; i < chbuf.length()/2; i++ ) {
	            	char c = chbuf.get();
	            	String s = Character.toString(c);
	            	byte[] b = s.getBytes("ASCII");
	            	bArray.add(b);
	                //System.out.print(chbuf.get()); 
	            }
	            buf.clear();
	        }
	        in.close();
	        System.out.println("Done");
	        for(int j = 0; j < bArray.size(); j++){
	        	byte[] b = bArray.get(j);
	        	String str = new String(b, "ASCII");
	        	//System.out.println(str);
	        }
	        
	    }
	   
	   public static byte[] toBytes(char payload){  
		   ByteArrayOutputStream bOut = new ByteArrayOutputStream();  
		   OutputStreamWriter wOut = new OutputStreamWriter(bOut, Charset.forName("ASCII").newEncoder());  
		   PrintWriter writer = new PrintWriter(wOut);  
		   writer.print(payload);  
		   writer.close();  
		          
		  return bOut.toByteArray();  
		}  
	//public static void main(String[] args) {
	//}
	 * 
	 */
}
