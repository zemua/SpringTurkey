package devs.mrp.springturkey.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

class EnumUtilsTest {

	@Test
	void test() {
		Predicate<String> predicate = EnumUtils.getEnumPredicate(TestEnum.class);
		assertTrue(predicate.test("ONE"));
		assertTrue(predicate.test("TWO"));
		assertFalse(predicate.test("THREE"));
		assertFalse(predicate.test("FOUR"));
		assertFalse(predicate.test("one"));
		assertFalse(predicate.test("two"));
		assertTrue(predicate.test("three"));
		assertFalse(predicate.test("four"));
	}

	enum TestEnum {
		ONE, TWO, three;
	}

}
