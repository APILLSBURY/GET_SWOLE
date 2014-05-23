package cs65.dartmouth.get_swole;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cs65.dartmouth.get_swole.classes.GetSwoleClass;
import cs65.dartmouth.get_swole.classes.Workout;
import cs65.dartmouth.get_swole.classes.WorkoutsAdapter;
import cs65.dartmouth.get_swole.database.DatabaseWrapper;

public class WorkoutFragment extends ListFragment {
	
	DatabaseWrapper dbWrapper;
	WorkoutsAdapter workoutsAdapter;
	List<GetSwoleClass> workouts;
	Context mContext;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mContext = getActivity();
		
		dbWrapper = new DatabaseWrapper(mContext);
		configureListView();
					
	
	}
	
	@Override
	public void onResume() {
		configureListView();
		super.onResume();
	}
	
	private void configureListView() {
		dbWrapper.open();
		workouts = dbWrapper.getAllEntries(Workout.class);
		dbWrapper.close();
				
		workoutsAdapter = new WorkoutsAdapter(mContext, R.layout.workouts_list_row, workouts);
		
		setListAdapter(workoutsAdapter);
				
		// Define the listener interface
        OnItemClickListener listener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	
            	Bundle b = new Bundle();
            	GetSwoleClass w = workouts.get(position);
            	b.putLong(Globals.ID_TAG, w.getId());
            	
            	Intent intent = new Intent(getActivity(), WorkoutEditActivity.class);
            	intent.putExtras(b);
            	
            	getActivity().startActivity(intent);   
            }
        };

        // Get the ListView and wired the listener
        ListView listView = getListView();
        listView.setOnItemClickListener(listener);
	}
}

