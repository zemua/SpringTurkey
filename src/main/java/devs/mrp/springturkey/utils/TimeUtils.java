package devs.mrp.springturkey.utils;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class TimeUtils {

	public static boolean isParseableTime(String time) {
		try {
			LocalTime.parse(time);
		} catch (DateTimeParseException | NullPointerException e) {
			return false;
		}
		return true;
	}

}