package cs65.dartmouth.get_swole;

import java.util.Calendar;

import cs65.dartmouth.get_swole.classes.Workout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
// DONE
public class AppDialogFragment extends DialogFragment {

	public static final int DIALOG_ID_PHOTO_PICKER = 1;
	public static final int ID_PHOTO_FROM_CAMERA = 0;
	public static final int ID_PHOTO_FROM_GALLERY = 1;
	
	public static final int DIALOG_ID_NEW_WORKOUT = 2;
	public static final int DIALOG_ID_DATE = 3;
	public static final int DIALOG_ID_TIME = 4;
	
	private static final String DIALOG_ID_KEY = "dialog_id";
	
	public static AppDialogFragment newInstance(int id) {
		
		AppDialogFragment frag = new AppDialogFragment();
		Bundle args = new Bundle();
		args.putInt(DIALOG_ID_KEY, id);
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
			// On save, get data from view and pass to manual input activity
			b.setPositiveButton(getString(R.string.positive), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// we want to launch new activity 
	            	
	            	Bundle b = new Bundle();
	            	b.putString(Globals.NAME_TAG, nameInput.getText().toString());
	            	
	            	Intent intent = new Intent(getActivity(), WorkoutEditActivity.class);
	            	intent.putExtras(b);
	            	
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
		default:
			return null;		
		
		}
	}
	

	
}
