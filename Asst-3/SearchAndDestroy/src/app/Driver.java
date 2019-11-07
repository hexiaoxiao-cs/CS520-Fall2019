package app;

import java.util.stream.*;

import structures.*;

public class Driver {
	
	final static int dim=10;
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
			updateBeliefMatrix(queriedX,queriedY);	
			showBeliefs();
			//queriedX= ;	<--set next coord to query here
			//queriedY= ;   <--set next coord to query here (or else it will go in an infinite loop)
			
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
