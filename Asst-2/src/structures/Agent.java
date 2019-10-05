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
	
	public boolean query(Environment e, int x, int y) {//return false if queried a mine (game over)
		 if (e.board.arr[x][y]==Grid.eMine) 
			 return false;
		  
		 
		
		//board.markMine(x, y);
		//board.markClear(x, y);
		 
		 return true;
	}
	

}
