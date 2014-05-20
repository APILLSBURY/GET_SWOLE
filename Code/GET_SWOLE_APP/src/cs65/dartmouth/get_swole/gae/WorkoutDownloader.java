package cs65.dartmouth.get_swole.gae;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class WorkoutDownloader {

	Context mContext;
	String mServerUrl;
	
	public WorkoutDownloader (Context context, String serverURL) {
		mContext = context;
		mServerUrl = serverURL;
	}
	
//	public boolean upload(ArrayList<ExerciseEntry> entryList) throws IOException {
//		
//		// Convert entries to string
//		// Create new JSON array: 
//		JSONArray jsonArray = new JSONArray();
//		
//		// Loop through the entries and turn to JSONObjects
//		for (ExerciseEntry entry : entryList) {
//			jsonArray.put(entry.toJSONObject());
//		}
//		
//		// Convert the jsonArray to string
//		String jsonArrayString = jsonArray.toString();
//		
//		// Get Shared Preferences
//		SharedPreferences GCMPreferences = mContext.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
//		
//		// Get registration id:
//		final String regId =  GCMPreferences.getString(MainActivity.PROPERTY_REG_ID, "");				
//		
//		if (!regId.isEmpty()) {
//			
//			Log.d("GCM", "regId is legit!!!");
//			
//			// Create new hashmap
//			Map<String, String> hashmap = new HashMap<String, String>();
//			
//			// Put the regId with key “regId” into hashmap
//			hashmap.put("regId", regId);
//			
//			// Put the data (the converted string jsonArray from earlier) with key “data” into hashmap
//			hashmap.put("data", jsonArrayString);
//	
//			// Post
//			ServerUtilities.post(mServerUrl, hashmap);
//		}
//		
//		return true;
//
//	}
//	
}
