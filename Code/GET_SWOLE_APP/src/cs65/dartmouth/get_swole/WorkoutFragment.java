package cs65.dartmouth.get_swole;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cs65.dartmouth.get_swole.classes.Workout;
import cs65.dartmouth.get_swole.database.DatabaseWrapper;

public class WorkoutFragment extends ListFragment {
	
	DatabaseWrapper dbWrapper;
	WorkoutsAdapter workoutsAdapter;
	List<Workout> workouts;
	Context mContext;
	IntentFilter mMessageIntentFilter;
	
	private BroadcastReceiver mMessageUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String msg = intent.getStringExtra("message");
			if (msg != null && msg.equals("update")) {
				Log.d(Globals.TAG, "Received update");	
				workoutsAdapter.notifyDataSetChanged();
				
			}
		}
	};
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mContext = getActivity();
		
		dbWrapper = new DatabaseWrapper(mContext);
		dbWrapper.open();
		workouts = dbWrapper.getAllEntries(Workout.class);
		dbWrapper.close();
				
		workoutsAdapter = new WorkoutsAdapter(mContext, R.layout.workouts_list_row, workouts);
		
		setListAdapter(workoutsAdapter);
				
		// Define the listener interface
        OnItemClickListener listener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	
            	Bundle b = new Bundle();
            	Workout w = workouts.get(position);
            	b.putLong(Globals.ID_TAG, w.getId());
            	
            	Intent intent = new Intent(getActivity(), WorkoutEditActivity.class);
            	intent.putExtras(b);
            	
            	getActivity().startActivity(intent);   
            }
        };

        // Get the ListView and wired the listener
        ListView listView = getListView();
        listView.setOnItemClickListener(listener);
        		
        // Listen for updates about the list of workouts
        mMessageIntentFilter = new IntentFilter();
		mMessageIntentFilter.addAction("UPDATE_NOTIFY");
					
	
	}
	
	@Override
	public void onResume() {
		getActivity().registerReceiver(mMessageUpdateReceiver, mMessageIntentFilter);
		super.onResume();
	}
	
	@Override
	public void onPause() {

		getActivity().unregisterReceiver(mMessageUpdateReceiver);
		super.onPause();
	}
    
    // ********** Private Classes **********
    
    private class WorkoutsAdapter extends ArrayAdapter<Workout> {
    	
    	private Context context;
    	private List<Workout> workouts;
    	
    	public WorkoutsAdapter(Context context, int resource, List<Workout> workouts) {
    		super(context, resource, workouts);
    		this.workouts = workouts;
    		this.context = context;	       
    	}
    	
    	
    	@Override 
    	public View getView(int position, View convertView, ViewGroup parent) {
    		
    		// Inflate the layout for a row
    		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	    View rowView = inflater.inflate(R.layout.workouts_list_row, parent, false);
    	    
    	    // Access the textviews to set
    	    TextView workoutView = (TextView) rowView.findViewById(R.id.workout_list_single_row);    		
    	    Workout workout = workouts.get(position);
    	    workoutView.setText(workout.getName());
    	    
    	    return rowView;
    	}
    }
    
}

