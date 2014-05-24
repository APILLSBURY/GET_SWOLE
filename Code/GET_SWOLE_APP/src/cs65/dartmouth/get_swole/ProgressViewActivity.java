package cs65.dartmouth.get_swole;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import cs65.dartmouth.get_swole.classes.Exercise;
import cs65.dartmouth.get_swole.classes.WorkoutInstance;
import cs65.dartmouth.get_swole.database.DatabaseWrapper;

public class ProgressViewActivity extends Activity {
	LineGraphView progressChart;
	boolean repsChecked;
	boolean weightChecked;
	Exercise exercise; // used to compare goals
	ArrayList<Exercise> instanceExercises;
	ArrayList<WorkoutInstance> instanceWorkouts;
	GraphViewSeries repsDataSeries;
	
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
		instanceWorkouts = dbWrapper.getAllInstances(wId);
		dbWrapper.close();

		// We need to grab the exercises from these instances
		instanceExercises = new ArrayList<Exercise>();
		for (WorkoutInstance wi : instanceWorkouts) {
			
			boolean added = false;
			for (Exercise e : wi.getExerciseList()) {
				if (e.getId() == eId) {
					instanceExercises.add(e);
					added = true;
					break;
				}			
			}
			if (!added) {
				instanceExercises.add(null); // place holder so that instance exercises is the same size as instance workouts
			}	
		}
		
		// Create the graph
		FrameLayout graphLayout = (FrameLayout) findViewById(R.id.graphLayout);
		initializeGraph();
		
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
		
		TextView name = (TextView) findViewById(R.id.exerciseTitleProgress);
		name.setText(exercise.getName());
	}
	
	private void initializeGraph() {
		progressChart = new LineGraphView(this, exercise.getName());
		progressChart.getGraphViewStyle().setGridColor(Color.GREEN);
		progressChart.getGraphViewStyle().setHorizontalLabelsColor(Color.DKGRAY);
		progressChart.getGraphViewStyle().setVerticalLabelsColor(Color.DKGRAY);
		progressChart.getGraphViewStyle().setTextSize(20);
		progressChart.setBackgroundColor(Color.BLACK);
	}
	
	// Based on checkbox preferences and exercise instances
	public void refreshGraphView() {

		GraphViewData[] repsData;
		
		if (repsChecked) {
		
			repsData = new GraphViewData [] {
					new GraphViewData(1, 2),
					new GraphViewData(2, 3),
					new GraphViewData(3,3)
			};
			/*
			int dataIndex = 0;
			GraphViewData [] repsData = new GraphViewData[instanceWorkouts.size()];
			
			for (int i = 0 ; i < instanceWorkouts.size(); i++) {
				if (instanceExercises.get(i) != null) {
					int maxReps = instanceExercises.get(i).getMaxReps();
					if (maxReps != -1) repsData[dataIndex++] = new GraphViewData(instanceWorkouts.get(i).getTime().getTimeInMillis(), maxReps);
				}
			}
			*/
			
		}
		else {
			repsData = new GraphViewData [] {};
		}
		
		if (repsDataSeries == null)  {
			repsDataSeries = new GraphViewSeries(repsData);
			progressChart.addSeries(repsDataSeries);

		}
		else repsDataSeries.resetData(repsData);
		
		/*
		if (weightChecked) {
			int dataIndex = 0;
			GraphViewData [] weightData = new GraphViewData[instanceWorkouts.size()];
			
			for (int i = 0; i < instanceWorkouts.size(); i++) {
				if (instanceExercises.get(i) != null) {
					int maxWeight = instanceExercises.get(i).getMaxWeight();
					if (maxWeight != -1) weightData[dataIndex++] = new GraphViewData(instanceWorkouts.get(i).getTime().getTimeInMillis(), maxWeight);
				}
			}
			
			progressChart.addSeries(new GraphViewSeries(weightData));
		}*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.progress_view, menu);
		return true;
	}

}
