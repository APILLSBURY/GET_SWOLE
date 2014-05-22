package cs65.dartmouth.get_swole.classes;

import java.util.Calendar;

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
		long startTime = startDate.getTimeInMillis();
		long endTime = endDate.getTimeInMillis();
		long calTime = cal.getTimeInMillis();
		return (startTime <= calTime && calTime <= endTime && cal.get(Calendar.DAY_OF_WEEK) == day);
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
}
