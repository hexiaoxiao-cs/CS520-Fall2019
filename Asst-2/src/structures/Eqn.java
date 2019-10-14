package structures;

import java.util.*;

public class Eqn implements Comparable<Eqn>{
	public ArrayList<Integer[]> pts = new ArrayList<Integer[]>();
	public int sum = 0;
	public Eqn(ArrayList<Integer[]> p, int s) {
		pts=p;
		sum=s;
	}
	@Override
	public int compareTo(Eqn o) {
		// TODO Auto-generated method stub
		return this.pts.size()-o.pts.size();
	}
}
