package com.bitsPlease.FileReconciler;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.Options;

public class CommandLine {

	public static void main(String[] args) {

		Options opt = new Options();
		opt.addOption("file", true, "the file name");
		opt.addOption("to", true, "the address");
		
		try {
			org.apache.commons.cli.CommandLine line = new BasicParser().parse(opt, args);
			
			if(line.hasOption("file")) {
				if (line.hasOption("to")){
					String fileName = line.getOptionValue("file");
					String ip = line.getOptionValue("to");
					// Do the magic
					// The example to check the parameters
					System.out.println("Reconcile file " + fileName + " with computer " + ip);
				}
			}
		} catch (org.apache.commons.cli.ParseException exp) {
			System.out.println("Invalid expression: " + exp.getMessage());
		}
		
	}

}
