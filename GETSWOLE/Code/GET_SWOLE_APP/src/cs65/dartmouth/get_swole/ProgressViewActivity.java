package cs65.dartmouth.get_swole;

import java.awt.Checkbox;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import cs65.dartmouth.get_swole.classes.Exercise;
import cs65.dartmouth.get_swole.classes.WorkoutInstance;
import cs65.dartmouth.get_swole.database.DatabaseWrapper;

public class ProgressViewActivity extends Activity {
	LineGraphView progressChart;
	CheckBox weightCheckbox;
	CheckBox repsCheckbox;
	boolean repsChecked;
	boolean weightChecked;
	
	Exercise exercise; // used to compare goals
	ArrayList<Exercise> instanceExercises;
	ArrayList<WorkoutInstance> instanceWorkouts;
	GraphViewSeries repsDataSeries;
	GraphViewSeries weightDataSeries;
	
	final DateFormat dateTimeFormatter = DateFormat.getDateTimeInstance();        
    
    
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
		repsCheckbox = (CheckBox) findViewById(R.id.checkBoxReps);
		weightCheckbox = (CheckBox) findViewById(R.id.checkBoxWeight);
		repsCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!isChecked && !weightChecked) repsCheckbox.setChecked(true);
				else {
					repsChecked = isChecked;	
					refreshGraphView();
				}
			}
			
		});
		weightCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!isChecked && !repsChecked) weightCheckbox.setChecked(true);
				else {
					weightChecked = isChecked;
					refreshGraphView();
				}
			}
		});
		repsCheckbox.setChecked(true);
		weightCheckbox.setChecked(true);
		
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
		progressChart.setCustomLabelFormatter(new CustomLabelFormatter() 
	    {
	        @Override
	        public String formatLabel(double value, boolean isValueX) 
	        {
	            if (isValueX)
	            {
	                return dateTimeFormatter.format(new Date((long) value));
	            }
	            return null; // let graphview generate Y-axis label for us
	        }
	    });
	}
	
	// Based on checkbox preferences and exercise instances
	public void refreshGraphView() {

		GraphViewData[] repsData, weightData;
		
		if (repsChecked) {
			int dataIndex = 0;
			GraphViewData[] tempRepsData = new GraphViewData[instanceWorkouts.size()];
			
			for (int i = 0 ; i < instanceWorkouts.size(); i++) {
				if (instanceExercises.get(i) != null) { // we did this exercise during this workout
					int maxReps = instanceExercises.get(i).getMaxReps();
					if (maxReps != -1) // we did some reps of this exercise
						tempRepsData[dataIndex++] = new GraphViewData(instanceWorkouts.get(i).getTime().getTimeInMillis(), maxReps);
				}
			}			
			repsData = clamp(tempRepsData, dataIndex);
			
		}
		else repsData = new GraphViewData [] {};
		
		if (repsDataSeries == null)  {
			repsDataSeries = new GraphViewSeries(repsData);
			progressChart.addSeries(repsDataSeries);
		}
		else repsDataSeries.resetData(repsData);		
		
		if (weightChecked) {
			int dataIndex = 0;
			GraphViewData [] tempWeightData = new GraphViewData[instanceWorkouts.size()];
			
			for (int i = 0; i < instanceWorkouts.size(); i++) {
				if (instanceExercises.get(i) != null) {
					int maxWeight = instanceExercises.get(i).getMaxWeight();
					if (maxWeight != -1) tempWeightData[dataIndex++] = new GraphViewData(instanceWorkouts.get(i).getTime().getTimeInMillis(), maxWeight);
				}
			}
			
			weightData = clamp(tempWeightData, dataIndex);
		}
		else weightData = new GraphViewData [] {};

		if (weightDataSeries == null) {
			weightDataSeries = new GraphViewSeries(weightData);
			progressChart.addSeries(weightDataSeries);
		}
		else weightDataSeries.resetData(weightData);
	}

	// might be to large with empty spaces
	private GraphViewData[] clamp(GraphViewData[] data, int size) {

		GraphViewData[] clamped = new GraphViewData[size];
		for (int i = 0; i < size; i++)
			clamped[i] = data[i];
		return clamped;
	}

}
