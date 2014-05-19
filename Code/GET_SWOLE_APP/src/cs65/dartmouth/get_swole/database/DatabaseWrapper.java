package cs65.dartmouth.get_swole.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import cs65.dartmouth.get_swole.classes.*;
import cs65.dartmouth.get_swole.*;

public class DatabaseWrapper {

	// Database fields
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	
	private static final String[] WORKOUT_COLUMNS = {
									DatabaseHelper.WORKOUT_ID, 
									DatabaseHelper.WORKOUT_NAME, 
									DatabaseHelper.WORKOUT_EXERCISE_LIST, 
									DatabaseHelper.WORKOUT_SCHEDULED_DATES, 
									DatabaseHelper.WORKOUT_START_DATE, 
									DatabaseHelper.WORKOUT_FREQUENCY_LIST, 
									DatabaseHelper.WORKOUT_NOTES };
	
	private static final String[] WORKOUT_INSTANCE_COLUMNS = {
									DatabaseHelper.WORKOUT_INSTANCE_ID, 
									DatabaseHelper.WORKOUT_INSTANCE_WORKOUT, 
									DatabaseHelper.WORKOUT_INSTANCE_EXERCISE_LIST, 
									DatabaseHelper.WORKOUT_INSTANCE_TIME };

	private static final String[] EXERCISE_COLUMNS = {
									DatabaseHelper.EXERCISE_ID, 
									DatabaseHelper.EXERCISE_NAME, 
									DatabaseHelper.EXERCISE_REPS, 
									DatabaseHelper.EXERCISE_WEIGHT, 
									DatabaseHelper.EXERCISE_REPS_GOAL, 
									DatabaseHelper.EXERCISE_WEIGHT_GOAL, 
									DatabaseHelper.EXERCISE_REST, 
									DatabaseHelper.EXERCISE_NOTES };

	private static final String[] FREQUENCY_COLUMNS = {
									DatabaseHelper.FREQUENCY_ID, 
									DatabaseHelper.FREQUENCY_DAY, 
									DatabaseHelper.FREQUENCY_START_DATE, 
									DatabaseHelper.FREQUENCY_END_DATE };
	
	private static final  int WORKOUT_NUM = 0;
	private static final  int WORKOUT_INSTANCE_NUM = 1;
	private static final  int EXERCISE_NUM = 2;
	private static final  int FREQUENCY_NUM = 3;
    
    private static final String[] ID_ROW_KEYS = {
    								DatabaseHelper.WORKOUT_ID, 
    								DatabaseHelper.WORKOUT_INSTANCE_ID, 
    								DatabaseHelper.EXERCISE_ID, 
    								DatabaseHelper.FREQUENCY_ID};
	
	private static final String[][] ALL_COLUMNS_OF_ALL_TABLES = {
											WORKOUT_COLUMNS, 
											WORKOUT_INSTANCE_COLUMNS, 
											EXERCISE_COLUMNS, 
											FREQUENCY_COLUMNS};
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

	public DatabaseWrapper(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	
	//create a workout entry
	public Workout createEntry(Workout workout) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.WORKOUT_NAME, workout.getName());
		values.put(DatabaseHelper.WORKOUT_EXERCISE_LIST, workout.getExerciseListByteArray());
		values.put(DatabaseHelper.WORKOUT_SCHEDULED_DATES, workout.getScheduledDatesByteArray());
		values.put(DatabaseHelper.WORKOUT_START_DATE, DATE_FORMAT.format(workout.getStartDate()));
		values.put(DatabaseHelper.WORKOUT_FREQUENCY_LIST, workout.getFrequencyListByteArray());
		values.put(DatabaseHelper.WORKOUT_NOTES, workout.getNotes());
		
		if (database == null) {
			open();
		}
		
		long insertId = database.insert(DatabaseHelper.TABLE_NAME_WORKOUT, null, values); //ILLEGAL STATE EXCEPTION
		workout.setId(insertId); //entry.setId(cursor.getLong(0))
		Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_WORKOUT,
				WORKOUT_COLUMNS, DatabaseHelper.WORKOUT_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Workout newWorkout = cursorToEntry(cursor, new Workout());

		cursor.close();
		return newWorkout;
	}

	
	//create a workout instance entry
	public WorkoutInstance createEntry(WorkoutInstance workoutInstance) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.WORKOUT_INSTANCE_WORKOUT, workoutInstance.getWorkout().getId());
		values.put(DatabaseHelper.WORKOUT_INSTANCE_EXERCISE_LIST, workoutInstance.getExerciseListByteArray());
		values.put(DatabaseHelper.WORKOUT_INSTANCE_TIME, DATE_FORMAT.format(workoutInstance.getTime()));
		
		if (database == null) {
			open();
		}
		
		long insertId = database.insert(DatabaseHelper.TABLE_NAME_WORKOUT_INSTANCE, null, values); //ILLEGAL STATE EXCEPTION
		workoutInstance.setId(insertId); //entry.setId(cursor.getLong(0))
		Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_WORKOUT_INSTANCE,
				WORKOUT_INSTANCE_COLUMNS, DatabaseHelper.WORKOUT_INSTANCE_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		WorkoutInstance newWorkoutInstance = cursorToEntry(cursor, new WorkoutInstance());

		cursor.close();
		return newWorkoutInstance;
	}
	
	//create an exercise entry
	public Exercise createEntry(Exercise exercise) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.EXERCISE_NAME, exercise.getName());
		values.put(DatabaseHelper.EXERCISE_REPS, exercise.getReps());
		values.put(DatabaseHelper.EXERCISE_WEIGHT, exercise.getWeight());
		values.put(DatabaseHelper.EXERCISE_REPS_GOAL, exercise.getRepsGoal());
		values.put(DatabaseHelper.EXERCISE_WEIGHT_GOAL, exercise.getWeightGoal());
		values.put(DatabaseHelper.EXERCISE_REST, exercise.getRest());
		values.put(DatabaseHelper.EXERCISE_NOTES, exercise.getNotes());
		
		if (database == null) {
			open();
		}
		
		long insertId = database.insert(DatabaseHelper.TABLE_NAME_EXERCISE, null, values); //ILLEGAL STATE EXCEPTION
		exercise.setId(insertId); //entry.setId(cursor.getLong(0))
		Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_EXERCISE,
				EXERCISE_COLUMNS, DatabaseHelper.EXERCISE_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Exercise newExercise = cursorToEntry(cursor, new Exercise());

		cursor.close();
		return newExercise;
	}

	//create a frequency entry
	public Frequency createEntry(Frequency frequency) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FREQUENCY_DAY, frequency.getDay());
		values.put(DatabaseHelper.FREQUENCY_START_DATE, DATE_FORMAT.format(frequency.getStartDate()));
		values.put(DatabaseHelper.FREQUENCY_END_DATE, DATE_FORMAT.format(frequency.getEndDate()));
		
		if (database == null) {
			open();
		}
		
		long insertId = database.insert(DatabaseHelper.TABLE_NAME_FREQUENCY, null, values); //ILLEGAL STATE EXCEPTION
		frequency.setId(insertId); //entry.setId(cursor.getLong(0))
		Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_FREQUENCY,
				FREQUENCY_COLUMNS, DatabaseHelper.FREQUENCY_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Frequency newFrequency = cursorToEntry(cursor, new Frequency());

		cursor.close();
		return newFrequency;
	}

	public void deleteEntry (Workout workout) {
		deleteEntry(workout.getId(), Workout.class);
	}

	public void deleteEntry (WorkoutInstance workoutInstance) {
		deleteEntry(workoutInstance.getId(), WorkoutInstance.class);
	}

	public void deleteEntry (Exercise exercise) {
		deleteEntry(exercise.getId(), Exercise.class);
	}

	public void deleteEntry (Frequency frequency) {
		deleteEntry(frequency.getId(), Frequency.class);
	}
	
	public void deleteEntry (Long id, Class<? extends GetSwoleClass> c) {
		int index = getClassIndex(c);
		String tableName = DatabaseHelper.TABLE_NAMES[index];
		String idRowKey = ID_ROW_KEYS[index];
		Log.d(Globals.TAG, "delete entry = " + id);
		Log.d(Globals.TAG, "Entry deleted from table " + tableName + " with id: " + id);
		database.delete(tableName, idRowKey + " = " + id, null);
	}
	
	public void deleteAllEntries(Class<? extends GetSwoleClass> c) {
		String tableName = DatabaseHelper.TABLE_NAMES[getClassIndex(c)];
		Log.d(Globals.TAG, "Deleting all entries from table " + tableName);
		database.delete(tableName, null, null);
	}
	
	public List getAllEntries(Class<? extends GetSwoleClass> c) {
		int index = getClassIndex(c);
		String tableName =  DatabaseHelper.TABLE_NAMES[index];
		String[] allColumns = ALL_COLUMNS_OF_ALL_TABLES[index];
		List<GetSwoleClass> entries = new ArrayList<GetSwoleClass>();

		Cursor cursor = database.query(tableName,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			entries.add(getEntryFromClass(cursor, c));
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return entries;
	}
	
	public GetSwoleClass getEntryById(long id, Class<? extends GetSwoleClass> c) {
		int index = getClassIndex(c);
		Cursor cursor = database.query(DatabaseHelper.TABLE_NAMES[index],
				ALL_COLUMNS_OF_ALL_TABLES[index], null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			if (id == cursor.getLong(cursor.getColumnIndex(ID_ROW_KEYS[index]))) {
				GetSwoleClass entry = getEntryFromClass(cursor, c);
				cursor.close();
				return entry;
			}
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return null;
	}

	private GetSwoleClass getEntryFromClass(Cursor cursor, Class<? extends GetSwoleClass> c) {
		int index = getClassIndex(c);
		switch (index) {
		case WORKOUT_NUM:
			return cursorToEntry(cursor, new Workout());
		case WORKOUT_INSTANCE_NUM:
			return cursorToEntry(cursor, new WorkoutInstance());
		case EXERCISE_NUM:
			return cursorToEntry(cursor, new Exercise());
		case FREQUENCY_NUM:
			return cursorToEntry(cursor, new Frequency());
		default:
			Log.e(Globals.TAG, "Something is very, very wrong in getEntryFromClass.  Class is " + c.getName());
			return null;
		}
	}
	
	private Workout cursorToEntry(Cursor cursor, Workout workout) {
		workout.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.WORKOUT_ID)));
		workout.setInputType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_INPUT_TYPE)));
		workout.setActivityType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_ACTIVITY_TYPE)));
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_DATE_TIME))));
		}
		catch (Exception pe) {
			Log.d(TAG, "Parse Exception converting string "
					+ cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_DATE_TIME)) 
					+ " to date format: " + pe.getMessage());
		}
		workout.setDateTime(c);
		workout.setDuration(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_DURATION)));
		workout.setDistance(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_DISTANCE)));
		workout.setAvgSpeed(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_AVG_SPEED)));
		workout.setCalories(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_CALORIES)));
		workout.setClimb(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_CLIMB)));
		workout.setHeartRate(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_HEARTRATE)));
		workout.setComment(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_COMMENT)));
		workout.setPrivacy(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_PRIVACY)));
		workout.setLocationFromByteArray(cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.KEY_GPS_DATA)));
		return workout;
	}
	
	private WorkoutInstance cursorToEntry(Cursor cursor, WorkoutInstance workoutInstance) {
		workoutInstance.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.WORKOUT_ID)));
		workoutInstance.setInputType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_INPUT_TYPE)));
		workoutInstance.setActivityType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_ACTIVITY_TYPE)));
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_DATE_TIME))));
		}
		catch (Exception pe) {
			Log.d(TAG, "Parse Exception converting string "
					+ cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_DATE_TIME)) 
					+ " to date format: " + pe.getMessage());
		}
		workoutInstance.setDateTime(c);
		workoutInstance.setDuration(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_DURATION)));
		workoutInstance.setDistance(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_DISTANCE)));
		workoutInstance.setAvgSpeed(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_AVG_SPEED)));
		workoutInstance.setCalories(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_CALORIES)));
		workoutInstance.setClimb(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_CLIMB)));
		workoutInstance.setHeartRate(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_HEARTRATE)));
		workoutInstance.setComment(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_COMMENT)));
		workoutInstance.setPrivacy(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_PRIVACY)));
		workoutInstance.setLocationFromByteArray(cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.KEY_GPS_DATA)));
		return workoutInstance;
	}
	
	private Exercise cursorToEntry(Cursor cursor, Exercise exercise) {
		exercise.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.WORKOUT_ID)));
		exercise.setInputType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_INPUT_TYPE)));
		exercise.setActivityType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_ACTIVITY_TYPE)));
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_DATE_TIME))));
		}
		catch (Exception pe) {
			Log.d(TAG, "Parse Exception converting string "
					+ cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_DATE_TIME)) 
					+ " to date format: " + pe.getMessage());
		}
		exercise.setDateTime(c);
		exercise.setDuration(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_DURATION)));
		exercise.setDistance(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_DISTANCE)));
		exercise.setAvgSpeed(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_AVG_SPEED)));
		exercise.setCalories(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_CALORIES)));
		exercise.setClimb(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_CLIMB)));
		exercise.setHeartRate(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_HEARTRATE)));
		exercise.setComment(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_COMMENT)));
		exercise.setPrivacy(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_PRIVACY)));
		exercise.setLocationFromByteArray(cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.KEY_GPS_DATA)));
		return exercise;
	}
	
	private Frequency cursorToEntry(Cursor cursor, Frequency frequency) {
		frequency.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.WORKOUT_ID)));
		frequency.setInputType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_INPUT_TYPE)));
		frequency.setActivityType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_ACTIVITY_TYPE)));
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_DATE_TIME))));
		}
		catch (Exception pe) {
			Log.d(TAG, "Parse Exception converting string "
					+ cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_DATE_TIME)) 
					+ " to date format: " + pe.getMessage());
		}
		frequency.setDateTime(c);
		frequency.setDuration(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_DURATION)));
		frequency.setDistance(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_DISTANCE)));
		frequency.setAvgSpeed(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_AVG_SPEED)));
		frequency.setCalories(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_CALORIES)));
		frequency.setClimb(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_CLIMB)));
		frequency.setHeartRate(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_HEARTRATE)));
		frequency.setComment(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_COMMENT)));
		frequency.setPrivacy(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_PRIVACY)));
		frequency.setLocationFromByteArray(cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.KEY_GPS_DATA)));
		return frequency;
	}
	
	private int getClassIndex(Class<? extends GetSwoleClass> c) {
		if (c == Workout.class) {
			return WORKOUT_NUM;
		}
		else if (c == WorkoutInstance.class) {
			return WORKOUT_INSTANCE_NUM;
		}
		else if (c == Exercise.class) {
			return EXERCISE_NUM;
		}
		else if (c == Frequency.class) {
			return FREQUENCY_NUM;
		}
		else {
			Log.e(Globals.TAG, c.getName() + " is not the correct type of class");
			return -1;
		}
	}
}
