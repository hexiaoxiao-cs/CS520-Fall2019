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

		public Coord(int x, int y, Coord parent) {
			this.x = x;
			this.y = y;
			this.parent = parent;
		}

		public String toString() {
			return "(" + x + "," + y + ")";
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

	public void Astar() {
		PriorityQueue<Integer> fringe = new PriorityQueue<Integer>();

	}

	private double Euclid(int x1, int y1, int x2, int y2) { // find euclid distance
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	private double Manhattan(int x1, int y1, int x2, int y2) { // find manhattan distance
		return Math.sqrt(Math.abs(x1 - x2) + Math.abs(y1 - y2));
	}

	// MAIN METHOD:
	public static void main(String args[]) {
		dim = 10;
		prob = 0.2;
		grid = new Grid(dim, prob); // dim, probability.
		grid.show();

		DFS();
		grid.show();
		grid.clearOccupied();
		BFS();
		grid.show();

	}

}
