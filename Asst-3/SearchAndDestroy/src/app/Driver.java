package app;

import java.text.DecimalFormat;
import java.util.stream.*;

import structures.*;

public class Driver {
	
	final static int dim=50;	 
	static double[][] belief;	//X
	static double[][] probFound;		//Y
	static int[] maxProbCoord=new int[2];	//location of max value in Y
	static Map map;
	public static void main(String[] args) {
		
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
		int numQueries=1;
		while(!map.query(queriedX,queriedY) ){
			numQueries++;
			updateBeliefMatrix(queriedX,queriedY);	//updates X and Y matrices.
			System.out.println("querired "+queriedX+" , "+queriedY);
			System.out.println("X matrix");showDecimalsMatrix(belief);
			System.out.println("Y matrix");showDecimalsMatrix(probFound);
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
			if (cx!=x||cy!=y) {
				double bj=belief[cx][cy];
				double bi=belief[x][y];
				double falseNegRate=map.arr[cx][cy].falseNegProb;
				belief[cx][cy]=bj/(bi*falseNegRate+(1-bi));//formula
				updateProbFoundMatrix(cx,cy);
				//System.out.println("update "+cx+","+cy);
			}
		});
	}
	 
	private static double probFoundIfSearched(int x,int y) {//exercise 2
		//System.out.println((1-map.arr[x][y].falseNegProb)*belief[x][y]+" for "+x+" ,"+y);
		return (1-map.arr[x][y].falseNegProb)*belief[x][y];
	}
	
	private static void showDecimalsMatrix(double[][] matrix) { 
		
		for (int i=0;i<matrix.length;i++) {
			System.out.print(i+"\t");
			for (int j=0;j<matrix.length;j++) {
				double b=matrix[i][j];
				if (b==0||b==1) { System.out.print("\t"+(int)b); continue;}
				 
				System.out.print("\t"+new DecimalFormat("#.####").format(b).substring(1));
			}
			System.out.println();
		} 
		System.out.print("\n\t");
		for (int i=0;i<matrix.length;i++) {
			System.out.print("\t  "+i); 
		}
		System.out.println("\n "); 
	}

}
