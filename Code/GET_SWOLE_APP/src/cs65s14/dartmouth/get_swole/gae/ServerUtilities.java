package cs65s14.dartmouth.get_swole.gae;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import cs65s14.dartmouth.get_swole.R;
import cs65s14.dartmouth.get_swole.Globals;
import android.content.Context;
import android.util.Log;

/**
 * Helper class used to communicate with AppEngine server
 * @author Cameron Price
 *
 */
public class ServerUtilities {

	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();
	
	/**
	 * Sends the registration ID to your server over HTTP, so it can use
	 * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
	 * since the device sends upstream messages to a server that echoes back the
	 * message using the 'from' address in the message.
	 */
	public static void sendRegistrationIdToBackend(Context context, String regId) {
		String serverUrl = context.getString(R.string.server_url) + "/register";
		Map<String, String> params = new HashMap<String, String>();
		params.put("regId", regId);

		// Once GCM returns a registration id, we need to register it in the
		// server. As the server might be down, we will retry it a couple
		// times.
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			
			Log.d(Globals.TAG, "Trying to send registration id to backend");
			
			try {
				post(serverUrl, params);
				
				Log.d(Globals.TAG, "Should have called serverutilities post");
				
			} catch (IOException e) {
				// Here we are simplifying and retrying on any error; in a real
				// application, it should retry only on unrecoverable errors
				// (like HTTP error code 503).
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					// Activity finished before we complete - exit.
					Thread.currentThread().interrupt();
				}
				// increase backoff exponentially
				backoff *= 2;
			}
		}

	}
	
	public static String post(String endpoint, Map<String, String> params) throws IOException {
		
		Log.d(Globals.TAG, "ServerUtility Posting");
		
		URL url;
		try {
			url = new URL(endpoint);
		} 
		catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}
		
		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
		
		// constructs the POST body using the parameters
		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
			if (iterator.hasNext()) {
				bodyBuilder.append('&');
			}
		}
		
		String body = bodyBuilder.toString();
		byte[] bytes = body.getBytes();
		HttpURLConnection conn = null;
		
		try {
			
			Log.d(Globals.TAG, body);
			Log.d(Globals.TAG, "URL: " + url.toString());
			
			conn = (HttpURLConnection) url.openConnection();
			
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setFixedLengthStreamingMode(bytes.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			
			// post the request
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			out.close();
			
			// handle the response
			int status = conn.getResponseCode();
			if (status != 200) {
				throw new IOException("Post failed with error code " + status);
			}

			// Get Response
			InputStream is = conn.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\n');
			}
			rd.close();
			return response.toString();

		} 
		finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	
	}

}