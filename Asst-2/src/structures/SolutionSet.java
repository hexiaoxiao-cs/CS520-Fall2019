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
	public ArrayList<int[]> solns= new ArrayList<int[]>();
	public int[][] mines; // for calculating probability
	//public int[] soln_filled;
	public SolutionSet(ArrayList<Eqn> eqns) {
		equations=eqns;
		min=0;
		max=20;
		for(Eqn c : equations) {
			for(Integer[] coord: c.pts) {
				if(vars.contains(new Coordinate(coord[0],coord[1]))) {
					Coordinate tmp= vars.remove(vars.indexOf(new Coordinate(coord[0],coord[1])));
					tmp.occur++;
					vars.add(tmp);
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
		Collections.reverse(vars);
		//mines=new int[min+1][vars.size()+1];
		
	}
	public boolean isAbleSatisfied(int[] assignments) {
		//soln_filled=assignments.clone();
		
		for(Eqn c : equations) {
			int tmp=c.sum;
			int counts=0;
			int not_filled=0;
			for(Integer[] Coord : c.pts) {
				if(assignments[vars.indexOf(new Coordinate(Coord[0],Coord[1]))]==-1) {not_filled++;continue;}
				tmp-=assignments[vars.indexOf(new Coordinate(Coord[0],Coord[1]))];
				//System.out.println(tmp);
				if(assignments[vars.indexOf(new Coordinate(Coord[0],Coord[1]))]==0) {counts++;}
				if(tmp<0) {//System.out.println("Equation Failed on "+equations.indexOf(c));
				return false;}
			}
			if(not_filled==0&&tmp!=0) {return false;}//FOr this constraint can not be satisfied by the
			//given condition anyway
			if(tmp==not_filled|| tmp==0) {
				if(tmp!=0) {//set all stuff to 1, Constraint Enforced
					//System.out.println("Constraint Enforced to ones!");
					for(Integer[] Coord : c.pts) {
						if(assignments[vars.indexOf(new Coordinate(Coord[0],Coord[1]))]==-1) {
						assignments[vars.indexOf(new Coordinate(Coord[0],Coord[1]))]=1;}
						//if(assignments[vars.indexOf(Coord)]==0) {counts++;}
					}
					
				}
				else { //set all stuff to 0, Constraint Enforced
					//System.out.println("Constraint Enforced to zero!");
					for(Integer[] Coord : c.pts) {
						if(assignments[vars.indexOf(new Coordinate(Coord[0],Coord[1]))]==-1) {assignments[vars.indexOf(new Coordinate(Coord[0],Coord[1]))]=0;}
						//if(assignments[vars.indexOf(Coord)]==0) {counts++;}
					}
				}
			}
		}
		return true;
	}
	public void find_soln_set() {
		int[] soln = new int[vars.size()];
		//f(vars.size()>50) {return ;} //Not Checking any solution space > 2^25
		Arrays.fill(soln, -1);//Initial Set to -1
		find_soln_set_recursive(soln,0);
		
	}
	public void find_soln_set_recursive(int[] soln,int index) {
		int[] curr_soln=soln.clone();
		if(index==soln.length) {solns.add(curr_soln);return;}//satisfied solns
		if(curr_soln[index]==-1) {
			curr_soln[index]++;
		}
		else {
			if(isAbleSatisfied(curr_soln)==true) {
				if(index>=max) {solns.add(soln);}else {
				find_soln_set_recursive(curr_soln,index+1); return;}}// this item is constrained
			//no need for a second search
		}
		if(isAbleSatisfied(curr_soln)==true) {
			
		find_soln_set_recursive(curr_soln,index+1);}
		curr_soln=soln.clone();
		if(curr_soln[index]==-1) {
			curr_soln[index]+=2;
		}
		if(isAbleSatisfied(curr_soln)==true) {
			if(index>=max) {solns.add(soln);}else {
		find_soln_set_recursive(curr_soln,index+1);}}
		return;
	}
	
	public Integer[] getNextBestGuess() {
		int[] sum = new int[vars.size()];
		int small_num;
		Integer[] smallest= new Integer[2];
		for(int[] Soln: solns) {
			for(int i = 0; i<vars.size();i++) {
				if(Soln[i]!=-1) {
					sum[i]+=Soln[i];
				}
			}
		}
		if(sum.length==0) {return null;}
		small_num=sum[0];
		smallest[0]=vars.get(0).x;
		smallest[1]=vars.get(0).y;
		if(vars.size()<=max) {
			for(int i = 1; i < vars.size();i++) {
				if(sum[i]<small_num) {
					small_num=sum[i];
					smallest[0]=vars.get(i).x;
					smallest[1]=vars.get(i).y;
				}
			}
		}
		else {
			for(int i = 1; i <= max;i++) {
				if(sum[i]<small_num) {
					small_num=sum[i];
					smallest[0]=vars.get(i).x;
					smallest[1]=vars.get(i).y;
				}
			}
		}
		return smallest;
	}
	public void dump() {
		int[] sum = new int[vars.size()];
		if(solns.size()==0) {//System.out.println("Cannot Find Any Feasible Solns.\nGoing to Random Guess!\n");
		return;
		}
		for(Coordinate var:vars) {
			//System.out.println("Var:"+var.x+","+var.y+" has occurance "+var.occur);
		}
		for(int[] Soln: solns) {
			for(int i = 0; i<vars.size();i++) {
				sum[i]+=Soln[i];
			}
		}
		for (int i = 0; i <vars.size();i++) {
			//System.out.println("Variable: "+vars.get(i).x+","+vars.get(i).y+" can be a mine of prob "+(double)((double)sum[i]/(double)solns.size()));
		}
		
	}
	
}
