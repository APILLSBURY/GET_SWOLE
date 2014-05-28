package cs65.dartmouth.get_swole.classes;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cs65.dartmouth.get_swole.R;
import cs65.dartmouth.get_swole.R.id;
import cs65.dartmouth.get_swole.R.layout;

/**
 * 
 * PRIVATE CLASSES
 *
 */
public class ExerciseArrayAdapter extends ArrayAdapter<Exercise> {
	
	private Context context;
	private List<Exercise> exercises;
	private int resource;
	
	public ExerciseArrayAdapter(Context context, int resource, List<Exercise> exercises) {
		super(context, resource, exercises);
		this.exercises = exercises;
		this.context = context;	
		this.resource = resource;
	}
	
	
	@Override 
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// Inflate the layout for a row
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(resource, parent, false);
	    
	    TextView exerciseTitleView = (TextView) rowView.findViewById(R.id.exercise_list_single_row_title);    		
	    Exercise e = exercises.get(position);
	    exerciseTitleView.setText(e.getName());
	    
	    // Access the textviews to set
	    if (resource == R.layout.exercises_list_row) {
	    	TextView exerciseDetailsView = (TextView) rowView.findViewById(R.id.exercise_list_single_row_details);
	    	exerciseDetailsView.setText(e.toString(context));
	    }
	   
	    
	    return rowView;
	}
}

