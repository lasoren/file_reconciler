
public class Main {

	public static void main(String args[]) {
		if (args[0].equals("client")) {
		Runnable r = new FRSocketClient("127.0.0.1", 42069);
		new Thread(r).start();
		} else if (args[0].equals("server")){
			Runnable r = new FRSocketServer("127.0.0.1", 42069);
			new Thread(r).start();
		} else {
			System.out.println("Error");
		}
	}
}
