package app;

import structures.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Minesweeper{

	public static void main(String args[]) {
		int dim=5;
		int numMines=3;
		Environment e=new Environment(dim,numMines);
		Agent a=new Agent(dim);
		e.board.show();
		
		
		//Start:
		
		/*
		 * 
		while(a.board.numMines<numMines) {	//while we havent found all the mines
			int[] queryCoord=a.assessKB();
			if (queryCoord==null) {
				//queryCoord[0]=random;
				//queryCoord[1]=random;
			}
			a.query(e, queryCoord[0], queryCoord[1]);
			 
			a.board.show();
		
		}
		
		*/


		 
		 
	}


}
