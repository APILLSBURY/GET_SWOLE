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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Utils {
	
	
	
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