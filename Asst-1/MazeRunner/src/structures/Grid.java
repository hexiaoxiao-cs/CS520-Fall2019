package structures;

import java.util.*;
import java.util.stream.Collectors;

import app.MazeRunner.Coord;

public class Grid {
 
	public int[][] arr;
	public int dim;
	public double p_burn;
	
	
	public final static int WallNum=-1;
	public final static int FreeNum=0;
	public final static int OccupiedNum=1;
	public final static int BurntNum=2;
	public final static int StartNum=-2;
	public final static int EndNum=-3;
	
	public final String Wall="+";//-1   //(char)0x2593+""; 
	public final String Free=" ";//0
	public final String Occupied="*";//1
	public final String Burnt="&";//2
	public final String Start="S";//-2
	public final String End="G";//-3
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
			System.out.print(i+"\t");
			for (int j=0;j<dim;j++) {
				toPrint="\0";
				switch(arr[i][j]) {
					case -3 : toPrint=End; break;
					case -2 : toPrint=Start; break;
					case -1 : toPrint=Wall; break;
					case 0  : toPrint=Free; break;
					case 1  : toPrint=Occupied; break;
					case 2  : toPrint=Burnt; break;
					default : toPrint=arr[i][j]+""; break;//System.err.println("\nPANIC:Printing Grid at ("+i+","+j+") with value "+arr[i][j]+". Program Will EXIT!\n"); System.exit(-2);
				}
				System.out.print( " "+toPrint +" " );
			}
			System.out.println();
		} 
		System.out.print("\t");
		for (int i=0;i<dim;i++) {
			if ((i+"").length()==1)
				System.out.print(" "+i+" ");
			else if ((i+"").length()==2)
				System.out.print(" "+i);
			else
				System.out.print(i);
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
			return arr[x][y]!=BurntNum&&arr[x][y]!=OccupiedNum&&arr[x][y]!=WallNum;
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
		
		if (isFree(x,y+1)) {	list.add(new Coord(x,y+1,null));}
		if (isFree(x+1,y)) { list.add(new Coord(x+1,y,null));}
		if (isFree(x-1,y)) { list.add(new Coord(x-1,y,null));}
		if (isFree(x,y-1)) {	list.add(new Coord(x,y-1,null));}
		
		return list;//list.stream().distinct().collect(Collectors.toList());//remove duplicates.
	}
	public boolean isBurnt(int x, int y) {
		if (x>=0&&y>=0&&x<dim&&y<dim&&arr[x][y]==BurntNum) {
			return true;
		}
		return false;
	}
	public void updateGrid() {
		int x=0,y=0;
		int counter=0;
		double rand;
		double prob_cb;
		for(x=0;x<=dim-1;x++) {
			for(y=0;y<=dim-1;y++) {
				
				if(isFree(x,y)) {
					counter=0;
					if(isBurnt(x-1,y)) {counter++;}
					if(isBurnt(x+1,y)) {counter++;}
					if(isBurnt(x,y-1)) {counter++;}
					if(isBurnt(x,y+1)) {counter++;}
					if(counter==0) {continue;}
					rand=((double)(Math.random()*1000)+1)/1000;
					prob_cb=1-Math.pow((1-p_burn), counter);
					if(rand<prob_cb) {
						setFire(x,y);
					}
				}
			}
		}
	}
	
	public void setFire(int x, int y) {
//		if(arr[x][y]!=StartNum &&arr[x][y]!=EndNum && arr[x][y]!=WallNum) {
			arr[x][y]=BurntNum;
//		}
	}
	
	public void showPath(Coord goal) {
		
		for (Coord ptr = goal; ptr != null; ptr = ptr.parent) {
			//System.out.println("parent of " + ptr + " is " + ptr.parent);
			 occupy(ptr.x, ptr.y);
		}
		 arr[0][0] =  StartNum;
		 arr[dim - 1][dim - 1] =  EndNum;
	}
	public int getNumAt(int x,int y) {
		return arr[x][y];
	}
	 
	public Grid mate(Grid p2) {//p1 is 'this' instance.
		if (dim!=p2.dim) return null;
		 double percentage= ((double)(Math.random()*1000)+1)/1000; //This is the percentage of the board that will be child from parent 1.
		//Create child:
		Grid child=new Grid(dim,0);
		for(int i=0;i<dim;i++) {
			for(int j=0;j<dim;j++) {
				double rand=((double)(Math.random()*1000)+1)/1000; 
				if (rand<percentage) {
					child.arr[i][j]= this.getNumAt(i, j); 
				}else {
					child.arr[i][j]= p2.getNumAt(i, j); 
				} 
			}
		} 
		return child;
	}
	public void mutate(double mutationStrength) {
		//change a 10% of squares.
		int amountOfChanges=(int) Math.ceil(dim*dim*mutationStrength);
		for(int i=0;i<amountOfChanges;i++) {
			int x=(int)(Math.random()*dim); 
			int y=(int)(Math.random()*dim); 
			
			if (arr[x][y]==WallNum) {
				arr[x][y]=FreeNum;
			}else if(arr[x][y]==FreeNum) {
				arr[x][y]=WallNum;
			}
		}
		
 
	}

}