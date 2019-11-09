package app;

import java.util.stream.*;

import structures.*;

public class Driver {
	
	final static int dim=50;	//<--dim=50 takes like 30 seconds.
	static double[][] belief;	//X
	static double[][] probFound;		//Y
	static int[] maxProbCoord=new int[2];	//location of max value in Y
	static Map map;
	public static void main(String[] args) {
		int numQueries=0;
		map=new Map(dim);
		belief=new double[dim][dim];
		probFound=new double[dim][dim];
		//Initialize:
		for(int i=0;i<dim;i++) {
			for(int j=0;j<dim;j++) {
				belief[i][j]=1.0/(dim*dim);
			}
		}
		map.show();
		int queriedX=(int)(Math.random()*dim);
		int queriedY=(int)(Math.random()*dim);
		while(!map.query(queriedX,queriedY) ){
			numQueries++;
			updateBeliefMatrix(queriedX,queriedY);	//updates X and Y matrices.
			//System.out.println("X matrix");showDecimalsMatrix(belief);
			//System.out.println("Y matrix");showDecimalsMatrix(probFound);
			queriedX=maxProbCoord[0];
			queriedY= maxProbCoord[1];   //<--set next coord to query here (based on max value in Y matrix)
			
		}
		System.out.println("Number of Queries="+numQueries+" used to find [target]. \nTerrain type="+map.arr[map.targetCoord[0]][map.targetCoord[1]]+".");
		//map.show();
		
		 
	}
	
	private static void updateBeliefMatrix(int x,int y) {
		updateSingle(x,y);
		updateOthers(x,y);
	}
	
	private static void updateSingle(int x,int y) {	//failed x and y
		double b=belief[x][y];
		double falseNegRate=map.arr[x][y].falseNegProb;
		belief[x][y]=(b*falseNegRate)/(b*falseNegRate+(1-b));//formula
		updateProbFoundMatrix(x,y);
	}
	private static void updateProbFoundMatrix(int x, int y) {
		probFound[x][y]=probFoundIfSearched(x,y);
		if(probFoundIfSearched(maxProbCoord[0],maxProbCoord[1])<probFound[x][y]){
			maxProbCoord[0]=x;
			maxProbCoord[1]=y;
		} 
	}
	
	private static void updateOthers(int x, int y) {	//failed x and y
		map.getAllCoords().stream().forEach(c->{ 
			int cx=c[0];
			int cy=c[1];
			if (cx!=x&&cy!=y) {
				double bj=belief[cx][cy];
				double bi=belief[x][y];
				double falseNegRate=map.arr[cx][cy].falseNegProb;
				belief[cx][cy]=bj/(bi*falseNegRate+(1-bi));//formula
				updateProbFoundMatrix(cx,cy);
			}
		});
	}
	/*
	private static int[] searchNext() {
		double max=0;
		int[]coord=new int[2];
		for(int i=0;i<dim;i++) {
			for(int j=0;j<dim;j++) {
				double p=probFoundIfSearched(i,j);
				if(max<p){
					max=p;
					coord[0]=i;
					coord[1]=j;
				}
			}
		}
		return coord;
	}*/
	private static double probFoundIfSearched(int x,int y) {//exercise 2
		return (1-map.arr[x][y].falseNegProb)*belief[x][y];
	}
	
	private static void showDecimalsMatrix(double[][] matrix) { 
		
		for (int i=0;i<matrix.length;i++) {
			System.out.print(i+"\t");
			for (int j=0;j<matrix.length;j++) {
				double b=matrix[i][j];
				if (b==0||b==1) { System.out.print("   "+(int)b); continue;}
				String s=(""+b); 
				s=s.substring(1,Math.min(s.length(),5));
				switch (s.length()) {
					case 2: System.out.print( "  "+s ); break;
					case 3: System.out.print( " "+s ); break;
					case 4: System.out.print( s ); break;
				} 
			}
			System.out.println();
		} 
		System.out.print("\n\t");
		for (int i=0;i<matrix.length;i++) {
			switch((i+"").length()) {
				case 1: System.out.print("  "+i+" "); break;
				case 2: System.out.print(" "+i+" "); break;
				case 3: System.out.print(" "+i); break;
			}
		}
		System.out.println("\n "); 
	}

}
