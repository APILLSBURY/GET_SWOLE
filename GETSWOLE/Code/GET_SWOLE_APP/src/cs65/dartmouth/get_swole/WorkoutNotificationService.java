package cs65.dartmouth.get_swole;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
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

    	Bundle b = intent.getExtras();
    	Workout w = (Workout) dbWrapper.getEntryById(b.getLong(Globals.ID_TAG), Workout.class);
    	showNotification(w);
    	
    	return START_STICKY;
    }
    
    @Override
	public IBinder onBind(Intent intent) {

		return null;
		
	}
    
    private void showNotification(Workout w) {
		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, WorkoutDoActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        Notification notification = new Notification.Builder(this)
        .setContentTitle(w.getName() + " scheduled for today.")
        .setContentText(getResources().getString(R.string.notification_text)).setSmallIcon(R.drawable.ic_launcher)
        .setContentIntent(contentIntent).build();
        mNotificationManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags = notification.flags  | Notification.FLAG_ONGOING_EVENT;
        
        mNotificationManager.notify(0, notification); 

	}
}

