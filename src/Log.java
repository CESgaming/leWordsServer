import java.util.*;
import java.io.*;
public class Log {


	String path;
	GregorianCalendar d;
	FileOutputStream fstream;
	DataOutputStream fout;
	BufferedWriter bw ;

	public  Log(String p){
		d= new GregorianCalendar();
		@SuppressWarnings("deprecation")
		Date ee = d.getTime();
		String date =  + ee.getHours()+":"+ee.getMinutes()+":"+ee.getSeconds();
		
		
	
		
		try {
			fstream = new FileOutputStream(p);
			fout = new DataOutputStream(fstream);
			bw = new BufferedWriter(new OutputStreamWriter(fout));
			path    = p;
		}catch (FileNotFoundException e) {
			System.out.println("Path not found.");
			e.printStackTrace();
		} 
		try {
			bw.write("Log file for leWordsServer\n" +
					"Log starts record at " + date 
					+"\nWrite Log to $(PATH)="+path+".\n\n");
				
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void updateLog(int type,String body,boolean display){
		if (display) System.out.println(body);
		d= new GregorianCalendar();
		@SuppressWarnings("deprecation")
		Date e = d.getTime();

		
		
		String date =  + e.getHours()+":"+e.getMinutes()+":"+e.getSeconds();
		String prefix="";
		
		switch(type){
		case 0:
			break;
			
		case 10: //1X Server information
			prefix = "\t Id 10: Server|dictionary|initialized:\t";
			
			
			break;
		case 11:// Server online
			prefix ="\t Id 11: Server|online:\t";
			
			break;
		case 12:// Server offline
			prefix ="\t Id 11: Server|offline:\t";
			
			
		case 20: //2x Client to Server information
			break;
		case 21: //Player connected
			prefix ="\t Id 21: Client|Connected:\t";
			
			break;
		case 22: //Player disconnected
			prefix ="\t Id 22: Client|Diconnected:\t";
			
			break;
		case 30:// 3X Network
			prefix ="\t Id 30: Server|Port:\t";
		
			break;
			
		case 40://
			prefix ="\t Id 40: Game|Start:\t";
			break;
		case 41://
			prefix ="\t Id 41: Game|End:\t";
			break;
		case 42://
			prefix ="\t Id 42: Game|NewBoard:\t";
			break;
		default:
			prefix ="\t Id default:\t";
			break;
		}
	
		writeLog(date+ "|"+prefix+body);

	}

	public void writeLog(String u){
		
		
		try {
			bw.append(u);
			bw.newLine();
		} catch (IOException e) {
			System.out.println("Could not write Log.");
			e.printStackTrace();
		}
		try {
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}