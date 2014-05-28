package cs65s14.dartmouth.get_swole;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import cs65s14.dartmouth.get_swole.R;
import cs65s14.dartmouth.get_swole.classes.ExerciseArrayAdapter;
import cs65s14.dartmouth.get_swole.classes.GetSwoleClass;
import cs65s14.dartmouth.get_swole.classes.Workout;
import cs65s14.dartmouth.get_swole.database.DatabaseWrapper;

public class ProgressExerciseListActivity extends Activity {

	Context context = this;
	Workout workout;
	ExerciseArrayAdapter exerciseAdapter;
	ListView exerciseList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_exercise_progress);	
		
		exerciseList = (ListView) findViewById(R.id.progressExerciseListView);
		TextView workoutName = (TextView) findViewById(R.id.progressWorkoutName);
		
		DatabaseWrapper dbWrapper = new DatabaseWrapper(this);
		dbWrapper.open();
		workout = (Workout) dbWrapper.getEntryById(getIntent().getExtras().getLong(Globals.ID_TAG), Workout.class);
		dbWrapper.close();
		exerciseAdapter = new ExerciseArrayAdapter(this, R.layout.exercises_list_row, workout.getExerciseList());
		
		workoutName.setText(workout.getName());
		exerciseList.setAdapter(exerciseAdapter);
				
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
        exerciseList.setOnItemClickListener(listener);
	}

}
