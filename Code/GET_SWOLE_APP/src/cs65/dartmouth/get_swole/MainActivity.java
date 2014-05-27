package cs65.dartmouth.get_swole;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import cs65.dartmouth.get_swole.gae.ServerUtilities;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	// Google Cloud Messaging
	public static final int WORKOUT_FRAGMENT = 0;
	public static final int SCHEDULE_FRAGMENT = 1;
	public static final int PROGRESS_MAIN_FRAGMENT = 2;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private GoogleCloudMessaging gcm;
    private static String regId;
    private Context mContext;
    public static String SENDER_ID = "1096115807294"; 
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	public SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		//actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLUE));

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		// Set Default Page to Schedule
		mViewPager.setCurrentItem(1);
		
		// Google Cloud Messaging
		mContext = getApplicationContext();
				
		// Check device for Play Services APK. If check succeeds, proceed with
		// GCM registration.
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regId = getRegistrationId(mContext);

			if (regId.isEmpty()) {
				Log.d(Globals.TAG, "regId is empty");
				registerInBackground();
				Log.d(Globals.TAG, "Should be registering");
			}
		}
        
        else {
            Log.i(Globals.TAG, "No valid Google Play Services APK found.");
        }
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}
	
	// Menu Bar Options
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
	    switch(item.getItemId()){
	    case R.id.profile_activity:
	    	Intent profile = new Intent(this, ProfileActivity.class);
	    	profile.putExtra("regId", regId); // send regId for profile
	    	startActivity(profile);
	    	return true;
	    case R.id.friends_activity:
	    	Intent friends = new Intent(this, FriendsActivity.class);
	    	startActivity(friends);
	    	return true;
	    case R.id.settings_activity:
	        Intent settings = new Intent(this, SettingsActivity.class);
	        startActivity(settings);
	        return true;     
	    case R.id.action_add_workout:
	    	// We want to display the dialog for the workout name
	    	DialogFragment fragment = AppDialogFragment.newInstance(AppDialogFragment.DIALOG_ID_NEW_WORKOUT);
	        fragment.show(getFragmentManager(), getString(R.string.dialog_fragment_tag_new_workout));
	    	
	    }
	    return false;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		@SuppressLint("UseSparseArrays")
		private Map<Integer, Object> mPageReferenceMap = new HashMap<Integer, Object>();
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			
			Fragment currFragment = null;
			
			switch (position) {
			
			// WorkoutFragment
			case WORKOUT_FRAGMENT:
			
				currFragment = new WorkoutFragment();
				Bundle args1 = new Bundle();
				currFragment.setArguments(args1);
	
				mPageReferenceMap.put(Integer.valueOf(position), currFragment);
				
				return currFragment;
				
			// ScheduleFragment
			case SCHEDULE_FRAGMENT:
				currFragment = new ScheduleFragment();
				Bundle args2 = new Bundle();
				currFragment.setArguments(args2);
	
				mPageReferenceMap.put(Integer.valueOf(position),currFragment);
			
				return currFragment;
				
			// ProgressMainFragment	
			case PROGRESS_MAIN_FRAGMENT:
			
				currFragment = new ProgressMainFragment();
				Bundle args3 = new Bundle();
				currFragment.setArguments(args3);
	
				mPageReferenceMap.put(Integer.valueOf(position), currFragment);
				
				return currFragment;
				
			}
			
			return currFragment;
			
		}
		
        @SuppressWarnings("deprecation")
        @Override
        public void destroyItem(View container, int position, Object object) {

            super.destroyItem(container, position, object);

            mPageReferenceMap.remove(Integer.valueOf(position));
            
            Toast.makeText(getApplicationContext(), "Destroyed Item " + position, Toast.LENGTH_SHORT).show();
        }
        
        public Object getFragment(int key) {
            return mPageReferenceMap.get(key);
        }

		@Override
		public int getCount() {
			// Show 4 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}
	
	
	// You need to do the Play Services APK check here too.
	@Override
	protected void onResume() {
	    super.onResume();
	    checkPlayServices();
	}
	
	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(Globals.TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	
	// CODE TAKEN FROM LAB 6 AND DEMO CODE
	
	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			return "";
		}
		return registrationId;
	}
	
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(MainActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(mContext);
					}
					
					Log.d(Globals.TAG, "Now trying to register sender id");
					regId = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regId;
					
					Log.d(Globals.TAG, "Should have registered sender id");

					// You should send the registration ID to your server over
					// HTTP,
					// so it can use GCM/HTTP or CCS to send messages to your
					// app.
					// The request to your server should be authenticated if
					// your app
					// is using accounts.
					Log.d(Globals.TAG, "Now trying to do send registration id to backend");
					ServerUtilities.sendRegistrationIdToBackend(getApplicationContext(), regId);
					Log.d(Globals.TAG, "Now should have sent registration id to backend");

					// For this demo: we don't need to send it because the
					// device
					// will send upstream messages to a server that echo back
					// the
					// message using the 'from' address in the message.

					// Persist the regID - no need to register again.
					storeRegistrationId(mContext, regId);
					
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Log.i(Globals.TAG, "gcm register msg: " + msg);
			}
		}.execute(null, null, null);
	}
	
	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}


}
