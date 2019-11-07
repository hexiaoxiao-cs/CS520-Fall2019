package app;

import java.util.stream.*;

import structures.*;

public class Driver {
	
	final static int dim=50;	//<--dim=50 takes like 30 seconds.
	static double[][] belief;	
	static Map map;
	public static void main(String[] args) {
		int numQueries=0;
		map=new Map(dim);
		belief=new double[dim][dim];
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
			updateBeliefMatrix(queriedX,queriedY);		//based on exercise 1
			showBeliefs();
			int[] next=searchNext(); 
			queriedX=next[0];
			queriedY= next[1];   //<--set next coord to query here (based on exercise 2)
			
		}
		System.out.println("Number of Queries="+numQueries);
		
		 
	}
	
	private static void updateBeliefMatrix(int x,int y) {
		updateSingle(x,y);
		updateOthers(x,y);
	}
	
	private static void updateSingle(int x,int y) {	//failed x and y
		double b=belief[x][y];
		double falseNegRate=map.arr[x][y].falseNegProb;
		belief[x][y]=(b*falseNegRate)/(b*falseNegRate+(1-b));//formula
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
			}
		});
	}
	
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
	}
	private static double probFoundIfSearched(int x,int y) {//exercise 2
		return belief[x][y]*(map.arr[x][y].falseNegProb+1);
	}
	
	private static void showBeliefs() { 
		for (int i=0;i<dim;i++) {
			System.out.print(i+"\t");
			for (int j=0;j<dim;j++) {
				double b=belief[i][j];
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
		for (int i=0;i<dim;i++) {
			switch((i+"").length()) {
				case 1: System.out.print("  "+i+" "); break;
				case 2: System.out.print(" "+i+" "); break;
				case 3: System.out.print(" "+i); break;
			}
		}
		System.out.println("\n "); 
	}

}
