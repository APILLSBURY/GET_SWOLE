package cs65.dartmouth.get_swole.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	//Workout table
	public static final String TABLE_NAME_WORKOUT = "workout";
	public static final String WORKOUT_ID = "_id";
	public static final String WORKOUT_NAME = "name";
	public static final String WORKOUT_EXERCISE_LIST = "exerciselist";
	public static final String WORKOUT_SCHEDULED_DATES = "scheduleddates";
	public static final String WORKOUT_START_DATE = "startdate";
	public static final String WORKOUT_FREQUENCY_LIST = "frequencylist";
	
	//Workout instance table
	public static final String TABLE_NAME_WORKOUT_INSTANCE = "workout_instance";
	public static final String WORKOUT_INSTANCE_ID = "_id";
	public static final String WORKOUT_INSTANCE_NAME = "name";
	public static final String WORKOUT_INSTANCE_EXERCISE_LIST = "exerciselist";
	public static final String WORKOUT_INSTANCE_TIME = "time";
	
	//Exercise table
	public static final String TABLE_NAME_EXERCISE = "exercise";
	public static final String EXERCISE_ID = "_id";
	public static final String EXERCISE_NAME = "name";
	public static final String EXERCISE_REPS = "reps";
	public static final String EXERCISE_WEIGHT = "weight";
	public static final String EXERCISE_REPS_GOAL = "repsgoal";
	public static final String EXERCISE_WEIGHT_GOAL = "weightgoal";
	
	//Frequency table
	public static final String TABLE_NAME_FREQUENCY = "frequency";
	public static final String FREQUENCY_ID = "_id";
	public static final String FREQUENCY_DAY = "day";
	public static final String FREQUENCY_START_DATE = "startdate";
	public static final String FREQUENCY_END_DATE = "enddate";
	

	private static final String DATABASE_NAME = "getswole.db";
	private static final int DATABASE_VERSION = 1;

    // SQL query to create the table for the first time
    // Data types are defined below
    public static final String CREATE_TABLE_WORKOUT = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_WORKOUT + " ("
            + WORKOUT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + WORKOUT_NAME + " TEXT, "
            + WORKOUT_EXERCISE_LIST + " BLOB, "
            + WORKOUT_SCHEDULED_DATES + " BLOB, "
            + WORKOUT_START_DATE + " DATE, "
            + WORKOUT_FREQUENCY_LIST + " BLOB, " + ");";  
    
    public static final String CREATE_TABLE_EXERCISE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_WORKOUT_INSTANCE + " ("
            + WORKOUT_INSTANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + WORKOUT_INSTANCE_NAME + " TEXT, "
            + WORKOUT_EXERCISE_LIST + " BLOB, "
            + WORKOUT_INSTANCE_TIME + " DATETIME, " + ");";  
    
    public static final String CREATE_TABLE_WORKOUT_INSTANCE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_EXERCISE + " ("
            + EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + EXERCISE_NAME + " TEXT, "
            + EXERCISE_REPS + " INT, "
            + EXERCISE_WEIGHT + " INT, "
            + EXERCISE_REPS_GOAL + " INT, "
            + EXERCISE_WEIGHT_GOAL + " INT, " + ");";  
    
    public static final String CREATE_TABLE_FREQUENCY = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_FREQUENCY + " ("
            + FREQUENCY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FREQUENCY_DAY + " INT, "
            + FREQUENCY_START_DATE + " DATE, "
            + FREQUENCY_END_DATE + " DATE, " + ");";
    
    public static final String[] TABLE_NAMES = {TABLE_NAME_WORKOUT, TABLE_NAME_WORKOUT_INSTANCE, 
    												TABLE_NAME_EXERCISE, TABLE_NAME_FREQUENCY};
    public static final String[] CREATE_TABLE_COMMANDS = {CREATE_TABLE_WORKOUT, CREATE_TABLE_WORKOUT_INSTANCE, 
		CREATE_TABLE_EXERCISE, CREATE_TABLE_FREQUENCY};
    
    @Override
    public void onCreate(SQLiteDatabase database) {
    	for (String createCommand : CREATE_TABLE_COMMANDS) {
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