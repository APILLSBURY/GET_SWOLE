package cs65.dartmouth.get_swole.classes;

import java.util.Calendar;

public class Frequency {

	private int day;
	private Calendar startDate;
	private Calendar endDate;
	private long id;
	
	public Frequency (int day) {
		this.day = day;
		startDate = null;
		endDate = null;
		id = -1;
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
	
	public long getId() {
		return id;
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
	
	public void setId(long i) {
		id = i;
	}
}
