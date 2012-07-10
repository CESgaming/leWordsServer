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
	Markov m ;

	public Random rnd = new Random();
	public GameManager(Log log)
	{

		b = new Board(dim);
		h = new Hypercube();
		d = new Dictionary();
		f = new Filter();
		m= new Markov();
		h.fillHypercube();
		d.fillCompleteDictionary();


		m.createProbabilityTable();
	//	m.createProbabilityCube();



		int k= 0;
		do
		{k++;

		b.letters = m.createTable(dim);
	//	b.letters = m.createTableMarkov3(dim);
		f.filterLevelOne(d,h,b);
		f.filterLevelTwo(b);
		f.filterLevelThree(b);

		}
		while(b.boardDictionary.length<400 );
		//b.boardDictionary.printDictionary();

		this.log = log;
	}

	public void setUpNewBoard()
	{
		int k= 0;
		do
		{k++;

		b.letters = m.createTable(dim);
	//	b.letters = m.createTableMarkov3(dim);
		f.filterLevelOne(d,h,b);
		f.filterLevelTwo(b);
		f.filterLevelThree(b);

		}
		while(b.boardDictionary.length<400 );
		System.out.println("setUpNewBoard() has found a board!");
		b.boardDictionary.printDictionary();
		System.out.println(b.boardDictionary.length);
		int temp=0;
		for (int i=0;i<b.boardDictionary.length;i++){
			temp += b.calcWordPoints( b.boardDictionary.dictionary[i]);
		}
		System.out.println(temp);

	}


	public void run()
	{
		boolean running = true;
		int numberOfClients=0;
		GAMESTATE = 2;
		startTime = System.currentTimeMillis();
		boolean boardFound = false;
		boolean endMessageshowed= false;
		boolean startMessageshowed= false;
		boolean notified = false;
		while(running)
		{
			if (!boardFound){
				setUpNewBoard();
				log.updateLog(42, "New Board has beend found. It has "+ b.boardDictionary.length+" words.", false);
				boardFound = true;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			switch(GAMESTATE)
			{
			case 0:
				break;
			case 1: 
				
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
				startMessageshowed = false;
				notified  = false ; 
				endMessageshowed = false;
				GAMESTATE = 2;
				break;


			case 2:
			
				
				if (!startMessageshowed){
					log.updateLog(40, "New Round has started.", false);
				startMessageshowed = true;
				}
				
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
				if(curTime>120000 && !endMessageshowed){
					log.updateLog(41, "Round is over", false);
					
					endMessageshowed= true;
				}
				if(curTime>120000  && !notified){
					notified = true;
					boardFound=false;
				}
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
		log.updateLog(21, "Player << "+ client.name+ " >> , UserID = "+ client.ID+ " connected.", false);

	}

}
