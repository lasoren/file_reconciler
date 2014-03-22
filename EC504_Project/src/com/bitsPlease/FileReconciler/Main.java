package com.bitsPlease.FileReconciler;

public class Main implements ClientFunctions, ServerFunctions {

	static SocketClient r;
	
	public static void main(String args[]) {
		CommandLine.check(String args[]);
		if (args[0].equals("client")) {
			Main.r = new FRSocketClient(CommandLine.getIP(), 42069, new Main());
			Main.r.start();
		} else if (args[0].equals("server")){
			Main.r = new FRSocketServer(CommandLine.getIP(), 42069, new Main());
			Main.r.start();
		} else {
			System.out.println("Error");
		}
	}

	@Override
	public void clientOnTestPacket(String data) {
		System.out.println("Client got server's response, sending final response to server");
		Main.r.send(ServerOpcodes.test2.name());
	}

	@Override
	public void serverOnTestPacket(String data) {
		System.out.println("Server got initial packet from client. Responding with next packet");
		Main.r.send(ClientOpcodes.test1.name());
	}

	@Override
	public void serverOnSecondTestPacket(String data) {
		System.out.println("Server received final packet from client!");
		
	}
	@Override
	public void clientOnConnect() {
		System.out.println("Client connected to server! Sending initial packet to server");
		Main.r.send(ServerOpcodes.test1.name());
	}
	
	@Override
	public void serverOnConnect() {
		System.out.println("Connected to client!");
	}

	@Override
	public void serverOnError(String err) {
		System.out.println("Error: " + err);
	}

	@Override
	public void clientOnError(String err) {
		System.out.println("Error: " + err);	
	}
}
