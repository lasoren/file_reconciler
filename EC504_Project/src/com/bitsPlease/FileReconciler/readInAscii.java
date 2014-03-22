package com.bitsPlease.FileReconciler;
import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;

public class readInAscii {
	   public static void main(String args[]) throws Exception
	    {
		   	int BUFSIZE = 256;
	        String inputFile = "FIRST.txt";
	        FileInputStream in = new FileInputStream(inputFile);
	        FileChannel ch = in.getChannel();
	        ByteBuffer buf = ByteBuffer.allocateDirect(BUFSIZE);  // BUFSIZE = 256

	        Charset cs = Charset.forName("ASCII"); // Or whatever encoding you want

	        /* read the file into a buffer, 256 bytes at a time */
	        int rd;
	        while ( (rd = ch.read( buf )) != -1 ) {
	            buf.rewind();
	            CharBuffer chbuf = cs.decode(buf);
	            for ( int i = 0; i < chbuf.length(); i++ ) {
	                /* print each character */
	                System.out.print(chbuf.get());
	            }
	            buf.clear();
	        }
	        in.close();
	    }
	
	//public static void main(String[] args) {
	//}
}
