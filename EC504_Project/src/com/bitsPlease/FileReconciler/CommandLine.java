package com.bitsPlease.FileReconciler;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.Options;

public class CommandLine {
	
	static String fileName;
	static String ip;
	static String also;

	public static void check(String[] args) {

		Options opt = new Options();
		opt.addOption("file", true, "the file name");
		opt.addOption("to", true, "the address");
		opt.addOption("also", true, "an additional address");
		
		try {
			org.apache.commons.cli.CommandLine line = new BasicParser().parse(opt, args);
			
			if(line.hasOption("file")) {
				fileName = line.getOptionValue("file");

			} else System.out.println("Include the file name after the parameter -file");
			
			if (line.hasOption("to")){
				ip = line.getOptionValue("to");
			} else System.out.println("Include the IP of the recepient after the parameter -to");
			
			if (line.hasOption("also")) {
				also = line.getOptionValue("also");
			}
			if (ip == null) {
				System.out.println("Reconcile file " + fileName + " with localhost");
			} else {
				System.out.println("Reconcile file " + fileName + " with computer " + ip);
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
