package cs65.dartmouth.get_swole;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cs65.dartmouth.get_swole.classes.Exercise;
import cs65.dartmouth.get_swole.classes.ExerciseArrayAdapter;
import cs65.dartmouth.get_swole.classes.Set;
import cs65.dartmouth.get_swole.classes.Workout;
import cs65.dartmouth.get_swole.classes.WorkoutInstance;
import cs65.dartmouth.get_swole.database.DatabaseWrapper;

public class WorkoutDoActivity extends Activity {
	
	WorkoutInstance workoutInstance;
	ExerciseArrayAdapter mAdapter;
	ArrayList<ArrayList<Set>> doneSets;

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
		LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.workout_buttons);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		// Set the buttons to have the correct names and callbacks
		Button button1 = new Button(this);
		Button button2 = new Button(this);
		
		// set the button text/callbacks
		button1.setText(getString(R.string.timer));
		button1.setLayoutParams(params);
		button1.setOnClickListener(new OnClickListener() {
		     @Override
		     public void onClick(View v) {
			        // begin timer
		    	 	/*
			    	DialogFragment fragment = AppDialogFragment.newInstance(AppDialogFragment.DIALOG_ID_TIMER);
	     	        fragment.show(getFragmentManager(), getString(R.string.dialog_fragment_tag_timer)); */
		     }
		});
		button2.setText(getString(R.string.stop_workout_button));	
		button2.setLayoutParams(params);
		button2.setOnClickListener(new OnClickListener() {
		     @Override
		     public void onClick(View v) {
		           onFinishWorkout();
		     }
		});
		
		buttonLayout.addView(button1);
		buttonLayout.addView(button2);
					
		// Initialize all instance variables
		workoutInstance = new WorkoutInstance();
		workoutInstance.setTime(Calendar.getInstance());
		
		long id = getIntent().getExtras().getLong(Globals.ID_TAG, -1L);
		DatabaseWrapper dbWrapper = new DatabaseWrapper(this);
		dbWrapper.open();
		workoutInstance.setWorkout((Workout) dbWrapper.getEntryById(id, Workout.class));
		dbWrapper.close();

		nameView.setText(workoutInstance.getWorkout().getName());		
		
		doneSets = new ArrayList<ArrayList<Set>>(workoutInstance.getWorkout().getExerciseList().size());
		
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
                	Exercise e = workoutInstance.getWorkout().getExerciseList().get(position);
                	doneSets.set(position, e.getSetList());
                	DialogFragment fragment = AppDialogFragment.newInstance(e, false, position);
        	        fragment.show(getFragmentManager(), getString(R.string.dialog_fragment_tag_do_exercise));
                		
                }
	    };

	    // Get the ListView and wired the listener
	    listView.setOnItemClickListener(mListener);
    
	}
	
	// NEEDS EDITING
	public void onDoExercise(Exercise e, int position) {
		// We want to add this exercise to the instance's list of exercises
		// Need to check the done sets list 
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
	
	public void setDoneSets(ArrayList<Set> sets, int position) {
		doneSets.set(position, sets);
	}
	
	public ArrayList<Set> getDoneSets(int position) {
		return doneSets.get(position);
	}
}