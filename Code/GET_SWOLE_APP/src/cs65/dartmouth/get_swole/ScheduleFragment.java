//THIS CODE IS BASED OFF OF CODE FROM https://github.com/mukesh4u/Android-Calendar-Sync

package cs65.dartmouth.get_swole;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import cs65.dartmouth.get_swole.classes.CalendarAdapter;
import cs65.dartmouth.get_swole.classes.Frequency;
import cs65.dartmouth.get_swole.classes.Workout;
import cs65.dartmouth.get_swole.classes.WorkoutsAdapter;
import cs65.dartmouth.get_swole.database.DatabaseWrapper;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleFragment extends ListFragment {
	
	public GregorianCalendar month, itemmonth;// calendar instances.

	public CalendarAdapter adapter;// adapter instance
	public Handler handler;	// for grabbing some event values for showing the dot marker.
	public ArrayList<String> items; // container to store calendar items which needs showing the event marker
	private View view;
	private Context context;
	private DatabaseWrapper dbWrapper;
	private WorkoutsAdapter workoutsAdapter;
	private ArrayList<ArrayList<Workout>> workoutsByDay;
	private int selectedGridvalue = 1;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
		context = getActivity();
		this.view = inflater.inflate(R.layout.fragment_schedule, null, false);
		dbWrapper = new DatabaseWrapper(context);
		
		Locale.setDefault(Locale.US);

		month = (GregorianCalendar) GregorianCalendar.getInstance();
		itemmonth = (GregorianCalendar) month.clone();

		items = new ArrayList<String>();

		workoutsByDay = getWorkoutsByDay();
		adapter = new CalendarAdapter(context, month, workoutsByDay);

		GridView gridview = (GridView) view.findViewById(R.id.gridview);
		gridview.setAdapter(adapter);

		handler = new Handler();
		handler.post(calendarUpdater);

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

		
		//set up the gridview
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				((CalendarAdapter) parent.getAdapter()).setSelected(v);
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
					configureListView(workoutsByDay.get(selectedGridvalue));
				}
			}
		});
		
		//set up the listview
		configureListView(workoutsByDay.get(selectedGridvalue));
		
		return view;
	}
	
	protected void setNextMonth() {
		if (month.get(GregorianCalendar.MONTH) == month.getActualMaximum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) + 1), month.getActualMinimum(GregorianCalendar.MONTH), 1);
		}
		else {
			month.set(GregorianCalendar.MONTH, month.get(GregorianCalendar.MONTH) + 1);
		}

	}

	protected void setPreviousMonth() {
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMinimum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) - 1),
					month.getActualMaximum(GregorianCalendar.MONTH), 1);
		} else {
			month.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) - 1);
		}

	}

	protected void showToast(String string) {
		Toast.makeText(getActivity().getApplicationContext(), string, Toast.LENGTH_SHORT).show();
	}

	public void refreshCalendar() {
		TextView title = (TextView) view.findViewById(R.id.title);

		adapter.refreshDays();
		adapter.notifyDataSetChanged();
		handler.post(calendarUpdater); // generate some calendar items

		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	}

	public Runnable calendarUpdater = new Runnable() {

		@Override
		public void run() {
			items.clear();

			// Print dates of the current week
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			adapter.setItems(items);
			adapter.notifyDataSetChanged();
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();
		configureListView(workoutsByDay.get(selectedGridvalue));
	}
	
	private void configureListView(ArrayList<Workout> workouts) {
				
		workoutsAdapter = new WorkoutsAdapter(context, R.layout.workouts_list_row, workouts);
		
		setListAdapter(workoutsAdapter);
		
		// Define the listener interface
		OnItemClickListener listener = new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Workout w = (Workout) parent.getItemAtPosition(position);
		    	showToast("workout " + w.getName() + " clicked"); 
		    }
		};
		
		// Get the ListView and wired the listener
		ListView listView = (ListView) view.findViewById(android.R.id.list);
		listView.setOnItemClickListener(listener);
	}
	
	private ArrayList<ArrayList<Workout>> getWorkoutsByDay() {
		dbWrapper.open();
		List<Workout> allWorkouts = dbWrapper.getAllEntries(Workout.class);
		dbWrapper.close();
		workoutsByDay = new ArrayList<ArrayList<Workout>>();
		ArrayList<Workout> dailyWorkouts;
		ArrayList<Calendar> scheduledDates;
		ArrayList<Frequency> frequencyList;
		int daysInMonth = month.getActualMaximum(GregorianCalendar.DATE);
		GregorianCalendar currDay = (GregorianCalendar) month.clone();
		currDay.set(GregorianCalendar.DATE, currDay.getActualMinimum(GregorianCalendar.DATE)); //set to first of month
		
		for (int i = 0; i < daysInMonth; i++) {
			dailyWorkouts = new ArrayList<Workout>();
			for (Workout w : allWorkouts) {
				scheduledDates = w.getScheduledDates();
				for (Calendar c : scheduledDates) {
					if (areSameDay(c, currDay)) {
						dailyWorkouts.add(w);
					}
				}
				frequencyList = w.getFrequencyList();
				for (Frequency f : frequencyList) {
					if (f.includesDate(currDay)) {
						dailyWorkouts.add(w);
					}
				}
			}
			workoutsByDay.add(i, dailyWorkouts);
			currDay.set(GregorianCalendar.DATE, currDay.get(GregorianCalendar.DATE + 1));
		}
		return workoutsByDay;
	}
	
	private boolean areSameDay (Calendar cal1, Calendar cal2) {
		return (cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE) 
					&& cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) 
					&& cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR));
	}
}