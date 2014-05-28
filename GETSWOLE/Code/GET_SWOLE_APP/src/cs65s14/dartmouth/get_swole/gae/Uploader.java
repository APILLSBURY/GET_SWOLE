package cs65s14.dartmouth.get_swole.gae;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;

import cs65s14.dartmouth.get_swole.Globals;
import cs65s14.dartmouth.get_swole.MainActivity;
import cs65s14.dartmouth.get_swole.classes.ProfileObject;
import cs65s14.dartmouth.get_swole.classes.Workout;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Uploader {

	Context mContext;
	String mServerUrl;
	
	public Uploader (Context context, String serverURL) {
		mContext = context;
		mServerUrl = serverURL;
	}
	
	public boolean uploadProfile(ProfileObject profile) throws IOException {
		
		Log.d(Globals.TAG, "uploadProfile");
		
		// Convert entries to string
		// Create new JSON array: 
		JSONArray jsonArray = new JSONArray();
		
		// Put profile into JSONArray
		jsonArray.put(profile.toJSONObject());
		
		// Convert the jsonArray to string
		String jsonArrayString = jsonArray.toString();
		
		// Get Shared Preferences
		SharedPreferences GCMPreferences = mContext.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		
		// Get registration id:
		final String regId =  GCMPreferences.getString(MainActivity.PROPERTY_REG_ID, "");				
		
		if (!regId.isEmpty()) {
			
			// Create new hashmap
			Map<String, String> hashmap = new HashMap<String, String>();
			
			// Put the regId with key “regId” into hashmap
			hashmap.put("regId", regId);
			
			// Put the data (the converted string jsonArray from earlier) with key “data” into hashmap
			hashmap.put("profile", jsonArrayString);
			
			Log.d(Globals.TAG, jsonArrayString);
			
			// Post
			ServerUtilities.post(mServerUrl, hashmap);
		}
	
		return true;
	}
	
	public boolean uploadWorkouts(ArrayList<Workout> workouts) throws IOException {
		
		// Convert entries to string
		// Create new JSON array: 
		JSONArray jsonArray = new JSONArray();
		
		// Loop through the entries and turn to JSONObjects
		for (Workout workout : workouts) {
			jsonArray.put(workout.toJSONObject()); // NEED TO DO THIS!!!
		}
		
		// Convert the jsonArray to string
		String jsonArrayString = jsonArray.toString();
		
		// Get Shared Preferences
		SharedPreferences GCMPreferences = mContext.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		
		// Get registration id:
		final String regId =  GCMPreferences.getString(MainActivity.PROPERTY_REG_ID, "");				
		
		if (!regId.isEmpty()) {
			
			// Create new hashmap
			Map<String, String> hashmap = new HashMap<String, String>();
			
			// Put the regId with key “regId” into hashmap
			hashmap.put("regId", regId);
			
			// Put the data (the converted string jsonArray from earlier) with key “data” into hashmap
			hashmap.put("workouts", jsonArrayString);
	
			Log.d(Globals.TAG, jsonArrayString);
			
			// Post
			ServerUtilities.post(mServerUrl, hashmap);
		}
		
		return true;

	}
	
}
