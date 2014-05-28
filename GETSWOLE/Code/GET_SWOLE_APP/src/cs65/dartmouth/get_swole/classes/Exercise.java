package cs65.dartmouth.get_swole.classes;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import cs65.dartmouth.get_swole.Utils;

public class Exercise extends GetSwoleClass {
	
	private ArrayList<Set> setList;
	private int repsGoal;
	private int weightGoal;
	private int rest;
	private boolean exerciseInstance;
	private long oldId;
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
		exerciseInstance = false;
		notes = "";
		oldId = -1;
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
			s += setList.get(i).toString() + ",";
		}
		if (s.length() >= 1) {
			s = s.substring(0, s.length() - 1); //remove the trailing "&"
		}
		return s;
	}
	
	public void setSetListFromString(String s) {
		String[] sets = s.split(",");
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
	
	public boolean getExerciseInstance() {
		return exerciseInstance;
	}
	
	public long getOldId() {
		return oldId;
	}
	
	public String getNotes() {
		return notes;
	}
	
	public int getMaxReps() {
		int max = -1;
		for (Set s : setList) {
			if (max < 0 || s.getReps() > max) max = s.getReps();
		}
		
		return max;
	}
	
	public int getMaxWeight() {
		int max = -1;
		for (Set s : setList) {
			if (max < 0 || s.getWeight() > max) max = s.getWeight();
		}
		return max;
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
	
	public void setExerciseInstance(boolean ei) {
		exerciseInstance = ei;
	}
	
	public void setExerciseInstanceFromInt(int i) {
		if (i == 0) {
			exerciseInstance = false;
		}
		else {
			exerciseInstance = true;
		}
	}
	
	public void setOldId(long oid) {
		oldId = oid;
	}
	
	public void setNotes(String n) {
		notes = n;
	}
	
	public String toString(Context context) {
		String s = "";
		for (Set set : setList) {
			int reps = set.getReps();
			int weight = set.getWeight();
			if ((reps == 0) && (weight == 0)) continue;
			if (reps == 0) s += Utils.getWeightString(context, weight) + ", ";
			else if (weight == 0) s+= reps + " reps, ";
			else { s += reps + " reps at " + Utils.getWeightString(context, weight) + ", ";
			}
		}
		if (rest != -1) {
			if (setList.isEmpty()) 
				return "Rest: " + rest + "secs";
			return s.substring(0, s.length() -2) + " Rest: " + rest + "secs";
		}
		if (setList.isEmpty()) return s;
		return s.substring(0, s.length() - 2);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public boolean equals(Exercise e) {
		
		if (e.getSetList().size() != setList.size()) return false;
		for (int i = 0; i < setList.size(); i++) { // compare sets
			if (!e.getSetList().get(i).equals(setList.get(i))) return false;
		}
		
		return (repsGoal == e.getRepsGoal() && weightGoal == e.getWeightGoal() && rest == e.getRest() && notes.equals(e.getNotes()));
	}

	// GCM JSONOBJECT HANDLING
	
	public JSONObject fromJSONObject(JSONObject obj) {	
		try {
			
			this.name = obj.getString("name");
			repsGoal = obj.getInt("repsGoal");
			weightGoal = obj.getInt("weightGoal");
			rest = obj.getInt("rest");
			notes = obj.getString("notes");
			setSetListFromString(obj.getString("setList"));

		}
		catch (JSONException e) {
			return null;
		}
		return obj;
	}
				
	public JSONObject toJSONObject() {
		
		try {
			
			JSONObject obj = new JSONObject();
			
			obj.put("name", this.name);
			obj.put("repsGoal", repsGoal);
			obj.put("weightGoal", weightGoal);
			obj.put("rest", rest);
			obj.put("notes", notes);
			obj.put("setList", getSetListString());
			
			return obj;
		}
		catch (JSONException e) {
			return null;
		}
	}
	
	
}
