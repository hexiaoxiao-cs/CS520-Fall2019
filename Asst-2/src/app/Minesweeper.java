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
		Environment e=new Environment(dim,numMines);	//this grid has all the answers (mine locations and clues)
		
		boolean baseline=true;
		Agent a=new Agent(dim,baseline); //grid filled with '?'

		//this is for testing baseline: 
		boolean allowInput=true;
		Scanner scan=new Scanner(System.in);

		//Start Game:
		while(a.board.numMines<numMines) {	//while we havent found all the mines
			System.out.println("environment board:"); e.board.show();
			System.out.println("agent board:"); a.board.show();

			int[] queryCoord=a.assessKB();	
			if (queryCoord==null) {
				if (baseline) {
					queryCoord=new int[2];
					//Computer chooses random coordinate to query:
					 
					//List<int[]> possibleCoords=a.board.getAllCoordsSuchThat((int[] coord)->true).stream().any;
					//queryCoord=possibleCoords.get((int)(Math.random()*possibleCoords.size())); 
					
					if (allowInput) {//if you choose random in the terminal: 
						System.out.println("\nInput random coordinate with format row+' '+column:");
						StringTokenizer input = new StringTokenizer(scan.nextLine());
						queryCoord[0]=Integer.parseInt(input.nextToken());
						queryCoord[1]=Integer.parseInt(input.nextToken()); 
					}
					
				}else {//not baseline stuff
					
					
					
				}
				

			}
			if (!a.query(e, queryCoord[0], queryCoord[1])) {
				
				// mine goes off, but the agent can continue using the fact that a mine was discovered there 
				//to update its knowledge base. 
				//In this way the game can continue until the entire board is revealed 
			} 
			if (baseline) { //System.out.println("done quering the main one. query lingering ones.");
				//for each hidden block, query uncovered neighbors with clues (not hidden)
				List<int[]> toQuery=new ArrayList<int[]>();
				Predicate<int[]> p1=(int[]coord)->a.board.arr[coord[0]][coord[1]]==Grid.aHidden;
				Predicate<int[]> p2=(int[]coord)->a.board.arr[coord[0]][coord[1]]>='0'&&a.board.arr[coord[0]][coord[1]]<='8';
				List<int[]>temp=a.board.getAllCoords().stream().filter(p1).collect(Collectors.toList());
				for(int[] coord:temp) {
					toQuery.addAll( a.board.getNeighbors(coord[0], coord[1]).stream().filter(p2).collect(Collectors.toList()));
				}
				toQuery.stream().distinct().forEach((int[]coord)->a.query(e, coord[0], coord[1]));
				
			}

		} 
		System.out.println(a.safelyIdentified+"/"+a.board.numMines+" safely identified."); 

	}
	


}
