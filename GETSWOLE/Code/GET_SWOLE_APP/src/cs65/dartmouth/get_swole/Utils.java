package cs65.dartmouth.get_swole;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Utils {
	
	// Convert pounds to kilograms
	public static double poundsToKilos(double pounds) {
		return (double) pounds / Globals.POUND_PER_KILO;
	}
	
	// Convert kilograms to pounds
	public static double kilosToPound(double kilos) {
		return (double) kilos * Globals.POUND_PER_KILO;
	}
	
	// Read from preference, the unit used for displaying lbs (taken from MyRuns)
	public static boolean getWeightUnits(Context context) {

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

		String[] weight_display_options = context.getResources().getStringArray(R.array.weight_display_name);

		String option = settings.getString(context.getString(R.string.preference_key_unit_display), weight_display_options[0]);

		String option_metric = context.getString(R.string.pounds);

		if (option.equals(option_metric))
			return true;
		else
			return false;
	}
	
	// Get numerical weight in int value
	public static int getWeight(Context context, int weight) {
		if (getWeightUnits(context))
			return weight;
		else
			return (int) poundsToKilos(weight);
	}
	
	// Get the weight in terms of units
	public static String getWeightString(Context context, int weight) {
		if (getWeightUnits(context))
			return weight + " lb";
		else
			return (int) poundsToKilos(weight) + " kg";
	}
	
	// Make a listview the same height as the sum of all of its children
	// http://stackoverflow.com/questions/1661293/why-do-listview-items-not-grow-to-wrap-their-content
	public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter(); 
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }     
	
	// Convert a bitmap photo to a string
	// http://stackoverflow.com/questions/13562429/how-many-ways-to-convert-bitmap-to-string-and-vice-versa
	public static String BitMapToString(Bitmap bitmap){
		ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        
        try {
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Log.d(Globals.TAG, "CONVERTED: \n" + temp);
        
        return temp;
    }
	
	// Convert a string of a photo back to a bitmap
	// http://stackoverflow.com/questions/13562429/how-many-ways-to-convert-bitmap-to-string-and-vice-versa
    public static Bitmap StringToBitMap(String encodedString){
    	try{
    		byte [] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
    		Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
    		
			Log.d(Globals.TAG, "Height: " + bitmap.getHeight() + " Width: "+ bitmap.getWidth());
    		
    		return bitmap;
    	}
    	catch(Exception e){
    		e.getMessage();
    		return null;
    	}
   }

	
}