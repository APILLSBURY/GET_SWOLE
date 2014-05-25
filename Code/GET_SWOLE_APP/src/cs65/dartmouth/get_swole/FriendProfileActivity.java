package cs65.dartmouth.get_swole;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cs65.dartmouth.get_swole.classes.ProfileObject;
import cs65.dartmouth.get_swole.classes.Workout;
import cs65.dartmouth.get_swole.gae.Downloader;
import cs65.dartmouth.get_swole.gae.Uploader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;

public class FriendProfileActivity extends ListActivity {
	
	Context mContext;
	Downloader mDownloader;
	
	// Information
	String friendRegId;
	String pictureString;
	String firstName;
	String lastName;
	String hometown;
	String sport;
	
	ArrayList<Workout> workouts;
	WorkoutsAdapter workoutsAdapter;
	
	// Retrieve all profiles
	private void updateWorkouts() {
		workoutsAdapter.clear();
		workoutsAdapter.addAll(workouts);
		workoutsAdapter.notifyDataSetInvalidated();
		Utils.setListViewHeightBasedOnChildren(getListView());
	}
	
	/**
	 * onCreate()
	 * @param Bundle savedInstanceStates
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_profile);
		
		// Get context
		mContext = getApplicationContext();
		
        // Create the serverURL
        String serverURL = getString(R.string.server_url) + "/get_workouts";
        
        // Set downloader
        mDownloader = new Downloader(mContext, serverURL);
     
        // Get Extras
        friendRegId = getIntent().getExtras().getString("regId");
        pictureString = getIntent().getExtras().getString("pictureString");
        firstName = getIntent().getExtras().getString("firstName");
        lastName = getIntent().getExtras().getString("lastName");
        hometown = getIntent().getExtras().getString("hometown");
        sport = getIntent().getExtras().getString("sport");
        
        // Set Values
     	EditText firstNameEditText = (EditText) findViewById(R.id.firstName);
     	firstNameEditText.setText(firstName);
     	firstNameEditText.setFocusable(false);
     	
     	EditText lastNameEditText = (EditText) findViewById(R.id.lastName);
     	lastNameEditText.setText(lastName);
     	lastNameEditText.setFocusable(false);
     	
     	EditText hometownEditText = (EditText) findViewById(R.id.hometown);
     	hometownEditText.setText(hometown);
     	hometownEditText.setFocusable(false);
     	
     	EditText sportEditText = (EditText) findViewById(R.id.sport);
     	sportEditText.setText(sport);
     	sportEditText.setFocusable(false);
     	
     	// Update the photo
     	if (!pictureString.equals("null")) {
			Bitmap photo = Utils.StringToBitMap(pictureString);
			ImageView imageView = (ImageView) findViewById(R.id.profilePhoto);
			
			imageView.setImageBitmap(photo);
     	}
     	
     	// Fetch all workouts
     	workouts = new ArrayList<Workout>();
     	fetchWorkouts();
     	
        // Update the Listview once got all friends
        workoutsAdapter = new WorkoutsAdapter(mContext);
        setListAdapter(workoutsAdapter);
     	
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateWorkouts();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	// Fetch all workouts
	public void fetchWorkouts() {
		
		new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
	 
				String msg = "";
	 
				try {
    	 
					msg = mDownloader.getWorkouts(friendRegId);
    	 
				}
				catch (IOException ex) {
					msg = "Error: " + ex.getMessage();
				}
	 
				return msg;

			}

			@Override
			protected void onPostExecute(String res) {
				//Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
				
				// Get all workouts from the response
				try {
					JSONArray jsonArray = new JSONArray(res);
	  
					for (int i = 0; i < jsonArray.length(); i++) {
						Workout workout = new Workout();
						workout.fromJSONObject(jsonArray.getJSONObject(i));
						workouts.add(workout);
					}
	  
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (!workouts.isEmpty()) {
					updateWorkouts();
				}
				
        	 
			}

		}.execute();	
		
	}
	
	// HANDLING LISTVIEW OF WORKOUTS
	
    @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
		
        Workout entry = workouts.get(position);
        
        Toast.makeText(mContext, entry.getExerciseList().toString(), Toast.LENGTH_LONG).show();
       
	}

	
    // ********** Private Classes **********
    
    private class WorkoutsAdapter extends ArrayAdapter<Workout> {
    	
		public WorkoutsAdapter(Context context) {
			super(context, android.R.layout.two_line_list_item);	
		}
    	
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			View listItemView = convertView;
			
			if (null == convertView) {
				listItemView = inflater.inflate(R.layout.workouts_friend_list_row, parent, false);
			}
			
    	    // Access the textview to set
    	    TextView workoutView = (TextView) listItemView.findViewById(R.id.workout_list_single_row); 
    	    workoutView.setTextColor(Color.BLACK);
    	    Workout workout = workouts.get(position);
    	    workoutView.setText(workout.getName());
    	    
			return listItemView;
		}
    	
    }
	
}