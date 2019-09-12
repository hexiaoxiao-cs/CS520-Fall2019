package structures;

import java.util.*;

public class Grid {
	public String[][] arr;
	public int dim;
	
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
	
	public void occupy(int x,int y) {
		if (isFree(x,y)) {
			arr[x][y]="* ";
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