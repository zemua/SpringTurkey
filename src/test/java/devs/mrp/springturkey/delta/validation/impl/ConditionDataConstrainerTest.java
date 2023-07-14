package devs.mrp.springturkey.delta.validation.impl;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;

class ConditionDataConstrainerTest {

	private static Stream<Arguments> provideCorrectValues() {
		return Stream.of(
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "fieldName", "columnName", "textValue")
				);
	}

	@ParameterizedTest
	@MethodSource("provideStringsForIsBlank")
	void pushesCorrectly(DeltaType deltaType, DeltaTable table, String fieldName, String columnName, String textValue) {
		fail("not yet implemented");
	}

	@Test
	void test() {
		fail("Not yet implemented");
	}

}
