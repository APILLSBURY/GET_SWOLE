package cs65.dartmouth.get_swole.classes;

public class Exercise {
	private String name;
	private int reps;
	private int weight;
	private int repsGoal;
	private int weightGoal;
	
	//default undefined numbers to -1
	public Exercise(String name) {
		this.name = name;
		reps = -1;
		weight = -1;
		repsGoal = -1;
		weightGoal = -1;
	}
	
	
	//GETTER METHODS
	public String getName() {
		return name;
	}
	
	public int getReps() {
		return reps;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public int getRepsGoal() {
		return repsGoal;
	}
	
	public int getWeightGoal() {
		return weightGoal;
	}
	
	
	//SETTER METHODS
	public void setName(String n) {
		name = n;
	}
	
	public void setReps(int r) {
		reps = r;
	}
	
	public void setWeight(int w) {
		weight = w;
	}
	
	public void setRepsGoal(int rg) {
		repsGoal = rg;
	}
	
	public void setWeightGoal(int wg) {
		weightGoal = wg;
	}
}
