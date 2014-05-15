package cs65.dartmouth.get_swole;

import java.util.ArrayList;
import java.util.Calendar;

public class WorkoutInstance {
	private Workout workout;
	private Calendar time;
	private ArrayList<Exercise> exerciseList;
	
	
	public WorkoutInstance(Workout w) {
		workout = w;
		time = Calendar.getInstance();
		exerciseList = new ArrayList<Exercise>();
	}
	
	public void addExercise(Exercise e) {
		exerciseList.add(e);
	}
	
	//GETTER METHODS
	public Workout getWorkout() {
		return workout;
	}
	
	public Calendar getTime() {
		return time;
	}
	
	public ArrayList<Exercise> getExerciseList() {
		return exerciseList;
	}
	
	
	//SETTER METHODS
	public void setWorkout(Workout w) {
		workout = w;
	}
	
	public void setTime(Calendar t) {
		time = t;
	}
	
	public void setExerciseList(ArrayList<Exercise> el) {
		exerciseList = el;
	}
}
