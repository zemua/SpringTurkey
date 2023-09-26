package devs.mrp.springturkey.delta;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import devs.mrp.springturkey.database.entity.Activity;
import devs.mrp.springturkey.database.entity.Condition;
import devs.mrp.springturkey.database.entity.Group;
import devs.mrp.springturkey.database.entity.RandomCheck;
import devs.mrp.springturkey.database.entity.RandomQuestion;
import devs.mrp.springturkey.database.entity.Setting;
import devs.mrp.springturkey.database.entity.enumerable.ActivityPlatform;
import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
import devs.mrp.springturkey.database.entity.enumerable.GroupType;
import devs.mrp.springturkey.database.entity.enumerable.PlatformType;
import devs.mrp.springturkey.delta.validation.FieldValidator;
import devs.mrp.springturkey.delta.validation.entity.ActivityCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.ConditionCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.GroupCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.SettingCreationDelta;
import devs.mrp.springturkey.utils.BooleanUtils;
import devs.mrp.springturkey.utils.EnumUtils;
import devs.mrp.springturkey.utils.UuidUtils;

public enum DeltaTable {

	GROUP(Map.of(
			"name", FieldValidator.builder().columnName("name").predicate(StringUtils::isAlphanumericSpace).modifiable(true).creatable(true).build(),
			"type", FieldValidator.builder().columnName("type").predicate(EnumUtils.getEnumPredicate(GroupType.class)).creatable(true).modifiable(false).build(),
			"preventClose", FieldValidator.builder().columnName("preventClose").predicate(BooleanUtils::isNullableBoolean).modifiable(true).creatable(true).build()
			),
			GroupCreationDelta.class,
			Group.class),
	ACTIVITY(Map.of(
			"categoryType", FieldValidator.builder().columnName("categoryType").predicate(EnumUtils.getEnumPredicate(CategoryType.class)).modifiable(true).creatable(true).build(),
			"groupId", FieldValidator.builder().columnName("group").predicate(UuidUtils::isNullableUuid).referenzable(Group.class).modifiable(true).creatable(true).build(),
			"preventClose", FieldValidator.builder().columnName("preventClosing").predicate(BooleanUtils::isNullableBoolean).modifiable(true).creatable(true).build(),
			"activityName", FieldValidator.builder().columnName("activityName").predicate(StringUtils::isAlphanumericSpace).creatable(true).build(),
			"activityType", FieldValidator.builder().columnName("activityType").predicate(EnumUtils.getEnumPredicate(ActivityPlatform.class)).creatable(true).build()
			),
			ActivityCreationDelta.class,
			Activity.class),
	CONDITION(Map.of(
			"requiredUsageMs", FieldValidator.builder().columnName("requiredUsageMs").predicate(StringUtils::isNumeric).modifiable(true).creatable(true).build(),
			"lastDaysToConsider", FieldValidator.builder().columnName("lastDaysToConsider").predicate(StringUtils::isNumeric).modifiable(true).creatable(true).build(),
			"conditionalGroup", FieldValidator.builder().columnName("conditionalGroup").predicate(UuidUtils::isNullableUuid).referenzable(Group.class).modifiable(true).creatable(true).build(),
			"targetGroup", FieldValidator.builder().columnName("targetGroup").predicate(UuidUtils::isNullableUuid).referenzable(Group.class).modifiable(true).creatable(true).build()
			),
			ConditionCreationDelta.class,
			Condition.class),
	SETTING(Map.of(
			"settingValue", FieldValidator.builder().columnName("settingValue").predicate(StringUtils::isAlphanumericSpace).modifiable(true).creatable(true).build(),
			"settingKey", FieldValidator.builder().columnName("settingKey").predicate(StringUtils::isAlphanumeric).creatable(true).build(),
			"platformType", FieldValidator.builder().columnName("platform").predicate(EnumUtils.getEnumPredicate(PlatformType.class)).creatable(true).build()
			),
			SettingCreationDelta.class,
			Setting.class),
	RANDOM_QUESTION(Map.of(
			"name", FieldValidator.builder().columnName("name").predicate(StringUtils::isAlphanumericSpace).modifiable(true).creatable(true).build(),
			"question", FieldValidator.builder().columnName("question").predicate(StringUtils::isNotBlank).modifiable(true).creatable(true).build(),
			"frequency", FieldValidator.builder().columnName("frequency").predicate(StringUtils::isNumeric).modifiable(true).creatable(true).build(),
			"multiplier", FieldValidator.builder().columnName("multiplier").predicate(StringUtils::isNumeric).modifiable(true).creatable(true).build()
			),
			null,
			RandomQuestion.class),
	RANDOM_CHECK(Map.of(
			"name", FieldValidator.builder().columnName("name").predicate(StringUtils::isAlphanumericSpace).modifiable(true).creatable(true).build(),
			"startActive", FieldValidator.builder().columnName("startActive").predicate(DeltaTable::parseableTime).modifiable(true).creatable(true).build(),
			"endActive", FieldValidator.builder().columnName("endActive").predicate(DeltaTable::parseableTime).modifiable(true).creatable(true).build(),
			"minCheckLapse", FieldValidator.builder().columnName("minCheckLapse").predicate(DeltaTable::parseableTime).modifiable(true).creatable(true).build(),
			"maxCheckLapse", FieldValidator.builder().columnName("maxCheckLapse").predicate(DeltaTable::parseableTime).modifiable(true).creatable(true).build(),
			"reward", FieldValidator.builder().columnName("reward").predicate(DeltaTable::parseableTime).modifiable(true).creatable(true).build(),
			"activeDays", FieldValidator.builder().columnName("activeDays").modifiable(true).creatable(true).build()
			),
			null,
			RandomCheck.class);

	private Map<String,FieldValidator> fieldMap;
	private Class<?> entityDtoClass;
	private Class<?> entity;

	DeltaTable(Map<String,FieldValidator> fieldMap, Class<?> dto, Class<?> entity) {
		this.fieldMap = fieldMap;
		this.entityDtoClass = dto;
		this.entity = entity;
	}

	public Map<String,FieldValidator> getFieldMap() {
		return fieldMap;
	}

	public Class<?> getDtoClass() {
		return entityDtoClass;
	}

	public Class<?> getEntityClass() {
		return entity;
	}

	private static boolean parseableTime(String time) { // TODO refactor, take this outside of the enum
		// TODO refactor using logic instead of catching an exception
		try {
			LocalTime.parse(time);
		} catch (DateTimeParseException | NullPointerException e) {
			return false;
		}
		return true;
	}

}
