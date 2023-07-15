package devs.mrp.springturkey.delta;

import static java.util.Map.entry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
import devs.mrp.springturkey.delta.validation.FieldValidator;

public enum DeltaTable {

	GROUP(Map.ofEntries(
			entry("name", FieldValidator.builder()
					.columnName("name")
					.predicate(StringUtils::isAlphanumericSpace)
					.build()),
			entry("preventClose", FieldValidator.builder()
					.columnName("prevent_close")
					.predicate(getBooleanPredicate())
					.build())
			)),
	ACTIVITY(Map.ofEntries(
			entry("categoryType", FieldValidator.builder()
					.columnName("category_type")
					.predicate(getEnumPredicate(CategoryType.class))
					.build()),
			entry("groupId", FieldValidator.builder()
					.columnName("turkey_group")
					.predicate(s -> getUuidPattern().matcher(s).matches()) // TODO check if regex can be avoided
					.build()),
			entry("preventClosing", FieldValidator.builder()
					.columnName("prevent_closing")
					.predicate(getBooleanPredicate())
					.build())
			)),
	CONDITION(Map.of(
			"requiredUsageMs", FieldValidator.builder()
			.columnName("required_usage_ms")
			.predicate(StringUtils::isNumeric)
			.build(),
			"lastDaysToConsider", FieldValidator.builder()
			.columnName("last_days_to_consider")
			.predicate(StringUtils::isNumeric)
			.build(),
			"conditionalGroup", FieldValidator.builder()
			.columnName("conditional_group")
			.predicate(StringUtils::isAlphanumericSpace)
			.build()
			)),
	SETTING(Map.of(
			"settingValue", FieldValidator.builder()
			.columnName("setting_value")
			.predicate(StringUtils::isAlphanumericSpace)
			.build()
			));

	private Map<String,FieldValidator> fieldMap;

	DeltaTable(Map<String,FieldValidator> fieldMap) {
		this.fieldMap = fieldMap;
	}

	public Map<String,FieldValidator> getFieldMap() {
		return fieldMap;
	}

	private static final Pattern uuidPattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

	private static Pattern getUuidPattern() {
		return uuidPattern;
	}

	private static final Predicate<String> getBooleanPredicate() {
		return s -> StringUtils.equalsAnyIgnoreCase(s, "true", "false");
	}

	private static final <T extends Enum<T>> Predicate<String> getEnumPredicate(Class<T> enumerable) {
		List<Enum<T>> types = Arrays.asList(enumerable.getEnumConstants());
		return s -> types.contains(s);
	}

}
