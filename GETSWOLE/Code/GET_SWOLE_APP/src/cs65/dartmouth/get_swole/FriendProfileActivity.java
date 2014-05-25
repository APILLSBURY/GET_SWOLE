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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;

public class FriendProfileActivity extends Activity {
	
	Context mContext;
	Downloader mDownloader;
	
	// Information
	String profilePicture;
	String firstName;
	String lastName;
	String hometown;
	String sport;
	
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
        String serverURL = getString(R.string.server_url) + "/get_friends";
        
        // Set downloader
        mDownloader = new Downloader(mContext, serverURL);
     
        // Get Extras

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
     	
	}
	
}