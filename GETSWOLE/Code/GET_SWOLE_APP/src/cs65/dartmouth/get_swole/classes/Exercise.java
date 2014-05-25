package cs65.dartmouth.get_swole.classes;

import org.json.JSONException;
import org.json.JSONObject;

public class Exercise extends GetSwoleClass {
	
	private int reps;
	private int weight;
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
		reps = -1;
		weight = -1;
		repsGoal = -1;
		weightGoal = -1;
		rest = -1;
		notes = "";
		id = -1;
	}
	
	
	//GETTER METHODS
	
	public int getReps() {
		return reps;
	}
	
	public int getWeight() {
		return weight;
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
	public void setReps(int r) {
		reps = r;
	}
	
	public void setWeight(int w) {
		weight = w;
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
		return name + ": " + reps + " reps at " + weight;
	}
	
	// GCM JSONOBJECT HANDLING
	
	public JSONObject fromJSONObject(JSONObject obj) {	
		try {
			
			this.name = obj.getString("name");
			reps = obj.getInt("reps");
			weight = obj.getInt("weight");
			repsGoal = obj.getInt("repsGoal");
			weightGoal = obj.getInt("weightGoal");
			rest = obj.getInt("rest");
			notes = obj.getString("notes");

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
			obj.put("reps", reps);
			obj.put("weight", weight);
			obj.put("repsGoal", repsGoal);
			obj.put("weightGoal", weightGoal);
			obj.put("rest", rest);
			obj.put("notes", notes);
			
			return obj;
		}
		catch (JSONException e) {
			return null;
		}
	}
	
}
