package cs65.dartmouth.get_swole.classes;

import java.util.ArrayList;

public class Exercise extends GetSwoleClass {
	
	private ArrayList<Set> setList;
	private int repsGoal;
	private int weightGoal;
	private int rest;
	private String notes;
	
	public Exercise() {
		this("");
	}
	
	//default undefined numbers to -1
	public Exercise(String name) {
		this.name = name;
		setList = new ArrayList<Set>();
		repsGoal = -1;
		weightGoal = -1;
		rest = -1;
		notes = "";
		id = -1;
	}
	
	
	public void addSet(Set set) {
		setList.add(set);
	}
	
	public void removeSet(Set set) {
		setList.remove(set);
	}
	
	//DATABASE HELPER TO CONVERT SETLIST TO A STRING
	public String getSetListString() {
		String s = "";
		for (int i = 0; i < setList.size(); i++) {
			s += setList.get(i).toString() + "&";
		}
		if (s.length() >= 1) {
			s = s.substring(0, s.length() - 1); //remove the trailing "&"
		}
		return s;
	}
	
	public void setSetListFromString(String s) {
		String[] sets = s.split("&");
		setList.clear();
		String[] repsByWeight;
		for (int i = 0; i < sets.length; i++) {
			if (!sets[i].isEmpty()) {
				 repsByWeight = sets[i].split("x");
				 setList.add(new Set(Integer.parseInt(repsByWeight[0]), Integer.parseInt(repsByWeight[1])));
			}
		}
	}
	
	
	//GETTER METHODS
	public ArrayList<Set> getSetList() {
		return setList;
	}
	
	public int getRepsGoal() {
		return repsGoal;
	}
	
	public int getWeightGoal() {
		return weightGoal;
	}
	
	public int getRest() {
		return rest;
	}
	
	public String getNotes() {
		return notes;
	}
	
	
	//SETTER METHODS
	public void setSetList(ArrayList<Set> s) {
		setList = s;
	}
	
	public void setRepsGoal(int rg) {
		repsGoal = rg;
	}
	
	public void setWeightGoal(int wg) {
		weightGoal = wg;
	}
	
	public void setRest(int r) {
		rest = r;
	}
	
	public void setNotes(String n) {
		notes = n;
	}
	
	@Override
	public String toString() {
		String s = name + ": ";
		for (Set set : setList) {
			s += set.getReps() + " reps at " + set.getWeight() + ", ";
		}
		return s.substring(0, s.length() - 2);
	}
	
	public boolean equals(Exercise e) {
		
		if (e.getSetList().size() != setList.size()) return false;
		for (int i = 0; i < setList.size(); i++) { // compare sets
			if (!e.getSetList().get(i).equals(setList.get(i))) return false;
		}
		
		return (repsGoal == e.getRepsGoal() && weightGoal == e.getWeightGoal() && rest == e.getRest() && notes.equals(e.getNotes()));
	}
}
