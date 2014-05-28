
package cs65s14.dartmouth.get_swole;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarUtility {
	public static final int GREATER_THAN = 1;
	public static final int EQUALS = 0;
	public static final int LESS_THAN = -1;
	
	public static final SimpleDateFormat HOURS_MINUTES_DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
	
	//THIS METHOD IS FROM https://github.com/mukesh4u/Android-Calendar-Sync
	public static String getDate(long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}
	
	//THIS METHOD IS MY CODE
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
