package devs.mrp.springturkey.delta.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import devs.mrp.springturkey.Exceptions.TurkeySurpriseException;
import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.delta.validation.constraints.ActivityModificationConstraints;
import devs.mrp.springturkey.delta.validation.constraints.ConditionModificationConstraints;
import devs.mrp.springturkey.delta.validation.constraints.RandomCheckModificationConstraints;

class FieldDataTest {

	// TODO validate creation using object

	@Test
	void testModification() throws WrongDataException {
		final FieldData validator = FieldData.builder()
				.modifiable(true)
				.mapeable(ActivityModificationConstraints.class)
				.build();
		assertTrue(validator.isValidModification(mapOf("activityName", "hello world")));
		assertThrows(WrongDataException.class, () -> validator.isValidModification(mapOf("activityName", "invalid character!")));

		final FieldData validator2 = FieldData.builder()
				.modifiable(true)
				.mapeable(ConditionModificationConstraints.class)
				.build();
		assertTrue(validator2.isValidModification(mapOf("lastDaysToConsider", 0)));
		assertThrows(WrongDataException.class, () -> validator2.isValidModification(mapOf("lastDaysToConsider", -1)));

		final FieldData validator3 = FieldData.builder()
				.modifiable(true)
				.mapeable(RandomCheckModificationConstraints.class)
				.build();
		assertTrue(validator3.isValidModification(mapOf("minCheckLapse", LocalTime.of(0, 1))));
		assertThrows(WrongDataException.class, () -> validator3.isValidModification(mapOf("minCheckLapse", LocalTime.of(0, 0))));

		final FieldData validator4 = FieldData.builder()
				.modifiable(false)
				.mapeable(RandomCheckModificationConstraints.class)
				.build();
		assertFalse(validator4.isValidModification(mapOf("minCheckLapse", LocalTime.of(0, 1))));
		assertFalse(validator4.isValidModification(mapOf("minCheckLapse", LocalTime.of(0, 0))));
	}

	private Map<String, Object> mapOf(String s, Object o) {
		Map<String,Object> map = new HashMap<>();
		map.put(s, o);
		return map;
	}

	@Test
	void testCreation() {
		FieldData validator = FieldData.builder()
				.columnName("some name")
				.creatable(true)
				.predicate(s -> Pattern.compile("^hello.+").matcher(s).matches())
				.build();

		assertTrue(validator.isValidCreation("hello world!"));
		assertFalse(validator.isValidCreation("bye world!"));

		validator = FieldData.builder()
				.columnName("some name")
				.creatable(true)
				.predicate(s -> Pattern.compile("^hello").matcher(s).matches())
				.build();
		assertFalse(validator.isValidCreation("hello world!"));
		assertFalse(validator.isValidCreation("bye world!"));

		validator = FieldData.builder()
				.columnName("some name")
				.creatable(false)
				.predicate(s -> Pattern.compile("^hello.+").matcher(s).matches())
				.build();

		assertFalse(validator.isValidCreation("hello world!"));
		assertFalse(validator.isValidCreation("bye world!"));
	}

	@Test
	void testNotNullFields() {
		assertThrows(TurkeySurpriseException.class, () -> FieldData.builder()
				.columnName(null)
				.predicate(s -> Pattern.compile("^hello").matcher(s).matches())
				.build());

		assertThrows(TurkeySurpriseException.class, () -> FieldData.builder()
				.columnName("some name")
				.predicate(null)
				.build());

		assertThrows(TurkeySurpriseException.class, () -> FieldData.builder()
				.columnName("some name")
				.build());

		assertThrows(TurkeySurpriseException.class, () -> FieldData.builder()
				.predicate(s -> Pattern.compile("^hello").matcher(s).matches())
				.build());
	}

}
