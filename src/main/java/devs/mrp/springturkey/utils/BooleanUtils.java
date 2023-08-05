package devs.mrp.springturkey.utils;

import org.apache.commons.lang3.StringUtils;

public class BooleanUtils {

	public static boolean isBoolean(String s) {
		return StringUtils.equalsAnyIgnoreCase(s, "true", "false");
	}

}
