package cs65s14.dartmouth.get_swole;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cs65s14.dartmouth.get_swole.R;
import cs65s14.dartmouth.get_swole.classes.Exercise;
import cs65s14.dartmouth.get_swole.classes.ExerciseArrayAdapter;
import cs65s14.dartmouth.get_swole.classes.Set;
import cs65s14.dartmouth.get_swole.classes.Workout;
import cs65s14.dartmouth.get_swole.classes.WorkoutInstance;
import cs65s14.dartmouth.get_swole.database.DatabaseWrapper;

public class WorkoutDoActivity extends Activity {
	
	DatabaseWrapper dbWrapper;
	WorkoutInstance workoutInstance;
	ExerciseArrayAdapter mAdapter;
	ArrayList<ArrayList<Set>> doneSets; // the sets of the exercises as they are completed/edited
	EditText commentBox;
	
	/**
	 * onCreate()
	 * @param Bundle savedInstanceStates
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workout);	
		
		stopWorkoutNotification();
		
		dbWrapper = new DatabaseWrapper(this);

		// Display		
		TextView nameView = (TextView) findViewById(R.id.workoutName);
		LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.workout_buttons);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		commentBox = (EditText) findViewById(R.id.workoutComments);
		
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
		    	 	
			    	DialogFragment fragment = AppDialogFragment.newInstance(AppDialogFragment.DIALOG_ID_TIMER);
	     	        fragment.show(getFragmentManager(), getString(R.string.dialog_fragment_tag_timer)); 
		     }
		});
		button2.setText(getString(R.string.stop_workout_button));
		button2.setTextColor(getResources().getColor(R.color.get_swole_orange));
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
		long id = getIntent().getExtras().getLong(Globals.ID_TAG, -1L);
		dbWrapper.open();
		workoutInstance = new WorkoutInstance((Workout) dbWrapper.getEntryById(id, Workout.class));
		workoutInstance.setTime(Calendar.getInstance());
		dbWrapper.close();
		
		// create an exercise list for the workoutinstance
		ArrayList<Exercise> wiExercises = new ArrayList<Exercise>();
		for (int i = 0; i < workoutInstance.getWorkout().getExerciseList().size(); i++) {
			wiExercises.add(null); // placeholder, we will get rid of them later
		}

		workoutInstance.setExerciseList(wiExercises);
		
		nameView.setText(workoutInstance.getWorkout().getName());		
		commentBox.setText(workoutInstance.getWorkout().getNotes());
		
		doneSets = new ArrayList<ArrayList<Set>>(workoutInstance.getWorkout().getExerciseList().size());
		for (int i = 0; i < workoutInstance.getWorkout().getExerciseList().size(); i++) {
			doneSets.add(new ArrayList<Set>());
		}
		
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
                	//doneSets.set(position, e.getSetList());
                	DialogFragment fragment = AppDialogFragment.newInstanceSets(e.getId(), 1, position);
        	        fragment.show(getFragmentManager(), getString(R.string.dialog_fragment_tag_do_sets));
                		
                }
	    };

	    // Get the ListView and wired the listener
	    listView.setOnItemClickListener(mListener);
    
	}

	
	public void onFinishWorkout() {
		// Need to get rid of the null spaces in the workout instance exercise list
		ArrayList<Exercise> finalList = new ArrayList<Exercise>();
		for (Exercise e : workoutInstance.getExerciseList()) {
			if (e != null) finalList.add(e);
		}
		workoutInstance.setExerciseList(finalList);
		
		// We want to save the workout into the database as a workout instance
		dbWrapper.open();
		WorkoutInstance saved = dbWrapper.createEntry(workoutInstance);	
		dbWrapper.close();
		finish(); // close the activity
			
	}
	
	public void setDoneSets(ArrayList<Set> sets, int position) {
		doneSets.set(position, sets);
		
		// get corresponding exercise
		Exercise original = workoutInstance.getWorkout().getExerciseList().get(position);
		
		Exercise e = new Exercise(original.getName());
		// set the id of this exercise to be the id of the original exercise
		e.setOldId(original.getId());
		// set the tag that this is an exercise instance
		e.setExerciseInstance(true);
		// update the set list 
		e.setSetList(sets);
		
		// We want to add this exercise to the instance's list of exercises
		dbWrapper.open(); 			
		e = dbWrapper.createEntry(e);
		dbWrapper.close();
				
		workoutInstance.getExerciseList().set(position, e);
				
	}
	
	public ArrayList<Set> getDoneSets(int position) {
		return doneSets.get(position);
	}
	
	public void stopWorkoutNotification() {
		Intent intent = new Intent(this, WorkoutNotificationService.class);
		stopService(intent);
	}
 	
}