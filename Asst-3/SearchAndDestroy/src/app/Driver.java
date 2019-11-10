package app;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.*;

import structures.*;

public class Driver {
	
	final static int dim=50;	
	static double[][] belief;	//X
	static double[][] probFound;		//Y
	static int[] maxProbCoord=new int[2];	//location of max value in X/Y depending on what Rule 1/2
	static Map map; 
	//static int[] maxElocation=new int[2];
	static double maxE=0;
	
	static int rule=2;
	
	
	static boolean exercise4=true;//EXERCISE 4 STUFF
	
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
		if(exercise4) {
			int[] next=neighborCoordToQuery(true,queriedX,queriedY,belief,probFound,1);
			queriedX=next[0];
			queriedY=next[1];
		}
		int numQueries=1;
		 
		while(!map.query(queriedX,queriedY) ) {
			updateBeliefMatrix(queriedX,queriedY);	//<-- updates X->updates Y matrices->updates maxProbCoord.
			//System.out.println("querired "+queriedX+" , "+queriedY);
			//System.out.println("X matrix");showDecimalsMatrix(belief);
			//System.out.println("Y matrix");showDecimalsMatrix(probFound);
			
			
			//EXERCISE 4 STUFF vvvvv
			if (exercise4) {
				int[] next=neighborCoordToQuery(false,queriedX,queriedY,belief.clone(),probFound.clone(),1);
				queriedX=next[0];
				queriedY=next[1];   //<--set next coord to query here (based on max value in Y matrix or X matrix,(rule 1 vs rule 2) )
			}else {
				queriedX=maxProbCoord[0];
				queriedY= maxProbCoord[1];   //<--set next coord to query here (based on max value in Y matrix or X matrix,(rule 1 vs rule 2) )
		
			}
			numQueries++;
		}
		System.out.println("Number of Queries="+numQueries+" used to find [target]. \nTerrain type="+map.arr[map.targetCoord[0]][map.targetCoord[1]]+".");
	 // System.out.println((double)(0.0000000000000006*0.000000000000000007));
	 
	}
	
	//EXERCISE 4 STUFF vvvvvvvvvvvvvv
	private static int[] neighborCoordToQuery(boolean firstTime,int currX,int currY, double[][] Xmatrix, double[][] Ymatrix,int levels) {//current x, y
		double[] info=findE(currX,currY,Xmatrix,Ymatrix,levels);
		double maxE=info[0];
		int x=(int)info[1];
		int y=(int)info[2];
		
		List<int[]> list=map.getLRUD(currX, currY);
		if (firstTime) list=map.getAllCoords();//System.out.println("___");
		for(int[] c:list) {
			info=findE(c[0],c[1],Xmatrix,Ymatrix,levels); 
			if (maxE<info[0]) { 
				maxE=info[0];
				x=(int)info[1];
				y=(int)info[2];
			}
			//System.out.println(+","+y);
		}//System.out.println("___");
		return new int[] {x,y};
	}
	 
	
	private static double[] findE(int iX,int iY,double[][]Xmatrix,double[][]Ymatrix,int levels) {//<-- returns array: { E(i|Oi), iX, iY }
		if (levels==0) { 
			return new double[]{Ymatrix[iX][iY],iX,iY};	//base case.
		}
		else {
			double[][] pretendXmatrix=new double[dim][];//System.arraycopy(src, srcPos, dest, destPos, length);Xmatrix.clone();
			double[][] pretendYmatrix=new double[dim][];
			for(int i=0;i<dim;i++) {
				pretendXmatrix[i]=Xmatrix[i].clone();
				pretendYmatrix[i]=Ymatrix[i].clone();
				
			}
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
				sum+=findE(n[0],n[1],pretendXmatrix,pretendYmatrix, levels-1)[0];
			}
			//formula exercise 4:
			double d=Ymatrix[iX][iY]+.25*((1+(map.arr[iX][iY].falseNegProb-1))*Xmatrix[iX][iY])*sum;
			return new double[]{d,iX,iY};
		}
			
	}
	
	
	 

	private static void updateBeliefMatrix(int x,int y) {
		updateSingle(x,y);
		updateOthers(x,y);
	}

	private static void updateSingle(int x,int y) {	//update X.
		double b=belief[x][y];
		double falseNegRate=map.arr[x][y].falseNegProb;
		 
		belief[x][y]=(b*falseNegRate)/(b*falseNegRate+(1-b));//formula
		//System.out.println(belief[x][y]);
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
