package cs65.dartmouth.get_swole;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cs65.dartmouth.get_swole.classes.ProfileObject;
import cs65.dartmouth.get_swole.gae.Downloader;
import cs65.dartmouth.get_swole.gae.Uploader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

public class FriendsActivity extends ListActivity {
	
	Downloader mDownloader;
	ArrayList<ProfileObject> friends;
	
	ProfilesAdapter profilesAdapter;
	Context mContext;
	
	// Retrieve all profiles
	private void updateFriendProfiles() {
		profilesAdapter.clear();
		profilesAdapter.addAll(friends);
		profilesAdapter.notifyDataSetInvalidated();
	}
	
	/**
	 * onCreate()
	 * @param Bundle savedInstanceStates
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		
		// Get context
		mContext = getApplicationContext();
		
        // Create the serverURL
        String serverURL = getString(R.string.server_url) + "/get_friends";
        
        // Set downloader
        mDownloader = new Downloader(mContext, serverURL);
     
        // Automatically get all friend profiles
        friends = new ArrayList<ProfileObject>();
        getFriends();
        
        // Update the Listview once got all friends
        profilesAdapter = new ProfilesAdapter(mContext);
        setListAdapter(profilesAdapter);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateFriendProfiles();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	/**
	 * getFriends()
	 */
	public void getFriends() {
	
		new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
	 
				String msg = "";
	 
				try {
    	 
					msg = mDownloader.getFriends();
    	 
					//msg = "Sent request for getting people's profiles";
    	 
				}
				catch (IOException ex) {
					msg = "Error: " + ex.getMessage();
				}
	 
				return msg;

			}

			@Override
			protected void onPostExecute(String res) {
				//Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
				
				// Get all profiles from the response
				try {
					JSONArray jsonArray = new JSONArray(res);
	  
					for (int i = 0; i < jsonArray.length(); i++) {
						ProfileObject profile = new ProfileObject();
						profile.fromJSONObject(jsonArray.getJSONObject(i));
						friends.add(profile);
					}
	  
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (!friends.isEmpty()) {
//					for (ProfileObject friend : friends)
//						Toast.makeText(getApplicationContext(), friend.getFirstName() + " " + friend.getLastName(), Toast.LENGTH_SHORT).show();
					
					updateFriendProfiles();
				}
				
        	 
			}

		}.execute();	
	}
	
    // ********** Private Classes **********
    
    private class ProfilesAdapter extends ArrayAdapter<ProfileObject> {
    	
		public ProfilesAdapter(Context context) {
			super(context, android.R.layout.two_line_list_item);	
		}
    	
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			View listItemView = convertView;
			
			if (null == convertView) {
				listItemView = inflater.inflate(android.R.layout.two_line_list_item, parent, false);
			}
			
			// Set up the text of the two views
			
			TextView titleView = (TextView) listItemView.findViewById(android.R.id.text1);
			titleView.setTextColor(Color.BLACK);
			
			TextView summaryView = (TextView) listItemView.findViewById(android.R.id.text2);
			summaryView.setTextColor(Color.BLACK);
			
			ProfileObject entry = getItem(position);

			
			
			// Set text on the view.
			titleView.setText(entry.firstName + " " + entry.lastName + " (" + entry.sport + ")");
			summaryView.setText(entry.hometown);

			return listItemView;
			
		}
    	
    }

	
	
}