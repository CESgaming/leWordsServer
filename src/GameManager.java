import java.net.*;
import java.util.Random;
import java.util.Vector;
import java.io.*;


public class GameManager  extends Thread {

	public Vector<ServerThread> clients = new Vector<ServerThread>();
	public Vector<ServerThread> del = new Vector<ServerThread>();
	public Vector<String> names = new Vector<String>();
	public ServerThread lastClient=null;
	public ServerThread deadClient=null;
	public Log log;
	long startTime=0;
	long curTime =0;
	int GAMESTATE=0;
	Board b;
	Hypercube h;
	Dictionary d;
	Filter f;
	int dim = 5;
	
	public Random rnd = new Random();
	public GameManager(Log log)
	{
		
		b = new Board(dim);
		h = new Hypercube();
		d = new Dictionary();
		f = new Filter();
		h.fillHypercube();
		d.fillCompleteDictionary();
	
		Markov m = new Markov();
		m.createProbabilityTable();
	

		
		int k= 0;
		do
		{k++;
			
			b.letters = m.createTable(dim);
			f.filterLevelOne(d,h,b);
			f.filterLevelTwo(b);
			f.filterLevelThree(b);
			
		}
		while(b.boardDictionary.length<400 );
		b.boardDictionary.printDictionary();
		
		this.log = log;
	}
	
	public void setUpNewBoard()
	{
		int k= 0;
		do
		{k++;
			
			b.letters = m.createTable(dim);
			f.filterLevelOne(d,h,b);
			f.filterLevelTwo(b);
			f.filterLevelThree(b);
			
		}
		while(b.boardDictionary.length<400 );
		b.boardDictionary.printDictionary();
		
	}
	

	public void run()
	{
		boolean running = true;
		int numberOfClients=0;
		GAMESTATE = 2;
		startTime = System.currentTimeMillis();
		while(running)
		{
			
			switch(GAMESTATE)
			{
			case 0:
				break;
			case 1:
				setUpNewBoard();
				for(int i =0; i < clients.size(); i++)
				{
					if(clients.elementAt(i).GAMESTATE!=1)
					{
						clients.elementAt(i).boardSend= false;
					clients.elementAt(i).b = b;
					clients.elementAt(i).GAMESTATE = 1;
					clients.elementAt(i).score =0;
					}
				}
				boolean ready = false;
				while(ready)
				{
				ready = true;
				for(int i =0; i < clients.size(); i++)
					if(!clients.elementAt(i).boardSend)
						ready=false;
					
						else if(clients.elementAt(i).GAMESTATE==1)
							clients.elementAt(i).GAMESTATE = 2;
				}
				startTime = System.currentTimeMillis();
				GAMESTATE = 2;
				break;
				
			
			case 2:
				curTime = (System.currentTimeMillis() - startTime);
			//Update the player count on all clients:
			numberOfClients = clients.size();
			//Now iterate through all clients and update their information
				
			if(clients.size()>0)
	        	for(int i =0; i < numberOfClients; i++)
	        	{
	        		if(!clients.elementAt(i).isAlive())
	        			{
	        			deadClient = clients.elementAt(i);
	        			del.add(clients.elementAt(i));
	        			continue;
	        			}
	        		else
	        		{
	        		if(!clients.elementAt(i).remove.contains(deadClient))
	        		clients.elementAt(i).remove.add(deadClient);
	        		clients.elementAt(i).time = curTime;
	        		}
	        		
	        	}
			for(int i =0; i < del.size(); i++)
			{
				clients.remove(del.elementAt(i));
			}
			del.clear();
			
			//Check if time is over
			//After 120 seconds, the client turns off though
			if(curTime>140000)
				GAMESTATE = 1;
			break;
			}
			
		}
		
	}
	
	public void add(ServerThread client)
	{
		lastClient = client;
		client.ID = rnd.nextInt()%1000;
		for(int i =0; i < clients.size(); i++)
    	{
			//synchronizing the clientlist with all clients. Clientception! 
			//CAUTION: Highly experimental, please consult a Professional before screwing with this!
			clients.elementAt(i).clients.add(client);

			client.clients.add(clients.elementAt(i));
			while(client.ID == clients.elementAt(i).ID)
			{
				client.ID = rnd.nextInt()%1000;
			}
    	}
		//Adding yourself to yourself...WE HAVE TO GO DEEPER!
		client.clients.add(client);
		clients.add(client);
		
	}
	
}
