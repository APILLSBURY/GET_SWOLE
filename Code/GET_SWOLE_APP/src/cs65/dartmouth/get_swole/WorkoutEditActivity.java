package cs65.dartmouth.get_swole;

import java.util.ArrayList;
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
import cs65.dartmouth.get_swole.classes.Set;
import cs65.dartmouth.get_swole.classes.Workout;
import cs65.dartmouth.get_swole.database.DatabaseWrapper;

public class WorkoutEditActivity extends Activity {
	
	Workout workout;
	List<Exercise> exercises;	
	ExerciseArrayAdapter mAdapter;
	ArrayList<Set> setsToSave;

	
	DatabaseWrapper dbWrapper = new DatabaseWrapper(this);
	
	Button button2;
	LinearLayout buttonLayout;
	boolean firstAdded;
	
	/**
	 * onCreate()
	 * @param Bundle savedInstanceStates
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workout);
		
		// Display	
		buttonLayout = (LinearLayout) findViewById(R.id.workout_buttons);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		TextView nameView = (TextView) findViewById(R.id.workoutName);

		
		// Set the buttons to have the correct names and callbacks
		Button button1 = new Button(this);
		button2 = new Button(this); // need to know whether we need to display this
		Button button3 = new Button(this); 

		// set the button text
		button1.setText(getString(R.string.add_exercise_button));
		button1.setLayoutParams(params);
		button1.setOnClickListener(new OnClickListener() {
    	     @Override
    	     public void onClick(View v) {
    	    	 	// Display dialog with nothing in it
    	 			DialogFragment fragment = AppDialogFragment.newInstance(AppDialogFragment.DIALOG_ID_EDIT_EXERCISE);
    	 			fragment.show(getFragmentManager(), getString(R.string.dialog_fragment_tag_edit_exercise));
    	     }
    	});
		
		button2.setText(getString(R.string.existing_exercise));
		button2.setLayoutParams(params);
		button2.setOnClickListener(new OnClickListener() {
    	     @Override
    	     public void onClick(View v) {
    	    		DialogFragment fragment = AppDialogFragment.newInstance(AppDialogFragment.DIALOG_ID_ADD_EXISTING_EXERCISE);
    	 			fragment.show(getFragmentManager(), getString(R.string.dialog_fragment_tag_existing_exercise));
    	     }
    	});
		
		button3.setText(getString(R.string.start_workout_button));
		button3.setLayoutParams(params);
		button3.setOnClickListener(new OnClickListener() {
   	     @Override
   	     public void onClick(View v) {
	          onStartWorkout();

   	     }
		});
		
		buttonLayout.addView(button1);
		buttonLayout.addView(button3);
		// don't add the second button yet
		
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
		
		if (dbWrapper.getAllEntries(Exercise.class).size() > 0) {
			firstAdded = true;
	        buttonLayout.addView(button2);
		}
		
		dbWrapper.close();
	
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
            	DialogFragment fragment = AppDialogFragment.newInstance(e, true);
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
        
		finish();
		
	    return false;
	}
	
	
	public void onStartWorkout() {
        
		Bundle b = new Bundle();
		b.putLong(Globals.ID_TAG, workout.getId());
		
		Intent intent = new Intent(this, WorkoutDoActivity.class);
		intent.putExtras(b);
		
		startActivity(intent);
		
		finish();
	}
	
	// DEALING WITH EXERCISES
	public void onAddNewExercise(Exercise e) {
		
		if (setsToSave != null) {
			e.setSetList(setsToSave);
		}
		setsToSave = null;
		
        dbWrapper.open();
        
        Exercise savedExercise = dbWrapper.createEntry(e);
		exercises.add(savedExercise);
        dbWrapper.updateExerciseList(workout);
        dbWrapper.close();
        
        if (!firstAdded) {
	        buttonLayout.addView(button2);
	        firstAdded = true;
        }
        
        mAdapter.notifyDataSetChanged();
	}
	
	//*
	// New exercise does not have a database id yet
	public void onEditExercise(long oldId, Exercise newExercise) {
			
		// we need to check whether we actually changed anything
		dbWrapper.open();
		Exercise oldExercise = (Exercise) dbWrapper.getEntryById(oldId, Exercise.class);
		dbWrapper.close();
		
		if (oldExercise.equals(newExercise)) {
			setsToSave = null;
			return; // then we don't want to do anything
		}
			
		// Remove the old exercise of this id from the exercise list of this workout	
		Exercise toRemove = null;
		for (Exercise e : exercises) {
			if (e.getId() == oldId) 
				toRemove = e;
		}
		
		exercises.remove(toRemove);	
        
        onAddNewExercise(newExercise);	
        
	}
	
	//*
	public void onUseExistingExercise(Exercise e) {
		
		dbWrapper.open();
		exercises.add(e);
		dbWrapper.updateExerciseList(workout);
		dbWrapper.close();
		mAdapter.notifyDataSetChanged();
	}
	
	//*
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
		
		setsToSave = null;
				
	}
	
	public void onEditSets(ArrayList<Set> sets) {

		// save the sets into the exercise
		setsToSave = sets;
		
	}
	
	public ArrayList<Set> getSetsToSave() {
		return setsToSave;
	}
	
}