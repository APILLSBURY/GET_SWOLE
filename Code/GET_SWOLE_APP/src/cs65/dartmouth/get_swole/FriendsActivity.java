package cs65.dartmouth.get_swole;

import java.io.IOException;

import cs65.dartmouth.get_swole.classes.ProfileObject;
import cs65.dartmouth.get_swole.gae.Downloader;
import cs65.dartmouth.get_swole.gae.Uploader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import android.app.Activity;
import android.content.SharedPreferences;

public class FriendsActivity extends Activity {
	
	// GCM
	public Downloader mDownloader;
	
	/**
	 * onCreate()
	 * @param Bundle savedInstanceStates
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		
		// GCM
		
        // Create the serverURL
        String serverURL = getString(R.string.server_url) + "/get_friends";
        
        // Set downloader
        mDownloader = new Downloader(getApplicationContext(), serverURL);
        
        getFriends();
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
	            	 
		        	 mDownloader.getFriends();
		        	 
		        	 msg = "Sent request for getting people's profiles";
                	 
            	 }
            	 catch (IOException ex) {
            		 msg = "Error: " + ex.getMessage();
            	 }
            	 
            	 return msg;

             }

             @Override
             protected void onPostExecute(String res) {
            	 Toast.makeText(getApplicationContext(), "Got a response!" + res, Toast.LENGTH_SHORT).show();
             }

         }.execute();
    	
		

		
	}
	
}