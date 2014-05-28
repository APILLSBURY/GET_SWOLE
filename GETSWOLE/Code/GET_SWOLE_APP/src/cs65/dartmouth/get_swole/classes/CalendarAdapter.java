//THIS CODE IS BASED OFF OF CODE FROM https://github.com/mukesh4u/Android-Calendar-Sync

package cs65.dartmouth.get_swole.classes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import cs65.dartmouth.get_swole.CalendarUtility;
import cs65.dartmouth.get_swole.Globals;
import cs65.dartmouth.get_swole.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CalendarAdapter extends BaseAdapter {
	private Context mContext;

	private java.util.Calendar month;
	public GregorianCalendar pmonth; // calendar instance for previous month
	/**
	 * calendar instance for previous month for getting complete view
	 */
	public GregorianCalendar pmonthmaxset;
	private GregorianCalendar selectedDate;
	int firstDay;
	int maxWeeknumber;
	int maxP;
	int calMaxP;
	int lastWeekDay;
	int leftDays;
	int mnthlength;
	String itemvalue, curentDateString;
	DateFormat df;

	private ArrayList<String> items;
	public static List<String> dayString;
	private View previousView;
	private ArrayList<ArrayList<GetSwoleClass>> workoutsByDay;

	//BASED ON METHOD FROM https://github.com/mukesh4u/Android-Calendar-Sync
	public CalendarAdapter(Context c, GregorianCalendar monthCalendar, ArrayList<ArrayList<GetSwoleClass>> workoutsByDay) {
		this.workoutsByDay = workoutsByDay;
		CalendarAdapter.dayString = new ArrayList<String>();
		Locale.setDefault(Locale.US);
		month = monthCalendar;
		selectedDate = (GregorianCalendar) monthCalendar.clone();
		mContext = c;
		month.set(GregorianCalendar.DAY_OF_MONTH, 1);
		this.items = new ArrayList<String>();
		df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		curentDateString = df.format(selectedDate.getTime());
		refreshDays();
	}

	public void setSelectedDate(Calendar c) {
		selectedDate = (GregorianCalendar) c;
	}
	
	public void setWorkoutsByDay(ArrayList<ArrayList<GetSwoleClass>> workoutsByDay) {
		this.workoutsByDay = workoutsByDay;
	}
	
	//METHOD FROM https://github.com/mukesh4u/Android-Calendar-Sync
	public void setItems(ArrayList<String> items) {
		for (int i = 0; i != items.size(); i++) {
			if (items.get(i).length() == 1) {
				items.set(i, "0" + items.get(i));
			}
		}
		this.items = items;
	}

	//METHOD FROM https://github.com/mukesh4u/Android-Calendar-Sync
	public int getCount() {
		return dayString.size();
	}

	//METHOD FROM https://github.com/mukesh4u/Android-Calendar-Sync
	public Object getItem(int position) {
		return dayString.get(position);
	}

	//METHOD FROM https://github.com/mukesh4u/Android-Calendar-Sync
	public long getItemId(int position) {
		return 0;
	}

	//BASED ON CODE FROM https://github.com/mukesh4u/Android-Calendar-Sync, but with a lot of customization
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		TextView dayView;
		if (convertView == null) { // if it's not recycled, initialize some attributes
			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.calendar_item, null);
		}
		dayView = (TextView) v.findViewById(R.id.date);
		v.setBackgroundResource(R.drawable.calendar_cell);
		// separates daystring into parts.
		String[] separatedTime = dayString.get(position).split("-");
		// taking last part of date. ie; 2 from 2012-12-02
		String gridvalueString = separatedTime[2].replaceFirst("^0*", "");
		int gridvalue = Integer.parseInt(gridvalueString);
		dayView.setPaintFlags(0);
		
		// checking whether the day is in current month or not.
		if ((gridvalue > 1) && (position < firstDay)) {
			// setting offdays to white color.
			dayView.setTextColor(Color.WHITE);
			dayView.setClickable(false);
			dayView.setFocusable(false);
		} else if ((gridvalue < 7) && (position > 28)) {
			dayView.setTextColor(Color.WHITE);
			dayView.setClickable(false);
			dayView.setFocusable(false);
		} else {
			// setting current month's days in black
			dayView.setTextColor(Color.BLACK);
			// setting days with workouts to have orange text
			if (!workoutsByDay.get(gridvalue).isEmpty()) {
				dayView.setTextColor(mContext.getResources().getColor(R.color.get_swole_orange));
			}
			Calendar currDate = Calendar.getInstance();
			currDate.set(Calendar.DATE, gridvalue);
			currDate.set(Calendar.MONTH, month.get(Calendar.MONTH));
			currDate.set(Calendar.YEAR, month.get(Calendar.YEAR));
			if (CalendarUtility.testDateEquality(currDate, selectedDate) == CalendarUtility.EQUALS) {
				setSelected(v, (GregorianCalendar) currDate);
			}
			if (CalendarUtility.testDateEquality(currDate, Calendar.getInstance()) == CalendarUtility.EQUALS) {
				dayView.setPaintFlags(dayView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
			}
		}
		
		dayView.setText(gridvalue + "");

		// create date string for comparison
		String date = dayString.get(position);

		if (date.length() == 1) {
			date = "0" + date;
		}
		String monthStr = "" + (month.get(GregorianCalendar.MONTH) + 1);
		if (monthStr.length() == 1) {
			monthStr = "0" + monthStr;
		}
		return v;
	}

	//METHOD FROM https://github.com/mukesh4u/Android-Calendar-Sync
	public View setSelected(View view, Calendar date) {
		if (previousView != null) {
			previousView.setBackgroundResource(R.drawable.calendar_cell);
		}
		previousView = view;
		view.setBackgroundResource(R.drawable.calendar_cel_selectl);
		selectedDate = (GregorianCalendar) date;
		return view;
	}
	
	
	//METHOD FROM https://github.com/mukesh4u/Android-Calendar-Sync
	public void refreshDays() {
		// clear items
		items.clear();
		dayString.clear();
		Locale.setDefault(Locale.US);
		pmonth = (GregorianCalendar) month.clone();
		// month start day. ie; sun, mon, etc
		firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
		// finding number of weeks in current month.
		maxWeeknumber = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
		// allocating maximum row number for the gridview.
		mnthlength = maxWeeknumber * 7;
		maxP = getMaxP(); // previous month maximum day 31,30....
		calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...
		/**
		 * Calendar instance for getting a complete gridview including the three
		 * month's (previous,current,next) dates.
		 */
		pmonthmaxset = (GregorianCalendar) pmonth.clone();
		/**
		 * setting the start date as previous month's required date.
		 */
		pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);

		/**
		 * filling calendar gridview.
		 */
		for (int n = 0; n < mnthlength; n++) {

			itemvalue = df.format(pmonthmaxset.getTime());
			pmonthmaxset.add(GregorianCalendar.DATE, 1);
			dayString.add(itemvalue);

		}
	}

	
	//METHOD FROM https://github.com/mukesh4u/Android-Calendar-Sync
	private int getMaxP() {
		int maxP;
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMinimum(GregorianCalendar.MONTH)) {
			pmonth.set((month.get(GregorianCalendar.YEAR) - 1),
					month.getActualMaximum(GregorianCalendar.MONTH), 1);
		} else {
			pmonth.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) - 1);
		}
		maxP = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

		return maxP;
	}

}