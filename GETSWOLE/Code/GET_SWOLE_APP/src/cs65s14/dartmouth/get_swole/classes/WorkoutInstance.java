package cs65s14.dartmouth.get_swole.classes;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import cs65s14.dartmouth.get_swole.CalendarUtility;
import cs65s14.dartmouth.get_swole.database.DatabaseWrapper;

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
		if (workout != null) {
			setName();
		}
		else {
			name = "";
		}
	}
	
	public void addExercise(Exercise e) {
		exerciseList.add(e);
	}
	
	//BYTE ARRAY METHODS
	public byte[] getExerciseListByteArray() {
		int[] intArray = new int[exerciseList.size()];
		for (int i = 0; i < exerciseList.size(); i++) {
			Exercise exercise = exerciseList.get(i);
			intArray[i] = (int) exercise.getId();
		}
		ByteBuffer byteBuffer = ByteBuffer.allocate(intArray.length * Integer.SIZE);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		intBuffer.put(intArray);
		return byteBuffer.array();
	}
	
	public void setExerciseListFromByteArray(byte[] byteArray, Context c) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		int[] intArray = new int[byteArray.length/Integer.SIZE];
		intBuffer.get(intArray);
		Exercise exercise;
		exerciseList.clear();
		DatabaseWrapper db = new DatabaseWrapper(c);
		db.open();
		for (int i = 0; i < intArray.length; i++) {
			exercise = (Exercise) db.getEntryById((long) intArray[i], Exercise.class);
			exerciseList.add(exercise);
		}
		db.close();
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
	
	public void setName() {
		name = workout.getName() + " completed at " + CalendarUtility.HOURS_MINUTES_DATE_FORMAT.format(time.getTime());
	}
	
	public void setExerciseList(ArrayList<Exercise> el) {
		exerciseList = el;
	}
	
	public String toString() {
		return workout.getName() + " on " + time.get(Calendar.MONTH) + "/" + time.get(Calendar.DAY_OF_MONTH);
	}
}
