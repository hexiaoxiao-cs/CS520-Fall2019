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
		int dim=23;
		int numMines=99;
		Environment e=new Environment(dim,numMines);	//this grid has all the answers (mine locations and clues)
		
		boolean baseline=true;
		Agent a=new Agent(dim,baseline); //grid filled with '?'

		//this is for testing baseline: 
		boolean allowInput=false;
		Scanner scan=new Scanner(System.in);

		//Start Game:
		while(a.board.numMines<numMines) {	//while we havent found all the mines
			
			System.out.println("num mines revealed="+a.board.numMines);
			System.out.println("environment board:"); e.board.show();
			System.out.println("agent board:"); a.board.show();

			int[] queryCoord=a.assessKB();	
			if (queryCoord==null) {
				if (baseline) {
					queryCoord=new int[2];
					//Computer chooses random coordinate to query (a random block with '?'):
					List<int[]> possibleCoords=a.board.getAllCoords().stream().filter((int[] coord)->a.board.arr[coord[0]][coord[1]]==Grid.aHidden ).collect(Collectors.toList());
					queryCoord=possibleCoords.get((int)(Math.random()*possibleCoords.size())); 
					/*
					if (allowInput) {//if you choose random in the terminal: 
						System.out.println("\nInput random coordinate with format row+' '+column:");
						StringTokenizer input = new StringTokenizer(scan.nextLine());
						queryCoord[0]=Integer.parseInt(input.nextToken());
						queryCoord[1]=Integer.parseInt(input.nextToken()); 
					}*/
					
				}else {//not baseline stuff
					
					
					
				}
				

			}
			if (!a.query(e, queryCoord[0], queryCoord[1])) {
				
				// mine goes off, but the agent can continue using the fact that a mine was discovered there 
				//to update its knowledge base. 
				//In this way the game can continue until the entire board is revealed 
			} 
			if (baseline) { 
				//Deduce everything before continuing:
				//	(after querying the main block, see if we need to update more blocks.)
				//	for each hidden block, query neighbors with clues (not hidden) to see if it marks/reveals hidden block
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
		System.out.println("All mines revealed: "+a.safelyIdentified+"/"+a.board.numMines+"~="+(int)((double)a.safelyIdentified/a.board.numMines*100)+"% safely identified."); 
		a.board.show();
		scan.close();
	}
	


}
