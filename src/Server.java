
import java.net.*;
import java.util.Vector;
import java.io.*;


public class Server {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {

		Log log= new Log("log.txt");
		int dim = 5;
		Board b;
		Hypercube h;
		Dictionary d;
		Filter f;
		ServerThread last = null;
		b = new Board(dim);
		h = new Hypercube();
		d = new Dictionary();
		f = new Filter();
		h.fillHypercube();
		d.fillCompleteDictionary();
		do {
			b.fillBoard();
			f.filterLevelOne(d,h,b);
			f.filterLevelTwo(b);
			f.filterLevelThree(b);
			// b.boardDictionary.printDictionary();
			//System.out.println(b.boardDictionary.length);

		}while(b.boardDictionary.length<50 );

		log.updateLog(10, "Dictionary is setup with .length>=50", false);
		
		// b.boardDictionary.printDictionary();
		// b.printBoard();
		GameManager gM = new GameManager(log);


		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(5222);
			log.updateLog(11,"Server has come online.",false);
		} catch (IOException e) {
			log.updateLog(30, "Could not listen on port: 5222.", false);
			System.err.println("Could not listen on port: 5222.");
			System.exit(1);
		}
		
		boolean listening = true;
		gM.start();

		while(listening)
		{


			last = new ServerThread(serverSocket.accept(), b,log);
			last.start();
			gM.add(last);

		}
		serverSocket.close();
	}
}