
import java.net.*;
import java.util.Vector;
import java.io.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Server {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {

		Log log= new Log("log.txt");
		JFrame jframe = new JFrame("Server");
		jframe.setSize(200,100);
		jframe.setVisible(true);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ServerThread last = null;

		
		
		// b.boardDictionary.printDictionary();
		// b.printBoard();
		 
		GameManager gM = new GameManager(log);
		gM.start();
		log.updateLog(10, "Dictionary is setup with .length>=50", false);

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