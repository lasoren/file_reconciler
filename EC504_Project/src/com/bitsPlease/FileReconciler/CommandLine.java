package com.bitsPlease.FileReconciler;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.Options;

public class CommandLine {
	
	private String fileName;
	private String ip;

	public static void check(String[] args) {

		Options opt = new Options();
		opt.addOption("file", true, "the file name");
		opt.addOption("to", true, "the address");
		
		try {
			org.apache.commons.cli.CommandLine line = new BasicParser().parse(opt, args);
			
			if(line.hasOption("file")) {
				if (line.hasOption("to")){
					fileName = line.getOptionValue("file");
					ip = line.getOptionValue("to");
					// Do the magic
					// The example to check the parameters
					System.out.println("Reconcile file " + fileName + " with computer " + ip);
				} else System.out.println("Include the IP of the recepient after the parameter -to");
			} else System.out.println("Include the file name after the parameter -file");
		} catch (org.apache.commons.cli.ParseException exp) {
			System.out.println("Invalid expression: " + exp.getMessage());
		}
		
	}
	
	public String getName() {
		return fileName;
	}
	
	public String getIP() {
		return ip;
	}

}
