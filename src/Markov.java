import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class Markov {

	double[][] prob;
	double[][] probSummed;
	double[][][] prob3d;
	double[][][] probSummed3d;

	// given prob[i][j] equals the prob
	// to get char j after char i

	public Markov() {
		prob = new double[26][26];
		probSummed = new double[26][26];
		for (int i = 0; i < 26; i++) {
			for (int j = 0; j < 26; j++) {
				prob[i][j] = 0;
				probSummed[i][j] = 0;
			}
		}
		prob3d = new double[26][26][26];
		probSummed3d = new double[26][26][26];
		for (int i = 0; i < 26; i++) {
			for (int j = 0; j < 26; j++) {
				for (int k = 0; k < 26; k++) {
					prob[i][j] = 0;
					probSummed[i][j] = 0;
				}
			}
		}

	}

	public char[][] createTable(int dim) {
		char[][] c = new char[dim][dim];
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				c[i][j] = 'e'; // you never know
			}
		}

		// create head of markov chain
		Random r = new Random();
		char h = (char) (r.nextInt(26) + 'a');
		int given = (int) h - 97;

		for (int i = 0; i < dim; i++) {

			// ------>
			// <------
			// ------>
			// <------
			if (i % 2 == 0) {
				for (int j = dim - 1; j >= 0; j--) {
					double p = r.nextDouble(); // between 0,1
					int follows = 0;
					if (p < probSummed[given][0]) {
						follows = 0;

					} else {
						for (int k = 0; k < 25; k++) {
							if (probSummed[given][k] < p
									&& p < probSummed[given][k + 1]) {
								follows = k + 1;
								k = 999;
							}
						}
					}

					c[i][j] = (char) (follows + 97);
					given = follows;
				}

			} else {
				for (int j = 0; j < dim; j++) {
					double p = r.nextDouble(); // between 0,1
					int follows = 0;
					if (p < probSummed[given][0]) {
						follows = 0;

					} else {
						for (int k = 0; k < 25; k++) {
							if (probSummed[given][k] < p
									&& p < probSummed[given][k + 1]) {
								follows = k + 1;
								k = 999;
							}
						}
					}

					c[i][j] = (char) (follows + 97);
					given = follows;
				}
			}
		}

		return c;
	}

	public void printProbabilityTable() {
		for (int i = 0; i < 26; i++) {
			for (int j = 0; j < 26; j++) {
				System.out.format("%.5f\t", prob[i][j]);

			}
			System.out.println();
		}

	}

	public void createProbabilityTable() {
		// getName and send it to the server

		try {
			FileInputStream fstream = new FileInputStream(
					"dictionary/dictionary.txt");
			DataInputStream fin = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(fin));
			int N = 1328090; // words in dictionary
			String word = br.readLine(); // read int and skip;

			for (int i = 0; i < N - 2; i++) {

				word = br.readLine().toLowerCase();
				if (word.length() > 2) {
					char last = word.charAt(0);
					char next;
					int k, l;
					for (int j = 1; j < word.length(); j++) {
						next = word.charAt(j);
						k = (int) last - 97;
						l = (int) next - 97;

						if (k < 26 && l < 26) { // else some motherfucking
												// french letter â'°
							prob[k][l] += 1;
						}
						last = next;
					}
				}
			}
			for (int i = 0; i < 26; i++) {
				// prob[i][4] /= 2; // nerf the 'e'
				// prob[i][0] *=1.3; // buff the 'a'

			}

			for (int i = 0; i < 26; i++) {
				int rowsum = 0;
				for (int j = 0; j < 26; j++) {
					rowsum += prob[i][j];
				}
				if (rowsum != 0) {
					for (int j = 0; j < 26; j++) {
						prob[i][j] /= rowsum;
						for (int k = 0; k <= j; k++) {
							probSummed[i][j] += prob[i][k];
						}
					}
				}

			}

		} catch (FileNotFoundException e) {
			System.out.println("dictionary.txt is missing");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("could read dictionary.txt");
			e.printStackTrace();
		}

	}

	public void createProbabilityCube() {
		// getName and send it to the server

		try {
			FileInputStream fstream = new FileInputStream(
					"dictionary/dictionary.txt");
			DataInputStream fin = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(fin));
			int N = 1328090; // words in dictionary
			String word = br.readLine(); // read int and skip;

			for (int i = 0; i < N - 3; i++) {

				word = br.readLine().toLowerCase();
				if (word.length() > 2) {
					char last = word.charAt(0);
					char middle = word.charAt(1);
					char next;
					int k, l, m;
					for (int j = 2; j < word.length(); j++) {
						next = word.charAt(j);
						k = (int) last - 97;
						l = (int) middle - 97;
						m = (int) next - 97;

						if (k < 26 && l < 26 && m < 26) { // else some
															// motherfucking
															// french letter
															// â'°
							prob3d[k][l][m] += 1;
						}
						middle = next;
						last = middle;

					}
				}
			}

			for (int i = 0; i < 26; i++) {
				for (int j = 0; j < 26; j++) {
					int rowsum = 0;
					for (int k = 0; k < 26; k++) {

						rowsum += prob3d[i][j][k];
					}
					if (rowsum!=0){
						float tillnow =0;
						for (int k=0;k<26;k++){
							prob3d[i][j][k] /= rowsum;
							tillnow +=prob3d[i][j][k];
							probSummed3d[i][j][k] =tillnow;
						}
					}
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println("dictionary.txt is missing");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("could read dictionary.txt");
			e.printStackTrace();
		}

	}
	public char[][] createTableMarkov3(int dim) {
		char[][] c = new char[dim][dim];
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				c[i][j] = 'e'; // you never know
			}
		}

		// create head of markov chain
		Random r = new Random();
		char h = (char) (r.nextInt(26) + 'a');
		char h2 = (char) (r.nextInt(26)+'a');
		int given = (int) h - 97;
		int given2 =(int) h2-97;

		for (int i = 0; i < dim; i++) {

			// ------>
			// <------
			// ------>
			// <------
			if (i % 2 == 0) {
				for (int j = dim - 1; j >= 0; j--) {
					double p = r.nextDouble(); // between 0,1
					int follows = 0;
					if (p < probSummed3d[given][given2][follows]) {
						follows = 0;

					} else {
						for (int k = 0; k < 25; k++) {
							if (probSummed3d[given][given2][k] < p
									&& p < probSummed3d[given][given2][k + 1]) {
								follows = k + 1;
								k = 999;
							}
						}
					}

					c[i][j] = (char) (follows + 97);
					given = given2;
					given2 = follows;
				}

			} else {
				for (int j = 0; j < dim; j++) {
					double p = r.nextDouble(); // between 0,1
					int follows = 0;
					if (p < probSummed3d[given][given2][0]) {
						follows = 0;

					} else {
						for (int k = 0; k < 25; k++) {
							if (probSummed3d[given][given2][k] < p
									&& p < probSummed3d[given][given2][k + 1]) {
								follows = k + 1;
								k = 999;
							}
						}
					}

					c[i][j] = (char) (follows + 97);
					given =given2;
					given2 = follows;
				}
			}
		}

		return c;
	}



}
