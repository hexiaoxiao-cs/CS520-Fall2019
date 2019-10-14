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
 
		boolean baseline=false;
		int dim=23;
		playGame(baseline,dim,99,0);
		 
		/*
		//print average final scores for plot:
		 baseline=true;	//for baseline
		 
		for(int densityNum=1;densityNum<100;densityNum++) {
			int repeat=500;
			double avg=0;
			for(int i=1;i<repeat;i++) { 
				avg+=playGame(baseline,dim,0,(double)densityNum/100);
			}
			avg/=repeat;
			System.out.println(avg);
		}*/
		
	}

	public static double playGame(boolean baseline, int dim, int numMines, double density) {	
		Environment e;
		if (density==0&&numMines>0) {
			e=new Environment(dim,numMines);	//this grid has all the answers (mine locations and clues)
		}else{
			e=new Environment(dim,density,true);	
			numMines=e.board.numMines;
		}
		
		
		Agent a=new Agent(dim,baseline); //grid filled with '?'

		//this is for testing baseline: 
		boolean allowInput=false;
		Scanner scan=new Scanner(System.in);
  
		Agent_Method_1 b=new Agent_Method_1(dim);
		 
		

		//Start Game:
		while(a.board.numMines<numMines) {	//while we havent found all the mines
			
			System.out.println("num mines revealed="+a.board.numMines);
			System.out.println("environment board:"); e.board.show();
 
			if(baseline) {
				System.out.println("agent board:"); a.board.show();
			}
			else {
				System.out.println("agent board:"); b.board.show();
			}
			 
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
					}
				else {
					queryCoord= new int[2];
					if (allowInput) {//You choose random in the terminal: 
						System.out.println("\nInput random coordinate with format row+' '+column:");
						StringTokenizer input = new StringTokenizer(scan.nextLine());
						queryCoord[0]=Integer.parseInt(input.nextToken());
						queryCoord[1]=Integer.parseInt(input.nextToken()); 
					}
					if(b.query(e, queryCoord[0], queryCoord[1])==false) {System.out.println("Boom"); return 0;}
					//For each query, the algorithm will automatically determine and expand the board to whether it can not derive a solution
					while(!b.safe_nodes.isEmpty()) {
						int[] coord= b.safe_nodes.poll();
						if(b.query(e,coord[0] , coord[1])==false) {System.out.println("Boom");return 0;}
					}
				}
				

			}
			/*if (!a.query(e, queryCoord[0], queryCoord[1])) {
				
				// mine goes off, but the agent can continue using the fact that a mine was discovered there 
				//to update its knowledge base. 
				//In this way the game can continue until the entire board is revealed 
			} */
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
		//System.out.println("All mines revealed: "+a.safelyIdentified+"/"+a.board.numMines+"~="+(int)((double)a.safelyIdentified/a.board.numMines*100)+"% safely identified."); 
		//a.board.show();
 
		scan.close();
		return (int)((double)a.safelyIdentified/a.board.numMines*100);
	}
	


}
