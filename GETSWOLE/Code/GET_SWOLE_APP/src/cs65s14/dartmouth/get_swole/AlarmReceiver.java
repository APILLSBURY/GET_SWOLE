package cs65s14.dartmouth.get_swole;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cs65s14.dartmouth.get_swole.classes.GetSwoleClass;
import cs65s14.dartmouth.get_swole.classes.Workout;
import cs65s14.dartmouth.get_swole.database.DatabaseWrapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
	  @Override
	  public void onReceive(Context context, Intent intent)  {
		  DatabaseWrapper dbWrapper = new DatabaseWrapper(context);
		  Log.e(Globals.TAG, "received message");
		  // Let's check which exercises are happening today
		  dbWrapper.open();
		  List<Workout> workouts = dbWrapper.getAllEntries(Workout.class);
		  dbWrapper.close();
		  ArrayList<GetSwoleClass> workoutsToday = ScheduleFragment.getDailyWorkouts(Calendar.getInstance(), workouts, null);
				  
		  // Start the notification service
		  for (GetSwoleClass workout : workoutsToday) {
			  Intent i = new Intent(context, WorkoutNotificationService.class);
			  Bundle b = new Bundle();
			  b.putLong(Globals.ID_TAG, workout.getId());
			  i.putExtras(b);
			  context.startService(i);
		  }
	  }
}
