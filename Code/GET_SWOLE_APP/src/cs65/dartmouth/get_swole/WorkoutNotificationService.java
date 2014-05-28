package cs65.dartmouth.get_swole;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import cs65.dartmouth.get_swole.classes.Workout;
import cs65.dartmouth.get_swole.database.DatabaseWrapper;

public class WorkoutNotificationService extends Service {

	private NotificationManager mNotificationManager;
    private DatabaseWrapper dbWrapper;

    @Override
    public void onCreate() {
    	super.onCreate();
    	dbWrapper = new DatabaseWrapper(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	super.onStartCommand(intent, flags, startId);
    	if (intent != null) {
	    	Bundle b = intent.getExtras();
	    	dbWrapper.open();
	    	Workout w = (Workout) dbWrapper.getEntryById(b.getLong(Globals.ID_TAG), Workout.class);
	    	dbWrapper.close();
	    	showNotification(w);
	    	Log.d(Globals.TAG, "service started");
    	}
	    return START_STICKY;
    }
    
    @Override
	public IBinder onBind(Intent intent) {

		return null;
		
	}
    
    private void showNotification(Workout w) {
    	Log.d(Globals.TAG, "showing notification");
		
    	Intent workoutDo = new Intent(this, WorkoutDoActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	Bundle b = new Bundle();
    	b.putLong(Globals.ID_TAG, w.getId());
    	workoutDo.putExtras(b);
    	
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, workoutDo, 0);
        Notification notification = new Notification.Builder(this)
        .setContentTitle(w.getName() + " scheduled for today.")
        .setContentText(getResources().getString(R.string.notification_text)).setSmallIcon(R.drawable.ic_launcher)
        .setContentIntent(contentIntent).build();
        mNotificationManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        
        mNotificationManager.notify(0, notification); 

	}
}

