package cs65.dartmouth.get_swole;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ProgressMainFragment extends ListFragment {
	
//	ExerciseEntryDbHelper dbHelper;
//	ActivityEntriesAdapter entriesAdapter;
//	HistoryTabDbUpdateReceiver receiver;
//	Context mContext;
	
	// retrieve records from the database and display them in the list view
	private void updateHistoryEntries() {
//		ArrayList<ExerciseEntry> entryList = dbHelper
//				.fetchEntries();
//		entriesAdapter.clear();
//		entriesAdapter.addAll(entryList);
//		entriesAdapter.notifyDataSetChanged();
	}
	
	//@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		mContext = getActivity();
//		
//		dbHelper = new ExerciseEntryDbHelper(mContext);
//		
//		entriesAdapter = new ActivityEntriesAdapter(mContext);
//		
//		setListAdapter(entriesAdapter);

		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		updateHistoryEntries();
	}
	
	@Override
	public void onPause() {
//		mContext.unregisterReceiver(receiver);
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	return inflater.inflate(R.layout.fragment_progress_main, container, false);
    }
	
    @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
//        ExerciseEntry entry = dbHelper.fetchEntryByIndex((long) position + 1);
//        
//        Log.d("CJP", "History Tab: " + position);
//        
//        if (entry.getInputType() == 0) {
//        
//	        Intent entryIntent = new Intent(mContext, DisplayEntryActivity.class);
//	        
//	        entryIntent.putExtra("_id", entry.getID());
//	        entryIntent.putExtra("activityType", Utils.parseActivityType(entry.getActivityType(), mContext));
//	        entryIntent.putExtra("dateTime", Utils.parseTime(entry.getTimeInMillis(),mContext));
//	        entryIntent.putExtra("duration", Utils.parseDuration( (int) entry.getDuration() * 60, mContext));  
//	        entryIntent.putExtra("distance", Utils.parseDistance(entry.getDistance(),mContext));
//	        
//	        startActivity(entryIntent);
//        }
//        
//        else {
//        	
//        	Intent entryIntent = new Intent(mContext, MapDisplayActivity.class);
//        	
//        	entryIntent.putExtra("taskType", 1);
//        	entryIntent.putExtra("_id", entry.getID());
//        	
//        	Log.d("CJP", "Now starting the activity w/ id " + entry.getID());
//        	
//        	startActivity(entryIntent);
//        	
//        }
//       
	}
    
    
    // ********** Private Classes **********
    
//    private class ActivityEntriesAdapter extends ArrayAdapter<ExerciseEntry> {
//    	
//		public ActivityEntriesAdapter(Context context) {
//			super(context, android.R.layout.two_line_list_item);	
//		}
//    	
//		public View getView(int position, View convertView, ViewGroup parent) {
//			
//			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			
//			View listItemView = convertView;
//			
//			if (null == convertView) {
//				listItemView = inflater.inflate(android.R.layout.two_line_list_item, parent, false);
//			}
//			
//			// Set up the text of the two views
//			
//			TextView titleView = (TextView) listItemView.findViewById(android.R.id.text1);
//			TextView summaryView = (TextView) listItemView.findViewById(android.R.id.text2);
//			
//			ExerciseEntry entry = getItem(position);
//			
//			
//			//parse data to readable format
//			String activityTypeString = Utils.parseActivityType(entry.getActivityType(), mContext);
//			String dateString = Utils.parseTime(entry.getTimeInMillis(), mContext);
//			String distanceString;
//			
//			String durationString;
//			// If manual input, multiply minutes by 60 to get seconds
//			if (entry.getInputType() == 0) {
//				durationString = Utils.parseDuration( (int) entry.getDuration() * 60,mContext);
//				distanceString = Utils.parseDistance(entry.getDistance(),mContext);
//			}
//			else {
//				durationString = Utils.parseDuration( (int) entry.getDuration(),mContext);
//				distanceString = Utils.parseMeters(entry.getDistance(), mContext); // converting meters
//			}
//
//			// Set text on the view.
//			titleView.setText(activityTypeString + ", " + dateString);
//			summaryView.setText(distanceString + ", " + durationString);
//
//			return listItemView;
//			
//		}
//    	
//    }

//    private class HistoryTabDbUpdateReceiver extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			new GetEntryListAsyncTask().execute();
//		}
//		
//    }
//    
//    private class GetEntryListAsyncTask extends AsyncTask<Void, Void, ArrayList<ExerciseEntry>> {
//
//		@Override
//		protected ArrayList<ExerciseEntry> doInBackground(Void... params) {
//            return dbHelper.fetchEntries();
//			
//		}
//    	
//		@Override
//		protected void onPostExecute(ArrayList<ExerciseEntry> entries) {
//			entriesAdapter.clear();
//			entriesAdapter.addAll(entries);
//			entriesAdapter.notifyDataSetChanged();
//		}
//
//    }
    
}

