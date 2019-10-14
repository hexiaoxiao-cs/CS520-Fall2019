package structures;

public class Coordinate implements Comparable<Coordinate>{
	int x;
	int y;
	int assign=-1;
	int occur;
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
		this.occur=0;
	}
	@Override
	public int compareTo(Coordinate o) {
		// TODO Auto-generated method stub
		return this.occur-o.occur;
	}
	public boolean equals(Coordinate o) {
		if(o.x==this.x&&o.y==this.y) {return true;}
		else {return false;}
	}
	public boolean equals(int[] o) {
		if(o[0]==this.x&&o[1]==this.y) {
			return true;
		}
		else {return false;}
	}
}
