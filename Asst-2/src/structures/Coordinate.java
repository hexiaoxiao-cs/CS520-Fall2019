package structures;

public class Coordinate implements Comparable<Coordinate>{
	public int x;
	public int y;
	//public int assign=-1;
	public int occur;
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
	public boolean equals(Object o) {
        if (o == this) { 
            return true; 
        } 
  
        /* Check if o is an instance of Complex or not 
          "null instanceof [type]" also returns false */
        if (!(o instanceof Coordinate)) { 
            return false; 
        } 
        Coordinate c = (Coordinate) o;
		if(c.x==this.x&&c.y==this.y) {return true;}
		else {return false;}
	}
	public boolean equals(int[] o) {
		if(o[0]==this.x&&o[1]==this.y) {
			return true;
		}
		else {return false;}
	}
}
