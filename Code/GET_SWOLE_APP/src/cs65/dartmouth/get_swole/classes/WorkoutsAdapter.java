package cs65.dartmouth.get_swole.classes;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cs65.dartmouth.get_swole.R;

public class WorkoutsAdapter extends ArrayAdapter<GetSwoleClass> {
	
	private Context context;
	private List<GetSwoleClass> workouts;
	private int resource;
	
	public WorkoutsAdapter(Context context, int resource, List<GetSwoleClass> workouts) {
		super(context, resource, workouts);
		this.workouts = workouts;
		this.context = context;	   
		this.resource = resource;
	}
	
	@Override 
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// Inflate the layout for a row
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(resource, parent, false);
	    
	    // Access the textviews to set
	    TextView workoutView = (TextView) rowView.findViewById(R.id.workout_list_single_row_title);    	
	    GetSwoleClass gWorkout = workouts.get(position);
	    workoutView.setText(gWorkout.getName());
	    
	    if (resource == R.layout.workouts_list_row) {
	    	// then we want to display the comments
		    TextView workoutDetails = (TextView) rowView.findViewById(R.id.workout_list_single_row_details);
		    workoutDetails.setText(((Workout) gWorkout).getNotes()) ;  	
	    }
	    
	    return rowView;
	}
}