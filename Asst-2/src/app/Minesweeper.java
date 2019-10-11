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
		Environment e=new Environment(dim,numMines);
		
		boolean baseline=true;
		Agent a=new Agent(dim,baseline);

		//this is for testing: 
		
		boolean allowInput=true;
		Scanner scan=new Scanner(System.in);

		//Start Game:
		while(a.board.numMines<numMines) {	//while we havent found all the mines
			System.out.println("environment board:"); e.board.show();
			System.out.println("agent board:"); a.board.show();

			int[] queryCoord=a.assessKB();	
			if (queryCoord==null) {
				if (baseline) {
					queryCoord= new int[2];
					//Computer chooses random coordinate to query:
					//queryCoord[0]=random;
					//queryCoord[1]=random;
	
	
					if (allowInput) {//You choose random in the terminal: 
						System.out.println("\nInput random coordinate with format row+' '+column:");
						StringTokenizer input = new StringTokenizer(scan.nextLine());
						queryCoord[0]=Integer.parseInt(input.nextToken());
						queryCoord[1]=Integer.parseInt(input.nextToken()); 
					}
				}else {
					
					
				}
				

			}
			if (!a.query(e, queryCoord[0], queryCoord[1])) {
				
				// mine goes off, but the agent can continue using the fact that a mine was discovered there 
				//to update its knowledge base. 
				//In this way the game can continue until the entire board is revealed 
			} 

		} 
		System.out.println(a.safelyIdentified+"/"+a.board.numMines+" safely identified."); 

	}
	


}
