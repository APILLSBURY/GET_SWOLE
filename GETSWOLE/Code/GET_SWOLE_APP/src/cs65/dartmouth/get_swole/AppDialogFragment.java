package cs65.dartmouth.get_swole;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import cs65.dartmouth.get_swole.classes.Exercise;
import cs65.dartmouth.get_swole.classes.ExerciseArrayAdapter;
import cs65.dartmouth.get_swole.classes.Set;
import cs65.dartmouth.get_swole.database.DatabaseWrapper;

public class AppDialogFragment extends DialogFragment {

	// For use by edit exercise dialog
	private static final String EXERCISE_ID = "Exercise_Id";
	private static final String EXERCISE_POSITION = "Exercise_Position";
	
	public static final int DIALOG_ID_NEW_WORKOUT = 2;
	public static final int DIALOG_ID_DATE = 3;
	public static final int DIALOG_ID_TIME = 4;
	public static final int DIALOG_ID_EDIT_EXERCISE = 5; // for adding new exercises, or editing already existing
	public static final int DIALOG_ID_ADD_EXISTING_EXERCISE = 6;
	public static final int DIALOG_ID_DO_EXERCISE = 7;
	public static final int DIALOG_ID_TIMER = 8;
	public static final int DIALOG_ID_EDIT_SETS = 9;
	public static final int DIALOG_ID_DO_SETS = 10;
	public static final int DIALOG_ID_VIEW_EXERCISE = 11;
	public static final int DIALOG_ID_VIEW_SETS = 12;
	
	private static final String DIALOG_ID_KEY = "dialog_id";
	
	private static final int [] repIds = {R.id.reps1, R.id.reps2, R.id.reps3, R.id.reps4, R.id.reps5, R.id.reps6, R.id.reps7, R.id.reps8};
	private static final int [] weightIds = {R.id.weight1, R.id.weight2, R.id.weight3, R.id.weight4, R.id.weight5, R.id.weight6, R.id.weight7, R.id.weight8};
	
	public static AppDialogFragment newInstance(int id) {
		
		AppDialogFragment frag = new AppDialogFragment();
		Bundle args = new Bundle();
		args.putInt(DIALOG_ID_KEY, id);
		frag.setArguments(args);
		return frag;
	}
	
	// For editing exercises
	public static AppDialogFragment newInstance(Exercise e, boolean edit, int position) {
		AppDialogFragment frag = new AppDialogFragment();
		Bundle args = new Bundle();
		if (edit) args.putInt(DIALOG_ID_KEY, DIALOG_ID_EDIT_EXERCISE);
		else {
			args.putInt(DIALOG_ID_KEY,  DIALOG_ID_DO_EXERCISE);
			args.putInt(EXERCISE_POSITION, position);
		}
		if (e != null) args.putLong(EXERCISE_ID, e.getId());
		frag.setArguments(args);
		return frag;
	}
	
	public static AppDialogFragment newInstanceSets(long id, boolean edit, int position) {
		
		AppDialogFragment frag = new AppDialogFragment();
		Bundle args = new Bundle();
		if (edit) args.putInt(DIALOG_ID_KEY, DIALOG_ID_EDIT_SETS);
		else {
			args.putInt(DIALOG_ID_KEY,  DIALOG_ID_DO_SETS);
			args.putInt(EXERCISE_POSITION, position);
		}
		args.putLong(EXERCISE_ID, id);
		frag.setArguments(args);
		return frag;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		int dialogId = getArguments().getInt(DIALOG_ID_KEY); // holds the id of the type of dialog to be used
		final Activity parent = getActivity();
		
		// For use by time/date setting dialogs
		Calendar c = Calendar.getInstance();
		LayoutInflater inflater;
		View v;
		
		AlertDialog.Builder b;
		DatabaseWrapper dbWrapper = new DatabaseWrapper(parent);
		
		switch (dialogId) {	
			
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
			nameInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
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
		    inflater = getActivity().getLayoutInflater();
		    v = inflater.inflate(R.layout.dialog_edit_exercise, null);
		    b.setView(v);		

		    // Text views
		    final EditText exerciseName = (EditText) v.findViewById(R.id.exerciseName);
			final EditText exerciseRepsGoal = (EditText) v.findViewById(R.id.exerciseRepsGoal);
			final EditText exerciseWeightGoal = (EditText) v.findViewById(R.id.exerciseWeightGoal);
			final EditText exerciseRest = (EditText) v.findViewById(R.id.exerciseRest);
			final EditText exerciseNotes = (EditText) v.findViewById(R.id.exerciseNotes);
				
			// Id of exercise entry that we are loading up, could not be there
		    final long id = getArguments().getLong(EXERCISE_ID, -1L);
		    
			Button setsButton = (Button) v.findViewById(R.id.editSets);
		    setsButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					// start the dialog fragment
					// Display dialog with nothing in it
    	 			DialogFragment fragment = AppDialogFragment.newInstanceSets(id, true, 0);
    	 			fragment.show(getFragmentManager(), getString(R.string.dialog_fragment_tag_edit_sets));
					
				}
			});
		    
		    // We fill the fields if there is data
			if (id != -1L) {	
				dbWrapper.open();
				Exercise e = (Exercise) dbWrapper.getEntryById(id, Exercise.class);
				dbWrapper.close();
				
				exerciseName.setText(e.getName());
				// Need to check if they are empty
				if (e.getRepsGoal() != -1) exerciseRepsGoal.setText(e.getRepsGoal() + "");
				if (e.getWeightGoal() != -1) exerciseWeightGoal.setText(e.getWeightGoal() + "");
				if (e.getRest() != -1) exerciseRest.setText(e.getRest() + "");
				exerciseNotes.setText(e.getNotes());
			}
			
			b.setPositiveButton(getString(R.string.positive), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					Exercise e = new Exercise(exerciseName.getText().toString());
					// the information about the sets has already been sent back
					if (!exerciseRepsGoal.getText().toString().isEmpty()) e.setRepsGoal(Integer.parseInt(exerciseRepsGoal.getText().toString()));
					if (!exerciseWeightGoal.getText().toString().isEmpty()) e.setWeightGoal(Integer.parseInt(exerciseWeightGoal.getText().toString()));
					if (!exerciseRest.getText().toString().isEmpty()) e.setRest(Integer.parseInt(exerciseRest.getText().toString()));
					e.setNotes(exerciseNotes.getText().toString());
										
					if (id == -1L) // if we are creating a new exercise						
						((WorkoutEditActivity) parent).onAddNewExercise(e); // we want to add this exercise to the workout's list
					else { // we are editing an exercise, meaning we need to delete the old one from this activities list
						((WorkoutEditActivity) parent).onEditExercise(id, e);
					}
				}
			});
			
			if (id == -1L) { // then this is a new entry, and we might want to just cancel
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
						((WorkoutEditActivity) parent).onDeleteExercise(id);
						
					}
				});
			}
			return b.create();
		case DIALOG_ID_ADD_EXISTING_EXERCISE:
			// Create custom dialog
			b = new AlertDialog.Builder(parent);

		    inflater = getActivity().getLayoutInflater();
		    v = inflater .inflate(R.layout.dialog_existing_exercise, null);    
		    final Spinner exerciseSpinner = (Spinner) v.findViewById(R.id.exercise_spinner);
		    
		    dbWrapper.open();
		    List<Exercise> dbExercises = dbWrapper.getAllEntries(Exercise.class);
		    final ArrayList<Exercise> exercises = new ArrayList<Exercise>();
		    exercises.addAll(dbExercises);
		    dbWrapper.close();

		    ExerciseArrayAdapter exerciseArrayAdapter = new ExerciseArrayAdapter(parent, R.layout.exercises_list_row, exercises);

		    exerciseSpinner.setAdapter(exerciseArrayAdapter);
		    
		    b.setView(v);	
		    
		    b.setPositiveButton(getString(R.string.positive), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// We want to add this exercise to the workout's list of exercises
				    int position = exerciseSpinner.getSelectedItemPosition();
		    		((WorkoutEditActivity) parent).onUseExistingExercise(exercises.get(position));
				}
		    });
		    
		    b.setNegativeButton(getString(R.string.negative), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// do nothing
				}
		    });
		    
		    return b.create();
		case DIALOG_ID_DO_EXERCISE:
			// Create custom dialog
			b = new AlertDialog.Builder(parent);

			 // Get the layout inflater
		    inflater = getActivity().getLayoutInflater();
		  	v = inflater.inflate(R.layout.dialog_edit_exercise, null);
		    b.setView(v);		

		    // Text views 
		    final EditText exerciseName2 = (EditText) v.findViewById(R.id.exerciseName);
			final EditText exerciseRepsGoal2 = (EditText) v.findViewById(R.id.exerciseRepsGoal);
			final EditText exerciseWeightGoal2 = (EditText) v.findViewById(R.id.exerciseWeightGoal);
			final EditText exerciseRest2 = (EditText) v.findViewById(R.id.exerciseRest);
			final EditText exerciseNotes2 = (EditText) v.findViewById(R.id.exerciseNotes);
			
			// Id of exercise entry that we are loading up, could not be there
		    final long id2 = getArguments().getLong(EXERCISE_ID, -1L); // cannot be -1 if doing an exercise
		    final int position = getArguments().getInt(EXERCISE_POSITION, -1); // can't be -1
			
		    Button setsButton2 = (Button) v.findViewById(R.id.editSets);
		    setsButton2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					// start the dialog fragment
					// Display dialog with nothing in it
    	 			DialogFragment fragment = AppDialogFragment.newInstanceSets(id2, false, position);
    	 			fragment.show(getFragmentManager(), getString(R.string.dialog_fragment_tag_edit_sets));
					
				}
			});
		    
			dbWrapper.open();
			Exercise e = (Exercise) dbWrapper.getEntryById(id2, Exercise.class);
			dbWrapper.close();
			
			exerciseName2.setText(e.getName());
			if (e.getRepsGoal() != -1) exerciseRepsGoal2.setText(e.getRepsGoal() + "");
			if (e.getWeightGoal() != -1) exerciseWeightGoal2.setText(e.getWeightGoal() + "");
			if (e.getRest() != -1) exerciseRest2.setText(e.getRest() + "");
			exerciseNotes2.setText(e.getNotes());
			
		    
			b.setPositiveButton(getString(R.string.positive), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					Exercise e = new Exercise(exerciseName2.getText().toString());
					// the information from the sets dialog has already been sent back
					if (!exerciseRepsGoal2.getText().toString().isEmpty()) e.setRepsGoal(Integer.parseInt(exerciseRepsGoal2.getText().toString()));
					if (!exerciseWeightGoal2.getText().toString().isEmpty()) e.setWeightGoal(Integer.parseInt(exerciseWeightGoal2.getText().toString()));
					if (!exerciseRest2.getText().toString().isEmpty()) e.setRest(Integer.parseInt(exerciseRest2.getText().toString()));
					e.setNotes(exerciseNotes2.getText().toString());
										
					((WorkoutDoActivity) parent).onDoExercise(e, position);
					
				}
			});
			
			b.setNegativeButton(getString(R.string.negative), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// do nothing
				}
			});
			
			return b.create();
			
		case DIALOG_ID_TIMER:
			
			// Create custom dialog
			b = new AlertDialog.Builder(parent);

			 // Get the layout inflater
		    inflater = getActivity().getLayoutInflater();
		    v = inflater.inflate(R.layout.dialog_timer, null);
		    b.setView(v);	
					    
		    final EditText entered = (EditText) v.findViewById(R.id.enteredTime);
		    final TextView time = (TextView) v.findViewById(R.id.time);
		    final Button start = (Button) v.findViewById(R.id.beginTimer);    
		    start.setOnClickListener(new View.OnClickListener() {
		    	
		    	@Override
		    	public void onClick(View v) {
		    		
		    		int seconds = 30;
		    		if (!entered.getText().toString().isEmpty())
		    			seconds = Integer.parseInt(entered.getText().toString());
		    		
		    		new CountDownTimer(seconds*1000, 1000) {
					    @Override
					    public void onTick(long millisUntilFinished) {
					       time.setText(""+(millisUntilFinished/1000));
					    }
					    @Override
					    public void onFinish() { }
					}.start();
					
		    	}
		    });
		    return b.create();
		case DIALOG_ID_EDIT_SETS:
			// Create custom dialog
			b = new AlertDialog.Builder(parent);
	
			 // Get the layout inflater
		    inflater = getActivity().getLayoutInflater();
		  	final View setsView = inflater.inflate(R.layout.dialog_edit_sets, null);
		    b.setView(setsView);		
		    
		    // get id 
		    // id might be -1
		    // Id of exercise entry that we are loading up, could not be there
		    long id3 = getArguments().getLong(EXERCISE_ID, -1L); // cannot be -1 if doing an exercise
			
	    	ArrayList<Set> sets = null;

		    if (((WorkoutEditActivity) parent).getSetsToSave() != null) {
	    		// fill slots with info from sets to save
	    		sets = ((WorkoutEditActivity) parent).getSetsToSave();
	  	
	    	}	    
		    else if (id3 != -1L) { // then we are editing
		    	
	    		// fill with info from this exercise, last saved
				dbWrapper.open();
				Exercise exercise = (Exercise) dbWrapper.getEntryById(id3, Exercise.class);
				dbWrapper.close();
				
				sets = exercise.getSetList();
					
		    }
		    	
		    if (sets != null) {
		    	for (int i = 0; i < sets.size(); i++) { // assume set size is at most 8
					if (sets.get(i).getReps() != 0)
					((EditText) setsView.findViewById(repIds[i])).setText(sets.get(i).getReps() + ""); 
					if (sets.get(i).getWeight() != 0)
					((EditText) setsView.findViewById(weightIds[i])).setText(sets.get(i).getWeight() + ""); 
				}
		    }
				  
		    
		    // send things back to activity the exercise
		    b.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					ArrayList<Set> setsToSave = new ArrayList<Set>();
					// save the information from the text views to edit sets
					for (int i = 0; i < 7; i++) { // assume entered data is at most 8
						Set s = null;
						if (!((EditText) setsView.findViewById(repIds[i])).getText().toString().isEmpty()) {
							s = new Set();
							s.setReps(Integer.parseInt(((EditText) setsView.findViewById(repIds[i])).getText().toString()));
						}

						if (!((EditText) setsView.findViewById(weightIds[i])).getText().toString().isEmpty()){
							if (s == null) s = new Set();
							s.setWeight(Integer.parseInt(((EditText) setsView.findViewById(weightIds[i])).getText().toString()));
						}
						
						if (s != null) setsToSave.add(s);
					}
					
					((WorkoutEditActivity) parent).onEditSets(setsToSave);
					
				}
			});
		    
		    b.setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					((WorkoutEditActivity) parent).resetSetsToSave();
				}
			}); 
					    
			return b.create();
		case DIALOG_ID_DO_SETS:
			// Create custom dialog
			b = new AlertDialog.Builder(parent);
	
			 // Get the layout inflater
		    inflater = getActivity().getLayoutInflater();
		  	final View setsView2 = inflater.inflate(R.layout.dialog_edit_sets, null);
		    b.setView(setsView2);		
		    
		    // get id 
		    // id might be -1
		    // Id of exercise entry that we are loading up, could not be there
		    long id4 = getArguments().getLong(EXERCISE_ID, -1L); // cannot be -1 if doing an exercise			    
		    final int pos = getArguments().getInt(EXERCISE_POSITION, -1);
		    
		    // if we have already done sets of this exercise, bring up those...
		    sets = ((WorkoutDoActivity) parent).getDoneSets(pos);
		    if (sets.isEmpty()) { // bring up from database
		    
		    	// fill with info from this exercise, last saved
				dbWrapper.open();
				Exercise exercise = (Exercise) dbWrapper.getEntryById(id4, Exercise.class);
				dbWrapper.close();
				
				sets = exercise.getSetList();	    
		    
		    }
		    
    		
								    	
	    	for (int i = 0; i < sets.size(); i++) { // assume set size is at most 8
				if (sets.get(i).getReps() != 0)
				((EditText) setsView2.findViewById(repIds[i])).setText(sets.get(i).getReps() + ""); 
				if (sets.get(i).getWeight() != 0)
				((EditText) setsView2.findViewById(weightIds[i])).setText(sets.get(i).getWeight() + ""); 
			}    
				  
		    
		    // send things back to activity the exercise
		    b.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					ArrayList<Set> setsToSave = new ArrayList<Set>();
					// save the information from the text views to edit sets
					for (int i = 0; i < 7; i++) { // assume entered data is at most 8
						Set s = null;
						if (!((EditText) setsView2.findViewById(repIds[i])).getText().toString().isEmpty()) {
							s = new Set();
							s.setReps(Integer.parseInt(((EditText) setsView2.findViewById(repIds[i])).getText().toString()));
						}

						if (!((EditText) setsView2.findViewById(weightIds[i])).getText().toString().isEmpty()){
							if (s == null) s = new Set();
							s.setWeight(Integer.parseInt(((EditText) setsView2.findViewById(weightIds[i])).getText().toString()));
						}
						
						if (s != null) setsToSave.add(s);
					}
					
					((WorkoutDoActivity) parent).setDoneSets(setsToSave, pos);
					
				}
			});
		    
		    b.setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// do nothing	
				}
			}); 
		    
			return b.create();
		case DIALOG_ID_VIEW_EXERCISE:
		case DIALOG_ID_VIEW_SETS:
		default:
			return null;		
		
		}
	}
	

	
}
