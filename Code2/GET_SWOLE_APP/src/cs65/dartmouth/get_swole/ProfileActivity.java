package cs65.dartmouth.get_swole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import cs65.dartmouth.get_swole.R;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * ProfileActivity.java  -  CS 65 Lab #1
 * @author Cameron Price
 * @date 4/5/14
 * @description This is the main activity for the ProfileActivity UI for My Runs
 */
public class ProfileActivity extends Activity {
	
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
//	private boolean isTakenFromCamera;
	private byte[] pictureArray;
	
	// ********** Main Functions ********** //
	
	/**
	 * onCreate()
	 * @param Bundle savedInstanceStates
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// onCreate on super class and set ProfileActivity UI on screen
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
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
		
		// Save ProfileActivity
		saveProfile();
		
		// Send toast message
		Toast.makeText(getApplicationContext(), getString(R.string.save_profile_message), Toast.LENGTH_SHORT).show();
		
		finish();
		
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
		
		mKey = getString(R.string.profile_first_name);
		String mValue = mPrefs.getString(mKey, "");
		((EditText) findViewById(R.id.firstName)).setText(mValue);
		
		mKey = getString(R.string.profile_last_name);
		mValue = mPrefs.getString(mKey, "");
		((EditText) findViewById(R.id.lastName)).setText(mValue);
		
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
		
		Log.d(TAG, "LOADED OTHER");
		
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
		String mValue = (String) ((EditText) findViewById(R.id.firstName)).getText().toString();
		mEditor.putString(mKey, mValue);
		
		mKey = getString(R.string.preference_key_profile_last_name);
		mValue = (String) ((EditText) findViewById(R.id.lastName)).getText().toString();
		mEditor.putString(mKey, mValue);
		
		// Save Gender

		mKey = getString(R.string.preference_key_profile_gender);
		RadioGroup mRadioGroup = (RadioGroup) findViewById(R.id.radioGender);
		int mIntValue = mRadioGroup.indexOfChild(findViewById(mRadioGroup.getCheckedRadioButtonId()));
		mEditor.putInt(mKey, mIntValue);
		
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
	
}