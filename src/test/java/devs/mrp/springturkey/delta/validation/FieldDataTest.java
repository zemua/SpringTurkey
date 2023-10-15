package devs.mrp.springturkey.delta.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.database.entity.Activity;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.entity.enumerable.ActivityPlatform;
import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
import devs.mrp.springturkey.delta.validation.constraints.ActivityModificationConstraints;
import devs.mrp.springturkey.delta.validation.constraints.ConditionModificationConstraints;
import devs.mrp.springturkey.delta.validation.constraints.RandomCheckModificationConstraints;

class FieldDataTest {

	@Test
	void testModification() throws WrongDataException {
		final FieldData validator = FieldData.builder()
				.modifiable(true)
				.build();
		assertTrue(validator.isValidModification(mapOf("activityName", "hello world"), ActivityModificationConstraints.class));
		assertThrows(WrongDataException.class, () -> validator.isValidModification(mapOf("activityName", "invalid character!"), ActivityModificationConstraints.class));

		final FieldData validator2 = FieldData.builder()
				.modifiable(true)
				.build();
		assertTrue(validator2.isValidModification(mapOf("lastDaysToConsider", 0), ConditionModificationConstraints.class));
		assertThrows(WrongDataException.class, () -> validator2.isValidModification(mapOf("lastDaysToConsider", -1), ConditionModificationConstraints.class));

		final FieldData validator3 = FieldData.builder()
				.modifiable(true)
				.build();
		assertTrue(validator3.isValidModification(mapOf("minCheckLapse", LocalTime.of(0, 1)), RandomCheckModificationConstraints.class));
		assertThrows(WrongDataException.class, () -> validator3.isValidModification(mapOf("minCheckLapse", LocalTime.of(0, 0)), RandomCheckModificationConstraints.class));

		final FieldData validator4 = FieldData.builder()
				.modifiable(false)
				.build();
		assertFalse(validator4.isValidModification(mapOf("minCheckLapse", LocalTime.of(0, 1)), RandomCheckModificationConstraints.class));
		assertFalse(validator4.isValidModification(mapOf("minCheckLapse", LocalTime.of(0, 0)), RandomCheckModificationConstraints.class));
	}

	private Map<String, Object> mapOf(String s, Object o) {
		Map<String,Object> map = new HashMap<>();
		map.put(s, o);
		return map;
	}

	@Test
	void testInvalidCreation() {
		FieldData validator = FieldData.builder()
				.creatable(true)
				.build();

		Activity activity = Activity.builder().build();

		assertThrows(WrongDataException.class, () -> validator.isValidCreation(activity));
	}

	@Test
	void testValidCreation() throws WrongDataException {
		FieldData validator = FieldData.builder()
				.creatable(true)
				.build();

		assertTrue(validator.isValidCreation(validActivity()));
	}

	@Test
	void testNotCreatable() throws WrongDataException {
		FieldData validator = FieldData.builder()
				.creatable(false)
				.build();

		assertFalse(validator.isValidCreation(validActivity()));
	}

	private Activity validActivity() {
		return Activity.builder()
				.user(new TurkeyUser())
				.activityName("some name")
				.activityType(ActivityPlatform.ANDROID_APP)
				.categoryType(CategoryType.NEGATIVE)
				.build();
	}

}
