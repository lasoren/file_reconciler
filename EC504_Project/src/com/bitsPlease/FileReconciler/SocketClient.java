package com.bitsPlease.FileReconciler;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class SocketClient extends Thread {
	
	protected String server;
	protected int port;
	protected BlockingQueue<String> sendQueue;
	protected DataInputStream streamIn;
	protected DataOutputStream streamOut;
	protected int bytes = 0;
	
	SocketClient (String ipaddr, int port) {
		this.server = ipaddr;
		this.port = port;
		this.sendQueue = new LinkedBlockingQueue<String>();
	}
	
	public synchronized void send(String line) {
    	sendQueue.add(line);
	}
	
	protected boolean processSendQueue() {
        while (!this.sendQueue.isEmpty()) {
        	try {
        		String packet = this.sendQueue.poll();
                streamOut.writeUTF(packet);
                streamOut.flush();
                bytes = bytes + packet.length();
                //System.out.println(bytes);
        	} catch (IOException e) {
        		return false;
        	}
        }
        return true;
	}
	
    public abstract void run();

	public abstract void close();
	
}
