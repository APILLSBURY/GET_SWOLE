package cs65.dartmouth.get_swole.classes;

import java.util.ArrayList;
import java.util.Calendar;

public class Workout {
	private String name;
	private ArrayList<Exercise> exerciseList;
	private Calendar startDate;
	private ArrayList<Calendar> scheduledDates;
	private ArrayList<Frequency> frequencyList;
	private String notes;
	private long id;
	
	
	public Workout(String name) {
		this.name = name;
		exerciseList = new ArrayList<Exercise>();
		startDate = Calendar.getInstance();
		scheduledDates = new ArrayList<Calendar>();
		frequencyList = new ArrayList<Frequency>();
		notes = "";
		id = -1;
	}
	
	//add a date to the list of scheduled dates
	public void addDate(Calendar date) {
		scheduledDates.add(date);
	}
	
	//add an exercise to the end of the list
	public void addExercise(Exercise e) {
		exerciseList.add(e);
	}
	
	//insert an exercise into a specific location
	public void addExercise(int index, Exercise e) {
		exerciseList.add(index, e);
	}
	
	
	//return true if we replaced an exercise, false if we couldn't find one with that name
	public boolean updateExercise(String name, Exercise newExercise) {
		for (int i = 0; i < exerciseList.size(); i++) {
			if (exerciseList.get(i).getName().equals(name)) {
				exerciseList.set(i, newExercise);
				return true;
			}
		}
		return false;
	}
	
	//return true if we found the date, false if not
	public boolean removeDate(Calendar cal) {
		int date = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		for (Calendar day : scheduledDates) {
			if (day.get(Calendar.DATE) == date && day.get(Calendar.MONTH) == month && day.get(Calendar.YEAR) == year) {
				scheduledDates.remove(day);
				return true;
			}
		}
		return false;
	}
	
	//add a frequency to the frequency list
	public void addFrequency(Frequency f) {
		frequencyList.add(f);
	}
	
	//GETTER METHODS
	public String getName() {
		return name;
	}
	
	public ArrayList<Exercise> getExerciseList() {
		return exerciseList;
	}
	
	public Calendar getStartDate() {
		return startDate;
	}
	
	public ArrayList<Calendar> getScheduledDates() {
		return scheduledDates;
	}
	
	public ArrayList<Frequency> getFrequencyList() {
		return frequencyList;
	}
	
	public long getId() {
		return id;
	}
	
	public String getNotes() {
		return notes;
	}
	
	
	//SETTER METHODS
	public void setName(String n) {
		name = n;
	}
	
	public void setExerciseList(ArrayList<Exercise> el) {
		exerciseList = el;
	}
	
	public void setStartDate(Calendar sd) {
		startDate = sd;
	}
	
	public void setScheduledDates(ArrayList<Calendar> sd) {
		scheduledDates = sd;
	}
	
	public void setFrequencyList(ArrayList<Frequency> f) {
		frequencyList = f;
	}
	
	public void setNotes(String n) {
		notes = n;
	}
	
	public void setId(long i) {
		id = i;
	}
}
