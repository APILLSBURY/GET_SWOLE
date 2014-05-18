package cs65.dartmouth.get_swole;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import cs65.dartmouth.get_swole.classes.Exercise;

public class AppDialogFragment extends DialogFragment {

	// For use by edit exercise dialog
	private static final String E_NAME = "Name";
	private static final String E_REPS = "Reps";
	private static final String E_WEIGHT = "Weight";
	private static final String E_RGOAL = "RepsGoal";
	private static final String E_WGOAL = "WeightGoal";
	private static final String E_REST = "Rest";
	private static final String E_NOTES = "Notes";
	
	public static final int DIALOG_ID_PHOTO_PICKER = 1;
	public static final int ID_PHOTO_FROM_CAMERA = 0;
	public static final int ID_PHOTO_FROM_GALLERY = 1;
	
	public static final int DIALOG_ID_NEW_WORKOUT = 2;
	public static final int DIALOG_ID_DATE = 3;
	public static final int DIALOG_ID_TIME = 4;
	public static final int DIALOG_ID_EDIT_EXERCISE = 5;
	
	private static final String DIALOG_ID_KEY = "dialog_id";
	
	public static AppDialogFragment newInstance(int id) {
		
		AppDialogFragment frag = new AppDialogFragment();
		Bundle args = new Bundle();
		args.putInt(DIALOG_ID_KEY, id);
		frag.setArguments(args);
		return frag;
	}
	
	public static AppDialogFragment newInstance(Exercise e) {
		AppDialogFragment frag = new AppDialogFragment();
		Bundle args = new Bundle();
		args.putInt(DIALOG_ID_KEY, DIALOG_ID_EDIT_EXERCISE);
		args.putString(E_NAME, e.getName());
		args.putInt(E_REPS, e.getReps());
		args.putInt(E_WEIGHT, e.getWeight());
		args.putInt(E_RGOAL, e.getRepsGoal());
		args.putInt(E_WGOAL, e.getWeightGoal());
		args.putInt(E_REST, e.getRest());
		args.putString(E_NOTES, e.getNotes());
		frag.setArguments(args);
		return frag;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		int dialogId = getArguments().getInt(DIALOG_ID_KEY); // holds the id of the type of dialog to be used
		final Activity parent = getActivity();
		
		// For use by time/date setting dialogs
		Calendar c = Calendar.getInstance();

		AlertDialog.Builder b;
		
		switch (dialogId) {	
		/*
		case DIALOG_ID_PHOTO_PICKER: // we are choosing a picture for the profile picture 
			b = new AlertDialog.Builder(parent);
			b.setTitle();
			// The click listener will use intents upon selection
			DialogInterface.OnClickListener dListener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					// Add callback
				}
			};
			b.setItems(R.array.profile_photo_picker_items, dListener);
			return b.create();*/
			
		case DIALOG_ID_DATE:
			// Create date picker dialog
			Dialog dpDialog = new DatePickerDialog(parent, 
					new DatePickerDialog.OnDateSetListener() {	 
			            @Override
			            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			            	// Add callback
			            }
	 
	        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)); 
			return dpDialog;
			
		case DIALOG_ID_TIME:
			// Create time picker dialog
			Dialog tpDialog = new TimePickerDialog(parent, 
					new TimePickerDialog.OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
							// Add callback
						}
			}, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false);
			return tpDialog;
		
		case DIALOG_ID_NEW_WORKOUT:
			// Create custom dialog
			b = new AlertDialog.Builder(parent);
			b.setTitle(getString(R.string.dialog_title_new_workout));		
			final EditText nameInput = new EditText(parent);
			b.setView(nameInput);
			nameInput.setHint(getString(R.string.dialog_hint_new_workout));
			b.setPositiveButton(getString(R.string.positive), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// we want to launch new activity 
	            	
	            	Bundle bundle = new Bundle();
	            	bundle.putString(Globals.NAME_TAG, nameInput.getText().toString());
	            	
	            	Intent intent = new Intent(getActivity(), WorkoutEditActivity.class);
	            	intent.putExtras(bundle);
	            	
	            	getActivity().startActivity(intent);   
					
				}
			});
			b.setNegativeButton(getString(R.string.negative), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// do nothing
				}
			});
			return b.create();
		case DIALOG_ID_EDIT_EXERCISE:
				
			// Create custom dialog
			b = new AlertDialog.Builder(parent);

			 // Get the layout inflater
		    LayoutInflater inflater = getActivity().getLayoutInflater();

		    // Inflate and set the layout for the dialog
		    // Pass null as the parent view because its going in the dialog layout
		    View v = inflater.inflate(R.layout.dialog_edit_exercise, null);
		    b.setView(v);		

		    // We fill the fields if there is data
			String name = savedInstanceState.getString(E_NAME, "");
			if (!name.isEmpty()) {		
				((EditText) v.findViewById(R.id.exerciseName)).setText(name);
				((EditText) v.findViewById(R.id.exerciseReps)).setText(savedInstanceState.getInt(E_REPS));
				((EditText) v.findViewById(R.id.exerciseWeight)).setText(savedInstanceState.getInt(E_WEIGHT));
				((EditText) v.findViewById(R.id.exerciseRepsGoal)).setText(savedInstanceState.getInt(E_RGOAL));
				((EditText) v.findViewById(R.id.exerciseWeightGoal)).setText(savedInstanceState.getInt(E_WGOAL));
				((EditText) v.findViewById(R.id.exerciseRest)).setText(savedInstanceState.getInt(E_REST));
				((EditText) v.findViewById(R.id.exerciseNotes)).setText(savedInstanceState.getInt(E_NOTES));

			}

			b.setPositiveButton(getString(R.string.positive), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// save the exercise into the exercise list of the workout
					       	
				}
			});
			b.setNegativeButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// delete the entry
				}
			});
			return b.create();
		default:
			return null;		
		
		}
	}
	

	
}
