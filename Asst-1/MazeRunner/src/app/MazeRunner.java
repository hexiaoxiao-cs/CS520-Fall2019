package app;

import structures.*;
import java.util.*;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Stack;

public class MazeRunner {

	public static Grid grid;
	public static int dim;
	public static double prob;

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
		public boolean equals(Coord c) {
			return (this.x==c.x)&&(this.y==c.y);
		}
	}

	public static void DFS() {
		Stack<Coord> fringe = new Stack<Coord>();
		fringe.push(new Coord(0, 0, null));
		Coord current = null;
		Coord goal = null;
		while (!fringe.isEmpty()) {
			current = fringe.pop();
			// Find all neighbors:
			for (Coord c : grid.getNeighbors(current.x, current.y)) {
				c.parent = current;
				fringe.push(c);
				grid.occupy(current.x, current.y);
			}
			if (grid.isGoal(current.x, current.y)) { // save goal coordinate so we can backtrack later
				goal = current;
			}
		}
		// Retrace steps to show path:
		grid.clearOccupied();
		grid.showPath(goal);
	}

	public static void BFS() {
		Queue<Coord> fringe = new LinkedList<Coord>();
		fringe.add(new Coord(0, 0, null));
		Coord current = null;
		Coord goal = null;
		while (!fringe.isEmpty()) {
			current = fringe.remove();
			// Find all neighbors:
			for (Coord c : grid.getNeighbors(current.x, current.y)) {
				c.parent = current;
				fringe.add(c);
				grid.occupy(current.x, current.y);
			}
			if (grid.isGoal(current.x, current.y)) { // save goal coordinate so we can backtrack later
				goal = current;
			}
		}
		// Retrace steps to show path:
		grid.clearOccupied();
		grid.showPath(goal); 
	}
	//Astar:
	//Arguments: 1. which weighting? 0 for euclid, 1 for Manhattan

	public static void BiBFS() {
		Queue<Coord> fringe1 = new LinkedList<Coord>();
		Queue<Coord> fringe2 = new LinkedList<Coord>();
		fringe1.add(new Coord(0, 0, null));
		fringe2.add(new Coord(dim-1, dim-1, null));
		Coord current1 = null;
		Coord current2 = null;
		Coord overlap=null;
		//left side bfs='8'. right side bfs='9'
		
		while (!fringe1.isEmpty()&&!fringe2.isEmpty() ) {
			current1 = fringe1.remove();
			current2 = fringe2.remove();
			List<Coord> neighbors=grid.getNeighbors(current1.x, current1.y);
			neighbors.removeIf((Coord coord)-> grid.getNumAt(coord.x, coord.y)==8);
				// Find all neighbors for both bfs sides:
			
			
			grid.show();
			
			
			System.out.println("neighbors of "+current1);
				for (Coord cc : neighbors) {
					
					System.out.println(cc);
				}
				
				
				
			for (Coord c : neighbors) {
				
				c.parent = current1;
				fringe1.add(c);
				
				if (grid.getNumAt(c.x, c.y)==9) {System.out.println(c);grid.show();
					overlap=c; break; 
				}
				grid.arr[current1.x][current1.y]=8;	
				
				
			}
			neighbors=grid.getNeighbors(current2.x, current2.y);
			neighbors.removeIf((Coord coord)-> grid.getNumAt(coord.x, coord.y)==9);
			
			
			System.out.println("neighbors of "+current2);
			for (Coord cc : neighbors) {
				
				System.out.println(cc);
			}
			
			
			
			for (Coord c : neighbors) {
				
				c.parent = current2;
				fringe2.add(c);
				
				if (grid.getNumAt(c.x, c.y)==8) {System.out.println(c);grid.show();
					overlap=c; break;
				}
				grid.arr[current2.x][current2.y]=9;	
			}
			//check if neig
			
		}
		//clear grid to show
		for(int i=0;i<dim;i++) {
			for(int j=0;j<dim;j++) {
				if(grid.arr[i][j]==8||grid.arr[i][j]==9)
					grid.arr[i][j]=Grid.FreeNum;
			}
		} 
		grid.arr[0][0]=Grid.StartNum;
		grid.arr[dim-1][dim-1]=Grid.EndNum;
		System.out.println("overlap="+overlap);
		grid.showPath(overlap); 
		
		
		
		
		
	}
	//Astar:
	//Arguments: 1. which weighting? false for euclid, true for Manhattan
	public static void Astar(boolean isManhattan) {
		PriorityQueue<Coord> open_set = new PriorityQueue<Coord>();
		open_set.add(new Coord(0,0,null));
		Coord current = null;
		Coord goal = null;
		double curr_weight;
		double curr_weight_to_start;
		Coord[][] closed_set=new Coord[grid.dim][grid.dim];
		while(!open_set.isEmpty()) {
			current = open_set.poll();//removed from open
			//System.out.println("Picked"+current.x+","+current.y+"point with priority"+current.weight);
			closed_set[current.x][current.y]=current;//put current to the closed set
			if(grid.isGoal(current.x, current.y)) {
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
		grid.showPath(goal);
		return;
	}

	private static double Euclid(int x1, int y1, int x2, int y2) { // find euclid distance
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	private static double Manhattan(int x1, int y1, int x2, int y2) { // find manhattan distance
		return Math.sqrt(Math.abs(x1 - x2) + Math.abs(y1 - y2));
	}

	// MAIN METHOD:
	public static void main(String args[]) {
		dim = 8;
		prob = 0.2;
		grid = new Grid(dim, prob); // dim, probability.
		//grid.show();

		//DFS();
		//grid.show();
		//grid.clearOccupied();
		//BFS();
		//grid.show();
		Astar(true);
		grid.show();
		grid.clearOccupied();
		Astar(false);
		grid.show();
		grid.clearOccupied();

	}

}
