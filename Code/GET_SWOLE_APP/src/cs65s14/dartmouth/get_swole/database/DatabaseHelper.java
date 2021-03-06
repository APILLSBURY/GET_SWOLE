package cs65s14.dartmouth.get_swole.database;

import cs65s14.dartmouth.get_swole.Globals;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	//Workout table
	public static final String TABLE_NAME_WORKOUT = "workoutTable";
	public static final String WORKOUT_ID = "_id";
	public static final String WORKOUT_NAME = "name";
	public static final String WORKOUT_OWNER = "owner";
	public static final String WORKOUT_EXERCISE_LIST = "exerciselist";
	public static final String WORKOUT_SCHEDULED_DATES = "scheduleddates";
	public static final String WORKOUT_START_DATE = "startdate";
	public static final String WORKOUT_FREQUENCY_LIST = "frequencylist";
	public static final String WORKOUT_NOTES = "notes";
	
	//Workout instance table
	public static final String TABLE_NAME_WORKOUT_INSTANCE = "workoutInstanceTable";
	public static final String WORKOUT_INSTANCE_ID = "_id";
	public static final String WORKOUT_INSTANCE_WORKOUT = "workout";
	public static final String WORKOUT_INSTANCE_NAME = "name";
	public static final String WORKOUT_INSTANCE_EXERCISE_LIST = "exerciselist";
	public static final String WORKOUT_INSTANCE_TIME = "time";
	
	//Exercise table
	public static final String TABLE_NAME_EXERCISE = "exerciseTable";
	public static final String EXERCISE_ID = "_id";
	public static final String EXERCISE_NAME = "name";
	public static final String EXERCISE_SET_LIST = "setlist";
	public static final String EXERCISE_REPS_GOAL = "repsgoal";
	public static final String EXERCISE_WEIGHT_GOAL = "weightgoal";
	public static final String EXERCISE_EXERCISE_INSTANCE = "exerciseinstance";
	public static final String EXERCISE_OLD_ID = "oldid";
	public static final String EXERCISE_REST = "rest";
	public static final String EXERCISE_NOTES = "notes";
	
	//Frequency table
	public static final String TABLE_NAME_FREQUENCY = "frequencyTable";
	public static final String FREQUENCY_ID = "_id";
	public static final String FREQUENCY_DAY = "day";
	public static final String FREQUENCY_START_DATE = "startdate";
	public static final String FREQUENCY_END_DATE = "enddate";
	

	private static final String DATABASE_NAME = "getswole.db";
	private static final int DATABASE_VERSION = 2;

    // SQL query to create the table for the first time
    // Data types are defined below
    public static final String CREATE_TABLE_WORKOUT = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_WORKOUT + " ("
            + WORKOUT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + WORKOUT_NAME + " TEXT, "
            + WORKOUT_OWNER + " TEXT, "
            + WORKOUT_EXERCISE_LIST + " BLOB, "
            + WORKOUT_SCHEDULED_DATES + " TEXT, "
            + WORKOUT_START_DATE + " DATETIME, "
            + WORKOUT_FREQUENCY_LIST + " BLOB, "
            + WORKOUT_NOTES + " TEXT );";  
    
    public static final String CREATE_TABLE_WORKOUT_INSTANCE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_WORKOUT_INSTANCE + " ("
            + WORKOUT_INSTANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + WORKOUT_INSTANCE_WORKOUT + " INTEGER, "
            + WORKOUT_INSTANCE_EXERCISE_LIST + " BLOB, "
            + WORKOUT_INSTANCE_NAME + " TEXT, "
            + WORKOUT_INSTANCE_TIME + " DATETIME, "
            + "FOREIGN KEY (" + WORKOUT_INSTANCE_WORKOUT + ") REFERENCES " + TABLE_NAME_WORKOUT + " (" + WORKOUT_ID + "));";
    
    public static final String CREATE_TABLE_EXERCISE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_EXERCISE + " ("
            + EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + EXERCISE_NAME + " TEXT, "
            + EXERCISE_SET_LIST + " TEXT, "
            + EXERCISE_REPS_GOAL + " INTEGER, "
            + EXERCISE_WEIGHT_GOAL + " INTEGER, "
            + EXERCISE_EXERCISE_INSTANCE + " BOOLEAN, "
            + EXERCISE_OLD_ID + " INTEGER, "
            + EXERCISE_REST + " INTEGER, "
            + EXERCISE_NOTES + " TEXT );";  
    
    public static final String CREATE_TABLE_FREQUENCY = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_FREQUENCY + " ("
            + FREQUENCY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FREQUENCY_DAY + " INTEGER, "
            + FREQUENCY_START_DATE + " DATETIME, "
            + FREQUENCY_END_DATE + " DATETIME );";
    
	public static final String[] TABLE_NAMES = {
									TABLE_NAME_WORKOUT, 
									TABLE_NAME_WORKOUT_INSTANCE, 
									TABLE_NAME_EXERCISE, 
									TABLE_NAME_FREQUENCY};
    
    public static final String[] CREATE_TABLE_COMMANDS = {
    								CREATE_TABLE_WORKOUT, 
							    	CREATE_TABLE_WORKOUT_INSTANCE, 
							    	CREATE_TABLE_EXERCISE, 
							    	CREATE_TABLE_FREQUENCY};
    
    @Override
    public void onCreate (SQLiteDatabase database) {
    	for (String createCommand : CREATE_TABLE_COMMANDS) {
    		Log.d(Globals.TAG, "Executing command " + createCommand);
	        database.execSQL(createCommand);
    	}
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(), "Upgrading database from version "
            + oldVersion + " to " + newVersion
            + ", which will destroy all old data");

    	for (String tableName : TABLE_NAMES) {
            database.execSQL("DROP TABLE IF EXISTS " + tableName);
    	}
        onCreate(database);
    }

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
}