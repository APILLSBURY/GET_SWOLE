package cs65.dartmouth.get_swole;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import cs65.dartmouth.get_swole.classes.Exercise;
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
		
		long id= getIntent().getExtras().getLong(Globals.ID_TAG, -1L); // get the id of the workout instance
		DatabaseWrapper dbWrapper = new DatabaseWrapper(this);
		dbWrapper.open();
		instance = (WorkoutInstance) dbWrapper.getEntryById(id, WorkoutInstance.class);
		dbWrapper.close();
		
		// Define a new adapter
        mAdapter = new ExerciseArrayAdapter(this, R.layout.exercises_list_row, instance.getExerciseList());

        // Assign the adapter to ListView
        ListView listView = (ListView) findViewById(R.id.completedExerciseListView);
        listView.setAdapter(mAdapter);

        // Define the listener interface
        OnItemClickListener mListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Open dialog to edit this exercise
            	DialogFragment fragment = AppDialogFragment.newInstance(instance, 2, position);
    	        fragment.show(getFragmentManager(), getString(R.string.dialog_fragment_tag_edit_exercise));
            		
            }
        };

        // Get the ListView and wired the listener
        listView.setOnItemClickListener(mListener); 
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_workout_instance, menu);
		return true;
	}

}
