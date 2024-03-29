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

public class MazeRunner{

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
		//Author: Sarah
		DFS_fringe_size=0;
		Stack<Coord> fringe = new Stack<Coord>();
		fringe.push(new Coord(0, 0, null));
		Coord current = null;
		Coord goal = null;
		
		 
		
		while (!fringe.isEmpty()) {
			current = fringe.pop();
			// Find all neighbors:
			
			if (grid.isGoal(current.x, current.y)) { // save goal coordinate so we can backtrack later
				goal = current;
				break;
			}
			grid.occupy(current.x, current.y);
			for (Coord c : grid.getNeighbors(current.x, current.y)) {
				//System.out.println(current.x+","+current.y);
				c.parent = current;
				//if(!fringe.contains(c)) {
					fringe.push(c); 
					//grid.arr[c.x][c.y]=7;
					grid.occupy(c.x, c.y);
				//}

				
			}//System.out.println(fringe.size());

			
			if (DFS_fringe_size<fringe.size())	//keep track of how large the fringe gets.
				DFS_fringe_size=fringe.size();
			
		}
		// Retrace steps to show path:
		//System.out.println(DFS_fringe_size);
		return goal;
	}

	public static Coord DFS_new() {
		//Author: Xiaoxiao He
		DFS_fringe_size=0;
		Stack<Coord> fringe = new Stack<Coord>();
		fringe.push(new Coord(0, 0, null));
		Coord current = null;
		Coord goal = null;
		while (!fringe.isEmpty()) {
			current = fringe.pop();
			// Find all neighbors:
			if (grid.isGoal(current.x, current.y)) { // save goal coordinate so we can backtrack later
				goal = current;
				break;
			}
			grid.occupy(current.x, current.y);
			for (Coord c : grid.getNeighbors_optimized(current.x, current.y)) {
				
				c.parent = current;
				//if(!fringe.contains(c))
					fringe.push(c); 
					grid.occupy(c.x, c.y);
			
				
			}//System.out.println(fringe.size());
			

			
			if (DFS_fringe_size<fringe.size())	//keep track of how large the fringe gets.
				DFS_fringe_size=fringe.size();
			
		}
		// Retrace steps to show path:
		//System.out.println(DFS_fringe_size);
		return goal;
	}
	
	public static Coord BFS() {
		//Author: Sarah Liang
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
				break;
			} 
		}
		
		//grid.clearOccupied();
		//grid.showPath(goal);
		// Retrace steps to show path:
		return goal;
	}
	public static Coord BFS_best_route(double[][] prob, int x, int y) {
		//Author: Xiaoxiao he
		Queue<Coord> fringe = new LinkedList<Coord>();
		fringe.add(new Coord(x, y, null));
		Coord current = null;
		Coord goal = null;
		//int counter=0;
		while (!fringe.isEmpty()) {
			current = fringe.remove();
			// Find all neighbors:
			if (grid.isGoal(current.x, current.y)) { // save goal coordinate so we can backtrack later
				goal = current;
				return goal;
			} 
			for (Coord c : grid.getNeighbors(current.x, current.y)) {
				c.parent = current;
				c.weight=current.weight+prob[c.x][c.y]+1;
				if(!fringe.contains(c)) {fringe.add(c);}
				grid.occupy(current.x, current.y);
			}
			//grid.occupy(current.x, current.y);

		}
		
		//grid.clearOccupied();
		//grid.showPath(goal);
		// Retrace steps to show path:
		return null;
	}
	public static Coord BFS_ULLR() {
		//Author Xiaoxiao He
		Queue<Coord> fringe = new LinkedList<Coord>();
		fringe.add(new Coord(0, grid.dim-1, null));
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
			if (current.x==grid.dim-1&&current.y==0) { // save goal coordinate so we can backtrack later
				goal = current;
				break;
			} 
		}
		
		//grid.clearOccupied();
		//grid.showPath(goal);
		// Retrace steps to show path:
		return goal;
	}
	public static double[][] BFS_findProb() {
		//Author Xiaoxiao he
		Queue<Coord> fringe = new LinkedList<Coord>();
		fringe.add(new Coord(0, grid.dim-1, null));
		Coord current = null;
		Coord goal = null;
		//int counter=0;
		//int[][] visited=new int [grid.dim-1][grid.dim-1];
		double[][] probability_map = new double[grid.dim][grid.dim];
		double curr_prob=0;
		int counter = 0;
		while (!fringe.isEmpty()) {
			
			current = fringe.remove();
			// Find all neighbors:
			//if(Manhattan(current.x,current.y,grid.dim-1,0)==depth) {return probability_map;}
			if (current.x==grid.dim-1&&current.y==0) { // save goal coordinate so we can backtrack later
				goal = current;
				break;
			} 
			if(grid.arr[current.x][current.y]==grid.BurntNum) {
				for (Coord c : grid.getNeighbors_nocheck(current.x, current.y)) {
				//c.parent = current;
				if(!fringe.contains(c)) {fringe.add(c);}
				
				//grid.occupy(current.x, current.y);
			}
				continue;
			}
			if(probability_map[current.x][current.y]!=0.0||!grid.isFree(current.x, current.y)) {
				continue;}
			if(grid.isBurnt((current.x-1),current.y)) {counter++;}
			if(grid.isBurnt(current.x+1,current.y)) {counter++;}
			if(grid.isBurnt(current.x,current.y-1)) {counter++;}
			if(grid.isBurnt(current.x,current.y+1)) {counter++;}
			curr_prob=(double) counter;
			if(current.x-1>0) {curr_prob+=probability_map[current.x-1][current.y];}
			if(current.y-1>0) {curr_prob+=probability_map[current.x][current.y-1];}
			if(current.x+1<grid.dim) {curr_prob+=probability_map[current.x+1][current.y];}
			if(current.y+1<grid.dim) {curr_prob+=probability_map[current.x][current.y+1];}
			probability_map[current.x][current.y]=1.0-Math.pow(1-grid.p_burn, curr_prob);
			counter=0;
			curr_prob=0;
			for (Coord c : grid.getNeighbors_nocheck(current.x, current.y)) {
				//c.parent = current;
				if(!fringe.contains(c)) {fringe.add(c);}
				
				//grid.occupy(current.x, current.y);
			}
			//grid.occupy(current.x, current.y);

		}
		
		//grid.clearOccupied();
		//grid.showPath(goal);
		// Retrace steps to show path:
		return probability_map;
	}
	//Astar:
	//Arguments: 1. which weighting? 0 for euclid, 1 for Manhattan

	public static Coord[] BiBFS() {
		//Author Sarah
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
	
	public static Coord[] BiBFS_showvisited() {
		//Author Xiaoxiao He
		Queue<Coord> fringeL = new LinkedList<Coord>();
		Queue<Coord> fringeR = new LinkedList<Coord>();
		Queue<Coord> fringePtr = null;
		Coord start=new Coord(0,0,null);
		int [][] visited = new int[grid.dim][grid.dim];
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
		visited[0][0]=1;
		currentRm[grid.dim-1][grid.dim-1]=end;
		//left side bfs='8'. right side bfs='9'
		while (!fringeL.isEmpty()&&!fringeR.isEmpty()&&overlap==null) { 
			currentL = fringeL.remove();
			currentR = fringeR.remove();
			//COLLECT NEIGHBORS FOR LEFT SIDE:
			List<Coord> neighbors=grid.getNeighbors(currentL.x, currentL.y);
			visited[currentL.x][currentL.y]=1;
			visited[currentR.x][currentR.y]=1;
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
		for(int x=0;x<grid.dim;x++) {
			for (int y=0;y<grid.dim;y++) {
				System.out.print(visited[x][y]+" ");
			}
			System.out.println();
		}
		
		toReturn[0]=overlap;
		toReturn[1]=overlap2;
		return toReturn;
	}
	//Astar:
	//Arguments: 1. which weighting? false for euclid, true for Manhattan
	public static Coord Astar(boolean isManhattan) {
		//Author Xiaoxiao He
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
					curr_weight = curr_weight_to_start+Manhattan(c.x,c.y,(grid.dim-1),(grid.dim-1));//g(x)+h(x)
				}
				else {
					curr_weight_to_start=current.weight_to_start+Euclid(current.x,current.y,c.x,c.y);//g(x) Actual distance between new point to the old point
					curr_weight = curr_weight_to_start+Euclid(c.x,c.y,(grid.dim-1),(grid.dim-1));
				}
				//System.out.println(curr_weight_to_start);
				//System.out.println(curr_weight+"\n");
				c.weight=curr_weight;
				c.weight_to_start=curr_weight_to_start;
				if(open_set.contains(c)) {
					if(open_set.removeIf((t)->(t.weight>c.weight)&&t.x==c.x&&t.y==c.y)==true) {//if in the open set it has a worse node than the one being inserted
						//sth being deleted
						open_set.add(c);
						A_manhattan_numExpansions++;
					}
					//else not doing anything, which is keep the node in the open_set since it is currently with a better route
				}
				else {
					//does not have c in open set
					open_set.add(c);
					A_manhattan_numExpansions++;
				}

				//fringe.add(c);
			}
		}
		//grid.showPath(goal);
		return goal; 
	}
	public static Coord Astar_showvisited(boolean isManhattan) {
		//Author Xiaoxiao He
		A_manhattan_numExpansions=0;
		
		PriorityQueue<Coord> open_set = new PriorityQueue<Coord>();
		open_set.add(new Coord(0,0,null));
		Coord current = null;
		Coord goal = null;
		double curr_weight;//current weight(for priority)
		double curr_weight_to_start;//current weight from the starting point
		Coord[][] closed_set=new Coord[grid.dim][grid.dim]; //a better way to store the closed set such that O(1) access with give x,y coordinate
		int [][] isvisited= new int [grid.dim][grid.dim];
		while(!open_set.isEmpty()) {
			current = open_set.poll();//removed from open
			isvisited[current.x][current.y]=1;
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
					curr_weight = curr_weight_to_start+Manhattan(c.x,c.y,(grid.dim-1),(grid.dim-1));//g(x)+h(x)
				}
				else {
					curr_weight_to_start=current.weight_to_start+Euclid(current.x,current.y,c.x,c.y);//g(x) Actual distance between new point to the old point
					curr_weight = curr_weight_to_start+Euclid(c.x,c.y,(grid.dim-1),(grid.dim-1));
				}
				//System.out.println(curr_weight_to_start);
				//System.out.println(curr_weight+"\n");
				c.weight=curr_weight;
				c.weight_to_start=curr_weight_to_start;
				if(open_set.contains(c)) {
					if(open_set.removeIf((t)->(t.weight>c.weight)&&t.x==c.x&&t.y==c.y)==true) {//if in the open set it has a worse node than the one being inserted
						//sth being deleted
						open_set.add(c);
						A_manhattan_numExpansions++;
					}
					//else not doing anything, which is keep the node in the open_set since it is currently with a better route
				}
				else {
					//does not have c in open set
					open_set.add(c);
					A_manhattan_numExpansions++;
				}

				//fringe.add(c);
			}
		}
		for(int x=0;x<grid.dim;x++) {
			for (int y=0;y<grid.dim;y++) {
				System.out.print(isvisited[x][y]+" ");
			}
			System.out.println();
		}
		//grid.showPath(goal);
		return goal; 
	}


	public static Coord Astar_ULLR(boolean isManhattan) {
		//Author Xiaoxiao He
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
					curr_weight = curr_weight_to_start+Manhattan(c.x,c.y,(grid.dim-1),(grid.dim-1));//g(x)+h(x)
				}
				else {
					curr_weight_to_start=current.weight_to_start+Euclid(current.x,current.y,c.x,c.y);//g(x) Actual distance between new point to the old point
					curr_weight = curr_weight_to_start+Euclid(c.x,c.y,(grid.dim-1),(grid.dim-1));
				}
				//System.out.println(curr_weight_to_start);
				//System.out.println(curr_weight+"\n");
				c.weight=curr_weight;
				c.weight_to_start=curr_weight_to_start;
				if(open_set.contains(c)) {
					if(open_set.removeIf((t)->(t.weight>c.weight)&&t.x==c.x&&t.y==c.y)==true) {//if in the open set it has a worse node than the one being inserted
						//sth being deleted
						open_set.add(c);
						A_manhattan_numExpansions++;
					}
					//else not doing anything, which is keep the node in the open_set since it is currently with a better route
				}
				else {
					//does not have c in open set
					open_set.add(c);
					A_manhattan_numExpansions++;
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
																		//which: DFS='d'. A star='a'. Both='b'
		//Author Sarah Liang
		Grid hardestSoFar = null;
		int hardestVal=0;
		
		final int pop=100;	//population
		final int numMate=4;	//number of children each parent pair produces.
		final int numGenerations=100;
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
					
					if (which=='b') {
						goal=DFS(); 
						grid.clearOccupied();
						goal=Astar(true);
						grid.clearOccupied(); 
						if (goal!=null) {
							futureGen.add(grid);
							hardness.add(A_manhattan_numExpansions+DFS_fringe_size);  
							//System.out.println(A_manhattan_numExpansions+" "+DFS_fringe_size);
							childrenCount++;
						}
						
					}else {
						if (which=='d') {
							goal=DFS(); 
						}else if (which=='a')
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
	
	public static void compare_btw_astars(int dim, int threshold_t) {
		//Author Xiaoxiao He
		double [] runtime_1 = new double[301];
		int [] expansion_1 = new int[301];
		double [] runtime_2 = new double[301];
		int [] expansion_2 = new int[301];
		
		int [] many = new int[301];
		for(int p_maze=0;p_maze<=300;p_maze++) {
			for (int t = 0; t<threshold_t;t++) {
				grid=new Grid(dim,(double)p_maze/1000.0);
				double timer1=System.nanoTime();
				if(Astar(false)==null) {continue;}
				double timer2=System.nanoTime();
				grid.clearOccupied();
				expansion_1[p_maze]+=A_manhattan_numExpansions;
				A_manhattan_numExpansions=0;
				many[p_maze]++;
				runtime_1[p_maze]+=(timer2-timer1);
				timer1=System.nanoTime();
				if(Astar(true)==null) {continue;}
				timer2=System.nanoTime();
				expansion_2[p_maze]+=A_manhattan_numExpansions;
				A_manhattan_numExpansions=0;
				//many[p_maze]++;
				runtime_2[p_maze]+=(timer2-timer1);
				
			}
		}
		//Printing Runtime for Euclid
		System.out.println("Euclid runtime");
		for(int p_maze=0;p_maze<=300;p_maze++) {
			System.out.println((double)p_maze/1000+","+runtime_1[p_maze]/((double) many[p_maze]*1000000.0));
		}
		System.out.println("Euclid expansion times");
		for(int p_maze=0;p_maze<=300;p_maze++) {
			System.out.println((double)p_maze/1000+","+(double)expansion_1[p_maze]/(double) many[p_maze]);
		}
		System.out.println("Manhattan runtime");
		for(int p_maze=0;p_maze<=300;p_maze++) {
			System.out.println((double)p_maze/1000+","+runtime_2[p_maze]/((double) many[p_maze]*1000000.0));
		}
		System.out.println("Manhattan expansion times");
		for(int p_maze=0;p_maze<=300;p_maze++) {
			System.out.println((double)p_maze/1000+","+(double)expansion_2[p_maze]/(double) many[p_maze]);
		}
	}

	public static void display_result(Coord goal) {
//		if(goal==null) {System.out.println("No Answer");}
		
		grid.clearOccupied();
		grid.showPath(goal);
		grid.show(); 
		grid.clearOccupied();
	}
	
	public static boolean baseCase_onFire(int dim, double p_maze, double p_burn) {
		//Author Xiaoxiao He
		Coord goal = null;
		Coord prev=null;
		Coord next=null;
		Coord curr=null;
		while(goal==null) {//finding a solvable map
			grid = new Grid(dim,p_maze);
			goal = BFS();
			if(BFS_ULLR()==null) {goal=null;continue;}
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

	public static int[] double_onFire(int dim, double p_maze, double p_burn) {
		//Author Xiaoxiao He
		Coord goal1 = null;
		Coord goal2 = new Coord(0,0,null);
		Coord prev=null;
		Coord next=null;
		Coord curr=null;
		double[][] prob;
		int counts[]=new int [2];
		boolean first_complete=false, second_complete=false;
		while(goal1==null) {//finding a solvable map
			grid = new Grid(dim,p_maze);
			goal1 = BFS();
			grid.clearOccupied();
			if(BFS_ULLR()==null) {goal1=null;continue;}
			grid.clearOccupied();
		}
		grid.p_burn=p_burn;
		//back track to get a list of points of nearest route
		curr=goal1;
		while(curr!=null) {
			next=curr.parent;
			curr.parent=prev;
			prev=curr;
			curr=next;
		}
		goal1=prev;
		grid.setFire(0, grid.dim-1);//UL set fire
		while(goal1!=null||goal2!=null) {
			if(goal1!=null&&grid.isBurnt(goal1.x, goal1.y)) {goal1=null;}
			if(goal2!=null&&grid.isBurnt(goal2.x, goal2.y)) {goal2=null;}
			if(goal1!=null) {//regular algo not die
				if(goal1.x==grid.dim-1&&goal1.y==grid.dim-1) {first_complete=true; goal1=null;}
				else {
					goal1=goal1.parent;
					}
				}
			if(goal2!=null) {
				if(goal2.x==grid.dim-1&&goal2.y==grid.dim-1) {second_complete=true;goal2=null;}
					else {
					prob=BFS_findProb();// get current prob matrix
					//List<Coord> sons=new ArrayList<Coord>();
					Coord best_son=null;
					for(Coord c: grid.getNeighbors(goal2.x, goal2.y)) {
						
						Coord son = BFS_best_route(prob,c.x,c.y);

						grid.clearOccupied();
						if(son==null) {continue;}
						//son.weight+=Math.random()/100.0;
						//System.out.println(son.weight);
						//sons.add(son);
						//need reverse back to get start point
						if(best_son==null) {best_son=c;
						best_son.weight=son.weight;}
						else {
							if(best_son.weight>son.weight) {best_son=c;
							best_son.weight=son.weight;}
						}
					}
					
					goal2=best_son;//choose the point and go to next time line
//					if(goal2!=null) {
//						grid.showPath(goal2);
//						grid.show();
//						grid.clearOccupied();
//					}
//					else {
//						grid.show();
//					}
				}
			}
			//grid.show();
			grid.updateGrid();
		}
		if(first_complete==true) {counts[0]++;}
		if(second_complete==true) {counts[1]++;}
//		for(Coord c = goal;c!=null;c=c.parent ) {
//			System.out.println("("+c.x+","+c.y+")");
//		}
		return counts;
	}
	public static void useGetHardest() {
		 
		grid=getHardestMaze( 100, 0.2, 'd');
		display_result(DFS());
		///grid.clearSpecificNum(7); 
	 
	}
	public static void get_Onfire_dist(int dim, double p_maze,int thres) {
		//Author Xiaoxiao He
		int [][] dist=new int[2][1000];
		for(int p_fire=0;p_fire<999;p_fire++) {
			for(int t=0;t<thres;t++) {
				double time1=System.nanoTime();
				int[] temp=double_onFire(dim,p_maze,(double)(p_fire+1)/1000.0);
				double time2=System.nanoTime();
				//System.out.println((time2-time1)/1000000.0);
				dist[0][p_fire]+=temp[0];
				dist[1][p_fire]+=temp[1];
			}
			System.out.println(p_fire);
			System.out.println((double)(p_fire+1.0)/1000.0+","+dist[0][p_fire]);
			System.out.println((double)(p_fire+1.0)/1000.0+","+dist[1][p_fire]);
		}
		System.out.println("OnlyBFS");
		for(int p_fire=0;p_fire<999;p_fire++) {
			System.out.println((double)(p_fire+1.0)/1000.0+","+dist[0][p_fire]);
		}
		System.out.println("HillClimbing");
		for(int p_fire=0;p_fire<999;p_fire++) {
			System.out.println((double)(p_fire+1.0)/1000.0+","+dist[1][p_fire]);
		}
	}
	public static void main(String args[]) throws IOException {
 
		
		//useGetHardest();
		//grid=new Grid(100, 0.2);
		//display_result(DFS());
		//grid.show();
 
		//grid=new Grid(150,0.21);
		//DFS();
		//grid=new Grid(15,0.0);
//
//		grid.p_burn=0.5;
//		grid.setFire(0, grid.dim-1);
		//int[] results=double_onFire(15, 0.3,0.5);
		//grid.show();
		///System.out.println(results[0]);
		//System.out.println(results[1]);
		//get_Onfire_dist(15,0.3,100);
//		grid.show();
//		double[][] prob=BFS_findProb();
//		for(int i=0;i<grid.dim;i++) {
//			for(int j = 0; j<grid.dim;j++) {
//				System.out.print(prob[i][j]+" ");
//			}
//			System.out.println();
//		}
//		grid=getHardestMaze( 100, 0.2, 'd');
//		System.out.println("done.");
//		grid.clearOccupied();
//		grid.show();
//		grid.showPath(DFS());
//		grid.show();
		
//		long startTime_dfs = System.nanoTime();
//		BFS();
//		long endTime_dfs = System.nanoTime();
//		System.out.println("BASECASE:"+((endTime_dfs-startTime_dfs)/1000000)+"ms");
		//grid=getHardestMaze( 10, 0.2, 'd');
		//grid=new Grid(20,0.05);
		//grid.showPath(Astar(true));
		//grid.show();
//		grid=getHardestMaze( 100, 0.2, 'a');
//		grid.clearOccupied();
//		grid.show();
//		grid.showPath(Astar(true));
//		grid.show();
//		grid=getHardestMaze( 100, 0.2, 'd');
//		grid.clearOccupied();
//		grid.show();
//		grid.showPath(DFS());
//		grid.show();
//	 
		
		
		//System.out.println(get_solvability_distribution(17,100));
//		get_solvability_distribution(15,1000);
//		int thres=100000;
//		int [][]results_dim_prob=new int[200][thres];
//		for (int dim=0; dim<200;dim++) {
//			results_dim_prob[dim]=get_solvability_distribution_no_printout(dim+1,thres);
//			for(int t=0;t<thres;t++) {
//				System.out.println((dim+1)+","+t+","+results_dim_prob[dim][t]);
//			}
//		}
//		
// 	
//		get_solvability_distribution_dim_p(100);
		//grid=new Grid(15,0.256);
//		BiBFS_showvisited();
		//DFS();
//		System.out.println();
//		Astar_showvisited(false);
//		grid.clearOccupied();
//		System.out.println();
//		Astar_showvisited(true);
		//grid=new Grid(50,0.1);
		//DFS();grid.show();
		//display_algos(500,0.2); 
//		display_algos(150,0.3);
		//display_algos(150,0.0);
//		grid.setFire(grid.dim-1, 0);
//		long startTime_dfs = System.nanoTime();
//		grid.updateGrid();
//		long endTime_dfs = System.nanoTime();
//		System.out.println((endTime_dfs-startTime_dfs)/10000);
//		grid=new Grid(15,0.2);
//		grid.p_burn=0.3;
//		grid.setFire(14, 0);
//		for(int i = 0; i<50;i++) {
//			grid.show();
//			grid.updateGrid();
//		}
		
		//System.out.println(baseCase_onFire(150,0.2,0.2));
//		get_avg_success(150,1000,100);
//		get_expected_length_distribution(150,1000);
//		System.out.println("Compare_Btw_Astar");
		//compare_btw_astars(150,1000);
//		System.in.read();
		//compare_DFS(2000);
		
	}
	public static void get_avg_success(int dim,int thres,int seg) {
		//Author Xiaoxiao he
		 int[] counter = new int[1000];
		 for(int prob = seg*166; prob<(seg+1)*166;prob++) {
			 //System.out.println(prob);
			 for(int counts=0;counts<thres;counts++) {
				 
				 if(baseCase_onFire(dim,0.3,(double)(prob+1)/1000)==true) {
					 counter[prob]++;
				 }
			 }
			 System.out.println((double)(prob)/1000+","+counter[prob]);
		 }

	}
	
	
	public static int[] get_solvability_distribution(int dim,int threshold_t) {
		//Author Xiaoxiao He
		//for this function we will test fixed dim, increasing prob by 0.001 from 0.01(0.00 is definite solvable) using A* euclid (fastest algo)
		int[] solved = new int[1000];
		for (int prob=1; prob<=999;prob++ ) {
			for(int trial =0;trial<threshold_t;trial++) {
				grid=new Grid(dim,(double) prob/1000);
				if(DFS()!=null) {
					solved[prob]++;
				}
			}
			System.out.println((double)prob/1000+","+(double)solved[prob]/(double)threshold_t);
		}
		return solved;
	}
	public static void get_solvability_distribution_dim_p(int threshold_t) throws FileNotFoundException  {
		//Author Xiaoxiao He
		
		//for this function we will test relationship between dim and prob, increasing prob by 0.001 from 0.01(0.00 is definite solvable) using A* euclid (fastest algo)
		//PrintStream o = new PrintStream(new File("C:\\Users\\Xiaoxiao He\\Desktop\\dim_p.csv")); 
		//System.setOut(o); 
		
		for(int dim=150;dim<=15000;dim*=2) {
			int[] count= new int [200];
			double prev;
			
			for (int prob=250; prob<350;prob++ ) {
				for(int trial =0;trial<threshold_t;trial++) {
					grid=new Grid(dim,(double) prob/1000);
					if(DFS()!=null) {
						count[prob-250]++;
					}
					
				}
				if(count[prob-250]>(threshold_t/2)) {}
				else {
					System.out.println(dim+","+(double)(prob-1.0)/1000+","+(double)count[prob-250-1]/(double)threshold_t);
					break;
				}

			}
			
		}
		return;
	}
	public static void get_expected_length_distribution(int dim, int threshold_t) {
		//Author Xiaoxiao He
		
		//for this function we will test fixed dim, increasing prob by 0.001 in a given 
		double[] expected_length = new double[2600];
		//int counter_sucess=0,counter_length=0;
		Coord goal;
		for(int prob=0;prob<=256;prob++) {
			int counter_sucess=0,counter_length=0;
			for(int trial=0;trial<threshold_t;trial++) {
				grid=new Grid(dim,(double)prob/1000);
				goal=BFS();
				if(goal==null) {continue;}
				//counting length
				while(goal!=null) {
					goal=goal.parent;
					counter_length++;
				}
				counter_sucess++;
			}
			if(counter_sucess!=0) {
				expected_length[prob]=(double)counter_length / (double) counter_sucess;
			}
			System.out.println((double)prob/1000+","+expected_length[prob]);
		}
		return;
	}
	public static void compare_DFS(int dim) {
		//Author Xiaoxiao He
		Coord goal;
		int counter_length=0;
		grid=new Grid(dim,0.3);
		double timer1= System.nanoTime();
		goal=DFS();
		double timer2= System.nanoTime();
		System.out.println("Old DFS:"+(timer2-timer1)/1000000.0);
		//grid.showPath(goal);
		while(goal!=null) {
			goal=goal.parent;
			counter_length++;
		}
		System.out.println(counter_length);
		grid.clearOccupied();
		counter_length=0;
		timer1= System.nanoTime();
		goal=DFS_new();
		timer2= System.nanoTime();
		System.out.println("New DFS:"+(timer2-timer1)/1000000.0);
		while(goal!=null) {
			goal=goal.parent;
			counter_length++;
		}
		System.out.println(counter_length);
	}
	public static void display_algos(int dim, double prob) {
		//Author Sarah Xiaoxiao
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
		//grid.show();
		
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
		if(goals[0]==null) {System.out.println("No Answer");}
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
		//if(goal==null) {System.out.println("Error");return;}
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
