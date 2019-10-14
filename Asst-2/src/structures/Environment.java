package structures;

public class Environment {
	public Grid board;
	public Environment(int dim, int numMines) {
		board=new Grid('e',dim,numMines);
	}
	public Environment(int dim, double density, boolean useDensity) {
		board=new Grid(dim,density);
		
	}
}
