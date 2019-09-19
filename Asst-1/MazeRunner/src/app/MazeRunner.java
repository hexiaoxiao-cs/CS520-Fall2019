package app;

import structures.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MazeRunner {

	public static Grid grid;
 
	//public static int dim;
	//public static double prob;
	public static int DFS_fringe_size=0;
	public static int A_manhattan_numExpansions=0;
	//public static int A_manhattan_max_nodes_expanded=0;
 

	public static class Coord implements Comparable<Coord> { // coordinate object. used in fringe.
		public int x;
		public int y;
		public double weight;
		public double weight_to_start;
		public Coord parent;
		public int f;	//estimated s->n->g. useful for priority queue for A*

		public Coord(int x, int y, Coord parent) {
			this.x = x;
			this.y = y;
			this.parent = parent;
			this.weight=0;
		}

		public String toString() {
			return "(" + x + "," + y + ")";
		}
		public int compareTo(Coord another) {
			if(another.weight==this.weight) {return 0;}
			if(another.weight<this.weight) {return 1;}
			else {return -1;}
		}
		public boolean equals(Object o) {//**should have parameter Object. Else .contains will not work for lists.
			if(!(o instanceof Coord)) return false;
			Coord c=(Coord)o;
			return (this.x==c.x)&&(this.y==c.y);
		}
	}

	public static Coord DFS() {
		DFS_fringe_size=0;
		Stack<Coord> fringe = new Stack<Coord>();
		fringe.push(new Coord(0, 0, null));
		Coord current = null;
		Coord goal = null;
		
		 
		
		while (!fringe.isEmpty()) {
			current = fringe.pop();
			// Find all neighbors:
			for (Coord c : grid.getNeighbors(current.x, current.y)) {
				c.parent = current;
				//if(!fringe.contains(c))
					fringe.push(c); 
				grid.occupy(current.x, current.y);
				
			}//System.out.println(fringe.size());
			if (grid.isGoal(current.x, current.y)) { // save goal coordinate so we can backtrack later
				goal = current;
			}
			
			if (DFS_fringe_size<fringe.size())	//keep track of how large the fringe gets.
				DFS_fringe_size=fringe.size();
			
		}
		// Retrace steps to show path:
		//System.out.println(DFS_fringe_size);
		return goal;
	}

	public static Coord BFS() {
		Queue<Coord> fringe = new LinkedList<Coord>();
		fringe.add(new Coord(0, 0, null));
		Coord current = null;
		Coord goal = null;
		//int counter=0;
		while (!fringe.isEmpty()) {
			current = fringe.remove();
			// Find all neighbors:

			for (Coord c : grid.getNeighbors(current.x, current.y)) {
				c.parent = current;
				if(!fringe.contains(c)) {fringe.add(c);}
				grid.occupy(current.x, current.y);
			}
			//grid.occupy(current.x, current.y);
			if (grid.isGoal(current.x, current.y)) { // save goal coordinate so we can backtrack later
				goal = current;
			} 
		}
		
		//grid.clearOccupied();
		//grid.showPath(goal);
		// Retrace steps to show path:
		return goal;
	}
	//Astar:
	//Arguments: 1. which weighting? 0 for euclid, 1 for Manhattan

	public static Coord[] BiBFS() {
		Queue<Coord> fringeL = new LinkedList<Coord>();
		Queue<Coord> fringeR = new LinkedList<Coord>();
		Queue<Coord> fringePtr = null;
		Coord start=new Coord(0,0,null);
		fringeL.add(start);
		Coord end = new Coord(grid.dim-1, grid.dim-1, null);
		fringeR.add(end);
		
		Coord currentL = null;
		Coord currentR = null;
		Coord overlap=null;
		Coord overlap2=null;
		Coord toReturn[]= new Coord[2];
		Coord[][] currentRm=new Coord[grid.dim][grid.dim];
		Coord[][] currentLm=new Coord[grid.dim][grid.dim];
		currentLm[0][0]=start;
		currentRm[grid.dim-1][grid.dim-1]=end;
		//left side bfs='8'. right side bfs='9'
		while (!fringeL.isEmpty()&&!fringeR.isEmpty()&&overlap==null) { 
			currentL = fringeL.remove();
			currentR = fringeR.remove();
			//COLLECT NEIGHBORS FOR LEFT SIDE:
			List<Coord> neighbors=grid.getNeighbors(currentL.x, currentL.y);
			//neighbors.removeIf((Coord coord)-> grid.getNumAt(coord.x, coord.y)==8);
			for (Coord c : neighbors) { 
				if(grid.getNumAt(c.x,c.y)==8) {continue;}
				c.parent = currentL;
				if(!fringeL.contains(c)) {fringeL.add(c);
				currentLm[c.x][c.y]=c;}
//if (fringeR.contains(new Coord(c.x,c.y,null))){
				if (currentRm[c.x][c.y]!=null){	//FOUND OVERLAPPED.
				 	overlap=c; 
					fringePtr=fringeR;  

					//final Coord tempOverlap=overlap;
					//overlap2=fringePtr.stream().filter((Coord x)->x.equals(tempOverlap)).findFirst().get();
					
					overlap2=currentRm[c.x][c.y]; 
					break; 
				}
				grid.arr[currentL.x][currentL.y]=8;	 
			}
			
			//COLLECT NEIGHBORS FOR RIGHT SIDE (if we still don't have an overlap):
			neighbors=grid.getNeighbors(currentR.x, currentR.y);
			//neighbors.removeIf((Coord coord)-> grid.getNumAt(coord.x, coord.y)==9);
			if(overlap==null)	{
				for (Coord c : neighbors) {
					if(grid.getNumAt(c.x, c.y)==9) {continue;}
					c.parent = currentR;
					if(!fringeR.contains(c)) {fringeR.add(c);
					currentRm[c.x][c.y]=c;}
					//if (fringeL.contains(new Coord(c.x,c.y,null))
					if (currentLm[c.x][c.y]!=null){	//FOUND OVERLAPPED.
						overlap=c;
						fringePtr=fringeL;
  
						overlap2=currentLm[c.x][c.y]; 
						break;
					}
					grid.arr[currentR.x][currentR.y]=9;	
				}
			} 
		} 
		
		
		
		//grid.show();
		//System.out.println("overlap="+overlap+"=overlap2="+overlap2);
		
		//clear numbers on grid to show: 
		for(int i=0;i<grid.dim;i++) {
			for(int j=0;j<grid.dim;j++) {
				if(grid.arr[i][j]==8||grid.arr[i][j]==9)
					grid.arr[i][j]=Grid.FreeNum;
			}
		} 
		grid.arr[0][0]=Grid.StartNum;
		grid.arr[grid.dim-1][grid.dim-1]=Grid.EndNum;
		
//		grid.showPath(overlap);
//		
//		//System.out.println("\nHere is one side of BiBfs:");
//		//grid.show();
//		
//		grid.showPath(overlap2);  
//		
		
		toReturn[0]=overlap;
		toReturn[1]=overlap2;
		return toReturn;
	}
	//Astar:
	//Arguments: 1. which weighting? false for euclid, true for Manhattan
	public static Coord Astar(boolean isManhattan) {
		A_manhattan_numExpansions=0;
		
		PriorityQueue<Coord> open_set = new PriorityQueue<Coord>();
		open_set.add(new Coord(0,0,null));
		Coord current = null;
		Coord goal = null;
		double curr_weight;//current weight(for priority)
		double curr_weight_to_start;//current weight from the starting point
		Coord[][] closed_set=new Coord[grid.dim][grid.dim]; //a better way to store the closed set such that O(1) access with give x,y coordinate
		while(!open_set.isEmpty()) {
			current = open_set.poll();//removed from open
			//System.out.println("Picked"+current.x+","+current.y+"point with priority"+current.weight);
			closed_set[current.x][current.y]=current;//put current to the closed set
			
			
			
			if(grid.isGoal(current.x, current.y)) {//if Goal Just go
				goal=current;
				break;
			}
 
			for (Coord c: grid.getNeighbors(current.x, current.y)) {
				c.parent = current;//first, set path
				//check whether in the closed set or (wall, burnt)
				if(closed_set[c.x][c.y]!=null||grid.isFree(c.x, c.y)==false) {
					continue;
				}
				//open_set.
				//Calculate heuristic function and the distance between current point and previous point
				if(isManhattan==true){
					curr_weight_to_start=current.weight_to_start+Manhattan(current.x,current.y,c.x,c.y);//g(x) Actual distance between new point to the old point
					
					curr_weight = curr_weight_to_start+Manhattan(current.x,current.y,grid.dim-1,grid.dim-1);//g(x)+h(x)
				 
					
				}
				else {
					curr_weight_to_start=current.weight_to_start+Euclid(current.x,current.y,c.x,c.y);//g(x) Actual distance between new point to the old point
					curr_weight = curr_weight_to_start+Euclid(current.x,current.y,grid.dim-1,grid.dim-1);
				}
				//System.out.println(curr_weight_to_start);
				//System.out.println(curr_weight+"\n");
				c.weight=curr_weight;
				c.weight_to_start=curr_weight_to_start;
				if(open_set.contains(c)) {
					if(open_set.removeIf((t)->t.weight>c.weight)==true) {//if in the open set it has a worse node than the one being inserted
						//sth being deleted
						open_set.add(c);
						if (isManhattan)
							A_manhattan_numExpansions++;
							
						
					}
					//else not doing anything, which is keep the node in the open_set since it is currently with a better route
				}
				else {
					//does not have c in open set
					open_set.add(c);
					if (isManhattan)
						A_manhattan_numExpansions++;
					
					
					
				}
				
				
				//fringe.add(c);
				
			}
			
		}
		//grid.showPath(goal);
		return goal; 
	}
	public static Coord Astar_ULLR(boolean isManhattan) {
		PriorityQueue<Coord> open_set = new PriorityQueue<Coord>();
		open_set.add(new Coord(0,grid.dim-1,null));
		Coord current = null;
		Coord goal = null;
		double curr_weight;//current weight(for priority)
		double curr_weight_to_start;//current weight from the starting point
		Coord[][] closed_set=new Coord[grid.dim][grid.dim]; //a better way to store the closed set such that O(1) access with give x,y coordinate
		while(!open_set.isEmpty()) {
			current = open_set.poll();//removed from open
			//System.out.println("Picked"+current.x+","+current.y+"point with priority"+current.weight);
			closed_set[current.x][current.y]=current;//put current to the closed set
			if(current.x==grid.dim-1&&current.y==0) {//if Goal Just go
				goal=current;
				break;
			}
 
			for (Coord c: grid.getNeighbors(current.x, current.y)) {
				c.parent = current;//first, set path
				//check whether in the closed set or (wall, burnt)
				if(closed_set[c.x][c.y]!=null||grid.isFree(c.x, c.y)==false) {
					continue;
				}
				//open_set.
				//Calculate heuristic function and the distance between current point and previous point
				if(isManhattan==true){
					curr_weight_to_start=current.weight_to_start+Manhattan(current.x,current.y,c.x,c.y);//g(x) Actual distance between new point to the old point
					
					curr_weight = curr_weight_to_start+Manhattan(current.x,current.y,grid.dim-1,0);//g(x)+h(x)
				}
				else {
					curr_weight_to_start=current.weight_to_start+Euclid(current.x,current.y,c.x,c.y);//g(x) Actual distance between new point to the old point
					curr_weight = curr_weight_to_start+Euclid(current.x,current.y,grid.dim-1,0);
				}
				//System.out.println(curr_weight_to_start);
				//System.out.println(curr_weight+"\n");
				c.weight=curr_weight;
				c.weight_to_start=curr_weight_to_start;
				if(open_set.contains(c)) {
					if(open_set.removeIf((t)->t.weight>c.weight)==true) {//if in the open set it has a worse node than the one being inserted
						//sth being deleted
						open_set.add(c);
					}
					//else not doing anything, which is keep the node in the open_set since it is currently with a better route
				}
				else {
					//does not have c in open set
					open_set.add(c);
				}
				
				
				//fringe.add(c);
				
			}
			
		}
		//grid.showPath(goal);
		return goal; 
	}
	private static double Euclid(int x1, int y1, int x2, int y2) { // find euclid distance
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
		
		
	}

	private static double Manhattan(int x1, int y1, int x2, int y2) { // find manhattan distance
		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
	}
	
	public static Grid getHardestMaze( int dim, double prob, char which) {//uses genetic algorithm model
																		//which: DFS='d'. A star='a'
		
		Grid hardestSoFar = null;
		int hardestVal=0;
		
		final int pop=50;	//population
		final int numMate=2;
		final int numGenerations=500;
		final int mutationFreq=2 ;	//for every 'mutationFreq' generations, mutate a board.
		
		List<Grid> futureGen=new ArrayList<Grid>();
		List<Integer> hardness=new ArrayList<Integer>();
		//for each child, there is a hardness rating.
			
		List<Grid> grids=new ArrayList<Grid>();
		Comparator<Integer> sortHighToLow= (Integer num1,Integer num2)-> num2-num1;
		//ancestor generation:
		while(grids.size()<pop) {
			grid=new Grid(dim,prob);
			if (Astar(false)!=null)	{//if solvable
				grid.clearOccupied();
				grids.add(grid);
				//System.out.println("whic");
			}
		}
		//Breed the grids:
		for(int i=0;i<numGenerations;i++) { System.out.println("generation="+i);
			futureGen.clear();
			hardness.clear();
			 
			if (i%mutationFreq==0) {
				grids.get((int)( Math.random()*grids.size())).mutate(0.5);
			}
			
			while(!grids.isEmpty()) {
				Grid parent1=grids.remove(0);
				Grid parent2=grids.remove(0); 
				int childrenCount=0;
				while(childrenCount<numMate) {//Mate parent pairs with n children each
					grid=parent1.mate(parent2);//
					Coord goal=null;
					 //grid.show();
					if (which=='d') {
						goal=DFS(); 
					}else
						goal=Astar(true);
					grid.clearOccupied(); 
					
					if (goal!=null) {
						futureGen.add(grid);
						 if (which=='d') {
							hardness.add(DFS_fringe_size);  
						}else {
							hardness.add(A_manhattan_numExpansions);  
						}
						childrenCount++;
					 	
					}
				}
			}
			
			
			
			//Now there are pop/2*numMate children.
			 
			//Get threshold hardest value to kill off easy children:
			List<Integer> list=hardness.stream().sorted(sortHighToLow).collect(Collectors.toList());
			int threshold=list.get(pop-1);//System.out.println("thres= "+threshold);
			//Replace with new population:
			grids.clear();
			for(int ind=0;ind<futureGen.size()&&grids.size()<pop;ind++) {
				if (hardness.get(ind)>=threshold) {
					grids.add(futureGen.get(ind));  
				}
			}  
			
			int hardestValue=hardness.stream().sorted(sortHighToLow).findFirst().get();
			if (hardestValue>hardestVal) {
				int hardestIndex=hardness.indexOf(hardestValue);
				hardestSoFar=futureGen.get(hardestIndex); 
				hardestVal=hardestValue;
				System.out.println("Discovered new Hardest="+hardestVal);
			}
			
			
			
		}
		// hardness.stream().sorted(sortHighToLow).forEach((s)->System.out.println(s+" "+hardness.indexOf(s)));
		
		
		return hardestSoFar;//get the hardest maze in the last generation.
	}

	public static void display_result(Coord goal) {
 
		grid.clearOccupied();
		grid.showPath(goal);
		grid.show(); 
		grid.clearOccupied();
	}
	
	public static boolean baseCase_onFire(int dim, double p_maze, double p_burn) {
		Coord goal = null;
		Coord prev=null;
		Coord next=null;
		Coord curr=null;
		while(goal==null) {//finding a solvable map
			grid = new Grid(dim,p_maze);
			goal = Astar(false);
			if(Astar_ULLR(false)==null) {goal=null;continue;}
		}
		grid.p_burn=p_burn;
		//back track to get a list of points of nearest route
		curr=goal;
		while(curr!=null) {
			next=curr.parent;
			curr.parent=prev;
			prev=curr;
			curr=next;
		}
		goal=prev;
		grid.setFire(0, grid.dim-1);//UL set fire
		while(goal!=null) {
			if(grid.isBurnt(goal.x, goal.y)) {return false;}
			goal=goal.parent;
			grid.updateGrid();
		}
		return true;
//		for(Coord c = goal;c!=null;c=c.parent ) {
//			System.out.println("("+c.x+","+c.y+")");
//		}
		
	}
	
	public static void main(String args[]) {
		grid=getHardestMaze( 100, 0.2, 'a');
		grid.clearOccupied();
		grid.show();
		grid.showPath(Astar(true));
		grid.show();
		grid=getHardestMaze( 100, 0.2, 'd');
		grid.clearOccupied();
		grid.show();
		grid.showPath(DFS());
		grid.show();
		
		
		
		
		//System.out.println(get_solvability_distribution(17,100));
		/*
		int[] results=get_solvability_distribution(17,100000);
		for(int a = 0; a<=998;a++) {
			System.out.println((double)(a+1)/1000+","+results[a]);
		}
 
		*/
 
		//grid=new Grid(50,0.1);
		//DFS();grid.show();
		//display_algos(500,0.2); 
		
 
		//display_algos(100,0.2);
//		grid=new Grid(15,0.2);
//		grid.p_burn=0.3;
//		grid.setFire(14, 0);
//		for(int i = 0; i<50;i++) {
//			grid.show();
//			grid.updateGrid();
//		}
		
		//System.out.println(baseCase_onFire(150,0.2,0.2));
		//get_avg_success(150,10000);
	}
	public static void get_avg_success(int dim,int thres) {
		 int[] counter = new int[1000];
		 for(int prob = 0; prob<=998;prob++) {
			 System.out.println(prob);
			 for(int counts=0;counts<=thres;counts++) {
				 if(baseCase_onFire(dim,0.26,(prob+1)/1000)==true) {
					 counter[prob]++;
				 }
			 }
			 
		 }
		 for(int prob=0;prob<=998;prob++) {
			 System.out.println((prob+1)/1000+","+(double) (counter[prob]/thres));
		 } 
	}
	public static int[] get_solvability_distribution(int dim,int threshold_t) {
		//for this function we will test fixed dim, increasing prob by 0.001 from 0.01(0.00 is definite solvable) using A* euclid (fastest algo)
		int[] solved = new int[1000];

		for (int prob=1; prob<=999;prob++ ) {
			for(int trial =0;trial<threshold_t;trial++) {
				grid=new Grid(dim,(double) prob/1000);
				if(Astar(false)!=null) {
					solved[prob-1]++;
				}
			}
		}
		return solved;
	}
	public static int[] get_expected_length_distribution(int dim, int threshold_t) {
		//for this function we will test fixed dim, increasing prob by 0.001 in a given 
		int[] length = new int[2600];
		
		return null;
	}
	public static void display_algos(int dim, double prob) {
/*dim = 8;
		prob = 0.2;
		grid = new Grid(dim, prob); // dim, probability.
		grid.show();

		//DFS();
		//grid.show();
		//grid.clearOccupied();
		BiBFS();
		grid.show();*/
		Coord goal;
		Coord[] goals = new Coord[2];
//		Parameters:
//		dim = 16;
//		prob = 0.2;
		
		grid = new Grid(dim, prob); // dim, probability.
		grid.show();
		
		long startTime_dfs = System.nanoTime();
		goal=DFS();
		long endTime_dfs = System.nanoTime(); 
		System.out.println("Result from DFS:");
		display_result(goal);
		long startTime_BFS= System.nanoTime();
		goal=BFS();
		long endTime_BFS=System.nanoTime(); 
		System.out.println("Result from BFS:"); 
		display_result(goal);
		long startTime_BiBFS=System.nanoTime();
		goals=BiBFS();
		long endTime_BiBFS=System.nanoTime();
		System.out.println("Result from Bidirection BFS:");
		grid.clearOccupied();
		grid.showPath(goals[0]);
		grid.showPath(goals[1]);
		grid.show();
		grid.clearOccupied();
		long startTime_Astar_1=System.nanoTime();
		goal=Astar(true);
		long endTime_Astar_1=System.nanoTime();
		System.out.println("Result from A* with Manhattan distance:");
		display_result(goal);
		long startTime_Astar_2=System.nanoTime();
		goal=Astar(false);
		long endTime_Astar_2=System.nanoTime();
		System.out.println("Result from A* with Euclid distance:");
		display_result(goal);
		System.out.println("Runtime:");
		System.out.println("DFS:"+((endTime_dfs-startTime_dfs)/1000000)+"ms");
		System.out.println("BFS:"+((endTime_BFS-startTime_BFS)/1000000)+"ms");
		System.out.println("BiBFS:"+((endTime_BiBFS-startTime_BiBFS)/1000000)+"ms");
		System.out.println("A*_Manhattan:"+((endTime_Astar_1-startTime_Astar_1)/1000000)+"ms");
		System.out.println("A*_Euclid:"+((endTime_Astar_2-startTime_Astar_2)/1000000)+"ms");
		
	}

}
