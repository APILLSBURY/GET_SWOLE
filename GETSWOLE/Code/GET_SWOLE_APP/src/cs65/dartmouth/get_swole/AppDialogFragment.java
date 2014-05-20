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
import android.widget.Toast;
import cs65.dartmouth.get_swole.classes.Exercise;
import cs65.dartmouth.get_swole.database.DatabaseWrapper;

public class AppDialogFragment extends DialogFragment {

	// For use by edit exercise dialog
	private static final String ENTRY_ID = "Id";
	
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
		args.putLong(ENTRY_ID, e.getId());
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
					if (nameInput.getText().toString().isEmpty()) {
						Toast.makeText(getActivity(), "Workout has no name. Try again.", Toast.LENGTH_SHORT).show();
					}
	            	
					else {
		            	Bundle bundle = new Bundle();
		            	bundle.putString(Globals.NAME_TAG, nameInput.getText().toString());
		            	
		            	Intent intent = new Intent(getActivity(), WorkoutEditActivity.class);
		            	intent.putExtras(bundle);
		            	
		            	getActivity().startActivity(intent);   
		            	getDialog().cancel(); // is this right?
		            
					}
					
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
		    View v = inflater.inflate(R.layout.dialog_edit_exercise, null);
		    b.setView(v);		

		    // Text views
		    final EditText exerciseName = (EditText) v.findViewById(R.id.exerciseName);
			final EditText exerciseReps = (EditText) v.findViewById(R.id.exerciseReps);
			final EditText exerciseWeight = (EditText) v.findViewById(R.id.exerciseWeight);
			final EditText exerciseRepsGoal = (EditText) v.findViewById(R.id.exerciseRepsGoal);
			final EditText exerciseWeightGoal = (EditText) v.findViewById(R.id.exerciseWeightGoal);
			final EditText exerciseRest = (EditText) v.findViewById(R.id.exerciseRest);
			final EditText exerciseNotes = (EditText) v.findViewById(R.id.exerciseNotes);
			
			// Id of exercise entry that we are loading up, could not be there
		    final long id = getArguments().getLong(ENTRY_ID, -1L);
		    
		    // We fill the fields if there is data
			if (id != -1L) {	
				DatabaseWrapper dbWrapper = new DatabaseWrapper(getActivity());
				dbWrapper.open();
				Exercise e = dbWrapper.getExerciseEntryById(id);
				dbWrapper.close();		
				
				exerciseName.setText(e.getName());
				exerciseReps.setText(e.getReps());
				exerciseWeight.setText(e.getWeight());
				exerciseRepsGoal.setText(e.getRepsGoal());
				exerciseWeightGoal.setText(e.getWeightGoal());
				exerciseRest.setText(e.getRest());
				exerciseNotes.setText(e.getNotes());
			}
			
			b.setPositiveButton(getString(R.string.positive), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// save the exercise into the exercise list of the workout
					DatabaseWrapper dbWrapper = new DatabaseWrapper(getActivity());
					dbWrapper.open();
					dbWrapper.deleteEntry(id, Exercise.class); // might be deleting nothing if id is -1
					Exercise e = new Exercise(exerciseName.getText().toString());
					e.setReps(Integer.parseInt(exerciseReps.getText().toString()));
					e.setRepsGoal(Integer.parseInt(exerciseRepsGoal.getText().toString()));
					e.setWeight(Integer.parseInt(exerciseWeight.getText().toString()));
					e.setWeightGoal(Integer.parseInt(exerciseWeightGoal.getText().toString()));
					e.setRest(Integer.parseInt(exerciseRest.getText().toString()));
					e.setNotes(exerciseNotes.getText().toString());
					dbWrapper.createEntry(e);		       	
					dbWrapper.close();
				}
			});
			
			if (id == -1) { // then this is a new entry, and we might want to just cancel
				b.setNegativeButton(getString(R.string.negative), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// do nothing
					}
				});
			}
			else { // this entry exists, and we might want to delete it
				b.setNegativeButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// delete the entry
						DatabaseWrapper dbWrapper = new DatabaseWrapper(getActivity());
						dbWrapper.open();
						dbWrapper.deleteEntry(id, Exercise.class);
						dbWrapper.close();
					}
				});
			}
			return b.create();
		default:
			return null;		
		
		}
	}
	

	
}
