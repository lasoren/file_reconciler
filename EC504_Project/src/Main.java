
public class Main implements ClientFunctions {

	static Runnable r;
	
	public static void main(String args[]) {
		if (args[0].equals("client")) {
			Main.r = new FRSocketClient("127.0.0.1", 42069, new Main());
		new Thread(Main.r).start();
		} else if (args[0].equals("server")){
			Main.r = new FRSocketServer("127.0.0.1", 42069);
			new Thread(Main.r).start();
		} else {
			System.out.println("Error");
		}
	}

	@Override
	public void onFirstPacket(String data) {
		System.out.println("GOT PACKET!!!!!!!!");
	}
}
