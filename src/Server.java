
import java.net.*;
import java.util.Vector;
import java.io.*;


public class Server {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {

		Log log= new Log("log.txt");

		ServerThread last = null;

		log.updateLog(10, "Dictionary is setup with .length>=50", false);
		
		// b.boardDictionary.printDictionary();
		// b.printBoard();
		GameManager gM = new GameManager(log);
		gM.start();

		ServerSocket serverSocket = null;
		int port = 5222; // actually works
		try {
			
			serverSocket = new ServerSocket(port);
			log.updateLog(11,"A wild Server appears!",true);
		} catch (IOException e) {
			log.updateLog(30, "Could not listen on port: "+port, false);
			System.err.println("Could not listen on port: "+port);
			System.exit(1);
		}
		
		boolean listening = true;

		while(listening)
		{


			last = new ServerThread(serverSocket.accept(), gM.b,log);
			last.start();
			gM.add(last);

		}
		serverSocket.close();
	}
}