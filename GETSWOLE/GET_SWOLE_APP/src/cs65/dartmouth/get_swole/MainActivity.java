package cs65.dartmouth.get_swole;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import cs65.dartmouth.get_swole.R;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
	    switch(item.getItemId()){
	    case R.id.profile_settings:
	    	Intent profile = new Intent(this, ProfileActivity.class);
	    	startActivity(profile);
	    	return true;
	    case R.id.friends_list:
	    	return false;
	    case R.id.get_swole_settings:
	        Intent settings = new Intent(this, SettingsActivity.class);
	        startActivity(settings);
	        return true;            
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

		private Map<Integer, Object> mPageReferenceMap = new HashMap<Integer, Object>();
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			
			Fragment currFragment = null;
			
			switch (position) {
			
			// WorkoutFragment
			case 0:
			
				currFragment = new WorkoutFragment();
				Bundle args1 = new Bundle();
				currFragment.setArguments(args1);
	
				mPageReferenceMap.put(Integer.valueOf(position), currFragment);
				
			// ScheduleFragment
			case 1:
				
				currFragment = new ScheduleFragment();
				Bundle args2 = new Bundle();
				currFragment.setArguments(args2);
	
				mPageReferenceMap.put(Integer.valueOf(position),currFragment);
			
			// ProgressMainFragment	
			case 2:
			
				currFragment = new ProgressMainFragment();
				Bundle args3 = new Bundle();
				currFragment.setArguments(args3);
	
				mPageReferenceMap.put(Integer.valueOf(position), currFragment);
				
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
	
//	@Override
//	public void onPause() {
//		super.onPause();
//	}
//	
//	@Override
//	public void onResume() {
//		super.onResume();
//	}
//	
//	@Override
//	public void

}
