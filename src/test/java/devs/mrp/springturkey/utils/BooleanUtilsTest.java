package devs.mrp.springturkey.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BooleanUtilsTest {

	@Test
	void test() {
		assertTrue(BooleanUtils.isBoolean("true"));
		assertTrue(BooleanUtils.isBoolean("false"));
		assertFalse(BooleanUtils.isBoolean("other"));
	}

}
