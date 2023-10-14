package devs.mrp.springturkey.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TimeUtilsTest {

	@Test
	void testCorrectTime() {
		String t = "12:12:12";
		assertTrue(TimeUtils.isParseableTime(t));
	}

	@Test
	void testIncorrectTime() {
		String t = "123:12:12";
		assertFalse(TimeUtils.isParseableTime(t));
	}

	@Test
	void testNullTime() {
		assertFalse(TimeUtils.isParseableTime(null));
	}

}
