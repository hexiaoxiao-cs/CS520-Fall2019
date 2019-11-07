package structures;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

 


public class Map {

	public Terrain[][] arr;
	public int dim=50; 
	public int[] targetCoord=new int[2];
	
	public static class Terrain{
		public double falseNegProb;
		public double assignToCellProb;
		public char character;
		public Terrain(double falseNegProb,double assignToCellProb,char character) {
			this.falseNegProb=falseNegProb;
			this.assignToCellProb=assignToCellProb;
			this.character=character;
		}
	}
	public final static Terrain flat=new Terrain(0.1,0.2,'_');	//(0,.2]
	public final static Terrain hilly=new Terrain(0.3,0.5,'^');	//(.2,.5]
	public final static Terrain forest=new Terrain(0.7,0.7,'*');	//(.5,.7]
	public final static Terrain cave=new Terrain(0.9,1,'O');	//(.7,1.0]
	
	
	
	public Map(int dim) {
		targetCoord[0]=(int)(Math.random()*dim);  
		targetCoord[1]=(int)(Math.random()*dim);
		//initialize board:
		this.dim=dim; 
		arr=new Terrain[dim][dim];
		for(int i=0;i<dim;i++) {
			for(int j=0;j<dim;j++) {
				double rand=((double)(Math.random()*1000)+1)/1000;  
				if (rand<flat.assignToCellProb) {
					arr[i][j]= flat; 
				}else if (rand<hilly.assignToCellProb) {
					arr[i][j]= hilly; 
				}else if (rand<forest.assignToCellProb) {
					arr[i][j]= forest; 
				}else {
					arr[i][j]=cave;
				}
			}
		} 
	}

	public boolean query(int x,int y) {
		return x==targetCoord[0]&&y==targetCoord[1];
	} 

	public void show() { 
		for (int i=0;i<dim;i++) {
			System.out.print(i+"\t");
			for (int j=0;j<dim;j++) {
				if (targetCoord[0]==i&&targetCoord[1]==j)
					System.out.print( "["+arr[i][j].character +"]" );
				else
					System.out.print( " "+arr[i][j].character +" " );
			}
			System.out.println();
		} 
		System.out.print("\n\t");
		for (int i=0;i<dim;i++) {
			switch((i+"").length()) {
			case 1: System.out.print(" "+i+" "); break;
			case 2: System.out.print(" "+i); break;
			case 3: System.out.print(i); break;
			}
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