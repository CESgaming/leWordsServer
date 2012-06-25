import java.net.*;
import java.util.Vector;
import java.io.*;


public class ServerThread extends Thread {
	private Socket socket = null;

	//Client information
	public String name = "";
	public int score;
	public int ID;
	
	public String word;
	public Board b;
	public Log log;
	boolean boardSend = false;
	public int GAMESTATE =2;
	public boolean disconnected = false;
	public int players;
	public float time;
	public Vector<String> names = new Vector<String>();
	public Vector<ServerThread> clients = new Vector<ServerThread>();
	public Vector<ServerThread> remove = new Vector<ServerThread>();
    public ServerThread(Socket socket,Board b,Log log) {
	super("ServerThread");
	this.socket = socket;
	this.b = b;
	this.log = log;
    }

    public void run() {

	try {
		
	    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	    BufferedReader in = new BufferedReader(
				    new InputStreamReader(
				    socket.getInputStream()));

	    String inputLine;
	    
	    DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
	    DataInputStream inStream = new DataInputStream(socket.getInputStream());
	    //Client Server Handshake and Authentication
	    String tempName = "";
	    int nameSize = inStream.readInt();
	    for(int i =0; i <nameSize ; i++)
	    	tempName+=inStream.readChar();
	    name = tempName;
	    
	   /* //First of all, send Board Information:
	    outStream.writeInt(5);
	    for(int i =0; i < b.dim; i++)
	    	for(int j =0; j < b.dim; j++)
	    		outStream.writeChar(b.letters[i][j]);
	    
	    //Send board dictionary
	    int dictSize = b.boardDictionary.length;
	    outStream.writeInt(dictSize);
	    for(int i =0; i < dictSize; i++)
	    {
	    	word = b.boardDictionary.dictionary[i];
	    	outStream.writeInt(word.length());
	    	for(int j =0; j <word.length(); j++)
	    		outStream.writeChar(word.charAt(j));
	    		
	    }
	    */
	    GAMESTATE = 1;
	    boolean listening = true;
	    while (listening) 
	    {
	    	try{
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	    	outStream.writeInt(GAMESTATE);
	    	switch(GAMESTATE)
	    	{
	    	case 0:
	    		break;
	    	case 1:
	    		if(!boardSend)
	    		{
	    		
	    		//First of all, send Board Information:
	    	    outStream.writeInt(5);
	    	    for(int i =0; i < b.dim; i++)
	    	    	for(int j =0; j < b.dim; j++)
	    	    		outStream.writeChar(b.letters[i][j]);
	    	    
	    	    //Send board dictionary
	    	    int dictSizeNew = b.boardDictionary.length;
	    	    outStream.writeInt(dictSizeNew);
	    	    for(int i =0; i < dictSizeNew; i++)
	    	    {
	    	    	word = b.boardDictionary.dictionary[i];
	    	    	outStream.writeInt(word.length());
	    	    	for(int j =0; j <word.length(); j++)
	    	    		outStream.writeChar(word.charAt(j));
	    	    		
	    	    }
	    	    boardSend = true;
	    	    GAMESTATE = 2;
	    		}
	    	    
	    		break;
	    	case 2:

	    		//Write Time
	    	outStream.writeInt((int)(time)/1000);
	    	//Reading and writing meta data to every client
		    //System.out.println("listening");
	    	//Get points of client
		    score = inStream.readInt();
		    
		    //Delete disconnected players
		    for(int i=0; i < remove.size(); i++)
		    	clients.remove(remove.elementAt(i));
		    remove.clear();
	    	//Write names to player
		    players = clients.size();
		    outStream.writeInt(players);
		    
		    for(int i =0; i < players; i++)
		    {
		    	//Politely ask if the client already knows the person we are about to send him to
		    	outStream.writeInt(clients.elementAt(i).ID);
		    	outStream.writeInt(clients.elementAt(i).score);
		    	if(!inStream.readBoolean())
		    	{
		    	//He doesnt know him, so send him all we know
		    	outStream.writeInt(clients.elementAt(i).name.length());
		    	outStream.writeChars(clients.elementAt(i).name);
		    	}

		    }
		    break;
		    
	    	}
	    }
	    out.close();
	    in.close();
	    socket.close();

	} catch (IOException e) {
	    disconnected = true;
	    log.updateLog(21, "Player "+name+" disconnected", true);
	   // e.printStackTrace();
	}
    }
}