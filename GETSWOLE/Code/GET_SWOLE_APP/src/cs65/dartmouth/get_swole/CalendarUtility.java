//THIS CODE IS BASED OFF OF CODE FROM https://github.com/mukesh4u/Android-Calendar-Sync

package cs65.dartmouth.get_swole;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class CalendarUtility {
	public static final int GREATER_THAN = 1;
	public static final int EQUALS = 0;
	public static final int LESS_THAN = -1;
	
	public static String getDate(long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}
	

	public static int testDateEquality(Calendar cal1, Calendar cal2) {
		if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR)) {
			return GREATER_THAN;
		}
		else if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)) {
			return LESS_THAN;
		}
		else { //the year is equal
			if (cal1.get(Calendar.MONTH) > cal2.get(Calendar.MONTH)) {
				return GREATER_THAN;
			}
			else if (cal1.get(Calendar.MONTH) < cal2.get(Calendar.MONTH)) {
				return LESS_THAN;
			}
			else { //the month is equal
				if (cal1.get(Calendar.DATE) > cal2.get(Calendar.DATE)) {
					return GREATER_THAN;
				}
				else if (cal1.get(Calendar.DATE) < cal2.get(Calendar.DATE)) {
					return LESS_THAN;
				}
				else { //the date is equal
					return EQUALS;
				}
			}
		}
	}
}
