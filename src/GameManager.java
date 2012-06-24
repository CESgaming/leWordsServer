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
	public Random rnd = new Random();
	public GameManager()
	{}
	

	public void run()
	{
		boolean running = true;
		int numberOfClients=0;
		while(running)
		{
			//Update the player count on all clients:
			numberOfClients = clients.size();
			//Now iterate through all clients and update their information
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
				
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
	        		clients.elementAt(i).remove.add(deadClient);
	        		//clients.elementAt(i).players = numberOfClients;
	        		}
	        		
	        	}
			for(int i =0; i < del.size(); i++)
			{
				clients.remove(del.elementAt(i));
			}
			del.clear();
			
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
