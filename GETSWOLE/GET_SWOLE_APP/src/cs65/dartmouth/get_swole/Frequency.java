package cs65.dartmouth.get_swole;

import java.util.Calendar;

public class Frequency {

	private int day;
	private Calendar startDate;
	private Calendar endDate;
	
	public Frequency (int day) {
		this.day = day;
		startDate = null;
		endDate = null;
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
