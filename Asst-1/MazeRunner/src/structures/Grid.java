package structures;

import java.util.*;

import app.MazeRunner.Coord;

public class Grid {
 
	public int[][] arr;
	public int dim;
 
	
	
	public final int WallNum=-1;
	public final int FreeNum=0;
	public final int OccupiedNum=1;
	public final int BurntNum=2;
	public final int StartNum=-2;
	public final int EndNum=-3;
	
	public final String Wall="+ ";//-1
	public final String Free="  ";//0
	public final String Occupied="* ";//1
	public final String Burnt="& ";//2
	public final String Start="S ";//-2
	public final String End="G ";//-3
	public Grid(int dim, double p) {//initialize board:
		this.dim=dim;
		arr=new int[dim][dim];
		for(int i=0;i<dim;i++) {
			for(int j=0;j<dim;j++) {
				double rand=((double)(Math.random()*1000)+1)/1000; // System.out.println(rand+" "+p);
				if (rand<p) {
					arr[i][j]= -1; 
				}
			}
		} 
		arr[0][0]=-2;
		arr[dim-1][dim-1]=-3;
	}
	
	 
	public void show() {
		String toPrint;
		for (int i=0;i<dim;i++) {
			System.out.print(i+"  ");
			for (int j=0;j<dim;j++) {
				toPrint="\0";
				switch(arr[i][j]) {
					case -3 : toPrint=End; break;
					case -2 : toPrint=Start; break;
					case -1 : toPrint=Wall; break;
					case 0  : toPrint=Free; break;
					case 1  : toPrint=Occupied; break;
					case 2  : toPrint=Burnt; break;
					default : System.err.println("\nPANIC:Printing Grid at ("+i+","+j+") with value "+arr[i][j]+". Program Will EXIT!\n"); System.exit(-2);
				}
				System.out.print( toPrint +" " );
			}
			System.out.println();
		} 
		System.out.print("  ");
		for (int i=0;i<dim;i++) {
			System.out.print(" "+i+" ");
		}
		System.out.println("\n ");
		
	}
	public void clearOccupied(){
		for(int i=0;i<dim;i++) {
			for(int j=0;j<dim;j++) {
				if(arr[i][j]==OccupiedNum)
					arr[i][j]=FreeNum;
			}
		} 
		arr[0][0]=StartNum;
		arr[dim-1][dim-1]=EndNum;
	}
	
	public boolean isFree(int x,int y) {
		if (x>=0&&y>=0&&x<dim&&y<dim)
			return arr[x][y]==0||arr[x][y]==StartNum||arr[x][y]==EndNum;
		else return false;
	}
	public boolean isGoal(int x,int y) {
		return x==dim-1&&y==dim-1;
	}
	
	public void occupy(int x,int y) { //Maybe Not that useful
		if (isFree(x,y)) {
			arr[x][y]=OccupiedNum;
		} 
	}
	
	public List<Coord> getNeighbors(int x,int y){
		List <Coord> list=new ArrayList<Coord>();
		if (isFree(x,y+1)) {list.add(new Coord(x,y+1,null));}
		if (isFree(x+1,y)) { list.add(new Coord(x+1,y,null));}
		if (isFree(x-1,y)) { list.add(new Coord(x-1,y,null));}
		if (isFree(x,y-1)) {list.add(new Coord(x,y-1,null));}
		return list;
			
		
	}
	 
	
	
}