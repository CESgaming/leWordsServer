
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

		//gM.m.printProbabilityTable();
		float tmp=0;
		for (int i=0;i<26;i++){
			tmp += gM.m.prob[i][0];
		}
		System.out.println("a: "+tmp);
		 tmp=0;
		for (int i=0;i<26;i++){
			tmp += gM.m.prob[i][4];
		}
		System.out.println("e: "+tmp);
		
		
		
		while(listening)
		{


			last = new ServerThread(serverSocket.accept(), gM.b,log);
			last.start();
			gM.add(last);

		}
		serverSocket.close();
		
	}
}