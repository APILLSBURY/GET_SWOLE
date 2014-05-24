package cs65.dartmouth.get_swole;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LineGraphView;

import cs65.dartmouth.get_swole.classes.Exercise;
import cs65.dartmouth.get_swole.classes.WorkoutInstance;
import cs65.dartmouth.get_swole.database.DatabaseWrapper;

public class ProgressViewActivity extends Activity {
	GraphView progressChart;
	boolean repsChecked;
	boolean weightChecked;
	Exercise exercise; // used to compare goals
	ArrayList<WorkoutInstance> instances;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress_view);
		
		// Get exercise that we are looking at
		long eId = getIntent().getExtras().getLong(Globals.ID_TAG);
		long wId = getIntent().getExtras().getLong(Globals.ID_TAG_WORKOUT);
		
		DatabaseWrapper dbWrapper = new DatabaseWrapper(this);
		dbWrapper.open();
		exercise = (Exercise) dbWrapper.getEntryById(eId, Exercise.class);
		
		// We need to get all workoutinstances of this workout 
		instances = dbWrapper.getAllInstances(wId);
		dbWrapper.close();

		// Create the graph
		FrameLayout graphLayout = (FrameLayout) findViewById(R.id.graphLayout);
		progressChart = new LineGraphView(this, exercise.getName());
		
		// Will add data series based on preferences
		refreshGraphView();
		
		graphLayout.addView(progressChart);
		
		// Set callbacks for checked buttons
		CheckBox repsCheckbox = (CheckBox) findViewById(R.id.checkBoxReps);
		CheckBox weightCheckbox = (CheckBox) findViewById(R.id.checkBoxWeight);
		repsCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				repsChecked = isChecked;
				refreshGraphView();
			}
			
		});
		weightCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				weightChecked = isChecked;
				refreshGraphView();
			}
		});
		
		
	}
	
	public void refreshGraphView() {
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.progress_view, menu);
		return true;
	}

}
