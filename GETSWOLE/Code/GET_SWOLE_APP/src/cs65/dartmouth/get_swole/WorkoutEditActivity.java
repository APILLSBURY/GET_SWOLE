package cs65.dartmouth.get_swole;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
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
		TextView nameView = (TextView) findViewById(R.id.workoutName);
					
		long id = getIntent().getExtras().getLong(Globals.ID_TAG, -1L);
		if (id == -1) { // we are creating a new workout
			
			workout = new Workout(getIntent().getExtras().getString(Globals.NAME_TAG));
			
			nameView.setText(workout.getName());
			
			exercises = workout.getExerciseList();
			
		}
		else {
			DatabaseWrapper dbWrapper = new DatabaseWrapper(this);
			dbWrapper.open();
			workout = (Workout) dbWrapper.getEntryById(id, Workout.class);
	
			nameView.setText(workout.getName());
		
			exercises = workout.getExerciseList();
		}
		
		// Define a new adapter
        ExercisesAdapter mAdapter = new ExercisesAdapter(this, R.layout.exercises_list_row, exercises);

        // Assign the adapter to ListView
        ListView listView = (ListView) findViewById(R.id.exerciseListView);
        listView.setAdapter(mAdapter);

        // Define the listener interface
        OnItemClickListener mListener = new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Open dialog to edit this exercise
                    		
                    }
        };

        // Get the ListView and wired the listener
        listView.setOnItemClickListener(mListener);
		
	}
	
	public void onStartWorkout() {
		
		Bundle b = new Bundle();
		b.putLong(Globals.ID_TAG, workout.getId());
		
		Intent intent = new Intent(this, WorkoutDoActivity.class);
		intent.putExtras(b);
		
		startActivity(intent);
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