package app;
import structures.*;
import java.util.*;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Stack;

public class MazeRunner {
 
	public static Grid grid;
	public static class Coord{	//coordinate object. used in fringe.
		int x;
		int y;
		Coord parent;
		public Coord(int x,int y,Coord parent) {
			this.x=x;
			this.y=y;
			this.parent=parent;
		} 
		public String toString() {
			return "("+x+","+y+")";
		}
	}  
	public static List<Coord> DFS(){
		Stack<Coord> fringe=new Stack<Coord>(); 
		List<Coord> path=new ArrayList<Coord>();
		fringe.push(new Coord(0,0,null) );
		Coord current=null;
		Coord goal=null;
		while(!fringe.isEmpty()){
			 current=fringe.pop();  
			 //Find all neighbors:
			 for(Coord c:grid.getNeighbors(current.x, current.y)) {
					c.parent=current;  
					fringe.push(c);
					grid.occupy(current.x, current.y);
			}
			if (grid.isGoal(current.x, current.y)) {  //save goal coordinate so we can backtrack later
				goal=current;
			}  
		} 
		
		//Retrace steps to show path:
		grid.clearOccupied();
		for(Coord ptr=goal;ptr!=null;ptr=ptr.parent) {
			System.out.println("parent of "+ptr+" is "+ptr.parent);
			grid.occupy(ptr.x, ptr.y); 
		 
		}
		
		
		return path;
		
	}
	public void BFS(){
		Queue<Integer> fringe = new LinkedList<Integer>();
		
	}
	public void Astar() {
		PriorityQueue<Integer> fringe = new PriorityQueue<Integer>();
		
		
	} 
	
	
	public double Euclid(int x1,int y1,int x2,int y2) {	//find euclid distance
		return Math.sqrt( (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}
	public double Manhattan(int x1,int y1,int x2,int y2) {	//find manhattan distance
		return Math.sqrt( Math.abs(x1-x2) + Math.abs(y1-y2));
	}
	
	//MAIN METHOD: 
	public static void main(String args[]) {
		grid=new Grid(10,0.2); //dim, probability.
	 	grid.show();
		 
		DFS();
		grid.show();
		
		
		//here are some data structure examples:
 /*
		Stack<String> stack=new Stack<String>();
		
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
 
*/		
		
	}
	//DFS: Will Prioritize "Down" 
	public static void DFS(Grid maze) {
		//Queue<Integer,Integer> queue = new LinkedList<Integer>();
		
		return;
	}
	

}
