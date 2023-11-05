package devs.mrp.springturkey.utils;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public class BooleanUtils {

	private BooleanUtils() {
		// just hide the constructor from other classes
	}

	public static boolean isBoolean(String s) {
		return StringUtils.equalsAnyIgnoreCase(s, "true", "false");
	}

	public static boolean isNullableBoolean(String s) {
		return Objects.isNull(s) || isBoolean(s);
	}

}
