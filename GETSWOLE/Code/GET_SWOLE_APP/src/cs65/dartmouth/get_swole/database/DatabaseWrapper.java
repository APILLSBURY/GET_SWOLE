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
	
	private String[] workoutColumns = {
			DatabaseHelper.WORKOUT_ID, 
			DatabaseHelper.WORKOUT_NAME, 
			DatabaseHelper.WORKOUT_EXERCISE_LIST, 
			DatabaseHelper.WORKOUT_SCHEDULED_DATES, 
			DatabaseHelper.WORKOUT_START_DATE, 
			DatabaseHelper.WORKOUT_FREQUENCY_LIST, 
			DatabaseHelper.WORKOUT_NOTES };
	
	private String[] workoutInstanceColumns = {
			DatabaseHelper.WORKOUT_INSTANCE_ID, 
			DatabaseHelper.WORKOUT_INSTANCE_WORKOUT, 
			DatabaseHelper.WORKOUT_INSTANCE_EXERCISE_LIST, 
			DatabaseHelper.WORKOUT_INSTANCE_TIME };

	private String[] exerciseColumns = {
			DatabaseHelper.EXERCISE_ID, 
			DatabaseHelper.EXERCISE_NAME, 
			DatabaseHelper.EXERCISE_REPS, 
			DatabaseHelper.EXERCISE_WEIGHT, 
			DatabaseHelper.EXERCISE_REPS_GOAL, 
			DatabaseHelper.EXERCISE_WEIGHT_GOAL, 
			DatabaseHelper.EXERCISE_REST, 
			DatabaseHelper.EXERCISE_NOTES };

	private String[] frequencyColumns = {
			DatabaseHelper.FREQUENCY_ID, 
			DatabaseHelper.FREQUENCY_DAY, 
			DatabaseHelper.FREQUENCY_START_DATE, 
			DatabaseHelper.FREQUENCY_END_DATE };
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

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
		values.put(DatabaseHelper.WORKOUT_START_DATE, dateFormat.format(workout.getStartDate()));
		values.put(DatabaseHelper.WORKOUT_FREQUENCY_LIST, workout.getFrequencyListByteArray());
		values.put(DatabaseHelper.WORKOUT_NOTES, workout.getNotes());
		
		if (database == null) {
			open();
		}
		
		long insertId = database.insert(DatabaseHelper.TABLE_NAME_WORKOUT, null, values); //ILLEGAL STATE EXCEPTION
		workout.setId(insertId); //entry.setId(cursor.getLong(0))
		Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_WORKOUT,
				workoutColumns, DatabaseHelper.WORKOUT_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Workout newWorkout = cursorToEntry(cursor, Workout.class);

		cursor.close();
		return newWorkout;
	}

	
	//create a workout instance entry
	public Workout createEntry(WorkoutInstance workoutInstance) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.WORKOUT_INSTANCE_WORKOUT, workoutInstance.getWorkout().getId());
		values.put(DatabaseHelper.WORKOUT_INSTANCE_EXERCISE_LIST, workoutInstance.getExerciseListByteArray());
		values.put(DatabaseHelper.WORKOUT_INSTANCE_TIME, dateFormat.format(workoutInstance.getTime()));
		
		if (database == null) {
			open();
		}
		
		long insertId = database.insert(DatabaseHelper.TABLE_NAME_WORKOUT_INSTANCE, null, values); //ILLEGAL STATE EXCEPTION
		workoutInstance.setId(insertId); //entry.setId(cursor.getLong(0))
		Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_WORKOUT_INSTANCE,
				workoutInstanceColumns, DatabaseHelper.WORKOUT_INSTANCE_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Workout newWorkoutInstance = cursorToEntry(cursor, WorkoutInstance.class);

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
				exerciseColumns, DatabaseHelper.EXERCISE_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Exercise newExercise = cursorToEntry(cursor, Exercise.class);

		cursor.close();
		return newExercise;
	}

	//create a frequency entry
	public Frequency createEntry(Frequency frequency) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FREQUENCY_DAY, frequency.getDay());
		values.put(DatabaseHelper.FREQUENCY_START_DATE, dateFormat.format(frequency.getStartDate()));
		values.put(DatabaseHelper.FREQUENCY_END_DATE, dateFormat.format(frequency.getEndDate()));
		
		if (database == null) {
			open();
		}
		
		long insertId = database.insert(DatabaseHelper.TABLE_NAME_FREQUENCY, null, values); //ILLEGAL STATE EXCEPTION
		frequency.setId(insertId); //entry.setId(cursor.getLong(0))
		Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_FREQUENCY,
				frequencyColumns, DatabaseHelper.FREQUENCY_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Frequency newFrequency = cursorToEntry(cursor, Frequency.class);

		cursor.close();
		return newFrequency;
	}

	public void deleteEntry (Workout workout) {
		deleteEntry(workout.getId(), DatabaseHelper.TABLE_NAME_WORKOUT, DatabaseHelper.WORKOUT_ID);
	}

	public void deleteEntry (WorkoutInstance workoutInstance) {
		deleteEntry(workoutInstance.getId(), DatabaseHelper.TABLE_NAME_WORKOUT_INSTANCE, DatabaseHelper.WORKOUT_INSTANCE_ID);
	}

	public void deleteEntry (Exercise exercise) {
		deleteEntry(exercise.getId(), DatabaseHelper.TABLE_NAME_EXERCISE, DatabaseHelper.EXERCISE_ID);
	}

	public void deleteEntry (Frequency frequency) {
		deleteEntry(frequency.getId(), DatabaseHelper.TABLE_NAME_FREQUENCY, DatabaseHelper.FREQUENCY_ID);
	}
	
	public void deleteEntry(long id, String tableName, String IdRowKey) {
		Log.d(Globals.TAG, "delete entry = " + id);
		Log.d(Globals.TAG, "Entry deleted from table " + tableName + " with id: " + id);
		database.delete(tableName, IdRowKey + " = " + id, null);
	}
	
	public void deleteAllEntries(Class c) {
		String tableName = getTableNameFromClass(c);
		if (tableName == null) {
			Log.e(Globals.TAG, "Cannot clear entries for class " + c.getName());
		}
		Log.d(TAG, "delete all entries from table " + tableName);
		database.delete(tableName, null, null);
	}
	
	public List getAllEntries(Class c) {
		String tableName = getTableNameFromClass(c);
		String[] allColumns = getColumnNamesFromClass(c);
		List entries;
		if (c == Workout.class) {
			entries = new ArrayList<Workout>();
		}
		else if (c == WorkoutInstance.class) {
			entries = new ArrayList<WorkoutInstance>();
		}
		else if (c == Exercise.class) {
			entries = new ArrayList<Exercise>();
		}
		else if (c == Frequency.class) {
			entries = new ArrayList<Frequency>();
		}

		Cursor cursor = database.query(tableName,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Object entry = cursorToEntry(cursor, c);
			entries.add(entry);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return entries;
	}
	
	public ExerciseEntry getEntryById(long id) {
		Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_ENTRIES,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			if (id == cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_ROWID))) {
				ExerciseEntry entry = cursorToEntry(cursor);
				cursor.close();
				return entry;
			}
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return null;
	}

	private ExerciseEntry cursorToEntry(Cursor cursor) {
		ExerciseEntry entry = new ExerciseEntry();
		entry.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_ROWID)));
		entry.setInputType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_INPUT_TYPE)));
		entry.setActivityType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_ACTIVITY_TYPE)));
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(dateFormat.parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_DATE_TIME))));
		}
		catch (Exception pe) {
			Log.d(TAG, "Parse Exception converting string "
					+ cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_DATE_TIME)) 
					+ " to date format: " + pe.getMessage());
		}
		entry.setDateTime(c);
		entry.setDuration(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_DURATION)));
		entry.setDistance(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_DISTANCE)));
		entry.setAvgSpeed(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_AVG_SPEED)));
		entry.setCalories(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_CALORIES)));
		entry.setClimb(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_CLIMB)));
		entry.setHeartRate(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_HEARTRATE)));
		entry.setComment(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_COMMENT)));
		entry.setPrivacy(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_PRIVACY)));
		entry.setLocationFromByteArray(cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.KEY_GPS_DATA)));
		return entry;
	}
	
	private String getTableNameFromClass(Class c) {
		if (c == Workout.class) {
			return DatabaseHelper.TABLE_NAME_WORKOUT;
		}
		else if (c == WorkoutInstance.class) {
			return DatabaseHelper.TABLE_NAME_WORKOUT_INSTANCE;
		}
		else if (c == Exercise.class) {
			return DatabaseHelper.TABLE_NAME_EXERCISE;
		}
		else if (c == Frequency.class) {
			return DatabaseHelper.TABLE_NAME_FREQUENCY;
		}
		else {
			return null;
		}
	}
	
	private String[] getColumnNamesFromClass(Class c) {
		if (c == Workout.class) {
			return workoutColumns;
		}
		else if (c == WorkoutInstance.class) {
			return workoutInstanceColumns;
		}
		else if (c == Exercise.class) {
			return exerciseColumns;
		}
		else if (c == Frequency.class) {
			return frequencyColumns;
		}
		else {
			return null;
		}
	}
}
