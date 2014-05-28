package cs65.dartmouth.get_swole;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import cs65.dartmouth.get_swole.classes.ExerciseArrayAdapter;
import cs65.dartmouth.get_swole.classes.WorkoutInstance;
import cs65.dartmouth.get_swole.database.DatabaseWrapper;

public class ViewWorkoutInstanceActivity extends Activity {

	WorkoutInstance instance;
	ExerciseArrayAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_workout_instance);
		
		long id= getIntent().getExtras().getLong(Globals.ID_TAG, -1L); // get the id of the workout instance - should not be -1
		DatabaseWrapper dbWrapper = new DatabaseWrapper(this);
		dbWrapper.open();
		instance = (WorkoutInstance) dbWrapper.getEntryById(id, WorkoutInstance.class);
		dbWrapper.close();
		
		TextView name = (TextView) findViewById(R.id.completedWorkoutName);
		name.setText(instance.getWorkout().getName());
		TextView timestamp = (TextView) findViewById(R.id.timestampWorkout);
		timestamp.setText("Completed on: " + android.text.format.DateFormat.format("MM-dd-yyyy hh:mm:ss", instance.getTime()));		
		
		// Define a new adapter
        mAdapter = new ExerciseArrayAdapter(this, R.layout.exercises_list_row, instance.getExerciseList());

        // Assign the adapter to ListView
        ListView listView = (ListView) findViewById(R.id.completedExerciseListView);
        listView.setAdapter(mAdapter);
        
	}

}
