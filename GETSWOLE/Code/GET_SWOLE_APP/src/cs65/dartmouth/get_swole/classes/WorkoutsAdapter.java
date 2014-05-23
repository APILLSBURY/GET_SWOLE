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
	
	public WorkoutsAdapter(Context context, int resource, List<GetSwoleClass> workouts) {
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
	    GetSwoleClass gWorkout = workouts.get(position);
	    workoutView.setText(gWorkout.getName());
	    
	    return rowView;
	}
}