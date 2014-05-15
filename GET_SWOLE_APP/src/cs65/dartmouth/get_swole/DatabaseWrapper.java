package cs65.dartmouth.get_swole;

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

public class DatabaseWrapper {

	// Database fields
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	private String[] allColumns = { 
			DatabaseHelper.KEY_ROWID, 
			DatabaseHelper.KEY_INPUT_TYPE, 
			DatabaseHelper.KEY_ACTIVITY_TYPE, 
			DatabaseHelper.KEY_DATE_TIME, 
			DatabaseHelper.KEY_DURATION, 
			DatabaseHelper.KEY_DISTANCE, 
			DatabaseHelper.KEY_AVG_PACE, 
			DatabaseHelper.KEY_AVG_SPEED, 
			DatabaseHelper.KEY_CALORIES, 
			DatabaseHelper.KEY_CLIMB, 
			DatabaseHelper.KEY_HEARTRATE, 
			DatabaseHelper.KEY_COMMENT, 
			DatabaseHelper.KEY_PRIVACY, 
			DatabaseHelper.KEY_GPS_DATA };

	private static final String TAG = "DATABASE";
	public static final int INPUT_MANUAL = 0;
	public static final int INPUT_GPS = 1;
	public static final int INPUT_AUTOMATIC = 0;
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

	public ExerciseEntry createEntry(ExerciseEntry entry) {

		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.KEY_INPUT_TYPE, entry.getInputType());
		values.put(DatabaseHelper.KEY_ACTIVITY_TYPE, entry.getFinalActivityType());
		values.put(DatabaseHelper.KEY_DATE_TIME, dateFormat.format(entry.getDateTime().getTime()));
		values.put(DatabaseHelper.KEY_DURATION, entry.getDuration());
		values.put(DatabaseHelper.KEY_DISTANCE, entry.getDistance());
		values.put(DatabaseHelper.KEY_AVG_PACE, 0);
		values.put(DatabaseHelper.KEY_AVG_SPEED, entry.getAvgSpeed());
		values.put(DatabaseHelper.KEY_CALORIES, entry.getCalories());
		values.put(DatabaseHelper.KEY_CLIMB, entry.getClimb());
		values.put(DatabaseHelper.KEY_HEARTRATE, entry.getHeartRate());
		values.put(DatabaseHelper.KEY_COMMENT, entry.getComment());
		values.put(DatabaseHelper.KEY_PRIVACY, entry.getPrivacy());
		values.put(DatabaseHelper.KEY_GPS_DATA, entry.getLocationByteArray());
		
		if (database == null) {
			open();
		}
		
		long insertId = database.insert(DatabaseHelper.TABLE_NAME_ENTRIES, null, values); //ILLEGAL STATE EXCEPTION
		entry.setId(insertId); //entry.setId(cursor.getLong(0))
		Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_ENTRIES,
				allColumns, DatabaseHelper.KEY_ROWID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		ExerciseEntry newEntry = cursorToEntry(cursor);

		cursor.close();
		return newEntry;
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
