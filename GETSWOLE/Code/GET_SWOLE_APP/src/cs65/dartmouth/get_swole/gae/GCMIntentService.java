package cs65.dartmouth.get_swole.gae;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import cs65.dartmouth.get_swole.MainActivity;

public class GCMIntentService extends IntentService {
	
	public GCMIntentService() {
		super(MainActivity.SENDER_ID);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
        
		Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            	
                //handle send error in here
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
            	
            	//handle delete message on server in here
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            	
            	Log.d("GCM", "Delete message");
            	
            	// If it's a regular GCM message, do some work.
            	String message = (String) extras.get("messages");
            	
            	Log.d("GCM", "Got the message!");
            	
            	String[] messageSplit = message.split(":");
            	
            	Log.d("GCM", "Message split: " + messageSplit[0] + " " + messageSplit[1]);
            	
            	// Delete the entry
//            	if (messageSplit[0].equals("delete")) {
//            		Log.d("GCM", "Try to delete the entry");
//            		new ExerciseEntryDbHelper(getApplicationContext()).removeEntry(Long.parseLong(messageSplit[1]));
//            	}
//    			Intent i = new Intent();
//    			i.setAction("GCM_NOTIFY");
//    			i.putExtra("message", message);
//    			sendBroadcast(i);
            }
        }
        
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
		
	}

//	public GCMIntentService () {
//		super(MainActivity.SENDER_ID);
//	}
//	
//	protected String[] getSenderIds(Context context) {
//		TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//		String senderId = telMgr.getDeviceId();
//		String[] senderIds = new String[1];
//		senderIds[1] = senderId;
//		return senderIds;
//	}
//	
//	@Override
//	protected void onError(Context context, String errMsg) {
//		// Called on registration error. This is called in the context of a Service - no dialog or UI.
//		Log.i("Error", errMsg);
//	}
//
//	@Override
//	protected void onMessage(Context context, Intent extraData) {
//		// Called when a cloud message has been received.
//		// Get message from the extras, split the message to extract id and operation. 
//		// If the operation is "delete", delete the requested exercise entry.
//		
//		String[] messages = extraData.getExtras().getString("msg").split(CONNECTIVITY_SERVICE);
//		
//		if (messages[1].equals("delete")) {
//			
//			// delete requested exercise entry
//			ExerciseEntryDbHelper dbHelper = new ExerciseEntryDbHelper(context);
//			
//			// Assume the id is the first string, and operation is the second string!
//			dbHelper.removeEntry(Long.parseLong(messages[0]));
//			
//		}
//	}
//
//	@Override
//	protected void onRegistered(Context context, String regId) {
//		
//		Log.d("GCM", "Registered with Server Utilities");
//		// Called when a registration token has been received.
//		ServerUtilities.register(context, regId);
//		
//	}
//
//	@Override
//	protected void onUnregistered(Context context, String regId) {
//		// If GCM registrar is registered - GCMRegistrar.isRegisteredOnServer(context)
//		if (GCMRegistrar.isRegisteredOnServer(context))
//			ServerUtilities.unregister(context, regId);
//		
//	}
//	
}
