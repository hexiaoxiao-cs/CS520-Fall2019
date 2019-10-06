package app;

import structures.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Minesweeper{

	public static void main(String args[]) {
		int dim=5;
		int numMines=3;
		int minesSafelyID = 0;	//number of mines safely identified.
		Environment e=new Environment(dim,numMines);
		Agent a=new Agent(dim);

		//this is for testing:
		boolean allowInput=true;
		Scanner scan=new Scanner(System.in);



		//Start:


		while(a.board.numMines<numMines) {	//while we havent found all the mines
			System.out.println("environment board:"); e.board.show();
			System.out.println("agent board:"); a.board.show();




			int[] queryCoord=a.assessKB();	
			if (queryCoord==null) {
				queryCoord= new int[2];
				//Random:
				//queryCoord[0]=random;
				//queryCoord[1]=random;
				
				//you choose random in the terminal: 
				System.out.println("\nInput random coordinate with format \"x+' '+y\":");
				StringTokenizer input = new StringTokenizer(scan.nextLine());
				queryCoord[0]=Integer.parseInt(input.nextToken());
				queryCoord[1]=Integer.parseInt(input.nextToken()); 


			}
			if (a.query(e, queryCoord[0], queryCoord[1]))
				minesSafelyID++;
			else {
				// mine goes off, but the agent can continue using the fact that a mine was discovered there 
				//to update its knowledge base. 
				//In this way the game can continue until the entire board is revealed 
			}

			a.board.show();

		}

		System.out.println(minesSafelyID+"/"+a.board.numMines+" safely identified");






	}


}
