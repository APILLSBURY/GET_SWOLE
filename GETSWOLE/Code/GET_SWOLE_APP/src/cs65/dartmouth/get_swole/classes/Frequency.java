package cs65.dartmouth.get_swole.classes;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import cs65.dartmouth.get_swole.CalendarUtility;
import cs65.dartmouth.get_swole.Globals;
import cs65.dartmouth.get_swole.database.DatabaseHelper;
import cs65.dartmouth.get_swole.database.DatabaseWrapper;

public class Frequency extends GetSwoleClass {

	private int day;
	private Calendar startDate;
	private Calendar endDate;
	
	public Frequency () {
		this(-1);
	}
	
	public Frequency (int day) {
		this.day = day;
		startDate = null;
		endDate = null;
		id = -1;
	}
	
	public boolean includesDate(Calendar cal) {
		Log.d(Globals.TAG, "cal date = " + cal.get(Calendar.DATE) + CalendarUtility.testDateEquality(startDate, cal) + ", " + CalendarUtility.testDateEquality(endDate, cal));
		return (CalendarUtility.testDateEquality(startDate, cal) != CalendarUtility.GREATER_THAN 
					&& CalendarUtility.testDateEquality(endDate, cal) != CalendarUtility.LESS_THAN 
					&& cal.get(Calendar.DAY_OF_WEEK) == day);
	}
	
	
	//GETTER METHODS
	public int getDay() {
		return day;
	}
	
	public Calendar getStartDate() {
		return startDate;
	}
	
	public Calendar getEndDate() {
		return endDate;
	}
	
	
	//SETTER METHODS
	public void setDay(int d) {
		day = d;
	}
	
	public void setStartDate(Calendar sd) {
		startDate = sd;
	}
	
	public void setEndDate(Calendar ed) {
		endDate = ed;
	}
	
	public JSONObject fromJSONObject(JSONObject obj) {	
		try {
			day = obj.getInt("day");
			startDate = Calendar.getInstance();
			startDate.setTime(DatabaseWrapper.DATE_FORMAT.parse(obj.getString("startDate")));
			endDate = Calendar.getInstance();
			endDate.setTime(DatabaseWrapper.DATE_FORMAT.parse(obj.getString("endDate")));
		}
		catch (Exception e) {
			Log.e(Globals.TAG, "Exception creating frequency from JSONObject", e);
			return null;
		}
		return obj;
	}
				
	public JSONObject toJSONObject() {
		try {
			JSONObject obj = new JSONObject();
			obj.put("day", day);
			obj.put("startDate", DatabaseWrapper.DATE_FORMAT.format(startDate.getTime()));
			obj.put("endDate", DatabaseWrapper.DATE_FORMAT.format(endDate.getTime()));
			return obj;
		}
		catch (JSONException e) {
			Log.e(Globals.TAG, "Exception creating JSONObject from frequency", e);
			return null;
		}
	}
}
