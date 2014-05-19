package cs65.dartmouth.get_swole.classes;

import java.util.ArrayList;
import java.util.Calendar;

public class WorkoutInstance extends GetSwoleClass {
	private Workout workout;
	private Calendar time;
	private ArrayList<Exercise> exerciseList;
	private long id;
	
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
	
	public long getId() {
		return id;
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
	
	public void setId(long i) {
		id = i;
	}
}
