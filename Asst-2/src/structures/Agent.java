package structures;

import java.util.*;

public class Agent {
	public Grid board;
	public boolean baseline;	//is baseline agent.
	public int safelyIdentified=0; 
	//public datasturcture KB;
	public Agent(int dim, boolean baseline) {
		board=new Grid('a',dim,0);
		this.baseline=baseline;
	}



	public int[] assessKB() {	
		int[] queryCoord;
		//assess KB here, return coordinates {x,y} to query.


		return null;
	}


	public void updateKB(  ) {	

		//add to KB here
		//perform any relevant inference or deductions to learn more about the environment.
		//If the agent is able to determine that a cell has a mine, it should flag or mark it, 
		//and never query that cell

		//board.markMine(x, y);
		//board.markClear(x, y);

	}

	private List<int[]> hiddenNeighbors(int x, int y){
		List<int[]> ret=board.getNeighbors(x, y);
		ret.removeIf((int[] coord)->board.arr[coord[0]][coord[1]]!=Grid.aHidden );
		return ret;
	}
	private List<int[]> safeNeighbors(int x, int y){//safe neighbors are numbered cells
		List<int[]> ret=board.getNeighbors(x, y);
		ret.removeIf((int[] coord)->board.arr[coord[0]][coord[1]]<'0'||board.arr[coord[0]][coord[1]]>'9' );
		return ret;
	}

	public boolean query(Environment e, int x, int y) {//return num mines safely ID. if queried a mine, return -1
		board.show();
		char clue=e.board.arr[x][y];
		board.arr[x][y]=clue;
		if (e.board.arr[x][y]==Grid.eMine) {
			board.numMines++;
			return false;
		}

		if (baseline) {
			//board.getAllCoords().stream().forEach((int[] c->{ System.out.println()});
			int clueNum='8';
			if (clue!=Grid.eMine) {
				clueNum=clue-'0';
			}
			List<int[]> totalNeighbors=board.getNeighbors(x, y);
			List<int[]> hidden=hiddenNeighbors(x,y);
			//System.out.println(clueNum+"-"+board.numMines+"="+hidden.size());
			if(clueNum-board.numMines==hidden.size()) {
				hidden.stream().forEach((int[] coord)-> {markMine(coord[0],coord[1]);safelyIdentified++;});
			}
			List<int[]> safe=safeNeighbors(x,y);
			//System.out.println(8-clueNum-safe.size()+" ---"+hidden.size());
			if(totalNeighbors.size()-clueNum-safe.size()==hidden.size()) {
				hidden.stream().forEach((int[] coord)-> markSafe(e,coord[0],coord[1]));
			}
		
		//after everything, query all the safe negihbors.

	}else {
		updateKB(  ); 
	}

	return true;
}

//for agent board only:
public void markMine(int x, int y) {
	board.arr[x][y]=Grid.aMineAndCovered;
	board.numMines++;
}
//for agent board only:
public void markSafe(Environment e,int x, int y) {
	board.arr[x][y]=Grid.aSafeAndCovered; 
	query(e, x, y);	//show #.
}	


/*Implement the following simple agent as a baseline strategy to compare against your own:
 *  • For each cell, keep track of  
	– the number of safe squares identified around it – the number of mines identified around it.
	– the number of hidden squares around it.
	• If, for a given cell, 
		the total number of mines (the clue) minus the number of revealed mines is the number of hidden neighbors, every hidden neighbor is a mine.
		the total number of safe neighbors (8 - clue) minus the number of revealed safe neighbors is the number of hidden neighbors, every hidden neighbor is safe.

	• If no hidden cell can be conclusively identified as a mine or safe, pick a cell to reveal at random.
	This is a weak inference algorithm based entirely on local data and comparisons, but you should expect this to be quite effective in a lot of situations. How can you do better?
 */



}
