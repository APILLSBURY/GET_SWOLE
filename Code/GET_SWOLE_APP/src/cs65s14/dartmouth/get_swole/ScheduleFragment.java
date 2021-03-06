//THIS CODE IS BASED OFF OF CODE FROM https://github.com/mukesh4u/Android-Calendar-Sync
package cs65s14.dartmouth.get_swole;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cs65s14.dartmouth.get_swole.R;
import cs65s14.dartmouth.get_swole.classes.CalendarAdapter;
import cs65s14.dartmouth.get_swole.classes.Frequency;
import cs65s14.dartmouth.get_swole.classes.GetSwoleClass;
import cs65s14.dartmouth.get_swole.classes.Workout;
import cs65s14.dartmouth.get_swole.classes.WorkoutInstance;
import cs65s14.dartmouth.get_swole.classes.WorkoutsAdapter;
import cs65s14.dartmouth.get_swole.database.DatabaseWrapper;

public class ScheduleFragment extends ListFragment {
	
	public GregorianCalendar month;// calendar instances.

	public CalendarAdapter adapter;// adapter instance
	public ArrayList<String> items; // container to store calendar items which needs showing the event marker
	private View view;
	private Context context;
	private DatabaseWrapper dbWrapper;
	private WorkoutsAdapter workoutsAdapter;
	private ArrayList<ArrayList<GetSwoleClass>> workoutsByDay;
	private int selectedGridvalue;
	private Calendar selectedDay;
	
	//BASED ON METHOD FROM https://github.com/mukesh4u/Android-Calendar-Sync, but customized for my purposes
	// The schedule button is mine
	@Override
	public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
		context = getActivity();
		this.view = inflater.inflate(R.layout.fragment_schedule, null, false);
		dbWrapper = new DatabaseWrapper(context);
		
		Locale.setDefault(Locale.US);

		month = (GregorianCalendar) GregorianCalendar.getInstance();
		selectedDay = (Calendar) month.clone();
		selectedGridvalue = selectedDay.get(Calendar.DATE);

		getWorkoutsByDay();
		adapter = new CalendarAdapter(context, month, workoutsByDay);
		
		GridView gridview = (GridView) view.findViewById(R.id.gridview);
		gridview.setAdapter(adapter);

		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

		
		//set up the previous button
		RelativeLayout previous = (RelativeLayout) view.findViewById(R.id.previous);

		previous.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setPreviousMonth();
				refreshCalendar();
			}
		});

		
		//set up the next button
		RelativeLayout next = (RelativeLayout) view.findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setNextMonth();
				refreshCalendar();
			}
		});
		
		
		//set up the schedule button
		Button scheduleButton = (Button) view.findViewById(R.id.scheduleWorkoutsToday);
		scheduleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment fragment = AppDialogFragment.newInstanceScheduleNew(selectedDay);
				fragment.show(getActivity().getFragmentManager(), getString(R.string.dialog_fragment_tag_schedule_new));
			}
		});

		
		//set up the gridview
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				String selectedGridDate = CalendarAdapter.dayString.get(position);
				String[] separatedTime = selectedGridDate.split("-");
				String gridvalueString = separatedTime[2].replaceFirst("^0*", "");// taking last part of date. ie; 2 from 2012-12-02.
				int gridvalue = Integer.parseInt(gridvalueString);
				// navigate to next or previous month on clicking offdays.
				if ((gridvalue > 10) && (position < 8)) {
					setPreviousMonth();
					refreshCalendar();
				}
				else if ((gridvalue < 7) && (position > 28)) {
					setNextMonth();
					refreshCalendar();
				}
				else {
					selectedGridvalue = gridvalue;
					selectedDay.set(Calendar.DATE, selectedGridvalue); 
					((CalendarAdapter) parent.getAdapter()).setSelected(v, selectedDay);
					configureListView(workoutsByDay.get(selectedGridvalue));
				}
			}
		});
		
		//set up the listview
		configureListView(workoutsByDay.get(selectedGridvalue));
		return view;
	}
	
	//BASED ON METHOD FROM https://github.com/mukesh4u/Android-Calendar-Sync
	protected void setNextMonth() {
		if (month.get(GregorianCalendar.MONTH) == month.getActualMaximum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) + 1), month.getActualMinimum(GregorianCalendar.MONTH), 1);
		}
		else {
			month.set(GregorianCalendar.MONTH, month.get(GregorianCalendar.MONTH) + 1);
		}
		if (selectedDay.get(Calendar.DATE) > month.getActualMaximum(Calendar.DATE)) {
			selectedDay.set(Calendar.DATE, month.getActualMaximum(Calendar.DATE));
			selectedGridvalue = selectedDay.get(Calendar.DATE);
		}
		selectedDay.set(Calendar.MONTH, month.get(Calendar.MONTH));
		adapter.setSelectedDate(selectedDay);
		getWorkoutsByDay();
		adapter.setWorkoutsByDay(workoutsByDay);
		configureListView(workoutsByDay.get(selectedGridvalue));
	}

	//BASED ON METHOD FROM https://github.com/mukesh4u/Android-Calendar-Sync
	protected void setPreviousMonth() {
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMinimum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) - 1),
					month.getActualMaximum(GregorianCalendar.MONTH), 1);
		} else {
			month.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) - 1);
		}
		if (selectedDay.get(Calendar.DATE) > month.getActualMaximum(Calendar.DATE)) {
			selectedDay.set(Calendar.DATE, month.getActualMaximum(Calendar.DATE));
			selectedGridvalue = selectedDay.get(Calendar.DATE);
		}
		selectedDay.set(Calendar.MONTH, month.get(Calendar.MONTH));
		selectedGridvalue = selectedDay.get(Calendar.DATE);
		adapter.setSelectedDate(selectedDay);
		getWorkoutsByDay();
		adapter.setWorkoutsByDay(workoutsByDay);
		configureListView(workoutsByDay.get(selectedGridvalue));
	}

	//METHOD FROM https://github.com/mukesh4u/Android-Calendar-Sync
	protected void showToast(String string) {
		Toast.makeText(getActivity().getApplicationContext(), string, Toast.LENGTH_SHORT).show();
	}

	public void updateCalendar() {
		getWorkoutsByDay();
		adapter.setSelectedDate(selectedDay);
		adapter.setWorkoutsByDay(workoutsByDay);
		configureListView(workoutsByDay.get(selectedGridvalue));
		refreshCalendar();
	}

	//BASED ON METHOD FROM https://github.com/mukesh4u/Android-Calendar-Sync
	public void refreshCalendar() {
		TextView title = (TextView) view.findViewById(R.id.title);
		adapter.refreshDays();
		adapter.notifyDataSetChanged();

		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	}

	//BASED ON METHOD FROM https://github.com/mukesh4u/Android-Calendar-Sync
	public Runnable calendarUpdater = new Runnable() {

		@Override
		public void run() {
			items.clear();

			// Print dates of the current week
			adapter.setItems(items);
			adapter.notifyDataSetChanged();
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();
		updateCalendar();
	}
	
	
	//MY CODE
	private void configureListView(ArrayList<GetSwoleClass> workouts) {
				
		workoutsAdapter = new WorkoutsAdapter(context, R.layout.workouts_list_row_small, workouts);
		
		setListAdapter(workoutsAdapter);
		
		// Define the listener interface
		OnItemClickListener listener = new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				GetSwoleClass w = (GetSwoleClass) parent.getItemAtPosition(position);
		    	if (w instanceof WorkoutInstance) {
		    		// start a new activity to display this workoutinstance
		    		Intent intent = new Intent(getActivity(), ViewWorkoutInstanceActivity.class);
		    		Bundle b = new Bundle();
		    		b.putLong(Globals.ID_TAG, w.getId());
		    		intent.putExtras(b);
		    		startActivity(intent);
		    	}
		    	else { // we want to launch a dialog to either change the scheduling or do the workout
		    		DialogFragment fragment = AppDialogFragment.newInstanceSchedule(w, selectedDay.getTimeInMillis(), AppDialogFragment.DIALOG_ID_SCHEDULE_UPDATE);
					fragment.show(getActivity().getFragmentManager(), getString(R.string.dialog_fragment_tag_schedule_new));
		    	}
		    }
		};
		
		// Get the ListView and wired the listener
		ListView listView = (ListView) view.findViewById(android.R.id.list);
		listView.setOnItemClickListener(listener);
	}
	
	//MY CODE
	private void getWorkoutsByDay() {
		dbWrapper.open();
		List<Workout> allWorkouts = dbWrapper.getAllEntries(Workout.class);
		List<WorkoutInstance> allWorkoutInstances = dbWrapper.getAllEntries(WorkoutInstance.class);
		dbWrapper.close();
		workoutsByDay = new ArrayList<ArrayList<GetSwoleClass>>();
		workoutsByDay.add(null); //add a null value for day 0
		int daysInMonth = month.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		Log.d(Globals.TAG, "daysInMonth=" + daysInMonth);
		GregorianCalendar currDay = (GregorianCalendar) month.clone();
		currDay.set(GregorianCalendar.DATE, currDay.getActualMinimum(GregorianCalendar.DATE)); //set to first of month
		
		boolean end = false;
		while (!end) {
			workoutsByDay.add(getDailyWorkouts(currDay, allWorkouts, allWorkoutInstances));
			
			//Log.d(Globals.TAG, "currDay = " + currDay.get(GregorianCalendar.DATE));
			if ((currDay.get(GregorianCalendar.DATE) + 1) > daysInMonth) {
				end = true;
			}
			else {
				currDay.set(GregorianCalendar.DATE, currDay.get(GregorianCalendar.DATE) + 1);
			}
		}
	}
	
	public static ArrayList<GetSwoleClass> getDailyWorkouts(Calendar currDay, List<Workout> allWorkouts, List<WorkoutInstance> allWorkoutInstances) {
		ArrayList<Calendar> scheduledDates;
		ArrayList<Frequency> frequencyList;
		ArrayList<GetSwoleClass> dailyWorkouts = new ArrayList<GetSwoleClass>();
		
		if (allWorkoutInstances != null) {
			//add completed workouts
			if (CalendarUtility.testDateEquality(currDay, Calendar.getInstance()) != CalendarUtility.GREATER_THAN) {
				for (WorkoutInstance wi : allWorkoutInstances) {
					if (CalendarUtility.testDateEquality(currDay, wi.getTime()) == CalendarUtility.EQUALS) {
						dailyWorkouts.add(wi);
					}
				}
			}
		}
		
		//add upcoming scheduled workouts
		if (CalendarUtility.testDateEquality(currDay, Calendar.getInstance()) != CalendarUtility.LESS_THAN) {
			for (Workout w : allWorkouts) {
				
				scheduledDates = w.getScheduledDates();
				for (Calendar c : scheduledDates) {
					if (CalendarUtility.testDateEquality(c, currDay) == CalendarUtility.EQUALS) {
						dailyWorkouts.add(w);
						Log.d(Globals.TAG, "date " + currDay.get(GregorianCalendar.DATE) + " is in scheduled dates!");
					}
				}
				frequencyList = w.getFrequencyList();
				for (Frequency f : frequencyList) {
					if (f.includesDate(currDay)) {
						Log.d(Globals.TAG, "date " + currDay.get(GregorianCalendar.DATE) + " is in frequency!");
						dailyWorkouts.add(w);
					}
				}
			}
		}
		return dailyWorkouts;
	}
}

