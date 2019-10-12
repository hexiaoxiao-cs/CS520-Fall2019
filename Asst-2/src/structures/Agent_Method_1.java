package structures;

import java.util.*;


public class Agent_Method_1 {
	public Grid board;
	//public data structure KB;
	public LinkedList<Eqn> equations= new LinkedList<Eqn>(); // Our KB
	public Queue<int[]> safe_nodes = new LinkedList<int[]>(); // Our Processing Queue
	//public int[][] = new 
	//KB contains equations
	//Equations means every variable (Coordinate) is either 1 or 0 indicating mine or not mine
	//Then there is a sum of the equation indicating how many mines are there in the variables
	
	//Fast Singleton Reference:
	//If we deduce the fact that (x,y) is a mine or not, we will store such information in to our board so that we can reduce
	//the time on query (shrinking our KB)
	
	//Queue implemented for Query processing
	
	
	//Format: A[0]->x, A[1]->y, A[2]->Number
	//public boolean [][] flag= new boolean [board.dim][board.dim];//Our Flag Matrix (Like the windows Minesweeper flagging)
	//Flags:
	//0-> I Don't know anything
	//-1-> It maybe a mine
	//-2-> It is a mine (predicted result)
	//-3-> It cannot be a mine (when the board return 0 set this value
	
	//Search: Using BFS to expand the whole minesweeper graph
	public int[][] getSurrounding(int x, int y){// get surrounding block index
		int [][] result= new int [8][2];
		result[0][0]= x-1;
		result[0][1]= y-1;
		result[1][0]= x-1;
		result[1][1]= y;
		result[2][0]= x-1;
		result[2][1]= y+1;
		result[3][0]= x;
		result[3][1]= y-1;
		result[4][0]= x;
		result[4][1]= y+1;
		result[5][0]= x+1;
		result[5][1]= y-1;
		result[6][0]= x+1;
		result[6][1]= y;
		result[7][0]= x+1;
		result[7][1]= y+1;
		return result;
	}
	public void updateKB(Eqn to_insert) {
		if (to_insert.sum==0) { //Never Insert some Eqn with Sum = 0
			for (int[] c : to_insert.pts) {
				safe_nodes.add(c);
				board.arr[c[0]][c[1]]=board.aSafe;
			}
			//Update KB
			for(Eqn c: equations) {
				for(int[] coord : c.pts) {
					if(board.arr[coord[0]][coord[1]]==board.aSafe) {
						c.pts.remove(coord);// delete this entry
					}
					if(board.arr[coord[0]][coord[1]]==board.aMine||board.arr[coord[0]][coord[1]]==board.eMine) { 
						c.pts.remove(coord);
						c.sum--;
					}
					if(c.pts.size()==c.sum) {//only one stuff -> means singleton, And First if statement make sure that sum!=0
						for(int[] mine: c.pts) {
							board.arr[mine[0]][mine[1]]=board.aMine;
						}
						equations.remove(c);
						break;
					}
				}
			}
			return;
		}
		else {
			boolean isInserted=false;
			for (Eqn c : equations) {
				if(c.pts.containsAll(to_insert.pts)) {
					if(c.pts.size()==to_insert.pts.size()) {//repetitive, rejection
						return;
					}
					//non-repetitive, split
					//indicating to_insert.pts \subseteq c.pts
					//therefore we will delete c.pts from equations, and delete to_insert.pts stuff and sum from c.pts then insert back to KB
					equations.remove(c);
					c.pts.removeAll(to_insert.pts);
					c.sum-=to_insert.sum;
					updateKB(c);
					if(isInserted==false) {
						isInserted=true;
						equations.add(to_insert);
					}
				}
				else {
					if(to_insert.pts.containsAll(c.pts)) {
						//not going to be repetitive since it is in the first case
						//then it is non-repetitive, split
						//indicating c.pts \subseteq to_insert.pts
						//therefore we will check whether we have inserted to_insert to equations
						//Then if we inserted the to_insert, first delete it
						//Then get subset of insert_to and add it back to the set
						if(isInserted==true) {
							isInserted=false;
							equations.remove(to_insert);
						}
						to_insert.pts.removeAll(c.pts);
						to_insert.sum-=c.sum;
						isInserted=true;
						equations.add(to_insert);
						
					}
				}
				
			}
		}
	}
	
	public Agent_Method_1(int dim) {
		board=new Grid('a',dim,0);
		
	}
	
	
	public boolean query(Environment e, int x, int y) {//return true if queried safe, else false if queried a mine
		 if (e.board.arr[x][y]==Grid.eMine) 
			 return false;
		 board.arr[x][y]=e.board.arr[x][y];
		 ArrayList<int[]> add_list= new ArrayList<int[]>();
		 Eqn a = new Eqn();
		 
		 updateKB(); 
		 
		 return true;
	}
}