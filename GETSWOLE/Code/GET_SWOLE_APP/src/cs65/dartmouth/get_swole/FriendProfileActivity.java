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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;

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
	int gender;
	double height;
	double weight;
	String bio;
	String email;
	String phone;
	
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
        Bundle extras = getIntent().getExtras();
        
        friendRegId = extras.getString("regId");
        pictureString = extras.getString("pictureString");
        firstName = extras.getString("firstName");
        lastName = extras.getString("lastName");
        hometown = extras.getString("hometown");
        sport = extras.getString("sport");
        gender = extras.getInt("gender");
        height = extras.getDouble("height");
        weight = extras.getDouble("weight");
        bio = extras.getString("bio");
        email = extras.getString("email");
        phone = extras.getString("phone");
        
        // Set Values
     	TextView nameTextView = (TextView) findViewById(R.id.name);
        if (firstName.equals("") && lastName.equals(""))
        	nameTextView.setVisibility(View.GONE);
        else 
        	nameTextView.setText(firstName + " " + lastName);
        nameTextView.setFocusable(false);
     	
     	TextView hometownTextView = (TextView) findViewById(R.id.hometown);
        if (hometown.equals("")) 
        	hometownTextView.setVisibility(View.GONE);
        else
        	hometownTextView.setText(hometown);
     	hometownTextView.setFocusable(false);
     	
     	TextView sportTextView = (TextView) findViewById(R.id.sport);
        if (sport.equals("")) 
        	sportTextView.setVisibility(View.GONE);
        else
        	sportTextView.setText(sport);
     	sportTextView.setFocusable(false);
     	
     	// Bio Information
 		TextView bioTextView = (TextView) findViewById(R.id.bio);   
 		EditText bioTextInput = (EditText) findViewById(R.id.bioInputText);
     	TextView genderTextInput = (TextView) findViewById(R.id.genderInput);
     	TextView heightTextInput = (TextView) findViewById(R.id.heightinput);
     	TextView weightTextInput = (TextView) findViewById(R.id.weightinput);
     	if (gender == -1 && height == -1 && weight == -1 && bio.equals("")) {
     		bioTextView.setVisibility(View.GONE);
     		
     		TextView genderTextView = (TextView) findViewById(R.id.gender);
     		genderTextView.setVisibility(View.GONE);
     		genderTextInput.setVisibility(View.GONE);
     		
     		TextView heightTextView = (TextView) findViewById(R.id.height);
     		heightTextView.setVisibility(View.GONE);
     		heightTextInput.setVisibility(View.GONE);
     		
     		TextView weightTextView = (TextView) findViewById(R.id.weight);
     		weightTextView.setVisibility(View.GONE);
     		weightTextInput.setVisibility(View.GONE);
     		
     		bioTextView.setVisibility(View.GONE);
     		bioTextInput.setVisibility(View.GONE);
     	}
     	else {
     	
	     	if (gender != -1)
	     		genderTextInput.setText(Globals.genders[gender]);
	     	else {
	     		TextView genderTextView = (TextView) findViewById(R.id.gender);
	     		genderTextView.setVisibility(View.GONE);
	     		genderTextInput.setVisibility(View.GONE);
	     	}
	     	genderTextInput.setFocusable(false);
	
	     	if (height != -1) {
	     		heightTextInput.setText((int) height / 12 + "'" + (int) height % 12 + "''");
	     	}
	     	else {
	     		TextView heightTextView = (TextView) findViewById(R.id.height);
	     		heightTextView.setVisibility(View.GONE);
	     		heightTextInput.setVisibility(View.GONE);
	     	}
	     	heightTextInput.setFocusable(false);
	     	
	
	     	if (weight != -1)
	     		weightTextInput.setText(weight + " lb");
	     	else {
	     		TextView weightTextView = (TextView) findViewById(R.id.weight);
	     		weightTextView.setVisibility(View.GONE);
	     		weightTextInput.setVisibility(View.GONE);
	     	}
	     	weightTextInput.setFocusable(false);
	     	
	     	if (!bio.equals(""))
	     		bioTextInput.setText(bio);
	     	else
	     		bioTextInput.setVisibility(View.GONE);
	     	bioTextInput.setFocusable(false);
     	}
     	
     	// Contact Information
     	
 		TextView contactTextView = (TextView) findViewById(R.id.contactInfo);     	
     	TextView emailTextInput = (TextView) findViewById(R.id.email);	
 		TextView phoneTextInput = (TextView) findViewById(R.id.phone);
 		
     	// Set email listener
 		emailTextInput.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                sendEmail();
              }
          });
     	
     	// Set phone listener
 		phoneTextInput.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
              phoneCall();
            }
        });
 		
     	if (email.equals("") && phone.equals("")) {
     		contactTextView.setVisibility(View.GONE);
     		emailTextInput.setVisibility(View.GONE);
     		phoneTextInput.setVisibility(View.GONE);
     	}
     	else if (!email.equals("") && phone.equals("")) {
     		phoneTextInput.setVisibility(View.GONE);
     		emailTextInput.setText(email);
     		emailTextInput.setFocusable(false);
     	}
     	else if (email.equals("") && !phone.equals("")) {
     		emailTextInput.setVisibility(View.GONE);
     		phoneTextInput.setText(phone);
     		phoneTextInput.setFocusable(false);
     	}
     	else {
     		emailTextInput.setText(email);
     		emailTextInput.setFocusable(false);
     		phoneTextInput.setText(phone);
     		phoneTextInput.setFocusable(false);
     	}
     	
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
	
	// Email intent
	public void sendEmail() {
		if (!email.equals("")) {
			/* Create the Intent */

			final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

			/* Fill it with Data */
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email} );
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");

			/* Send it off to the Activity-Chooser */
			startActivity(Intent.createChooser(emailIntent, "Send mail..."));
		}
	}
	
	// Phone call intent
	public void phoneCall() {
		if (!phone.equals("")) {
			String number = "tel:" + phone.trim();
			startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(number)));
		}
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