package cs65.dartmouth.get_swole.classes;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import cs65.dartmouth.get_swole.database.DatabaseWrapper;

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
			exercise = (Exercise) db.getEntryById((long) id, Exercise.class);
			exerciseList.add(exercise);
		}
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