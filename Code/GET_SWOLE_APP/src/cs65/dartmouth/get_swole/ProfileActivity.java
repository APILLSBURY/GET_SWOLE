package cs65.dartmouth.get_swole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import cs65.dartmouth.get_swole.classes.ProfileObject;
import cs65.dartmouth.get_swole.classes.Workout;
import cs65.dartmouth.get_swole.database.DatabaseWrapper;
import cs65.dartmouth.get_swole.gae.Uploader;

/**
 * ProfileActivity.java  -  CS 65 Lab #1
 * @author Cameron Price
 * @date 4/5/14
 * @description This is the main activity for the ProfileActivity UI for My Runs
 */
public class ProfileActivity extends ListActivity {
	
	// Tag for logging purposes
	private static final String TAG = "CJP"; 
	
	// Camera and Cropping instances, taken from CameraControlActivity.java in class lecture
	public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
	public static final int REQUEST_CODE_TAKE_FROM_GALLERY = 1;
	public static final int REQUEST_CODE_CROP_PHOTO = 2;

	// Image string and Uri instance state key, taken from CameraControlActivity.java in class lecture
	private static final String IMAGE_UNSPECIFIED = "image/*";
	private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
	
	// Declare Uri, ImageView, and boolean for camera, taken from CameraControlActivity.java in class lecture
	private Uri mImageCaptureUri;
	private ImageView profileImageView;
	private byte[] pictureArray;
	
	// Workout List
	WorkoutAdapter workoutAdapter;
	ArrayList<Workout> workouts;
	public Map<String, String> workoutCheck = new HashMap<String, String>();
	
	// ProfileObject for GCM
	private ProfileObject profileObj;
	private Uploader profileUploader;
	private Uploader workoutsUploader;
	private Context mContext;
	private String regId;
	
	// ********** Main Functions ********** //
	
	private void updateWorkoutList() {
		workoutAdapter.clear();
		workoutAdapter.addAll(workouts);
		workoutAdapter.notifyDataSetInvalidated();
		Utils.setListViewHeightBasedOnChildren(getListView());
	}
	
	/**
	 * onCreate()
	 * @param Bundle savedInstanceStates
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// onCreate on super class and set ProfileActivity UI on screen
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		// Store the context
		mContext = getApplicationContext();
		
		// Get the RegID
		regId = getIntent().getExtras().getString("regId");
		
		// Get image view from UI
		profileImageView = (ImageView) findViewById(R.id.profilePhoto);
		
		// Taken from camera lecture
		if (savedInstanceState != null) {
			mImageCaptureUri = savedInstanceState.getParcelable(URI_INSTANCE_STATE_KEY);
			pictureArray = savedInstanceState.getByteArray(URI_INSTANCE_STATE_KEY);
			
			// Load Image to View
			loadImageToView();
		}
		else
			loadProfile();
		
	
		// Make sure that the correct height units are being displayed
		if (Utils.getHeightUnits(mContext)) {}
		else {
			EditText feetInput = (EditText) findViewById(R.id.feetinput);
			feetInput.setVisibility(View.GONE);
			TextView feet = (TextView) findViewById(R.id.feet);
			feet.setVisibility(View.GONE);
			TextView cmLabel = (TextView) findViewById(R.id.inch); // change the label to cm
			cmLabel.setText("cm");
		}
		
		// Make sure that the correct weight units are being displayed
		if (Utils.getWeightUnits(mContext)) {}
		else {
			TextView kgLabel = (TextView) findViewById(R.id.lb); // change label to kg
			kgLabel.setText("kg");
		}
		
		// Get Workout List and set to adapter
		DatabaseWrapper dbWrapper = new DatabaseWrapper(mContext);
		dbWrapper.open();
		workouts = (ArrayList<Workout>) dbWrapper.getAllEntries(Workout.class);
		dbWrapper.close();
		
		workoutAdapter = new WorkoutAdapter(mContext);
		setListAdapter(workoutAdapter);
		
        // ********** LAB 6 - App Engine **********
        
        // Set uploader objects to be
        profileUploader = new Uploader(mContext, getString(R.string.server_url) + "/post_profile");
        workoutsUploader = new Uploader(mContext, getString(R.string.server_url) + "/post_workouts");
        
        // Instantiate profile object
		profileObj = new ProfileObject();
 		// Store the regId
 		profileObj.setId(regId);
        
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		updateWorkoutList();
	}
	
	/**
	 * onSaveInstanceState()
	 * @param Bundle outState
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		// Save the image capture uri before the activity goes into background, take from Camera lecture
		outState.putParcelable(URI_INSTANCE_STATE_KEY, mImageCaptureUri);
		
		// If the pictureArray is not saved, make sure to retrieve it
		if (pictureArray != null)
			outState.putByteArray(URI_INSTANCE_STATE_KEY, pictureArray);
	}
	
	/**
	 * onChangePhotoClicked()
	 */
	public void onChangePhotoClicked(View v) {
		
		// Call Change Photo Dialog Fragment
		DialogFragment photo = ChangePhotoDialogFragment.newInstance(1);
	    photo.show(getFragmentManager(), "changePhoto");
		
	}
	
	/**
	 * cameraShot()
	 */
	public void cameraShot() {
		
		// Note: Code taking from Camera example in class
		
		// Initialize camera intent
		Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		// Construct temporary image path and name to save the taken image
		mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
		camera.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
		camera.putExtra("return-data", true);
		
		Log.d(TAG, mImageCaptureUri.getPath());
		
		// Save current path of file
		
	
		try {
			// Start a camera capturing activity
			startActivityForResult(camera, REQUEST_CODE_TAKE_FROM_CAMERA);
		} 
		
		catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * pickFromGallery()
	 */
	public void pickFromGallery() {
		
		// Initialize gallery intent
		Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		
//		// Construct temporary image path and name to save the taken image
//		mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
//		gallery.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
//		gallery.putExtra("return-data", true);
//		
//		Log.d(TAG, mImageCaptureUri.getPath());
	
		try {
			startActivityForResult(gallery, REQUEST_CODE_TAKE_FROM_GALLERY);
		} 
		
		catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * onActivityResult
	 * @param int requestCode
	 * @param int resultCode
	 * @param Intent data
	 */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
    	// Code from Camera Lecture
    	
    	Log.d(TAG, "Now try to crop image from gallery 1 ->" + requestCode);
    	
    	// If result was not okay, don't move forward
		if (resultCode != RESULT_OK) 
			return;

		// If result okay, move forward
		switch (requestCode) {
		
		// After camera, crop photo
		case REQUEST_CODE_TAKE_FROM_CAMERA:
			
			// Send image taken from camera for cropping
			cropImage();

			break;

		case REQUEST_CODE_TAKE_FROM_GALLERY:
			
			Log.d(TAG, "Now try to crop image from gallery 2");

			// Get data for the Uri after gallery
			mImageCaptureUri = data.getData();
			String path = getRealPathFromURI(mImageCaptureUri);
			
			// Check to make sure the photo exists on the phone
			if (path == null) {
				Toast.makeText(getApplicationContext(), "Photo does not physically exist on phone!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			File galleryfile = new File(path);
			
			if (!galleryfile.exists()) {
				Toast.makeText(getApplicationContext(), "Photo does not physically exist on phone!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			//Send image taken from gallery for cropping
			cropImage();
			
			Log.d(TAG, "Should have cropped!");
			
			break;
			
		// After cropping photo, set image to view
		case REQUEST_CODE_CROP_PHOTO:
			
			// Update image view after image crop
			Bundle extras = data.getExtras();
			
			if (extras != null) {
				
				// Get photo
				Bitmap photo = (Bitmap) extras.getParcelable("data");
				
				// Dump photo
				dumpPhoto(photo);
				
				// Load image to view
				loadImageToView();
				
			}
			
			// Delete temporary image taken by camera after crop.
			// this is the temp image
			String filepath = mImageCaptureUri.getPath();
			// this is the backup image file generated by the crop app
			String backupfile = filepath.replaceAll(".jpg$", "~2.jpg");
			File f = new File(filepath);
			if (f.exists()) {
				Log.d(TAG, "Should delete photo");
				Log.d(TAG, filepath);
				
				f.delete();
				
			}

			f = new File(backupfile);
			if (f.exists()) {
				Log.d(TAG, "Should delete backup");
				Log.d(TAG, backupfile);
				
				f.delete();
			}
			break;
		}
    } 
    
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = new String[] { android.provider.MediaStore.Images.ImageColumns.DATA };

        Cursor cursor = getContentResolver().query(contentUri, proj, null,
                null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        
        String filename = cursor.getString(column_index);
        cursor.close();
        
        return filename;
    }
    

    /**
	 * onSaveClicked()
	 * @param View v
	 * @description Saves profile image, sends toast, closes app
	 */
	public void onSaveClicked(View v) {
		
		// Check that necessary fields are inputted (name)
		
		// Check name
		String mKey = getString(R.string.preference_key_profile_first_name);
		String first = (String) ((EditText) findViewById(R.id.firstName)).getText().toString();
		
		mKey = getString(R.string.preference_key_profile_last_name);
		String last = (String) ((EditText) findViewById(R.id.lastName)).getText().toString();
		
		// Send toast that you must enter at least your name for a profile
		if (first.equals("") && last.equals("")) {
			// Send toast message
			Toast.makeText(getApplicationContext(), "Must enter your name!", Toast.LENGTH_SHORT).show();
			onPause();
			onResume();
			return;
		}
		
		// Save ProfileActivity
		saveProfile();
		
		// Save Profile on the Datastore
		uploadProfile();
		
		// Upload Workouts
		uploadWorkouts();
		
		// Send toast message
		Toast.makeText(getApplicationContext(), getString(R.string.save_profile_message), Toast.LENGTH_SHORT).show();
		
		finish();
		
	}

	/**
	 * uploadProfile()
	 */
	public void uploadProfile() {
	
	   	new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
            	 
            	String msg = "";
            	 
               	try {
               		
               		// Set profile photo string
               		if (pictureArray != null) {

            			ByteArrayInputStream inputStream = new ByteArrayInputStream(pictureArray);
            			Bitmap bmap = BitmapFactory.decodeStream(inputStream);
            			
            			profileObj.setPictureString(Utils.BitMapToString(bmap));
            			
            			inputStream.close();

               		}
               		else 
               			profileObj.setPictureString("null");
               		
		        	// Upload history of all entries
	        		profileUploader.uploadProfile(profileObj);
	        		
	        		Log.d(Globals.TAG, "Profile should have been uploaded!");
		        	 
		        	msg = "Uploaded the profile";
                	 
            	 }
            	 catch (IOException ex) {
            		msg = "Error: " + ex.getMessage();
            	 }
            	 
            	 return msg;

             }

             @Override
             protected void onPostExecute(String res) {
            	 
             }

         }.execute();
		
	}
	
	/**
	 * uploadWorkouts()
	 */
	public void uploadWorkouts() {
		
	   	new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
            	 
            	String msg = "";
            	 
               	try {
               		
               		// Make list to upload
               		ArrayList<Workout> uploadWorkouts = new ArrayList<Workout>();
               		
           			// Loop through workouts to find workout
           			for (Workout workoutData : workouts) {
               			
                   		// Add all workouts that are meant to be uploaded
                   		for (String workoutListed : workoutCheck.keySet()) {
               				
                   			Log.d(Globals.TAG, "Listed: " + workoutListed);
                   			
               				// If workout is listed and found the workout, add the workout to the list to upload
               				if (workoutListed.equals(workoutData.getName() + "," + workoutData.getId())) {
               					workoutData.setRegId(regId); // regId before adding to the list (for the datastore)
               					if (workoutData.getOwner().isEmpty())
               						workoutData.setOwner(profileObj.getFirstName() + " " + profileObj.getLastName()); // set the owner name
               					uploadWorkouts.add(workoutData);
                       			Log.d(Globals.TAG, "Added: " + workoutData.getName());
               				}

               			}
               			
               		}
               		
           				// Upload history of all entries
           				workoutsUploader.uploadWorkouts(uploadWorkouts);
		        	 
           				msg = "Uploaded the workouts";
                	 
            	 }
            	 catch (IOException ex) {
            		msg = "Error: " + ex.getMessage();
            	 }
            	 
            	 return msg;

             }

             @Override
             protected void onPostExecute(String res) {
            	 
             }

         }.execute();
		
	}
	
	/**
	 * onCancelClicked()
	 * @param View v
	 * @description Send toast, close app
	 */
	public void onCancelClicked(View v) {
		
		// Send toast message
		Toast.makeText(getApplicationContext(), getString(R.string.cancel_profile_message), Toast.LENGTH_SHORT).show();
		
		finish();
		
	}    
    
	// ********** Helper Functions ********** //
	
	/**
	 * loadProfile()
	 * @description Load profile information and profile image
	 */
	private void loadProfile() {

		// Load and update all profile views

		Log.d(TAG, "Entered into Load ProfileActivity");
		
		// Get the shared preferences - create or retrieve the activity
		// preference object

		String mKey = getString(R.string.profile_shared_preferences);
		SharedPreferences mPrefs = getSharedPreferences(mKey, MODE_PRIVATE);
		
		// Load Name
		
		mKey = getString(R.string.preference_key_profile_first_name);
		String mValue = mPrefs.getString(mKey, "");
		((EditText) findViewById(R.id.firstName)).setText(mValue);
		
		mKey = getString(R.string.preference_key_profile_last_name);
		mValue = mPrefs.getString(mKey, "");
		((EditText) findViewById(R.id.lastName)).setText(mValue);
		
		// Load Hometown
		mKey = getString(R.string.preference_key_profile_hometown);
		mValue = mPrefs.getString(mKey, "");
		((EditText) findViewById(R.id.hometown)).setText(mValue);
		
		// Load Sport
		mKey = getString(R.string.preference_key_profile_sport);
		mValue = mPrefs.getString(mKey, "");
		((EditText) findViewById(R.id.sport)).setText(mValue);
		
		// Load Gender

		mKey = getString(R.string.preference_key_profile_gender);

		int mIntValue = mPrefs.getInt(mKey, -1);
		
		// In case not one saved before
		if (mIntValue >= 0) {
			
			// Find the radio button that should be checked.
			RadioButton radioBtn = (RadioButton) ((RadioGroup) findViewById(R.id.radioGender)).getChildAt(mIntValue);
			
			// Check the button
			radioBtn.setChecked(true);
			
		}
		// uncheck them all
		else {
	
			((RadioGroup) findViewById(R.id.radioGender)).clearCheck();
		}
		
		// Load Height
		
		mKey = getString(R.string.preference_key_profile_height_feet);
		String feet = mPrefs.getString(mKey, "");
		
		mKey = getString(R.string.preference_key_profile_height_in);
		String in = mPrefs.getString(mKey, "");
		
		if (Utils.getHeightUnits(mContext)) {
			Log.d(Globals.TAG, "Loading in");
			
			// Check that it is not empty
			if (in.equals("") && feet.equals("")) {
				((EditText) findViewById(R.id.feetinput)).setText(feet);
				((EditText) findViewById(R.id.inchinput)).setText(in);
			}
			else if (!in.equals("") && feet.equals("")) {
				((EditText) findViewById(R.id.feetinput)).setText(feet);
				((EditText) findViewById(R.id.inchinput)).setText(Utils.decimalFormat.format(Double.parseDouble(in)));
			}
			else if (in.equals("") && !feet.equals("")) {
				((EditText) findViewById(R.id.feetinput)).setText(Utils.decimalFormat.format(Integer.parseInt(feet)));
				((EditText) findViewById(R.id.inchinput)).setText(in);
			}
			else if (!in.equals("") && !feet.equals("")) {
				((EditText) findViewById(R.id.feetinput)).setText(Utils.decimalFormat.format(Integer.parseInt(feet)));
				((EditText) findViewById(R.id.inchinput)).setText(Utils.decimalFormat.format(Double.parseDouble(in)));
			}
		}
		else {
			Log.d(Globals.TAG, "Loading cm");
			
			if (in.equals("") && feet.equals("")) {
				((EditText) findViewById(R.id.inchinput)).setText(in);
			}
			else if (!in.equals("") && feet.equals("")) {
				// Convert to cm
				double cm = Utils.inchesToCentimeters(Double.parseDouble(in));
				((EditText) findViewById(R.id.inchinput)).setText(Utils.decimalFormat.format(cm));
			}
			else {
				// Convert to cm
				double cm = Utils.inchesToCentimeters(Double.parseDouble(feet) * 12 + Double.parseDouble(in));
				((EditText) findViewById(R.id.inchinput)).setText(Utils.decimalFormat.format(cm));
			}
		}
		
		Log.d(Globals.TAG, "GOT THROUGH HEIGHT!");
		
		// Load Weight
		
		mKey = getString(R.string.preference_key_profile_weight);
		String pounds = mPrefs.getString(mKey, "");
		
		if (Utils.getWeightUnits(mContext)) {
			if (!pounds.equals(""))
				((EditText) findViewById(R.id.weightinput)).setText(Utils.decimalFormat.format(Double.parseDouble(pounds)));
		}
		else {
			if (!pounds.equals("")){
				// Convert to kg
				double kg = Utils.poundsToKilos(Double.parseDouble(pounds));
				((EditText) findViewById(R.id.weightinput)).setText(Utils.decimalFormat.format(kg));
			}
		}
		
		Log.d(Globals.TAG, "GOT THROUGH WEIGHT!");
		
		// Load Bio
		mKey = getString(R.string.preference_key_profile_bio);
		mValue = mPrefs.getString(mKey, "");
		((EditText) findViewById(R.id.bioInputText)).setText(mValue);
		
		// Load Email
		mKey = getString(R.string.preference_key_profile_email);
		mValue = mPrefs.getString(mKey, "");
		((EditText) findViewById(R.id.email)).setText(mValue);
		
		// Load Phone
		mKey = getString(R.string.preference_key_profile_phone);
		mValue = mPrefs.getString(mKey, "");
		((EditText) findViewById(R.id.phone)).setText(mValue);
		
		// Grab existing Image, code help from TA
		try {
			
			// Get image
			FileInputStream fis = openFileInput(getString(R.string.profile_saved_photo_file_name));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			// Compress image in byte array
			byte[] buffer = new byte[5 * 1024];
			int n;
			while ((n = fis.read(buffer)) > -1) {
				bos.write(buffer, 0, n); //
			}
			fis.close();
			
			// Save picture array
			pictureArray = bos.toByteArray();
		}
		catch (IOException e){
			
		}
		
		Log.d(TAG, "Now before loaded image to view");
		
		loadImageToView();
		
		Log.d(TAG, "loaded image to view");
		
	}
	
	/**
	 * saveProfile()
	 * @description Save profile image and profile information
	 */
	private void saveProfile() {

		Log.d(TAG, "saveProfile()");

		// Getting the shared preferences editor

		String mKey = getString(R.string.profile_shared_preferences);
		SharedPreferences mPrefs = getSharedPreferences(mKey, MODE_PRIVATE);
		
		SharedPreferences.Editor mEditor = mPrefs.edit();
		mEditor.clear();

		// Save Name Information
		
		mKey = getString(R.string.preference_key_profile_first_name);
		String first = (String) ((EditText) findViewById(R.id.firstName)).getText().toString();
		mEditor.putString(mKey, first);
		
		mKey = getString(R.string.preference_key_profile_last_name);
		String last = (String) ((EditText) findViewById(R.id.lastName)).getText().toString();
		mEditor.putString(mKey, last);
		
		profileObj.setName(first, last);
		
		// Save Hometown
		mKey = getString(R.string.preference_key_profile_hometown);
		String mValue = (String) ((EditText) findViewById(R.id.hometown)).getText().toString();
		mEditor.putString(mKey, mValue);
		
		profileObj.setHometown(mValue);
		
		// Save Sport
		mKey = getString(R.string.preference_key_profile_sport);
		mValue = (String) ((EditText) findViewById(R.id.sport)).getText().toString();
		mEditor.putString(mKey, mValue);
		
		profileObj.setSport(mValue);
		
		// Save Gender

		mKey = getString(R.string.preference_key_profile_gender);
		RadioGroup mRadioGroup = (RadioGroup) findViewById(R.id.radioGender);
		int mIntValue = mRadioGroup.indexOfChild(findViewById(mRadioGroup.getCheckedRadioButtonId()));
		mEditor.putInt(mKey, mIntValue);
		
		profileObj.setGender(mIntValue);
		
		// Save Height
		
		String feetKey = getString(R.string.preference_key_profile_height_feet);
		String feetString = (String) ((EditText) findViewById(R.id.feetinput)).getText().toString();
		String inKey = getString(R.string.preference_key_profile_height_in);
		String inString = (String) ((EditText) findViewById(R.id.inchinput)).getText().toString();
		
		if (Utils.getHeightUnits(mContext)) {
			mEditor.putString(feetKey, feetString);
			mEditor.putString(inKey, inString);
			
			if ((!feetString.equals("") && !inString.equals(""))) {
				double feet = Double.parseDouble(feetString);
				double in = Double.parseDouble(inString);
				profileObj.setHeight(feet, in);
			}
			else if (!feetString.equals("") && inString.equals("")) {
				double feet = Double.parseDouble(feetString);
				profileObj.setHeight(feet, 0);
			}
			else if (feetString.equals("") && !inString.equals("")) {
				double in = Double.parseDouble(inString);
				profileObj.setHeight(0, in);
			}
		}
		else {
			if (!inString.equals("")) {
				double cm = Double.parseDouble(inString); // get the value in cm
				mEditor.putString(feetKey, String.valueOf((int) Utils.centimetersToInches(cm) / 12)); // set the feet as the same
				mEditor.putString(inKey, String.valueOf(Utils.centimetersToInches(cm) % 12)); // convert back to inches
				profileObj.setHeight(0, Utils.centimetersToInches(cm)); // save in terms of inches
			}
		}
		
		// Save Weight
		
		mKey = getString(R.string.preference_key_profile_weight);
		String pounds = (String) ((EditText) findViewById(R.id.weightinput)).getText().toString();
		
		if (Utils.getWeightUnits(mContext)) {
			mEditor.putString(mKey, pounds);
			
			if (!pounds.equals("")) {
				double weight = Double.parseDouble(((String) ((EditText) findViewById(R.id.weightinput)).getText().toString()));
				profileObj.setWeight(weight);
			}
		}
		else {
			if (!pounds.equals("")) {
				double kg = Double.parseDouble(pounds); // get the value in kg
				mEditor.putString(mKey, String.valueOf(Utils.kilosToPound(kg))); // convert back to pounds
				profileObj.setWeight(Utils.kilosToPound(kg)); // save in terms of pounds
			}
		}
		

		// Save Bio
		
		mKey = getString(R.string.preference_key_profile_bio);
		mValue = (String) ((EditText) findViewById(R.id.bioInputText)).getText().toString();
		mEditor.putString(mKey, mValue);
		
		profileObj.setBio(mValue);
		
		// Save Email
		
		mKey = getString(R.string.preference_key_profile_email);
		mValue = (String) ((EditText) findViewById(R.id.email)).getText().toString();
		mEditor.putString(mKey, mValue);
		
		profileObj.setEmail(mValue);
		
		// Save Phone
		
		mKey = getString(R.string.preference_key_profile_phone);
		mValue = (String) ((EditText) findViewById(R.id.phone)).getText().toString();
		mEditor.putString(mKey, mValue);
		
		profileObj.setPhone(mValue);
		
		// Save Workouts Shared (checked)
		if (!workoutCheck.isEmpty()) {
			Set<String> workoutSet = workoutCheck.keySet();
			for (String workoutString : workoutSet) {
				mEditor.putString(workoutString, workoutCheck.get(workoutString));
			}
		}
		
		// Commit all the changes into the shared preference
		mEditor.commit();
		
		// Save Picture
		savePhoto();

	}
	
	/**
	 * savePhoto()
	 * @description Save profile photo into internal storage
	 */
	private void savePhoto() {

		// Code from lecture on camera
		try {
			
			// Check to make sure pictureArray is not null, either wise will crash
			if (pictureArray != null) {
			
			// Grab profile_photo_file_name and save it from picture Array
			FileOutputStream fos = openFileOutput(getString(R.string.profile_saved_photo_file_name), MODE_PRIVATE);
			fos.write(pictureArray);
			fos.flush();
			fos.close();
			
			}
		} 
		
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
    
    /**
     * cropImage()
     * @description Crop and resize image for profile
     */
 	private void cropImage() {
 		
 		// Code from camera lecture
 		
 		// Use existing crop activity
 		Intent crop = new Intent("com.android.camera.action.CROP");
 		crop.setDataAndType(mImageCaptureUri, IMAGE_UNSPECIFIED);

 		// Specify image size
 		crop.putExtra("outputX", 100);
 		crop.putExtra("outputY", 100);

 		// Specify aspect ratio, 1:1
 		crop.putExtra("aspectX", 1);
 		crop.putExtra("aspectY", 1);
 		crop.putExtra("scale", true);
 		crop.putExtra("return-data", true);
 		
 		// REQUEST_CODE_CROP_PHOTO is an integer tag you defined to
 		// identify the activity in onActivityResult() when it returns
 		startActivityForResult(crop, REQUEST_CODE_CROP_PHOTO);
 	}
	
    /**
     * dumpPhoto()
     * @param Bitmap photo
     * @Description Get rid of photo from the picture array
     */
    private void dumpPhoto(Bitmap photo) {
    	
    	// Code assisted by TA
    	
    	try {
			
    		// Empty out the pictureArray with a nulled image
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			photo.compress(Bitmap.CompressFormat.PNG, 100, output);
			pictureArray = output.toByteArray();
		
			output.close();
			}
    	
    	catch (IOException ioe){}
    }
    
    /**
     * loadImageToView()
     * @description Load profile photo from internal storage
     */
	private void loadImageToView() {
		
		// Load profile photo from internal storage
		try {
			
			ByteArrayInputStream inputStream = new ByteArrayInputStream(pictureArray);
			Bitmap bmap = BitmapFactory.decodeStream(inputStream);
			profileImageView.setImageBitmap(bmap);
			inputStream.close();
			
		} 
		
		catch (Exception ex) {
			
			// Default profile photo if no photo saved before.
			profileImageView.setImageResource(R.drawable.default_profile);
		}
	}

	// ChangePhotoDialogFragment
	
	@SuppressLint("ValidFragment")
	public static class ChangePhotoDialogFragment extends DialogFragment {
	    
		public static ChangePhotoDialogFragment newInstance(int title) {
			ChangePhotoDialogFragment frag = new ChangePhotoDialogFragment();
			Bundle args = new Bundle();
			args.putInt("title", title);
			frag.setArguments(args);
			return frag;
		}
		
		@Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			// Get the parent activity
			final Activity parent = getActivity();
			
	    	// Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle("Select profile image")
	        	   .setItems(new String [] {"Take from camera", "Select from gallery"}, new DialogInterface.OnClickListener() {
	        		   public void onClick(DialogInterface dialog, int which) {
	        			   // Choose the Camera
	        			   if (which == 0)
	        				   ((ProfileActivity) parent).cameraShot();
	        			   // Choose the Gallery
	        			   else
	        				   ((ProfileActivity) parent).pickFromGallery();
	        		   }
	        	   });
	               
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
		
	}
	
	// HANDLING LISTVIEW OF WORKOUTS
	
    @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        // Get the workout
        Workout entry = workouts.get(position);
        
        // Get checkbox
        CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox1);
		
		Log.d(Globals.TAG, entry.getName());
		
		
		if (workoutCheck.get(entry.getName() + "," + entry.getId()) == null) {
	        //Toast.makeText(mContext, entry.getName(), Toast.LENGTH_SHORT).show();
	        
	        // Set the workout as checked
	        checkBox.setChecked(true);
			
			// Define hashmap, so that when onSaveClicked is called, info is not lost
			workoutCheck.put(entry.getName() + "," + entry.getId(), "check");
	        
		}
		else {
	        //Toast.makeText(mContext, entry.getName() + " unchecked", Toast.LENGTH_SHORT).show();
			
			// Set the workout as unchecked
	        checkBox.setChecked(false);

			// Define hashmap, so that when onSaveClicked is called, info is not lost
			workoutCheck.remove(entry.getName() + "," + entry.getId());
			
			Toast.makeText(mContext, workoutCheck.keySet().toString(), Toast.LENGTH_SHORT).show();
			
		}
		
       
	}

	
    // ********** Private Classes **********
    
    private class WorkoutAdapter extends ArrayAdapter<Workout> {
    	
		public WorkoutAdapter(Context context) {
			super(context, android.R.layout.two_line_list_item);	
		}
    	
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			View listItemView = convertView;
			
			if (null == convertView) {
				listItemView = inflater.inflate(R.layout.workouts_profile_list_row, parent, false);
			}
			
    	    // Access the textview to set
    	    TextView workoutView = (TextView) listItemView.findViewById(R.id.workout_list_single_row); 
    	    TextView ownerView = (TextView) listItemView.findViewById(R.id.ownerName);
    	    //workoutView.setTextColor(Color.BLACK);
    	    Workout workout = workouts.get(position);
    	    workoutView.setText(workout.getName());
    	    ownerView.setText(workout.getOwner());
    	    
    	    
            // Get checkbox if necessary
            CheckBox checkBox = (CheckBox) listItemView.findViewById(R.id.checkBox1);
            
            // Get Shared Preferences
    		String mKey = getString(R.string.profile_shared_preferences);
    		SharedPreferences mPrefs = getSharedPreferences(mKey, MODE_PRIVATE);
            
    		mKey = workout.getName() + "," + workout.getId();
    		String mValue = mPrefs.getString(mKey, "");
    		
    		// Check it if it is checked
    		if (!mValue.equals("")) {
    			checkBox.setChecked(true);
    			workoutCheck.put(mKey, "check"); // make sure that the hashmap has that info too
    		}
    	    
			return listItemView;
		}
    	
    }
	
}