package app;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.*;

import structures.*;

public class Driver {
	
	final static int dim=10;	
	static double[][] belief;	//X
	static double[][] probFound;		//Y
	static int[] maxProbCoord=new int[2];	//location of max value in X/Y depending on what Rule 1/2
	static Map map; 
	
	static int rule=1;
	
	
	static boolean exercise4=false;//EXERCISE 4 STUFF
	
	public static void main(String[] args) {int sum1=0;int sum2=0;
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
		
		while(!map.query(queriedX,queriedY)){
			numQueries++;
			updateBeliefMatrix(queriedX,queriedY);	//<-- updates X->updates Y matrices->updates maxProbCoord.
			//System.out.println("querired "+queriedX+" , "+queriedY);
			//System.out.println("X matrix");showDecimalsMatrix(belief);
			//System.out.println("Y matrix");showDecimalsMatrix(probFound);
			
			/*
			//EXERCISE 4 STUFF vvvvv
			if (exercise4) {
				int[] nextCoord=neighborCoordToQuery(queriedX,queriedY);
				queriedX=nextCoord[0];
				queriedY=nextCoord[1]; 
			}//^^^^^^^^^^^^^^^^^^^^^
			
			else {*/
				queriedX=maxProbCoord[0];
				queriedY= maxProbCoord[1];   //<--set next coord to query here (based on max value in Y matrix or X matrix,(rule 1 vs rule 2) )
		
			//}
		}
		System.out.println("Number of Queries="+numQueries+" used to find [target]. \nTerrain type="+map.arr[map.targetCoord[0]][map.targetCoord[1]]+".");
	  
		
	}
	
	//EXERCISE 4 STUFF vvvvvvvvvvvvvv
	private static int[] neighborCoordToQuery(int currX,int currY, double[][] Xmatrix, double[][] Ymatrix,int levels) {//current x, y
		//double[][] E=new double[dim][dim];	//<--E (utility) matrix
		//int[] ret=new int[2];
		//int[] maxElocation=new int[2];
		
		
		findE(iX,iY,Xmatrix,Ymatrix,levels);//<--returns E(i|Oi) 
		
		
		//return ret;
	}
	
	private static double findE(int iX,int iY,double[][]Xmatrix,double[][]Ymatrix,int levels) {//<-- returns E(i|Oi)
		if (levels==0) return Ymatrix[iX][iY];	//base case.
		else {
			double[][] pretendXmatrix=Xmatrix.clone();
			double[][] pretendYmatrix=Ymatrix.clone();
			//Change matrices to assume that we failed: 
			map.getAllCoords().stream().forEach(c->{ 
				int cx=c[0];
				int cy=c[1]; 
				double bi=pretendXmatrix[iX][iY];
				double falseNegRate=map.arr[cx][cy].falseNegProb;
				double bj=pretendXmatrix[cx][cy];
				//Xmatrix:
				if (cx==iX&&cy==iY) {//update single pretend failed.
					pretendXmatrix[cx][cy]=(bi*falseNegRate)/(bi*falseNegRate+(1-bi)); 
				}else {//update others.
					pretendXmatrix[cx][cy]=bj/(bi*falseNegRate+(1-bi)); 
				} 
				//Ymatrix:
				pretendYmatrix[cx][cy]=(1-map.arr[cx][cy].falseNegProb)*pretendXmatrix[cx][cy];
			});  
			//Stuff after sigma:
			double sum=0;
			for(int[] n:map.getLRUD(iX, iY)) {
				sum+=findE(n[0],n[1],pretendXmatrix,pretendYmatrix, levels-1);
			}
			//formula exercise 4:
			return Ymatrix[iX][iY]+.25*((1+(map.arr[iX][iY].falseNegProb-1))*Xmatrix[iX][iY])*sum;
				
		}
			
	}
	/*
	private static double neighborSums(int x,int y,int levels) {
		double sumOverNs=0;
		for(int[] n:map.getNeighbors(x, y)) {
			int nX= n[0];
			int nY= n[1];
			double sumOverN1s=0;
			for(int[] n1:map.getNeighbors(nX, nY)) {
				sumOverN1s+=findE(n1[0],n1[1],levels-1);
			}
			sumOverNs+=probFound[nX][nY]+.25*(_____ //should be  P(!F(n)|Ot^!F(i) 
																	)*sumOverN1s;
		}
		return sumOverNs;
	}
	//^^^^^^^^^^^^^^^^^^^^^*/

	private static void updateBeliefMatrix(int x,int y) {
		updateSingle(x,y);
		updateOthers(x,y);
	}

	private static void updateSingle(int x,int y) {	//update X.
		double b=belief[x][y];
		double falseNegRate=map.arr[x][y].falseNegProb;
		belief[x][y]=(b*falseNegRate)/(b*falseNegRate+(1-b));//formula
		updateProbFoundMatrix(x,y);	//update Y value, update max.
	}
	private static void updateOthers(int x, int y) {	//update X.
		map.getAllCoords().stream().forEach(c->{ 
			int cx=c[0];
			int cy=c[1];
			if (cx!=x||cy!=y) {
				double bj=belief[cx][cy];
				double bi=belief[x][y];
				double falseNegRate=map.arr[cx][cy].falseNegProb;
				belief[cx][cy]=bj/(bi*falseNegRate+(1-bi));//formula
				updateProbFoundMatrix(cx,cy);	//update Y, update max.
			}
		});
	}
	
	private static void updateProbFoundMatrix(int x, int y) {
		probFound[x][y]=(1-map.arr[x][y].falseNegProb)*belief[x][y];
		updateMax(x,y);
	}
	
	private static void updateMax(int x, int y) {
		if ((rule==1 &&probFound[maxProbCoord[0]][maxProbCoord[1]]<probFound[x][y])||
			
		  (rule==2 &&belief[maxProbCoord[0]][maxProbCoord[1]]<belief[x][y])){
			maxProbCoord[0]=x;
			maxProbCoord[1]=y;
		}
	}
	
	
	
	private static void showDecimalsMatrix(double[][] matrix) { 
		for (int i=0;i<matrix.length;i++) {
			System.out.print(i+"\t");
			for (int j=0;j<matrix.length;j++) { 
				System.out.print("\t"+new DecimalFormat("#.#####").format(matrix[i][j]).substring(1) );
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
