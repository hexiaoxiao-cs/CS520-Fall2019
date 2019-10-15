package structures;

import java.util.*;

public class Eqn implements Comparable<Eqn>{
	public ArrayList<Coordinate> pts = new ArrayList<Coordinate>();
	public int sum = 0;
	public Eqn(ArrayList<Coordinate> p, int s) {
		pts=p;
		sum=s;
	}
	@Override
	public int compareTo(Eqn o) {
		// TODO Auto-generated method stub
		return this.pts.size()-o.pts.size();
	}
}
