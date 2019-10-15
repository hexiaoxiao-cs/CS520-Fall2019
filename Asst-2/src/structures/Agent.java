package structures;

import java.util.*;

public class Agent {
	public Grid board;
	public ArrayList<Eqn> equations = new ArrayList<Eqn>(); // Our KB
	public Queue<Coordinate> safe_nodes = new LinkedList<Coordinate>(); // Our Processing Queue
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
	public ArrayList<Eqn> make_smaller_KB(int x, int y) {
		ArrayList<Eqn> smaller= new ArrayList<Eqn>();
		for(Eqn eq : equations) {
			boolean tmp = false;
			for(Coordinate coord:eq.pts) {
				if(Math.abs(coord.x-x)>=3 || Math.abs(coord.y-y)>=3) {
					tmp=false;
					break;
				}
				else {
					tmp=true;
				}
			}
			if(tmp==true) {
				smaller.add(eq);
			}
		}
		return smaller;
	}
	public ArrayList<Coordinate> getNextBestPoint_Limited() {//Search in the 5*5 area
		SolutionSet ss = new SolutionSet(equations);
		int[] final_results=new int[ss.vars.size()];
		ArrayList<Coordinate> to_return = new ArrayList<Coordinate> ();
		//Arrays.fill(final_results, -1);
		for(Coordinate c : ss.vars) {
			ArrayList<Eqn> smaller = make_smaller_KB(c.x,c.y);
			if(smaller.isEmpty()) {continue;}
			SolutionSet temp=new SolutionSet(smaller);
			temp.find_soln_set();
			if(temp.solns.isEmpty()) {continue;}
			int[] curr_stat=new int[temp.vars.size()];
			for(int[] soln : temp.solns) {
				for(int i = 0; i<temp.vars.size();i++) {
					curr_stat[i]+=soln[i];
				}
			}
			for(int i = 0; i < temp.vars.size();i++) {
				/*
				if(final_results[ss.vars.indexOf(temp.vars.get(i))]==-1) {
					final_results[ss.vars.indexOf(temp.vars.get(i))]=0;//indicating we searched this one and no solns in that space 
				}*/
				//System.out.println(final_results.toString());
				final_results[ss.vars.indexOf(temp.vars.get(i))]+=curr_stat[i];
			}
		}
		for(int i = 0 ; i < ss.vars.size(); i++) {
			Coordinate tt=new Coordinate(ss.vars.get(i).x,ss.vars.get(i).y);
			tt.occur=final_results[i];
			to_return.add(tt);
		}
		Collections.sort(to_return);
		return to_return;
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
				for (Coordinate c : to_insert.pts) {
					//System.out.println("Safe and Covered added," + c[0] + "," + c[1]);
					safe_nodes.add(c);

					board.arr[c.x][c.y] = board.aSafeAndCovered;
				}
			} else {
				for (Coordinate c : to_insert.pts) {
					// safe_nodes.add(c);
					//System.out.println("Mine and Covered added," + c[0] + "," + c[1]);
					board.arr[c.x][c.y] = board.aMineAndCovered;
					board.numMines++;
					safelyIdentified++;
				}
			}
			// Update KB
			for (Iterator<Eqn> iter_eqn = equations.iterator(); iter_eqn.hasNext();) {
				Eqn c = iter_eqn.next();
				for (Iterator<Coordinate> iter_c = c.pts.iterator(); iter_c.hasNext();) {
					Coordinate coord = iter_c.next();
					if (board.arr[coord.x][coord.y] == board.aSafeAndCovered
							|| (board.arr[coord.x][coord.y]>='0'&&board.arr[coord.x][coord.y]<='8')) {
						//System.out.println("Removed Safe and Covered " + coord[0] + "," + coord[1]+", with entry"+board.arr[coord[0]][coord[1]]);
						
						iter_c.remove();// delete this entry
						continue;
					}
					//System.out.println(coord[0] + "," + coord[1]+", with entry"+board.arr[coord[0]][coord[1]]);
					if (board.arr[coord.x][coord.y] == board.aMineAndCovered
							|| board.arr[coord.x][coord.y] == board.aMineExploded
							|| board.arr[coord.x][coord.y] == board.eMine) {
						//System.out.println(
//								"Removed Mine and Covered " + coord[0] + "," + coord[1] + ",Result Sum" + (c.sum - 1));
						iter_c.remove();
						c.sum--;
						continue;
					}
					if (c.pts.size() == c.sum || c.sum == 0) {// only one stuff -> means singleton, And First if
																// statement make sure that sum!=0
						if (c.sum == 0) {

							for (Coordinate mine : c.pts) {
								board.arr[mine.x][mine.y] = board.aSafeAndCovered;
								//System.out.println(
//										"Sum=0, Singleton, adding to Safe and Covered" + mine[0] + "," + mine[1]);
								safe_nodes.add(mine);
							}
							//System.out.println("Equation being removed");
							iter_eqn.remove();
							break;
						}
						for (Coordinate mine : c.pts) {
							board.arr[mine.x][mine.y] = board.aMineAndCovered;
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
					//System.out.println("Before " + c.pts.size());
					c.pts.removeAll(to_insert.pts);
					//System.out.println("After " + c.pts.size());
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
			 ArrayList<Coordinate> add_list= new ArrayList<Coordinate>();
			 int sum = board.arr[x][y]-'0';
			 for(int[] c : board.getNeighbors(x, y)) {
				 Coordinate c_int = new Coordinate(c[0],c[1]);
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
