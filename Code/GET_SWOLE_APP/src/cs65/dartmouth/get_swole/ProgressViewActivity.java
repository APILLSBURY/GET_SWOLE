// USING GRAPHVIEW LIBRARY

package cs65.dartmouth.get_swole;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;

import cs65.dartmouth.get_swole.classes.Exercise;
import cs65.dartmouth.get_swole.classes.WorkoutInstance;
import cs65.dartmouth.get_swole.database.DatabaseWrapper;

public class ProgressViewActivity extends Activity {
	static final String [] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	final DateFormat dateTimeFormatter = DateFormat.getDateTimeInstance();        
    final GraphViewData [] empty_data = new GraphViewData[] {};
    
	LineGraphView progressChart;
	ToggleButton toggleButton;
	boolean repsChecked = true;
	
	Exercise exercise; // used to compare goals
	ArrayList<Exercise> instanceExercises; // this and instance workouts are the same size
	ArrayList<WorkoutInstance> instanceWorkouts;
	GraphViewData[] repsData, weightData;
	
    
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
		instanceWorkouts = dbWrapper.getAllInstancesWorkout(wId);
		dbWrapper.close();
		
		// We need to grab the exercises from these instances
		int numExercises = 0;
		instanceExercises = new ArrayList<Exercise>();
		for (WorkoutInstance wi : instanceWorkouts) {
			
			boolean added = false;
			for (Exercise e : wi.getExerciseList()) {
				if (e.getOldId() == eId) {
					instanceExercises.add(e);
					numExercises++;
					added = true;
					break;
				}			
			}
			if (!added) {
				instanceExercises.add(null); // place holder so that instance exercises is the same size as instance workouts
			}	
		}
		
		// Create the graph
		LinearLayout graphLayout = (LinearLayout) findViewById(R.id.graphBox);
					
		if (numExercises < 2) { // not enough data
			TextView notEnoughText = new TextView(this);
			notEnoughText.setText(getString(R.string.not_enough_data));
			notEnoughText.setTextColor(Color.RED);
			notEnoughText.setTextSize(30);
			graphLayout.addView(notEnoughText);
		}
		else { // display the graph
			
			initializeGraph(); // creates the graph
			getExerciseInstanceData();
			refreshGraphView(); // updates the data
			graphLayout.addView(progressChart);
			
			// Set callbacks for checked buttons
			toggleButton = (ToggleButton) findViewById(R.id.repsToWeight);
			toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					repsChecked = !repsChecked;	
					refreshGraphView();
				}
				
			});
		}
		
		TextView name = (TextView) findViewById(R.id.exerciseTitleProgress);
		name.setText(exercise.getName());
	}
	
	private void initializeGraph() {
		progressChart = new LineGraphView(this, exercise.getName() + " Progress");
		progressChart.getGraphViewStyle().setGridColor(Color.DKGRAY);
		progressChart.getGraphViewStyle().setHorizontalLabelsColor(Color.DKGRAY);
		progressChart.getGraphViewStyle().setVerticalLabelsColor(Color.DKGRAY);
		progressChart.setCustomLabelFormatter(new CustomLabelFormatter() 
	    {
	        @Override
	        public String formatLabel(double value, boolean isValueX) 
	        {
	            if (isValueX)
	            {
	               Calendar c = Calendar.getInstance();
	               c.setTimeInMillis((long) value);
	               return MONTHS[c.get(Calendar.MONTH)]+c.get(Calendar.DAY_OF_MONTH);
	            }
	            return null;
	        }
	    });		
		//progressChart.setShowLegend(true);
		//progressChart.setLegendAlign(legendAlign
		
	}
	
	public void getExerciseInstanceData() {
		
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
		
		dataIndex = 0;
		GraphViewData [] tempWeightData = new GraphViewData[instanceWorkouts.size()];
		
		for (int i = 0; i < instanceWorkouts.size(); i++) {
			if (instanceExercises.get(i) != null) { // we did this exercise during this workout 
				int maxWeight = instanceExercises.get(i).getMaxWeight();
				if (maxWeight != -1) 
					tempWeightData[dataIndex++] = new GraphViewData(instanceWorkouts.get(i).getTime().getTimeInMillis(), maxWeight);
			}
		}
		
		weightData = clamp(tempWeightData, dataIndex);	
	}
	
	// Based on checkbox preferences and exercise instances
	public void refreshGraphView() {
		
		progressChart.removeAllSeries();
		
		if (repsChecked) {
			
			GraphViewSeries repsDataSeries = new GraphViewSeries(getString(R.string.progress_reps_line), new GraphViewSeriesStyle(Color.RED, 4), repsData);
			progressChart.addSeries(repsDataSeries);
		
			
		}
		else { // weight is checked		
			GraphViewSeries weightDataSeries = new GraphViewSeries(getString(R.string.progress_weight_line), new GraphViewSeriesStyle(Color.BLUE, 4), weightData);
			progressChart.addSeries(weightDataSeries);
		}

	}

	// might be to large with empty spaces
	private GraphViewData[] clamp(GraphViewData[] data, int size) {

		GraphViewData[] clamped = new GraphViewData[size];
		for (int i = 0; i < size; i++)
			clamped[i] = data[i];
		return clamped;
	}

}
