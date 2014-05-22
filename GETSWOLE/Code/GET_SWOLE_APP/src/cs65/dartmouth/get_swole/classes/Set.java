package cs65.dartmouth.get_swole.classes;

public class Set {
	private int reps;
	private int weight;
	
	public Set() {
		
	}
	public Set(int reps, int weight) {
		this.reps = reps;
		this.weight = weight;
	}
	
	
	//GETTER METHODS
	public int getReps() {
		return reps;
	}
	
	public int getWeight() {
		return weight;
	}
	
	
	//SETTER METHODS
	public void setReps(int r) {
		reps = r;
	}
	
	public void setWeight(int w) {
		weight = w;
	}
	
	@Override
	public String toString() {
		return reps + "x" + weight;
	}
	
	@Override
	public boolean equals(Object o) {
		return o.toString().equals(toString());
	}
}
