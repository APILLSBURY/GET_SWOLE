package cs65.dartmouth.get_swole.classes;

import java.util.ArrayList;
import java.util.Calendar;

public class WorkoutInstance extends GetSwoleClass {
	private Workout workout;
	private Calendar time;
	private ArrayList<Exercise> exerciseList;
	
	public WorkoutInstance() {
		this(null);
	}
	
	public WorkoutInstance(Workout w) {
		workout = w;
		time = Calendar.getInstance();
		exerciseList = new ArrayList<Exercise>();
		id = -1;
	}
	
	public void addExercise(Exercise e) {
		exerciseList.add(e);
	}
	
	//BYTE ARRAY METHODS
	
	
	
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
