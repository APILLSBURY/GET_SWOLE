package cs65s14.dartmouth.get_swole.classes;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cs65s14.dartmouth.get_swole.CalendarUtility;
import cs65s14.dartmouth.get_swole.Globals;
import cs65s14.dartmouth.get_swole.database.*;

import android.content.Context;
import android.util.Log;

public class Workout extends GetSwoleClass {
	private String regId;
	private String owner;
	
	private ArrayList<Exercise> exerciseList;
	private Calendar startDate;
	private ArrayList<Calendar> scheduledDates;
	private ArrayList<Frequency> frequencyList;
	private String notes;
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	
	public Workout() {
		this("");
	}
	
	public Workout(String name) {
		this.name = name;
		owner = "";
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
	
	//return true if we replaced an exercise, false if we couldn't find one with that name
	public void clearScheduling() {
		scheduledDates.clear();
		frequencyList.clear();
	}
	
	//return true if we found the date, false if not
	public boolean removeDate(Calendar cal) {
		Calendar toRemove = null;
		for (Calendar day : scheduledDates) {
			if (CalendarUtility.testDateEquality(cal, day) == (CalendarUtility.EQUALS)) {
				toRemove = day;
				break;
			}
		}
		if (toRemove != null) {
			scheduledDates.remove(toRemove);
			return true;
		}
		return false;
	}
	
	//add a frequency to the frequency list
	public void addFrequency(Frequency f) {
		frequencyList.add(f);
	}

	
	public String getScheduledDatesString() {
		String s = "";
		for (int i = 0; i < scheduledDates.size(); i++) {
			s += DATE_FORMAT.format(scheduledDates.get(i).getTime()) + ",";
		}
		if (s.length() >= 1) {
			s = s.substring(0, s.length() - 1); //remove the trailing "&"
		}
		return s;
	}
	
	public void setScheduledDatesFromString(String s) {
		String[] dates = s.split(",");
		Calendar cal;
		scheduledDates.clear();
		for (int i = 0; i < dates.length; i++) {
			cal = Calendar.getInstance();
			try {
				if (!dates[i].isEmpty()) {
					cal.setTime(DATE_FORMAT.parse(dates[i]));
					scheduledDates.add(cal);
				}
			}
			catch (Exception e) {
				Log.e(Globals.TAG, "Couldn't parse the date " + dates[i], e);
			}
		}
	}
	
	
	//BYTE ARRAY METHODS
	public byte[] getExerciseListByteArray() {
		int[] intArray = new int[exerciseList.size()];
		for (int i = 0; i < exerciseList.size(); i++) {
			Exercise exercise = exerciseList.get(i);
			intArray[i] = (int) exercise.getId();
		}
		ByteBuffer byteBuffer = ByteBuffer.allocate(intArray.length * Integer.SIZE);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		intBuffer.put(intArray);
		return byteBuffer.array();
	}
	
	public void setExerciseListFromByteArray(byte[] byteArray, Context c) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		int[] intArray = new int[byteArray.length/Integer.SIZE];
		intBuffer.get(intArray);
		Exercise exercise;
		exerciseList.clear();
		DatabaseWrapper db = new DatabaseWrapper(c);
		db.open();
		for (int i = 0; i < intArray.length; i++) {
			exercise = (Exercise) db.getEntryById((long) intArray[i], Exercise.class);
			exerciseList.add(exercise);
		}
		db.close();
	}
	
	
	public byte[] getFrequencyListByteArray() {
		int[] intArray = new int[frequencyList.size()];
		for (int i = 0; i < frequencyList.size(); i++) {
			Frequency frequency = frequencyList.get(i);
			intArray[i] = (int) frequency.getId();
		}
		ByteBuffer byteBuffer = ByteBuffer.allocate(intArray.length * Integer.SIZE);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		intBuffer.put(intArray);
		return byteBuffer.array();
	}
	
	public void setFrequencyListFromByteArray(byte[] byteArray, Context c) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		int[] intArray = new int[byteArray.length/Integer.SIZE];
		intBuffer.get(intArray);
		Frequency frequency;
		frequencyList.clear();
		DatabaseWrapper db = new DatabaseWrapper(c);
		db.open();
		for (int i = 0; i < intArray.length; i++) {
			frequency = (Frequency) db.getEntryById((long) intArray[i], Frequency.class);
			frequencyList.add(frequency);
		}
		db.close();
	}
	
	//GETTER METHODS
	public String getOwner() {
		return owner;
	}
	
	public ArrayList<Exercise> getExerciseList() {
		return exerciseList;
	}
	
	public String getExerciseListJSONString() {
		// Exercise list (convert to JSONArray)
		JSONArray jsonArray = new JSONArray();
		
		// Loop through the exercises and turn to JSONObjects
		for (Exercise exercise : exerciseList) {
			jsonArray.put(exercise.toJSONObject());
		}
		
		// Convert the jsonArray to string
		return jsonArray.toString();

	}
	
	public String getFrequencyListJSONString() {
		// Exercise list (convert to JSONArray)
		JSONArray jsonArray = new JSONArray();
		
		// Loop through the exercises and turn to JSONObjects
		for (Frequency freq : frequencyList) {
			jsonArray.put(freq.toJSONObject());
		}
		
		// Convert the jsonArray to string
		return jsonArray.toString();
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
	
	public String getRegId() {
		return regId;
	}
	
	
	//SETTER METHODS
	public void setOwner(String name) {
		owner = name;
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public void setExerciseList(ArrayList<Exercise> el) {
		exerciseList = el;
	}
	
	public void setExerciseList(String jsonString) {
		
		// Exercise list (convert from JSONArray to a real exercise list)
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(jsonString);
			for (int i = 0; i < jsonArray.length(); i++) {
				Exercise exercise = new Exercise();
				exercise.fromJSONObject(jsonArray.getJSONObject(i));
				exerciseList.add(exercise);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	public void setFrequencyList(String jsonString) {
		
		// Exercise list (convert from JSONArray to a real exercise list)
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(jsonString);
			for (int i = 0; i < jsonArray.length(); i++) {
				Frequency frequency = new Frequency();
				frequency.fromJSONObject(jsonArray.getJSONObject(i));
				frequencyList.add(frequency);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

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
	
	public void setRegId(String regID) {
		regId = regID;
	}
	
	// GCM JSONOBJECT HANDLING
	public JSONObject fromJSONObject(JSONObject obj) {	
		try {
			
			regId = obj.getString("regId");
			owner = obj.getString("owner");
			id = obj.getLong("id");
			this.name = obj.getString("name");
			notes = obj.getString("notes");
			setScheduledDatesFromString(obj.getString("scheduledDates"));
			
			// Frequency list
			setFrequencyList(obj.getString("frequencyList"));
			
			// Exercise list (convert from JSONArray to a real exercise list)
			setExerciseList(obj.getString("exerciseList"));

		}
		catch (JSONException e) {
			return null;
		}
		return obj;
	}
				
	public JSONObject toJSONObject() {
		
		try {
			
			JSONObject obj = new JSONObject();
			
			obj.put("regId", regId);
			obj.put("owner", owner);
			obj.put("id", id);
			obj.put("name", name);
			obj.put("notes", notes);
			obj.put("exerciseList", getExerciseListJSONString());
			obj.put("frequencyList", getFrequencyListJSONString());
			obj.put("scheduledDates", getScheduledDatesString());
			
			return obj;
		}
		catch (JSONException e) {
			return null;
		}
	}
	
}
