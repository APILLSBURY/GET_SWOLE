package cs65.dartmouth.get_swole;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cs65.dartmouth.get_swole.classes.GetSwoleClass;
import cs65.dartmouth.get_swole.classes.Workout;
import cs65.dartmouth.get_swole.database.DatabaseWrapper;

public class ProgressExerciseListActivity extends ListActivity {

	Context context = this;
	Workout workout;
	ExerciseArrayAdapter exerciseAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DatabaseWrapper dbWrapper = new DatabaseWrapper(this);
		dbWrapper.open();
		workout = (Workout) dbWrapper.getEntryById(getIntent().getExtras().getLong(Globals.ID_TAG), Workout.class);
		exerciseAdapter = new ExerciseArrayAdapter(this, R.layout.workouts_list_row, workout.getExerciseList());
		
		setListAdapter(exerciseAdapter);
				
		// Define the listener interface
        OnItemClickListener listener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	
            	Bundle b = new Bundle();
            	GetSwoleClass e = workout.getExerciseList().get(position);
            	b.putLong(Globals.ID_TAG, e.getId());
            	b.putLong(Globals.ID_TAG_WORKOUT, workout.getId());
            	
            	Intent intent = new Intent(context, ProgressViewActivity.class);
            	intent.putExtras(b);
            	
            	startActivity(intent);   
            }
        };

        // Get the ListView and wired the listener
        ListView listView = getListView();
        listView.setOnItemClickListener(listener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.exercise_progress, menu);
		return true;
	}

}
