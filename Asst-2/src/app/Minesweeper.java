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
	static boolean isConstrainted=false;
	static boolean isTotal=false;
	
	public static void main(String args[]) {
 
		boolean baseline=false;
		int dim=25;
		//this is for the bonus question #1
//		for (int i = 0 ; i<500; i++) {
//			playGame_1(baseline,dim,99,0);
//		}
		//this is the end for the Bonus question #1
		 
		//playGame(baseline,10,17,0);//dim=10, nummines=17 -> uncomment Line 152,138 for revealing the board
		
		//This is for the normal comparison 
		//print average final scores for plot:
		baseline=false;	//true for baseline
		isConstrainted=false;//Whether we adopt the num of mines constraint into the board
		isTotal=false;		//whether we search the total variable assignment tree or the partial tree
		for(int densityNum=1;densityNum<100;densityNum++) {
			int repeat=500;
			double avg=0;
			for(int i=1;i<repeat;i++) { 
				avg+=playGame(baseline,dim,0,(double)densityNum/100);
				//System.out.println(avg);
			}
			avg/=repeat;
			System.out.println(avg);
		}
		
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
		a.num_mines=numMines;
		//this is for testing baseline: 
		boolean allowInput=false;
		Scanner scan=new Scanner(System.in);
  
		//Agent_Method_1 b=new Agent_Method_1(dim);
		 
		

		//Start Game:
		while(a.board.numMines<numMines) {	//while we havent found all the mines
			
//			System.out.println("num mines revealed="+a.board.numMines);
//			System.out.println("num mines in board ="+e.board.numMines);
//			System.out.println("environment board:"); e.board.show();
// 
//			if(baseline) {
//				System.out.println("agent board:"); a.board.show();
//			}
//			else {
//				System.out.println("agent board:"); a.board.show();
//			}
			 
			int[] queryCoord=null;
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
					/*if (allowInput) {//You choose random in the terminal: 
						System.out.println("\nInput random coordinate with format row+' '+column:");
						StringTokenizer input = new StringTokenizer(scan.nextLine());
						queryCoord[0]=Integer.parseInt(input.nextToken());
						queryCoord[1]=Integer.parseInt(input.nextToken()); 
					}*/
					List<int[]> possibleCoords=a.board.getAllCoords().stream().filter((int[] coord)->a.board.arr[coord[0]][coord[1]]==Grid.aHidden ).collect(Collectors.toList());
					queryCoord=possibleCoords.get((int)(Math.random()*possibleCoords.size())); 
//					queryCoord[0]=0;//If you have played minesweeper, you will know that starting from coner is a good choice.
//					queryCoord[1]=0;
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
			else {
				while(a.board.numMines<numMines) {
					//System.out.println(a.equations.size());
//					System.out.println(queryCoord[0]+","+queryCoord[1]);
//					System.out.println(a.board.arr[queryCoord[0]][queryCoord[1]]);
					if(a.query(e, queryCoord[0], queryCoord[1])==false) {//System.out.println("Boom");
						//a.board.show();
						if(a.safe_nodes.isEmpty()) {
							List<int[]> possibleCoords=a.board.getAllCoords().stream().filter((int[] coord)->a.board.arr[coord[0]][coord[1]]==Grid.aHidden ).collect(Collectors.toList());
							if(possibleCoords.isEmpty()) {break;}
							queryCoord=possibleCoords.get((int)(Math.random()*possibleCoords.size())); 
							continue;
							}
						}
					//For each query, the algorithm will automatically determine and expand the board to whether it can not derive a solution
					while(!a.safe_nodes.isEmpty()) {
						Coordinate coord= a.safe_nodes.poll();
						
						if(a.query(e,coord.x , coord.y)==false) {//System.out.println("Boom");
						}
						//a.board.show();
					}
					if(a.board.numMines!=numMines) {
						ArrayList<Coordinate> temp;
						if(isTotal==false) {
							temp = a.getNextBestPoint_Limited();
							}
						
						else {
							
							temp = a.getNextBestPoint(isConstrainted);
						}
						int counts=0;
						boolean isChanged=false;
						for(Coordinate c : temp) {
							if(c.occur!=0) {
								if(counts!=0) {break;
								}
								else {
									if(a.board.arr[queryCoord[0]][queryCoord[1]]!=a.board.aHidden) {
										
										continue;
									}
									else {
									queryCoord[0]=c.x;
									queryCoord[1]=c.y;
									isChanged=true;
									break;
									}
								}
							}
							else {

								a.safe_nodes.add(c);
							}
						}
						Coordinate tt= a.safe_nodes.poll();
						if(tt==null) {
							if(isChanged==true) {
								continue;
							}
							else {
								List<int[]> possibleCoords=a.board.getAllCoords().stream().filter((int[] coord)->a.board.arr[coord[0]][coord[1]]==Grid.aHidden ).collect(Collectors.toList());
								if(possibleCoords.isEmpty()) {break;}
								queryCoord=possibleCoords.get((int)(Math.random()*possibleCoords.size())); 
								continue;
							}
						}else {
							if(a.board.arr[queryCoord[0]][queryCoord[1]]!=a.board.aHidden) {
								List<int[]> possibleCoords=a.board.getAllCoords().stream().filter((int[] coord)->a.board.arr[coord[0]][coord[1]]==Grid.aHidden ).collect(Collectors.toList());
								if(possibleCoords.isEmpty()) {break;}
								queryCoord=possibleCoords.get((int)(Math.random()*possibleCoords.size())); 
								continue;
							}else {
							queryCoord[0]=tt.x;
							queryCoord[1]=tt.y;
							}
						}
						
						/*
						int[] temp = a.getNextBestPoint();
						if(temp==null||queryCoord[0]==temp[0]&&queryCoord[1]==temp[1]) {// do some random stuff
							List<int[]> possibleCoords=a.board.getAllCoords().stream().filter((int[] coord)->a.board.arr[coord[0]][coord[1]]==Grid.aHidden ).collect(Collectors.toList());
							if(possibleCoords.isEmpty()) {break;}
							queryCoord=possibleCoords.get((int)(Math.random()*possibleCoords.size())); 
						}else {
							queryCoord=temp;
						}
						*/
//						System.out.println(queryCoord[0]+","+queryCoord[1]);
//						System.out.println(a.board.numMines);
					}
				}
			}
 
		} 
//		System.out.println("All mines revealed: "+a.safelyIdentified+"/"+e.board.numMines+"~="+(int)((double)a.safelyIdentified/a.board.numMines*100)+"% safely identified."); 
//		a.board.show();
// 
		scan.close();
		return (int)((double)a.safelyIdentified/e.board.numMines*100);
	}
	
	public static double playGame_1(boolean baseline, int dim, int numMines, double density) {	
		Environment e;
		if (density==0&&numMines>0) {
			e=new Environment(dim,numMines);	//this grid has all the answers (mine locations and clues)
		}else{
			e=new Environment(dim,density,true);	
			numMines=e.board.numMines;
		}
		
		
		Agent_Method_1 a=new Agent_Method_1(dim,baseline); //grid filled with '?'
		a.num_mines=numMines;
		//this is for testing baseline: 
		boolean allowInput=false;
		Scanner scan=new Scanner(System.in);
  
		//Agent_Method_1 b=new Agent_Method_1(dim);
		 
		

		//Start Game:
		while(a.board.numMines<numMines) {	//while we havent found all the mines
			
//			System.out.println("num mines revealed="+a.board.numMines);
//			System.out.println("num mines in board ="+e.board.numMines);
//			System.out.println("environment board:"); e.board.show();
// 
//			if(baseline) {
//				System.out.println("agent board:"); a.board.show();
//			}
//			else {
//				System.out.println("agent board:"); a.board.show();
//			}
			 
			int[] queryCoord=null;
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
					/*if (allowInput) {//You choose random in the terminal: 
						System.out.println("\nInput random coordinate with format row+' '+column:");
						StringTokenizer input = new StringTokenizer(scan.nextLine());
						queryCoord[0]=Integer.parseInt(input.nextToken());
						queryCoord[1]=Integer.parseInt(input.nextToken()); 
					}*/
					List<int[]> possibleCoords=a.board.getAllCoords().stream().filter((int[] coord)->a.board.arr[coord[0]][coord[1]]==Grid.aHidden ).collect(Collectors.toList());
					queryCoord=possibleCoords.get((int)(Math.random()*possibleCoords.size())); 
//					queryCoord[0]=0;//If you have played minesweeper, you will know that starting from coner is a good choice.
//					queryCoord[1]=0;
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
			else {
				while(a.board.numMines<numMines) {
					//System.out.println(a.equations.size());
//					System.out.println(queryCoord[0]+","+queryCoord[1]);
//					System.out.println(a.board.arr[queryCoord[0]][queryCoord[1]]);
					if(a.query(e, queryCoord[0], queryCoord[1])==false) {//System.out.println("Boom");
						if(a.safe_nodes.isEmpty()) {
							List<int[]> possibleCoords=a.board.getAllCoords().stream().filter((int[] coord)->a.board.arr[coord[0]][coord[1]]==Grid.aHidden ).collect(Collectors.toList());
							if(possibleCoords.isEmpty()) {break;}
							queryCoord=possibleCoords.get((int)(Math.random()*possibleCoords.size())); 
							continue;
							}
						}
					//For each query, the algorithm will automatically determine and expand the board to whether it can not derive a solution
					while(!a.safe_nodes.isEmpty()) {
						Coordinate coord= a.safe_nodes.poll();
						
						if(a.query(e,coord.x , coord.y)==false) {//System.out.println("Boom");
						}
						//a.board.show();
					}
					if(a.board.numMines!=numMines) {
						ArrayList<Coordinate> temp;
						if(isTotal==false) {
							temp = a.getNextBestPoint_notreveal_driver();
							}
						
						else {
							
							temp = a.getNextBestPoint_notreveal_driver();
						}
						int counts=0;
						boolean isChanged=false;
						for(Coordinate c : temp) {
							if(c.occur!=0) {
								if(counts!=0) {break;
								}
								else {
									if(a.board.arr[queryCoord[0]][queryCoord[1]]!=a.board.aHidden) {
										
										continue;
									}
									else {
									queryCoord[0]=c.x;
									queryCoord[1]=c.y;
									isChanged=true;
									break;
									}
								}
							}
							else {

								a.safe_nodes.add(c);
							}
						}
						Coordinate tt= a.safe_nodes.poll();
						if(tt==null) {
							if(isChanged==true) {
								continue;
							}
							else {
								List<int[]> possibleCoords=a.board.getAllCoords().stream().filter((int[] coord)->a.board.arr[coord[0]][coord[1]]==Grid.aHidden ).collect(Collectors.toList());
								if(possibleCoords.isEmpty()) {break;}
								queryCoord=possibleCoords.get((int)(Math.random()*possibleCoords.size())); 
								continue;
							}
						}else {
							if(a.board.arr[queryCoord[0]][queryCoord[1]]!=a.board.aHidden) {
								List<int[]> possibleCoords=a.board.getAllCoords().stream().filter((int[] coord)->a.board.arr[coord[0]][coord[1]]==Grid.aHidden ).collect(Collectors.toList());
								if(possibleCoords.isEmpty()) {break;}
								queryCoord=possibleCoords.get((int)(Math.random()*possibleCoords.size())); 
								continue;
							}else {
							queryCoord[0]=tt.x;
							queryCoord[1]=tt.y;
							}
						}
						
						/*
						int[] temp = a.getNextBestPoint();
						if(temp==null||queryCoord[0]==temp[0]&&queryCoord[1]==temp[1]) {// do some random stuff
							List<int[]> possibleCoords=a.board.getAllCoords().stream().filter((int[] coord)->a.board.arr[coord[0]][coord[1]]==Grid.aHidden ).collect(Collectors.toList());
							if(possibleCoords.isEmpty()) {break;}
							queryCoord=possibleCoords.get((int)(Math.random()*possibleCoords.size())); 
						}else {
							queryCoord=temp;
						}
						*/
//						System.out.println(queryCoord[0]+","+queryCoord[1]);
//						System.out.println(a.board.numMines);
					}
				}
			}
 
		} 
//		System.out.println("All mines revealed: "+a.safelyIdentified+"/"+e.board.numMines+"~="+(int)((double)a.safelyIdentified/a.board.numMines*100)+"% safely identified."); 
		
		//stat for bonus
		int safe_ident=0;
		for(int i = 0 ; i < e.board.dim;i++) {
			for(int j=0; j<e.board.dim;j++) {
				if(e.board.arr[i][j]==e.board.eMine&&a.board.arr[i][j]==a.board.aMineAndCovered) {
					safe_ident++;
				}
			}
		}
//		System.out.println("All mines revealed: "+safe_ident+"/"+e.board.numMines+"~="+(int)((double)safe_ident/a.board.numMines*100)+"% safely identified."); 
//		System.out.println("Wrong Mines labeled:" + (a.safelyIdentified-safe_ident));
		//a.board.show();
// 
		System.out.println((double)safe_ident/e.board.numMines*100+","+(a.safelyIdentified-safe_ident));
		scan.close();
		return (int)((double)safe_ident/e.board.numMines*100);
	}
	

}
