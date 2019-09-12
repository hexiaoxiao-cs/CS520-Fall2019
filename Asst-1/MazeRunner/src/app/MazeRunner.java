package app;
import structures.*;
import java.util.*;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Stack;

public class MazeRunner {
	static Grid grid;
	public static void main(String args[]) {
		grid=new Grid(8,0.3); //dim, probability.
		grid.occupy(0, 1);	//(x,y)
		grid.show();
		 
		
		
		//here are some data structure examples:
		Stack<String> stack=new Stack<String>();
		
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		
		
	}
	//DFS: Will Prioritize "Down" 
	public static void DFS(Grid maze) {
		//Queue<Integer,Integer> queue = new LinkedList<Integer>();
		
		return;
	}
	

}
