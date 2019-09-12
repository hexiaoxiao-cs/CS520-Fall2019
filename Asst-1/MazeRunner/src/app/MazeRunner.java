package app;
import structures.*;
import java.util.*;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Stack;

public class MazeRunner {
	 
	public static void main(String args[]) {
		Grid grid=new Grid(8,0.3); //dim, probability.
		grid.occupy(0, 1);	//(x,y)
		grid.show();
		 
		
		
		//here are some data structure examples:
		Stack<String> stack=new Stack<String>();
		Queue<Integer> queue = new LinkedList<Integer>();
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		
		
	}
	
	public void DFS(Grid maze) {
		
		return;
		
	}
	

}
