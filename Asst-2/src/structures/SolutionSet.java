package structures;

import java.util.*;



public class SolutionSet {
	public ArrayList<Eqn> equations; // Our Constraint
	public int min,max; //Number of constraints
	public int solved_largest_vars=0;
	public int solved_largest_eqns=0;
	public int solved_largest_solns=0;
	public ArrayList<Coordinate> vars=new ArrayList<Coordinate>();//list of varibles
	//public ArrayList<Eqn[]> point_eqns=new ArrayList<Eqn[]>();
	public int[] soln_for_num_of_mines;
	public int[] next_probing=new int[2];
	public int[] soln;
	public int[][] mine;
	public SolutionSet(ArrayList<Eqn> eqns) {
		equations=eqns;
		min=0;
		max=0;
		for(Eqn c : equations) {
			for(int[] coord: c.pts) {
				if(vars.contains(coord)) {
					vars.get(vars.indexOf(new Coordinate(coord[0],coord[1]))).occur++;
				}
				else {//not in list
					vars.add(new Coordinate(coord[0],coord[1]));
				}
				
			}
			min+=c.sum;
			
		}
		//Add all stuff to the list
		//Then sort it
		Collections.sort(vars);
		soln=new int[min+1];
		mine=new int[min+1][vars.size()+1];
		
	}
	
	
}
