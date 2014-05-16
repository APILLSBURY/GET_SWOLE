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
			DatabaseHelper.WORKOUT_INSTANCE_NAME, 
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
		Workout workout = cursorToWorkout(cursor);

		cursor.close();
		return workout;
	}

	public void deleteEntry (ExerciseEntry entry) {
		deleteEntry(entry.getId());
	}
	
	public void deleteEntry(long id) {
		Log.d(TAG, "delete entry = " + id);
		System.out.println("Comment deleted with id: " + id);
		database.delete(DatabaseHelper.TABLE_NAME_ENTRIES, DatabaseHelper.KEY_ROWID + " = " + id, null);
	}
	
	public void deleteAllEntries() {
		System.out.println("Comment deleted all");
		Log.d(TAG, "delete all = ");
		database.delete(DatabaseHelper.TABLE_NAME_ENTRIES, null, null);
	}
	
	public List<ExerciseEntry> getAllEntries() {
		List<ExerciseEntry> entries = new ArrayList<ExerciseEntry>();

		Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_ENTRIES,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ExerciseEntry entry = cursorToEntry(cursor);
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
}
