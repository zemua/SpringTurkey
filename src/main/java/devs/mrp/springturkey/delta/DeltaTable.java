package devs.mrp.springturkey.delta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
import devs.mrp.springturkey.delta.validation.FieldValidator;
import devs.mrp.springturkey.delta.validation.entity.ActivityCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.ConditionCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.GroupCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.SettingCreationDelta;

public enum DeltaTable {

	GROUP(Map.of(
			"name", FieldValidator.builder().columnName("name").predicate(StringUtils::isAlphanumericSpace).build(),
			"preventClose", FieldValidator.builder().columnName("prevent_close").predicate(getBooleanPredicate()).build()
			),
			GroupCreationDelta.class),
	ACTIVITY(Map.of(
			"categoryType", FieldValidator.builder().columnName("category_type").predicate(getEnumPredicate(CategoryType.class)).build(),
			"groupId", FieldValidator.builder().columnName("turkey_group").predicate(getUuidPredicate()).build(),
			"preventClosing", FieldValidator.builder().columnName("prevent_closing").predicate(getBooleanPredicate()).build()
			),
			ActivityCreationDelta.class),
	CONDITION(Map.of(
			"requiredUsageMs", FieldValidator.builder().columnName("required_usage_ms").predicate(StringUtils::isNumeric).build(),
			"lastDaysToConsider", FieldValidator.builder().columnName("last_days_to_consider").predicate(StringUtils::isNumeric).build(),
			"conditionalGroup", FieldValidator.builder().columnName("conditional_group").predicate(StringUtils::isAlphanumericSpace).build()
			),
			ConditionCreationDelta.class),
	SETTING(Map.of(
			"settingValue", FieldValidator.builder().columnName("setting_value").predicate(StringUtils::isAlphanumericSpace).build()
			),
			SettingCreationDelta.class);

	private Map<String,FieldValidator> fieldMap;
	private Class<?> entityDtoClass;

	DeltaTable(Map<String,FieldValidator> fieldMap, Class<?> clazz) {
		this.fieldMap = fieldMap;
		this.entityDtoClass = clazz;
	}

	public Map<String,FieldValidator> getFieldMap() {
		return fieldMap;
	}

	public Class<?> getEntityClass() {
		return entityDtoClass;
	}

	private static final Predicate<String> getUuidPredicate() {
		return s -> isUuid(s);
	}

	private static boolean isUuid(String s) { // TODO refactor
		int uuidLength = 36;
		if (s.length() != uuidLength) {
			return false;
		}
		List<Integer> slahes = List.of(8,13,18,23);
		for (int i = 0; i < uuidLength; i++) {
			char c = s.charAt(i);
			if (slahes.contains(i)) {
				if ('-' != c) {
					return false;
				}
			} else {
				if (!Character.isLetterOrDigit(c)) {
					return false;
				}
			}
		}
		return true;
	}

	private static final Predicate<String> getBooleanPredicate() {
		return s -> StringUtils.equalsAnyIgnoreCase(s, "true", "false");
	}

	private static final <T extends Enum<T>> Predicate<String> getEnumPredicate(Class<T> enumerable) {
		List<Enum<T>> types = Arrays.asList(enumerable.getEnumConstants());
		List<String> enumNames = types.stream().map(Enum::name).toList();
		return enumNames::contains;
	}

}
