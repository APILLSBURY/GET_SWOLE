package cs65.dartmouth.get_swole;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import cs65.dartmouth.get_swole.classes.Exercise;
import cs65.dartmouth.get_swole.classes.Workout;
import cs65.dartmouth.get_swole.classes.WorkoutInstance;
import cs65.dartmouth.get_swole.database.DatabaseWrapper;

// NOT DONE
public class WorkoutDoActivity extends Activity {
	
	WorkoutInstance workoutInstance;
	ExerciseArrayAdapter mAdapter;

	/**
	 * onCreate()
	 * @param Bundle savedInstanceStates
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workout);	
		
		// Display		
		TextView nameView = (TextView) findViewById(R.id.workoutName);
				
		// Set the buttons to have the correct names and callbacks
		Button button1 = (Button) findViewById(R.id.button1);
		
		// set the button text
		button1.setText(getString(R.string.stop_workout_button));			
		button1.setOnClickListener(new OnClickListener() {
		     @Override
		     public void onClick(View v) {
		           onFinishWorkout();
		     }
		});
					
		// Initialize all instance variables
		workoutInstance = new WorkoutInstance();
		workoutInstance.setTime(Calendar.getInstance());
		
		long id = getIntent().getExtras().getLong(Globals.ID_TAG, -1L);
		DatabaseWrapper dbWrapper = new DatabaseWrapper(this);
		dbWrapper.open();
		workoutInstance.setWorkout((Workout) dbWrapper.getEntryById(id, Workout.class));
		dbWrapper.close();

		nameView.setText(workoutInstance.getWorkout().getName());		
		
		// Define a new adapter
	    mAdapter = new ExerciseArrayAdapter(this, R.layout.exercises_list_row, workoutInstance.getWorkout().getExerciseList());

	    // Assign the adapter to ListView
	    ListView listView = (ListView) findViewById(R.id.exerciseListView);
	    listView.setAdapter(mAdapter);

	    // Define the listener interface
	    OnItemClickListener mListener = new OnItemClickListener() {
	    	
	    		@Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    						
                        // Open dialog to edit this exercise
                	Exercise e = workoutInstance.getExerciseList().get(position);
                	DialogFragment fragment = AppDialogFragment.newInstance(e, false);
        	        fragment.show(getFragmentManager(), getString(R.string.dialog_fragment_tag_do_exercise));
                		
                }
	    };

	    // Get the ListView and wired the listener
	    listView.setOnItemClickListener(mListener);
		
    
	}
	
	public void onDoExercise(Exercise e) {
		// We want to add this exercise to the instance's list of exercises
		workoutInstance.addExercise(e);
	}
	
	public void onFinishWorkout() {
		
		// We want to save the workout into the database as a workout instance
		DatabaseWrapper dbWrapper = new DatabaseWrapper(this);
		dbWrapper.open();
		dbWrapper.createEntry(workoutInstance);
		dbWrapper.close();
		
		finish(); // close the activity
			
	}
}