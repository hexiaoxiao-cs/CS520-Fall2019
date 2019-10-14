package structures;

import java.util.*;

public class Agent {
	public Grid board;
	public ArrayList<Eqn> equations = new ArrayList<Eqn>(); // Our KB
	public Queue<Integer[]> safe_nodes = new LinkedList<Integer[]>(); // Our Processing Queue
	// public int[][] = new
	// KB contains equations
	// Equations means every variable (Coordinate) is either 1 or 0 indicating mine
	// or not mine
	// Then there is a sum of the equation indicating how many mines are there in
	// the variables

	// Fast Singleton Reference:
	// If we deduce the fact that (x,y) is a mine or not, we will store such
	// information in to our board so that we can reduce
	// the time on query (shrinking our KB)

	// Queue implemented for Query processing

	// Format: A[0]->x, A[1]->y, A[2]->Number
	// public boolean [][] flag= new boolean [board.dim][board.dim];//Our Flag
	// Matrix (Like the windows Minesweeper flagging)
	// Flags:
	// 0-> I Don't know anything
	// -1-> It maybe a mine
	// -2-> It is a mine (predicted result)
	// -3-> It cannot be a mine (when the board return 0 set this value

	// Search: Using BFS to expand the whole minesweeper graph

	public boolean baseline; // is baseline agent.
	public int safelyIdentified = 0; // number of safely identified mines.
	// public datasturcture KB;

	public Agent(int dim, boolean baseline) {
		board = new Grid('a', dim, 0);
		this.baseline = baseline;
	}

	public int[] getNextBestPoint() {
		SolutionSet ss = new SolutionSet(equations);
		ss.find_soln_set();
		Integer[] a = ss.getNextBestGuess();
		int[] b= new int[2];
		if(a==null) {
			return null;
		}
		b[0]=a[0];
		b[1]=a[1];
		return b;

	}

	public void updateKB(Eqn to_insert) {

		// add to KB here
		// perform any relevant inference or deductions to learn more about the
		// environment.
		// If the agent is able to determine that a cell has a mine, it should flag or
		// mark it,
		// and never query that cell

		// board.markMine(x, y);
		// board.markClear(x, y);

		if (to_insert.sum == to_insert.pts.size() || to_insert.sum == 0) { // Never Insert some Eqn with Sum = 0
			if (to_insert.sum == 0) {
				for (Integer[] c : to_insert.pts) {
					//System.out.println("Safe and Covered added," + c[0] + "," + c[1]);
					safe_nodes.add(c);

					board.arr[c[0]][c[1]] = board.aSafeAndCovered;
				}
			} else {
				for (Integer[] c : to_insert.pts) {
					// safe_nodes.add(c);
					//System.out.println("Mine and Covered added," + c[0] + "," + c[1]);
					board.arr[c[0]][c[1]] = board.aMineAndCovered;
					board.numMines++;
					safelyIdentified++;
				}
			}
			// Update KB
			for (Iterator<Eqn> iter_eqn = equations.iterator(); iter_eqn.hasNext();) {
				Eqn c = iter_eqn.next();
				for (Iterator<Integer[]> iter_c = c.pts.iterator(); iter_c.hasNext();) {
					Integer[] coord = iter_c.next();
					if (board.arr[coord[0]][coord[1]] == board.aSafeAndCovered) {
						//System.out.println("Removed Safe and Covered " + coord[0] + "," + coord[1]);
						iter_c.remove();// delete this entry
						continue;
					}
					if (board.arr[coord[0]][coord[1]] == board.aMineAndCovered
							|| board.arr[coord[0]][coord[1]] == board.aMineExploded
							|| board.arr[coord[0]][coord[1]] == board.eMine) {
						//System.out.println(
//								"Removed Mine and Covered " + coord[0] + "," + coord[1] + ",Result Sum" + (c.sum - 1));
						iter_c.remove();
						c.sum--;
						continue;
					}
					if (c.pts.size() == c.sum || c.sum == 0) {// only one stuff -> means singleton, And First if
																// statement make sure that sum!=0
						if (c.sum == 0) {

							for (Integer[] mine : c.pts) {
								board.arr[mine[0]][mine[1]] = board.aSafeAndCovered;
								//System.out.println(
//										"Sum=0, Singleton, adding to Safe and Covered" + mine[0] + "," + mine[1]);
								safe_nodes.add(mine);
							}
							//System.out.println("Equation being removed");
							iter_eqn.remove();
							break;
						}
						for (Integer[] mine : c.pts) {
							board.arr[mine[0]][mine[1]] = board.aMineAndCovered;
							board.numMines++;
							safelyIdentified++;
							//System.out
							//		.println("Sum!=0, Singleton, adding to Mine and Covered" + mine[0] + "," + mine[1]);
						}
						//System.out.println("Equation being Removed");
						iter_eqn.remove();
						break;
					}
				}
			}
			return;
		} else {
			boolean isInserted = false;
			ArrayList<Eqn> to_add = new ArrayList<Eqn>();
			// ArrayList<Eqn> to_delete=new ArrayList<Eqn>();
			//System.out.println("Not singleton or trivial answers");
			for (Iterator<Eqn> iter_eqn = equations.iterator(); iter_eqn.hasNext();) {
				Eqn c = iter_eqn.next();
				if (c.pts.containsAll(to_insert.pts)) {
					if (c.pts.size() == to_insert.pts.size()) {// repetitive, rejection
						//System.out.println("Repetitive, Rejection");
						return;
					}
					// non-repetitive, split
					// indicating to_insert.pts \subseteq c.pts
					// therefore we will delete c.pts from equations, and delete to_insert.pts stuff
					// and sum from c.pts then insert back to KB
					iter_eqn.remove();
					c.pts.removeAll(to_insert.pts);
					c.sum -= to_insert.sum;
					to_add.add(c);
					if (isInserted == false) {
						isInserted = true;
						to_add.add(to_insert);
					}
				} else {
					if (to_insert.pts.containsAll(c.pts)) {
						// not going to be repetitive since it is in the first case
						// then it is non-repetitive, split
						// indicating c.pts \subseteq to_insert.pts
						// therefore we will check whether we have inserted to_insert to equations
						// Then if we inserted the to_insert, first delete it
						// Then get subset of insert_to and add it back to the set
						if (isInserted == true) {
							isInserted = false;
							to_add.remove(to_insert);
						}
						to_insert.pts.removeAll(c.pts);
						to_insert.sum -= c.sum;
						isInserted = true;
						to_add.add(to_insert);

					} 
				}

			}

			if (isInserted == false) {
				to_add.add(to_insert);
			}
			for (Eqn c : to_add) {
				equations.add(c);
			}
		}

	}

	private List<int[]> hiddenNeighbors(int x, int y) {
		List<int[]> ret = board.getNeighbors(x, y);
		ret.removeIf((int[] coord) -> board.arr[coord[0]][coord[1]] != Grid.aHidden);
		return ret;
	}

	private List<int[]> safeNeighbors(int x, int y) {// safe neighbors are numbered cells
		List<int[]> ret = board.getNeighbors(x, y);
		ret.removeIf((int[] coord) -> board.arr[coord[0]][coord[1]] < '0' || board.arr[coord[0]][coord[1]] > '8');
		return ret;
	}

	private List<int[]> foundMineNeighbors(int x, int y) {// found mine neighbors are cells marked 'M' or exploded '*'
		List<int[]> ret = board.getNeighbors(x, y);
		ret.removeIf((int[] coord) -> board.arr[coord[0]][coord[1]] != Grid.aMineAndCovered
				&& board.arr[coord[0]][coord[1]] != Grid.aMineExploded);
		return ret;
	}

	public boolean query(Environment e, int x, int y) {// return num mines safely ID. if queried a mine, return -1

		char clue = e.board.arr[x][y];
		board.arr[x][y] = clue;
		if (e.board.arr[x][y] == Grid.eMine) {
			board.numMines++;
			return false;
		}

		if (baseline && clue != Grid.eMine) {
			int clueNum = clue - '0';
			List<int[]> totalNeighbors = board.getNeighbors(x, y);
			List<int[]> hidden = hiddenNeighbors(x, y);
			if (clueNum - foundMineNeighbors(x, y).size() == hidden.size()) {
				// "If, for a given cell, the total number of mines (the clue) minus the number
				// of revealed mines is the number of hidden neighbors, every hidden neighbor is
				// a mine."
				hidden.stream().forEach((int[] coord) -> {
					markMine(coord[0], coord[1]);
					safelyIdentified++;
				});
			}
			List<int[]> safe = safeNeighbors(x, y);
			if (totalNeighbors.size() - clueNum - safe.size() == hidden.size()) {
				// "If, for a given cell, the total number of safe neighbors (8 - clue) minus
				// the number of revealed safe neighbors is the number of hidden neighbors,
				// every hidden neighbor is safe."
				hidden.stream().forEach((int[] coord) -> markSafe(e, coord[0], coord[1]));
			}

		} else {
			 if (e.board.arr[x][y]==Grid.eMine) 
				 return false;
			 board.arr[x][y]=e.board.arr[x][y];
			 ArrayList<Integer[]> add_list= new ArrayList<Integer[]>();
			 int sum = board.arr[x][y]-'0';
			 for(int[] c : board.getNeighbors(x, y)) {
				 Integer[] c_int = new Integer[2];
				 c_int[0]=c[0];
				 c_int[1]=c[1];
				 	if(board.arr[c[0]][c[1]]==board.aMineAndCovered|| board.arr[c[0]][c[1]]==board.aMineExploded) {
				 		//System.out.println("MineAndCovered"+(sum-1));
				 		sum--;
				 	}
					if(board.arr[c[0]][c[1]]==board.aHidden) {
						//System.out.println("Hidden Node,"+c[0]+","+c[1]);
						add_list.add(c_int);

				 }
			 }
			 //System.out.println("Current Eqn originated from"+x+","+y+"with sum"+sum);
			 Eqn a = new Eqn(add_list,sum);
			 
			 updateKB(a); 
			 
			 return true;
		}

		return true;
	}

//for agent board only:
	public void markMine(int x, int y) {
		if (board.arr[x][y] == '*')
			System.err.print("bomb already exploded, shouldnt be here");
		// //System.out.println(y+" "+x+" is marked.");
		board.arr[x][y] = Grid.aMineAndCovered;
		board.numMines++;
	}

//for agent board only:
	public void markSafe(Environment e, int x, int y) {
		board.arr[x][y] = Grid.aSafeAndCovered;
		query(e, x, y); // show #.
	}

}
