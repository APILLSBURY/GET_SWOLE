package cs65.dartmouth.get_swole;

import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import cs65.dartmouth.get_swole.classes.Workout;
import cs65.dartmouth.get_swole.database.DatabaseWrapper;

public class WorkoutEditActivity extends Activity {
	
	List<Exercise> exercises;	
	Workout workout;
	ExerciseArrayAdapter mAdapter;
	DatabaseWrapper dbWrapper = new DatabaseWrapper(this);

	
	/**
	 * onCreate()
	 * @param Bundle savedInstanceStates
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workout);
		
		// Display	
		LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.workout_buttons);
		
		// Set the buttons to have the correct names and callbacks
		Button button1 = (Button) findViewById(R.id.button1);
		Button button2 = (Button) findViewById(R.id.button2);
		
		// set the button text
		button1.setText(getString(R.string.add_exercise_button));			
		button1.setOnClickListener(new OnClickListener() {
    	     @Override
    	     public void onClick(View v) {
    	    	 	// Display dialog with nothing in it
    	 			DialogFragment fragment = AppDialogFragment.newInstance(AppDialogFragment.DIALOG_ID_EDIT_EXERCISE);
    	 			fragment.show(getFragmentManager(), getString(R.string.dialog_fragment_tag_edit_exercise));
    	     }
    	});
		button2.setText(getString(R.string.start_workout_button));
		button2.setOnClickListener(new OnClickListener() {
    	     @Override
    	     public void onClick(View v) {
    	          onStartWorkout();
    	     }
    	});
		
		// Get the id of the workout from the intent - if it is not there, then we are making a new workout
		long id = getIntent().getExtras().getLong(Globals.ID_TAG, -1L);
		
		dbWrapper.open();
		
		if (id == -1) { // we are creating a new workout, it is added to the database
			Workout w = new Workout(getIntent().getExtras().getString(Globals.NAME_TAG));	
			workout = dbWrapper.createEntry(w);
		}
		else {
			workout = (Workout) dbWrapper.getEntryById(id, Workout.class);		
		}
		
		dbWrapper.close();
	
		TextView nameView = (TextView) findViewById(R.id.workoutName);
		nameView.setText(workout.getName());		
		exercises = workout.getExerciseList();
		
		// Define a new adapter
        mAdapter = new ExerciseArrayAdapter(this, R.layout.exercises_list_row, exercises);

        // Assign the adapter to ListView
        ListView listView = (ListView) findViewById(R.id.exerciseListView);
        listView.setAdapter(mAdapter);

        // Define the listener interface
        OnItemClickListener mListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Open dialog to edit this exercise
            	Exercise e = exercises.get(position);
            	DialogFragment fragment;
            	fragment = AppDialogFragment.newInstance(e, true);
    	        fragment.show(getFragmentManager(), getString(R.string.dialog_fragment_tag_edit_exercise));
            		
            }
        };

        // Get the ListView and wired the listener
        listView.setOnItemClickListener(mListener);     
        
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.delete, menu);		
		return true;
	}
	
	// Menu Bar Options
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
	   
		// Only the delete button so it shouldn't matter
		// delete this workout from the workouts database - all of its entries will still be there though
		dbWrapper.open();
		dbWrapper.deleteEntry(workout);
		dbWrapper.close();
			
		// Update the main activity that things might have changed in the database - this workout was deleted
		 Intent i = new Intent();
        i.setAction("UPDATE_NOTIFY");
        i.putExtra("message", "update");
        sendBroadcast(i);
        
		finish();
	
		
	    return false;
	}
		
		
	@Override 
	public void onBackPressed() { 
		
		// Update the main activity that things might have changed in the database - like if this is a new workout
        Intent i = new Intent();
        i.setAction("UPDATE_NOTIFY");
        i.putExtra("message", "update");
        sendBroadcast(i);
        
		finish();
		
	}
	
	public void onStartWorkout() {
		
		// Update the main activity that things might have changed in the database - like if this is a new workout
        Intent i = new Intent();
        i.setAction("UPDATE_NOTIFY");
        i.putExtra("message", "update");
        sendBroadcast(i);
        
		Bundle b = new Bundle();
		b.putLong(Globals.ID_TAG, workout.getId());
		
		Intent intent = new Intent(this, WorkoutDoActivity.class);
		intent.putExtras(b);
		
		startActivity(intent);
		
		finish();
	}
	
	// DEALING WITH EXERCISES
	public void onAddNewExercise(Exercise e) {
        
        dbWrapper.open();
        
        Exercise savedExercise = dbWrapper.createEntry(e);
		exercises.add(savedExercise);
        dbWrapper.updateExerciseList(workout);
        dbWrapper.close();
        
        mAdapter.notifyDataSetChanged();
	}
	
	// New exercise does not have a database id yet
	public void onEditExercise(long oldId, Exercise newExercise) {
		
		// Remove the old exercise of this id from the exercise list of this workout
		for (Exercise e : exercises) {
			if (e.getId() == oldId) 
				exercises.remove(e);
		}
        
        onAddNewExercise(newExercise);	
        
	}
	
	public void onUseExistingExercise(Exercise e) {
		
		exercises.add(e);
		dbWrapper.updateExerciseList(workout);
		mAdapter.notifyDataSetChanged();
	}
	
	public void onDeleteExercise(long id) {
		// Delete this exercise from THIS WORKOUT'S LIST. Not the exercise database
		Exercise toRemove = null;
		for (Exercise e : exercises) {
			if (e.getId() == id) 
				toRemove = e;
		}
		
		exercises.remove(toRemove);
		
		dbWrapper.open();
		dbWrapper.updateExerciseList(workout);
		dbWrapper.close();
		
		mAdapter.notifyDataSetChanged();
	}
	
}