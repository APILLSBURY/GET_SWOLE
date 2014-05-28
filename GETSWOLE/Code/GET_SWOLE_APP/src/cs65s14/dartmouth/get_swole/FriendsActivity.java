package cs65s14.dartmouth.get_swole;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cs65s14.dartmouth.get_swole.R;
import cs65s14.dartmouth.get_swole.classes.ProfileObject;
import cs65s14.dartmouth.get_swole.gae.Downloader;
import cs65s14.dartmouth.get_swole.gae.Uploader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class FriendsActivity extends ListActivity {
	
	Downloader mDownloader;
	ArrayList<ProfileObject> friends;
	
	ProfilesAdapter profilesAdapter;
	Context mContext;
	
	String regId;
	
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
			        orderFriends();
					updateFriendProfiles();
				}
				
        	 
			}

		}.execute();	
	}
	
    public void orderFriends() {
    	
        String[] names = new String[friends.size()];
        
        for (int i = 0; i < friends.size(); i++) {
        	names[i] = friends.get(i).getFirstName() + " " + friends.get(i).getLastName();
        }
        
        Arrays.sort(names);
        
        // Names are now sorted alphabetically, now need to remake friends arraylist
        
        ArrayList<ProfileObject> newFriendsList = new ArrayList<ProfileObject>();
        
        for (String name : names) {
        	for (ProfileObject friend : friends) {
        		
        		if (name.equals(friend.getFirstName() + " " + friend.getLastName()))
        			newFriendsList.add(friend);
        	}
        }
        
        // Update with the new list
        friends = newFriendsList;
    	
    }
	
    @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        ProfileObject entry = friends.get(position);
        
        Intent entryIntent = new Intent(mContext, FriendProfileActivity.class);

        entryIntent.putExtra("regId", entry.getId());
        entryIntent.putExtra("pictureString", entry.getPictureString().replace(' ', '+'));
        entryIntent.putExtra("firstName", entry.getFirstName());
        entryIntent.putExtra("lastName", entry.getLastName());
        entryIntent.putExtra("hometown", entry.getHometown());
        entryIntent.putExtra("sport", entry.getSport());  
        
        entryIntent.putExtra("gender", entry.getGender());
        entryIntent.putExtra("height", entry.getHeight());
        entryIntent.putExtra("weight", entry.getWeight());
        entryIntent.putExtra("bio", entry.getBio());
        entryIntent.putExtra("email", entry.getEmail());
        entryIntent.putExtra("phone", entry.getPhone());
        
        startActivity(entryIntent);
       
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
				listItemView = inflater.inflate(R.layout.friends_list_row, parent, false);
			}
			
			// Set up the text of the two views
			
			TextView titleView = (TextView) listItemView.findViewById(R.id.friend_list_row_title);
			TextView summaryView = (TextView) listItemView.findViewById(R.id.friend_list_row_summary);
			
			// Get Profile
			ProfileObject entry = getItem(position);
			
			// Set text on the views
			if (!entry.getSport().equals("")) {
				titleView.setText(entry.getFirstName() + " " + entry.getLastName() + " (" + entry.getSport() + ")");
			}
			else
				titleView.setText(entry.getFirstName() + " " + entry.getLastName() );
			summaryView.setText(entry.getHometown());

			// Set the profile picture, only if there exists a picture
			if (!entry.getPictureString().equals("null")) {
				Log.d(Globals.TAG, "TRANSFERED:\n" + entry.getPictureString());
				
				String updatedString = entry.getPictureString().replace(' ', '+');
				
				Bitmap photo = Utils.StringToBitMap(updatedString);
				
				ImageView imageView = (ImageView) listItemView.findViewById(R.id.friendProfilePhoto);
				
				imageView.setImageBitmap(photo);
			}
			
			return listItemView;
			
		}
    	
    }

	
	
}