package structures;

import java.util.*;

public class Grid {
	public int[][] arr;
	public int dim;
	
	public final String Wall="+ ";//-1
	public final String Free="O ";//0
	public final String Occupied="* ";//1
	public final String Burnt="& ";//2
	public final String Start="S ";//-2
	public final String End="G ";//-3
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
			System.out.print(i+"  ");
			for (int j=0;j<dim;j++) {
				toPrint="\0";
				switch(arr[i][j]) {
					case -3 : toPrint=End; break;
					case -2 : toPrint=Start; break;
					case -1 : toPrint=Wall; break;
					case 0  : toPrint=Free; break;
					case 1  : toPrint=Occupied; break;
					case 2  : toPrint=Burnt; break;
					default : System.err.println("\nPANIC:Printing Grid at ("+i+","+j+") with value "+arr[i][j]+". Program Will EXIT!\n"); System.exit(-2);
				}
				System.out.print( toPrint +" " );
			}
			System.out.println();
		} 
		System.out.print("  ");
		for (int i=0;i<dim;i++) {
			System.out.print(" "+i+" ");
		}
		
	}
	
	public boolean isFree(int x,int y) {
		return arr[x][y]==0;
	}
	
	public void occupy(int x,int y) { //Maybe Not that useful
		if (isFree(x,y)) {
			arr[x][y]=1;
		}else {
			System.err.println("\ncannot be occupied at ("+x+","+y+") because of wall\n");
		} 
	}
	
	/*

	public String moveChessPiece(String from, String to) throws Exception {	//returns the from just in case you need it 
		String save=getChessPieceAt(from);
		setChessPieceAt(to,getChessPieceAt(from));
		setChessPieceAt(from,null);
		return save;
	}
	
	
	public String getChessPieceAt(String coord) throws Exception {
		return board[toArrayRowIndex(coord)][toArrayColIndex(coord)];	
	}
	
	  
	public void setChessPieceAt(String coord, String chessPiece) throws Exception {	//can use this method with pawn promotion.
		board[toArrayRowIndex(coord)][toArrayColIndex(coord)]=chessPiece;
	}
	

	
	 
	public static Board makeCopy(Board b) {
		Board copy=new Board();
		String [][]arr=new String[8][8];
		for (int i=0;i<8;i++) {
			for (int j=0;j<8;j++) 
				arr[i][j]=b.board[i][j];
		}
		copy.board=arr;
		return copy;
	}
	 
	private int toArrayRowIndex(String boardCoord) throws Exception {
		int result = 8-Integer.parseInt(boardCoord.charAt(1)+"");
		if (result<0||result>=8) throw new Exception(boardCoord+" is not a position on the board.");
		else		return result;
	}
	
	 
	private int toArrayColIndex(String boardCoord) throws Exception {
		int result= boardCoord.charAt(0)-'a'; 
		if (result<0||result>=8) throw new Exception(boardCoord+" is not a position on the board.");
		else		return result;
	}*/
}