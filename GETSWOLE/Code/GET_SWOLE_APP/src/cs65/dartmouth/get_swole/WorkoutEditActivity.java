package cs65.dartmouth.get_swole;

import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
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
		Button button2 = new Button(this);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		buttonLayout.addView(button2, params);
		
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
		
		DatabaseWrapper dbWrapper = new DatabaseWrapper(this);
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
        ExercisesAdapter mAdapter = new ExercisesAdapter(this, R.layout.exercises_list_row, exercises);

        // Assign the adapter to ListView
        ListView listView = (ListView) findViewById(R.id.exerciseListView);
        listView.setAdapter(mAdapter);

        // Define the listener interface
        OnItemClickListener mListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Open dialog to edit this exercise
            	Exercise e = exercises.get(position);
            	DialogFragment fragment;
            	fragment = AppDialogFragment.newInstance(e);
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
		DatabaseWrapper dbWrapper = new DatabaseWrapper(this);
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
		
		
	}
	
	public void onAddNewExercise(Exercise e) {
        
		DatabaseWrapper dbWrapper = new DatabaseWrapper(this);
        dbWrapper.open();
        
        Exercise savedExercise = dbWrapper.createEntry(e);
		exercises.add(savedExercise);
		
        dbWrapper.updateExerciseList(workout);
        dbWrapper.close();
        
        // update the screen too?? 
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
	
	public void onDeleteExercise(long id) {
		// Delete this exercise from THIS WORKOUT'S LIST. Not the exercise database
		for (Exercise e : exercises) {
			if (e.getId() == id) 
				exercises.remove(e);
		}
		
		DatabaseWrapper dbWrapper = new DatabaseWrapper(this);
		dbWrapper.open();
		dbWrapper.updateExerciseList(workout);
		dbWrapper.close();
	}
	
	/**
	 * 
	 * PRIVATE CLASSES
	 *
	 */
	private class ExercisesAdapter extends ArrayAdapter<Exercise> {
    	
    	private Context context;
    	private List<Exercise> exercises;
    	
    	public ExercisesAdapter(Context context, int resource, List<Exercise> exercises) {
    		super(context, resource, exercises);
    		this.exercises = exercises;
    		this.context = context;	       
    	}
    	
    	
    	@Override 
    	public View getView(int position, View convertView, ViewGroup parent) {
    		
    		// Inflate the layout for a row
    		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	    View rowView = inflater.inflate(R.layout.exercises_list_row, parent, false);
    	    
    	    // Access the textviews to set
    	    TextView exerciseView = (TextView) rowView.findViewById(R.id.exercise_list_single_row);    		
    	    Exercise e = exercises.get(position);
    	    exerciseView.setText(e.getName());
    	    
    	    return rowView;
    	}
    }
	
}