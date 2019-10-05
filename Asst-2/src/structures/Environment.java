package structures;

public class Environment {
	public Grid board;
	public Environment(int dim, int numMines) {
		board=new Grid('e',dim,numMines);
	}
}
