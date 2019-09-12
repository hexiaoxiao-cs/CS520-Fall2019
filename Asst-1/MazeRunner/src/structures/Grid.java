package structures;

import java.util.*;

public class Grid {
	private String[][] arr;
	private int dim;
	
	public final String Wall=(char)0x2593+""+(char)0x2593;
	public final String Free=(char)0x2591+""+(char)0x2591;
	public final String Occupied="* ";
	 
	
	public Grid(int dim, double p) {//initialize board:
		this.dim=dim;
		arr=new String[dim][dim];
		for(int i=0;i<dim;i++) {
			for(int j=0;j<dim;j++) {
				double rand=((double)(Math.random()*1000)+1)/1000; // System.out.println(rand+" "+p);
				if (rand<p) {
					arr[i][j]= Wall; 
				}else {
					arr[i][j]= Free;
				}
			}
		} 
		arr[0][0]="s ";
		arr[dim-1][dim-1]="G ";
	}
	
	 
	public void show() {
		for (int i=0;i<dim;i++) {
			System.out.print(i+"  ");
			for (int j=0;j<dim;j++) {
				System.out.print( arr[i][j] +" " );
			}
			System.out.println();
		} 
		System.out.print("  ");
		for (int i=0;i<dim;i++) {
			System.out.print(" "+i+" ");
		}
		
	}
	
	public boolean isFree(int x,int y) {
		return arr[x][y].equals(Free);
	}
	public boolean isGoal(int x,int y) {
		return x==dim&&y==dim;
	}
	
	public void occupy(int x,int y) {
		if (isFree(x,y)) {
			arr[x][y]="* ";
		}else {
			System.err.println("\ncannot be occupied at ("+x+","+y+") because of wall\n");
		} 
	}
	 
}