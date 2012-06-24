import java.util.*;
import java.io.*;
public class Board{


	public int dim;
	public char[][] letters;
	public int [][] points;
	public Dictionary boardDictionary;
	public boolean initialized= false;



	public Board(int d){
		dim = d;
		letters = new char[d][d];
		points  = new int[d][d];
	}




	public void fillBoard(){
		for (int i=0;i<dim;i++){
			for (int j=0;j<dim;j++){
				char c = getRandomChar();
				if (c=='x' || c=='y' || c == 'q'
						|| c == 'z' || c =='z' || c =='p'
						|| c=='v' || c =='w' || c=='c'){
					c = getRandomChar();
				}else if (c!='a' && c!='i' && c!='e' && c!='o'){
					c = getRandomChar();
				}
				if (c=='x' || c=='y' || c == 'q'
						|| c == 'z' || c =='z' || c =='p'
						|| c=='v' || c =='w' || c=='c'){
					c = getRandomChar();
				}else if (c!='a' && c!='i' && c!='e' && c!='o'){
					c = getRandomChar();
				}

				// from here
				/*
        if (i%3==1) {c  = 'a'; // TODO REMOVE THIS 
        }else if (i%3==2){ c= 'm';
        }else{
         c='n';}
        // to here
				 */
				c = 'a';
				letters[i][j] = c;
				points[i][j] = pointsOf(c);  
			}
		}
		letters[0][0] = 'a';
		letters[0][1] = 'a';
		letters[0][2] = 'c';
		letters[0][3] = 'h';
		letters[0][4] = 'e';
		letters[1][4] = 'n';
		letters[1][0] = 'l';
		letters[1][1] = 'r';
		letters[1][2] = 'n';
		letters[1][3] = 'u';
		letters[2][0] = 'f';
		letters[2][1] = 'i';
		letters[2][2] = 's';
		letters[2][3] = 'c';
		letters[3][0] = 'd';
		letters[3][1] = 'e';
		letters[3][2] = 'n';
		letters[3][3] = 'h';
		letters[1][4] = 'c';
		letters[2][4] = 'h';
		letters[3][4] = 'e';
		letters[4][4] = 'n';
		letters[4][3] = 'd';
		letters[4][2] = 'e';
		letters[4][1] = 'r';


		initialized = true;

	}

	public void printBoard(){


		if (!initialized) {
			System.out.println("Not initialized") ;
		}

		for (int i=0;i<dim;i++){
			for (int j=0;j<dim;j++){
				System.out.print(letters[i][j]);
				System.out.print(" ");
			}
			System.out.println(" ");
		}
		System.out.println("");

		for (int i=0;i<dim;i++){
			for (int j=0;j<dim;j++){
				System.out.print(points[i][j]);
				System.out.print(" ");
			}
			System.out.println(" ");
		}

		System.out.println("");

	}




	public char getRandomChar(){
		Random r = new Random();
		char c = (char)(r.nextInt(26) + 'a');
		return c;
	}

	public char getRandomVowel(){
		Random r = new Random();
		char c = (char)(r.nextInt(26) + 'a');
		if (c=='a' || c=='e' || c=='i' || c=='o'|| c=='u'){
			return c;
		}else{
			return getRandomVowel();
		}
	}

	public char getRandomConsonant(){
		Random r = new Random();
		char c = (char)(r.nextInt(26) + 'a');

		if (c=='a' || c=='e' || c=='i' || c=='o'|| c=='u'){
			return getRandomConsonant();
		}else{
			return c;
		}
	}

	public int pointsOf(char c){
		// this is heuristic
		// see http://de.wikipedia.org/wiki/Buchstabenh%C3%A4ufigkeit
		// points by position of frequency

		int points=0;
		switch(c){
		case'e':
		case'n':
		case'i':
		case's': 
			points = 1; 
			break;
		case'r':
		case'a':
		case't':
		case'd':
			points = 2; 
			break;
		case'h':
		case'u':
		case'l':
		case'c':
			points = 3; 
			break;
		case'g':
		case'm':
		case'o':
		case'b':
			points = 4; 
			break;
		case'w':
		case'f':
		case'k':
		case'z':
			points = 5;
			break;
		case'p':
		case'v':
		case'j':
			points = 6;
			break;
		case'y':
		case'x':
		case'q':
			points =7; 
			break;
		}



		return points;
	}

	public int checkWord(String S){
		int p =0 ; // if word doesnt exist : 0 points
		boolean found = false;
		for ( int i=0;i<boardDictionary.length &&!found ; i++){
			// for(int i=340;i<390;i++){
			// System.out.println(S );
			//    System.out.println(i );
			//    System.out.println(boardDictionary.dictionary[i] );
			if (boardDictionary.dictionary[i].equalsIgnoreCase(S)){
				// if( boardDictionary.dictionary[i] == S){
				p = calcWordPoints(boardDictionary.dictionary[i]); ; // TODO calc points correctly!
				found = true;
			}
		}


		return p;
	}

	public int calcWordPoints(String S){
		int p  =0 ;
		char[] c = S.toCharArray();
		for (int i=0;i<c.length;i++){
			p+=pointsOf(c[i]);

		}
		return p;


	}

}