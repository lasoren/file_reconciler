
public class Main implements ClientFunctions, ServerFunctions {

	static SocketClient r;
	
	public static void main(String args[]) {
		if (args[0].equals("client")) {
			Main.r = new FRSocketClient("127.0.0.1", 42069, new Main());
			Main.r.start();
		} else if (args[0].equals("server")){
			Main.r = new FRSocketServer("127.0.0.1", 42069, new Main());
			Main.r.start();
		} else {
			System.out.println("Error");
		}
	}

	@Override
	public void onFirstPacket(String data) {
		System.out.println("CLIENT GOT PACKET!!!!!!!!");
		Main.r.send("test3");
	}

	@Override
	public void onFirstPacket2(String data) {
		System.out.println("SERVER GOT PACKET!!!!!!!!");
		Main.r.send("test1");
		
	}

	@Override
	public void onSecondPacket(String data) {
		System.out.println("SERVER GOT FINAL PACKET!!!!!!!!");
		
	}
	@Override
	public void connected() {
		Main.r.send("test2");
		
	}
}
