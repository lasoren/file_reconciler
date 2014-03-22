package readIN;
import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;

public class readInAscii {
	
    public static void main(String[] args) {
    	readIn("FIRST.txt");
    }
    
    public static byte[] readIn(String textFile){
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
   
	/*
	   public static void main(String args[]) throws Exception
	    {
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
		   	int BUFSIZE = 256;
	        String inputFile = "FIRST.txt";
	        FileInputStream in = new FileInputStream(inputFile);
	        FileChannel ch = in.getChannel();
	        ByteBuffer buf = ByteBuffer.allocateDirect(BUFSIZE);  // BUFSIZE = 256

	        Charset cs = Charset.forName("ASCII"); 

	        int rd;
	        while ( (rd = ch.read( buf )) != -1 ) {
	            buf.rewind();
	            CharBuffer chbuf = cs.decode(buf);
	            for ( int i = 0; i < chbuf.length(); i++ ) {
	                System.out.println(chbuf.get());
	            }
	            buf.clear();
	        }
	        in.close();
	    }
	   */
	
	//public static void main(String[] args) {
	//}
}
