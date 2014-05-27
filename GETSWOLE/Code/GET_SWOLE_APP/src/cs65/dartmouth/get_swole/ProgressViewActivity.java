// USING GRAPHVIEW LIBRARY

package cs65.dartmouth.get_swole;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
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
	TextView currentDisplay;
	Button toggleViewButton;
	boolean repsChecked = true;
	
	Exercise exercise; // used to compare goals
	ArrayList<Exercise> instanceExercises; // this and instance workouts are the same size
	ArrayList<WorkoutInstance> instanceWorkouts;
	GraphViewSeries repsDataSeries, weightDataSeries, repsGoalSeries, weightGoalSeries;
	
    
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
			configureData();
			refreshGraphView(); // updates the data
			graphLayout.addView(progressChart);
			
			// Set callbacks for checked buttons
			toggleViewButton = (Button) findViewById(R.id.repsToWeight);
			currentDisplay = (TextView) findViewById(R.id.progressDisplay);
			toggleViewButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					repsChecked = !repsChecked;	
					// change button text
					if (repsChecked ) {
						toggleViewButton.setText(getString(R.string.progress_switch_to_weight));
						currentDisplay.setText(getString(R.string.progress_now_displaying_reps));
					}
					else {
						toggleViewButton.setText(getString(R.string.progress_switch_to_reps));
						currentDisplay.setText(getString(R.string.progress_now_displaying_weight));
					}
					refreshGraphView();
				}
				
			});
		}
		
		TextView name = (TextView) findViewById(R.id.exerciseTitleProgress);
		name.setText(exercise.getName());
	}
	
	private void initializeGraph() {
		progressChart = new LineGraphView(this, exercise.getName() + " Progress");
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
		progressChart.setShowLegend(true);
		progressChart.setLegendAlign(LegendAlign.BOTTOM);
		progressChart.setDrawBackground(true);
		
		
	}
	
	public void configureData() {
		
		int firstIndex = -1;
		int secondIndex = -1;
		
		// reps instance data
		int dataIndex = 0;
		GraphViewData[] tempRepsData = new GraphViewData[instanceWorkouts.size()];
		
		for (int i = 0 ; i < instanceWorkouts.size(); i++) {
			if (instanceExercises.get(i) != null) { // we did this exercise during this workout
				int maxReps = instanceExercises.get(i).getMaxReps();
				if (maxReps != -1) { // we did some reps of this exercise
					if (firstIndex == -1) firstIndex = i;
					else secondIndex = i;
					tempRepsData[dataIndex++] = new GraphViewData(instanceWorkouts.get(i).getTime().getTimeInMillis(), maxReps);
				}
			}
		}			
		GraphViewData [] repsData = clamp(tempRepsData, dataIndex);	
		repsDataSeries = new GraphViewSeries(getString(R.string.progress_actual_line), new GraphViewSeriesStyle(Color.BLUE, 4), repsData);

		// reps goal data - just a straight line
		if (exercise.getMaxReps() != -1) {
			GraphViewData [] repsGoal = new GraphViewData[2];
			repsGoal[0] = new GraphViewData(instanceWorkouts.get(firstIndex).getTime().getTimeInMillis(), exercise.getMaxReps());
			repsGoal[1] = new GraphViewData(instanceWorkouts.get(secondIndex).getTime().getTimeInMillis(), exercise.getMaxReps());
			repsGoalSeries = new GraphViewSeries(getString(R.string.progress_goal_line),  new GraphViewSeriesStyle(Color.RED, 4), repsGoal);
		}
		

		firstIndex = -1;
		secondIndex = -1;
		
		// weight instance data
		dataIndex = 0;
		GraphViewData [] tempWeightData = new GraphViewData[instanceWorkouts.size()];
		
		for (int i = 0; i < instanceWorkouts.size(); i++) {
			if (instanceExercises.get(i) != null) { // we did this exercise during this workout 
				int maxWeight = instanceExercises.get(i).getMaxWeight();
				if (maxWeight != -1) {
					if (firstIndex == -1) firstIndex = i;
					else secondIndex = i;
					tempWeightData[dataIndex++] = new GraphViewData(instanceWorkouts.get(i).getTime().getTimeInMillis(), maxWeight);
				}
			}
		}
		
		GraphViewData [] weightData = clamp(tempWeightData, dataIndex);	
		weightDataSeries = new GraphViewSeries(getString(R.string.progress_actual_line), new GraphViewSeriesStyle(Color.BLUE, 4), weightData);

		// weight goal data
		if (exercise.getMaxWeight() != -1) {
			GraphViewData [] weightGoal = new GraphViewData[2];
			weightGoal[0] = new GraphViewData(instanceWorkouts.get(firstIndex).getTime().getTimeInMillis(), exercise.getMaxWeight());
			weightGoal[1] = new GraphViewData(instanceWorkouts.get(secondIndex).getTime().getTimeInMillis(), exercise.getMaxWeight());
			weightGoalSeries = new GraphViewSeries(getString(R.string.progress_goal_line), new GraphViewSeriesStyle(Color.RED, 4), weightGoal);
		}
				
	}
	
	// Based on checkbox preferences and exercise instances
	public void refreshGraphView() {
		
		progressChart.removeAllSeries();
		
		if (repsChecked) {
			
			progressChart.addSeries(repsDataSeries);
			if (repsGoalSeries != null)
				progressChart.addSeries(repsGoalSeries);
		
			
		}
		else { // weight is checked		
			progressChart.addSeries(weightDataSeries);
			if (weightGoalSeries != null)
				progressChart.addSeries(weightGoalSeries);
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
