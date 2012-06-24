import java.util.*;
import java.io.*;
public class Filter{

	public Filter(){
		
	}


	

	public void filterLevelOne(Dictionary full,Hypercube h,Board b){
		
		// look at all possible sequences of 3 letters and filter all words
		// which dont start with any of the 3 letter sequences

		boolean [][][] bb = new boolean[26][26][26]; // free this later!
		for (int i=0;i<26;i++){
			for (int j=0;j<26;j++){
				for(int k=0;k<26;k++){
					bb[i][j][k] = false;
				}
			}
		}
		// estimate number of words that are possible
		// read ntuples.txt
		File f = null;
		Scanner scan = null;
		try{
			f = new File("dictionary/3tuplesOn5.txt");
			scan = new Scanner(f);
		}
		catch(Exception e){
			System.exit(0);
		}

		int griddim = scan.nextInt();
		int maxRecursionDepth = scan.nextInt();
		if (maxRecursionDepth!=3){
			System.out.println("Only works for level 3 recursion. Error!");
		}else{
			int currentRecursionDepth,k,l,m,xk,yk,xl,yl,xm,ym,x,y,z;
			int N = 0;
			char u, v, w;
			while(scan.hasNext()){
				// read path
				currentRecursionDepth = scan.nextInt();
				k = scan.nextInt();
				l = scan.nextInt();
				m = scan.nextInt();

				// from k l m to xk,yk  xl,yl  xm,ym
				xk = k%griddim;
				yk = k/griddim;

				xl = l%griddim;
				yl = l/griddim;

				xm = m%griddim;
				ym = m/griddim;


				// read corresponding chars from board
				u= b.letters[xk][yk];
				v =b.letters[xl][yl];
				w =b.letters[xm][ym];


				// transform char to x y z position in hypercube
				// a =0, b=1, ..., z=25
				x =(int)u -97;
				y =(int)v -97;
				z =(int)w -97;
				if (!bb[x][y][z]){
					if (h.from[x][y][z]>=0){
						bb[x][y][z] = true;
						N = N+h.to[x][y][z]-h.from[x][y][z]+1;
					}
				}
			} 


			// now that one has estimated the combinations, write words

			String[]  S = new String[N];

			int pos = 0;
			for (k=0;k<26;k++){
				for(l=0;l<26;l++){
					for(m=0;m<26;m++){
						if (bb[k][l][m]){ // prefix exists in board
							if (h.from[k][l][m]>=0){ // prefix exist in dictionary
								for (int p=h.from[k][l][m] ;p<=h.to[k][l][m];p++){
									S[pos] = full.dictionary[p-2];
									pos ++;
								}
							}
						}
					}
				}
			}

			b.boardDictionary = new Dictionary(N,S);
		}

	}


	public void filterLevelTwo(Board b){
		// filter all words which cant exisit because
		// the needed letters dont exist
		// the words where the letters exist but arent
		// neighboured dont get filtered
		//
		// state : we have a filtered dictionary
		// size ~ 30k
		// naive approach :
		//  check each word
		//    check each letter of word
		//      check whether that letter occours on our 5x5 grid
		//  ... wow this looks bad
		//  30k words * ~4letters/word * up to 25 checks
		//  better would be:
		//  check aachen
		//  see aach doesnt work because no h on grid
		//  therefore remove aachener aachenerin aachenerdom ....

		// other possibility
		// SIGMA = {all letters} \ {letters on grid}
		// for sigma in SIGMA
		//  for all words
		//    if sigma in word
		//      remove word
		//  
		//  start with a letter that occours often then already a lot of words
		//  get filtered
		//  i take this approach

		

		Stack<Character> Sigma= new Stack<Character>();
		boolean found=false;
		for (char c = 'a';c<='z';c++){
			found = false;
			for (int i = 0;i< b.dim &&!found;i++){
				for (int j=0;j<b.dim &&!found;j++){
					if (c == b.letters[i][j]){
						found = true;
					}
				}
			}
			if (!found) Sigma.push(c);
		}


		String tmp;
		boolean [] possible = new boolean[b.boardDictionary.length];
		for (int i=0;i<b.boardDictionary.length;i++){
			possible[i] = true;
		}
		char c;
		int notPossible = 0;
		while(!Sigma.empty()){
			c = Sigma.peek();


			Sigma.pop();
			// this is a bed letter
			for (int i=0;i<b.boardDictionary.length;i++){
				if (possible[i]){
					tmp = b.boardDictionary.dictionary[i];
					if (tmp==null){
						// shouldnt happen but it does
						possible[i] = false;
						notPossible++;
					}else if (-1!=tmp.indexOf(c)){
						possible[i] = false;
						notPossible++;
					}
				}
			}
		}


		
		int pos = 0;
		String[] newDictionary = new String[b.boardDictionary.length];
		for (int i=0;i<b.boardDictionary.length;i++){
			if (possible[i]){
				if (b.boardDictionary.dictionary[i]!=null){
					newDictionary[pos] = b.boardDictionary.dictionary[i];
					pos ++;
				}
			}
		}
		b.boardDictionary = new Dictionary(pos,newDictionary);

	}

	public void filterLevelThree(Board b){
		// the result after this filter is a
		// dictionary with only the possible words

		// take depthsearch vom hypercube.cpp and modify such that you only go to field which fit the char

		int[][] field = new int[b.dim+2][b.dim+2];
		for(int i=1 ; i<b.dim+1 ;i++){
			for(int j=1 ; j<b.dim+1 ;j++){
				field[i][j] = 0;
			}
		}

		for(int i=0 ; i<b.dim+2 ;i++){ 
			field[i][0] = -1;
			field[i][b.dim+1] = -1;
			field[0][i] = -1;
			field[b.dim+1][i] = -1;
		}

		char[] letters;
		int m;
		int count = 0;
		Stack <String> stack = new Stack <String>();
		for (String word : b.boardDictionary.dictionary)
		{
	
			letters = word.toCharArray();
			m = letters.length;

			boolean found =  recursiveSearchDepthN(false,b,field,b.dim,letters,0,m-1,0,0);
			if (found){
				count ++;
				stack.push(word);
			//	System.out.println("FOUND");


			}
		}
		String[] subDict = new String[count];
		for ( int i=0;i<count;i++){
			subDict[i] = stack.peek();
			stack.pop();
		}

		b.boardDictionary = new Dictionary(count,subDict);
	}

	public boolean recursiveSearchDepthN(boolean ret,Board b,int[][] field, int n,char[] word,int L, int Lmax,int posi,int posj){
		//boolean ret= false;
		
		//System.out.println("at start : word[L] = "+ word[L]);
		if (L==0) { //Level 0
			//System.out.println("L==0");
			for (int i = 1;i<n+1;i++){
				for(int j = 1; j<n+1;j++){
					//System.out.println(b.letters[i-1][j-1]+" "+ word[L]);
					if (b.letters[i-1][j-1] == word[L]){
						if (field[i][j] !=-1 ){ // else boundary
							if (field[i][j] !=1){// else already taken
								field[i][j] = 1;
								ret = recursiveSearchDepthN(ret,b,field,n,word,L+1,Lmax,i,j);
							//	if (ret) return ret; // dont keep searching 
								field[i][j] = 0;
							}
						}
					}

				}
			}
		}else if (L<Lmax){
			//System.out.println("L=="+L);
			for(int i=posi-1;i<posi+2;i++){
				for (int j=posj-1;j<posj+2;j++){
					
					if (field[i][j] !=-1 ){ // else boundary
						if (field[i][j] !=1){// else already taken
							//System.out.println(b.letters[i-1][j-1]+" "+ word[L]);

							if (b.letters[i-1][j-1] == word[L]){
								field[i][j] = 1;
								ret = recursiveSearchDepthN(ret,b,field,n,word,L+1,Lmax,i,j);
							//	if (ret) return ret; // dont keep searching
								field[i][j] = 0;
							}
						}
					}

				}
			}
		}else{ // L == Lmax
			//System.out.println("L==Lmax");
			for(int i=posi-1;i<posi+2;i++){
				for (int j=posj-1;j<posj+2;j++){

					if (field[i][j] != -1){
						if (field[i][j] != 1){
							//System.out.println(b.letters[i-1][j-1]+" "+ word[L]);
							if (b.letters[i-1][j-1] ==word[L]){
								ret = true;
							//	return ret;
							}
						}
					}
				}
			}
		}
		return ret;
	}




}