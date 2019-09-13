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

	public static class Coord { // coordinate object. used in fringe.
		public int x;
		public int y;
		public Coord parent;
		public int f;	//estimated s->n->g. useful for priority queue for A*

		public Coord(int x, int y, Coord parent) {
			this.x = x;
			this.y = y;
			this.parent = parent;
		}

		public String toString() {
			return "(" + x + "," + y + ")";
		}
		public boolean equals(Object o) {
			if (!(o instanceof Coord)) { 
	            return false; 
	        } 
			Coord c = (Coord) o; 
			return this.x==c.x&&this.y==c.y;
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

	public static void BiBFS() {
		Queue<Coord> fringe1 = new LinkedList<Coord>();
		Queue<Coord> fringe2 = new LinkedList<Coord>();
		Queue<Coord> fringePtr = null;
		fringe1.add(new Coord(0, 0, null));
		fringe2.add(new Coord(dim-1, dim-1, null));
		
		Coord current1 = null;
		Coord current2 = null;
		Coord overlap=null;
		Coord overlap2=null;
		//left side bfs='8'. right side bfs='9'
		
		while (!fringe1.isEmpty()&&!fringe2.isEmpty()&&overlap==null ) {
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
					overlap=c; 
					fringePtr=fringe2;  
					while(!fringePtr.isEmpty()&&overlap2==null) {
						Coord c2=fringePtr.remove(); System.out.println("remove "+c2);
						if (c2.equals(overlap))
							overlap2=c2;
					}
					
					
					
					
					
					
					break; 
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
					overlap=c;
					fringePtr=fringe1;
					break;
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
		
		/*while(!fringePtr.isEmpty()&&overlap2==null) {
			Coord c=fringePtr.remove(); System.out.println("remove "+c);
			if (c.equals(overlap))
				overlap2=c;
		}*/
		System.out.println("overlap="+overlap+"=overlap2="+overlap2);
		
		 
		grid.clearOccupied(); 
		grid.showPath(overlap); 
		grid.showPath(overlap2); 
		
		
		
		
		
	}
	
	
	public void Astar() {
		PriorityQueue<Coord> fringe = new PriorityQueue<Coord>( new Comparator<Coord>() {
			@Override
			public int compare(Coord o1, Coord o2) {
				return o1.f-o2.f;
			}
	    }); //^^ note that fringe will be sorted by Coordinate f values if you assign f-values to coord.
		
		
		
		
		
		

	}

	private double Euclid(int x1, int y1, int x2, int y2) { // find euclid distance
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	private double Manhattan(int x1, int y1, int x2, int y2) { // find manhattan distance
		return Math.sqrt(Math.abs(x1 - x2) + Math.abs(y1 - y2));
	}

	// MAIN METHOD:
	public static void main(String args[]) {
		dim = 5;
		prob = 0.1;
		grid = new Grid(dim, prob); // dim, probability.
		grid.show();

		//DFS();
		//grid.show();
		//grid.clearOccupied();
		BiBFS();
		grid.show();

	}

}
