package devs.mrp.springturkey.utils;

import java.util.List;
import java.util.Objects;

public class UuidUtils {

	private UuidUtils() {
		// hide constructor
	}

	private static final List<Integer> uuidSlahes = List.of(8,13,18,23);
	private static final int UUID_LENGTH = 36;

	public static boolean isUuid(String s) {
		if (s.length() != UUID_LENGTH) {
			return false;
		}
		for (int i = 0; i < UUID_LENGTH; i++) {
			char c = s.charAt(i);
			if (!isUuidChar(i, c)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNullableUuid(String s) {
		return Objects.isNull(s) || isUuid(s);
	}

	private static boolean isUuidChar(int position, char character) {
		if (position < 0 || position > UUID_LENGTH-1) {
			return false;
		}
		return uuidSlahes.contains(position) ? '-' == character : Character.isLetterOrDigit(character);
	}

}
