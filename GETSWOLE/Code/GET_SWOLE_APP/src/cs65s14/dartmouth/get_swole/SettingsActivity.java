package cs65s14.dartmouth.get_swole;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import cs65s14.dartmouth.get_swole.R;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {


	@SuppressWarnings("deprecation")
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
	    // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
        
        
        Preference historyPrefs = (Preference) findPreference("deleteHistory");
        historyPrefs.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
        		
                AppDialogFragment name = AppDialogFragment.newInstanceConfirm(0);
                name.show(getFragmentManager(), "Are you sure you want to delete history");
        		
        		return true;
        	}
        });
        
        Preference deletePrefs = (Preference) findPreference("deleteAll");
        deletePrefs.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
 
                AppDialogFragment name = AppDialogFragment.newInstanceConfirm(1);
                name.show(getFragmentManager(), "Are you sure you want to delete all");
        		
        		return true;
        	}
        });
        
	}

}
