package devs.mrp.springturkey.utils;

import java.util.List;

public class UuidUtils {

	private static final List<Integer> uuidSlahes = List.of(8,13,18,23);
	private static final int uuidLength = 36;

	public static boolean isUuid(String s) {
		if (s.length() != uuidLength) {
			return false;
		}
		for (int i = 0; i < uuidLength; i++) {
			char c = s.charAt(i);
			if (!isUuidChar(i, c)) {
				return false;
			}
		}
		return true;
	}

	private static boolean isUuidChar(int position, char character) {
		if (position < 0 || position > uuidLength-1) {
			return false;
		}
		return uuidSlahes.contains(position) ? '-' == character : Character.isLetterOrDigit(character);
	}

}
