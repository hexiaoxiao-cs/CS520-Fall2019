package structures;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/*
 * 
 * 
 * This file is old stuff from minesweeper
 * 
 * 
 * 
 */


public class Grid {

	public char[][] arr;
	public int dim;
	public int numMines=0; 

	public final static char eMine='*';
	public final static char aHidden='?';
	public final static char aMineAndCovered='M';
	public final static char aSafeAndCovered='C';
	public final static char aMineExploded=eMine;

	public Grid(char type,int dim, int numMines) {//initialize board:
		if (numMines>dim*dim) System.err.println("too many mines");
		this.dim=dim;
		this.numMines=numMines;
		arr=new char[dim][dim];
		for(int i=0;i<dim;i++) {
			for (int j=0;j<dim;j++)
				arr[i][j]='0';
		}
		if (type=='e') {//Environment Board.
			int minesPlaced=0;
			while(minesPlaced<numMines) {
				int x=(int)(Math.random()*dim);
				int y=(int)(Math.random()*dim);

				if (arr[x][y]!=eMine) {
					arr[x][y]=eMine; 
					for(int coord[]:getNeighbors(x,y)) { 
						int cx=coord[0];
						int cy=coord[1];
						if (arr[cx][cy]>='0'&&arr[cx][cy]<='8')
							arr[cx][cy]++;
					} 
					minesPlaced++; 
				} 


			}  
		}else {//Agent Board.
			for(int i=0;i<dim;i++) {
				for (int j=0;j<dim;j++)
					arr[i][j]=aHidden;
			} 
		}
	}

	public Grid(int dim, double density) {//generates environment board using mine density
		this.dim=dim;
		arr=new char[dim][dim];
		for(int i=0;i<dim;i++) {
			for (int j=0;j<dim;j++) {
				double rand=((double)(Math.random()*1000)+1)/1000; // System.out.println(rand+" "+p);
				if (rand<density) {
					arr[i][j]= eMine;  
					numMines++;
				
					for(int coord[]:getNeighbors(i,j)) { 
						int cx=coord[0];
						int cy=coord[1];
						if (arr[cx][cy]==0) arr[cx][cy]='0';
						if (arr[cx][cy]>='0'&&arr[cx][cy]<='8')
							arr[cx][cy]++;
					}  
				}else if (arr[i][j]==0) arr[i][j]='0'; 
			} 

		} 
	}

	public void show() { 
		for (int i=0;i<dim;i++) {
			System.out.print(i+"\t");
			for (int j=0;j<dim;j++) {
				System.out.print( " "+arr[i][j] +" " );
			}
			System.out.println();
		} 
		System.out.print("\n\t");
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

	public boolean isCellCoord(int x,int y) {
		return x>=0&&y>=0&&x<dim&&y<dim;
	} 

	public List<int[]> getNeighbors(int x,int y){
		List <int[]> list=new ArrayList<int[]>();
		for(int i=x-1;i<=x+1;i++) {
			for (int j=y-1;j<=y+1;j++) {
				if(isCellCoord(i,j)  ){
					int coord[]= {i,j};
					list.add(coord);
				}  
			}
		} 
		return list;
	}


	public List<int[]> getAllCoords() {
		List <int[]> list=new ArrayList<int[]>();
		for(int i=0;i<dim;i++) {
			for (int j=0;j<dim;j++) {
				int coord[]= {i,j};
				list.add(coord);
			}
		} 
		return list;
	}








}