package com.bitsPlease.FileReconciler;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.Options;

public class CommandLine {
	
	static String fileName;
	static String ip;
	static String also;

	public static void check(String[] args) throws Exception {

		Options opt = new Options();
		opt.addOption("file", true, "the file name");
		opt.addOption("to", true, "the address");
		opt.addOption("also", true, "an additional address");
		
		try {
			org.apache.commons.cli.CommandLine line = new BasicParser().parse(opt, args);
			
			if(line.hasOption("file")) {
				fileName = line.getOptionValue("file");

			} else {
				System.out.println("Include the file name after the parameter -file");
				throw new Exception("Did not receive file argument");
			}
			
			if (line.hasOption("to")){
				ip = line.getOptionValue("to");
			} else {
				System.out.println("Include the IP of the recepient after the parameter -to");
				throw new Exception("Did not receive host argument");
			}
			
			if (line.hasOption("also")) {
				also = line.getOptionValue("also");
				System.out.println("Reconcile file " + fileName + " with " + ip + " and " + also);
			} else {
				System.out.println("Reconcile file " + fileName + " with " + ip);
			}
		} catch (org.apache.commons.cli.ParseException exp) {
			System.out.println("Invalid expression: " + exp.getMessage());
		}
		
	}
	
	public static String getName() {
		return fileName;
	}
	
	public static String getIP() {
		return ip;
	}

}
