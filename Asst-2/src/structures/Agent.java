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
		ret.removeIf((int[] coord)->board.arr[coord[0]][coord[1]]<'0'||board.arr[coord[0]][coord[1]]>'8' );
		return ret;
	}
	private List<int[]> foundMineNeighbors(int x, int y){//safe neighbors are numbered cells
		List<int[]> ret=board.getNeighbors(x, y);
		ret.removeIf((int[] coord)->board.arr[coord[0]][coord[1]]!=Grid.aMineAndCovered&&board.arr[coord[0]][coord[1]]!=Grid.aMineExploded );
		return ret;
	}
	

	public boolean query(Environment e, int x, int y) {//return num mines safely ID. if queried a mine, return -1
		
		char clue=e.board.arr[x][y];
		board.arr[x][y]=clue;
		if (e.board.arr[x][y]==Grid.eMine) {
			board.numMines++;
			return false;
		}

		if (baseline&&clue!=Grid.eMine) {//System.out.print("querying "+x+","+y+"\t");board.show();
			//board.getAllCoords().stream().forEach((int[] c->{ System.out.println()});
		
	
			int clueNum='8';
			if (clue!=Grid.eMine) {
				clueNum=clue-'0';
			}
			List<int[]> totalNeighbors=board.getNeighbors(x, y);
			List<int[]> hidden=hiddenNeighbors(x,y);
			//System.out.println(clueNum+"-"+foundMineNeighbors(x,y).size()+"="+hidden.size());
			 
			if(clueNum-foundMineNeighbors(x,y).size()==hidden.size()) {//totalNeighbors.get(revealed)
				 hidden.stream().forEach((int[] coord)-> {markMine(coord[0],coord[1]);safelyIdentified++;});
			}
			List<int[]> safe=safeNeighbors(x,y);
			//System.out.println(8-clueNum-safe.size()+" ---"+hidden.size());
			if(totalNeighbors.size()-clueNum-safe.size()==hidden.size()) {
				hidden.stream().forEach((int[] coord)-> markSafe(e,coord[0],coord[1]));
			}
			 
	}else {
		updateKB(  ); 
	}

	return true;
}

//for agent board only:
public void markMine(int x, int y) {		if (board.arr[x][y]=='*') System.err.print("bomb already exploded, shouldnt be here");
	//System.out.println(y+" "+x+" is marked.");
	board.arr[x][y]=Grid.aMineAndCovered; 
	board.numMines++;
}
//for agent board only:
public void markSafe(Environment e,int x, int y) {
	board.arr[x][y]=Grid.aSafeAndCovered; 
	query(e, x, y);	//show #.
}	


 



}
