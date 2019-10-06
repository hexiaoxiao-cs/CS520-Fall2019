package structures;
 

public class Agent {
	public Grid board;
	//public datasturcture KB;
	public Agent(int dim) {
		board=new Grid('a',dim,0);
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
	
	public boolean query(Environment e, int x, int y) {//return true if queried safe, else false if queried a mine
		 if (e.board.arr[x][y]==Grid.eMine) 
			 return false;
		 board.arr[x][y]=e.board.arr[x][y];
		 updateKB(  ); 
		 
		 return true;
	}
	

}
