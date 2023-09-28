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
import devs.mrp.springturkey.delta.validation.FieldData;
import devs.mrp.springturkey.delta.validation.constraints.ActivityModificationConstraints;
import devs.mrp.springturkey.delta.validation.constraints.ConditionModificationConstraints;
import devs.mrp.springturkey.delta.validation.constraints.GroupModificationConstraints;
import devs.mrp.springturkey.delta.validation.constraints.RandomCheckModificationConstraints;
import devs.mrp.springturkey.delta.validation.constraints.RandomQuestionModificationConstraints;
import devs.mrp.springturkey.delta.validation.constraints.SettingModificationConstraints;
import devs.mrp.springturkey.delta.validation.entity.ActivityCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.ConditionCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.GroupCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.SettingCreationDelta;
import devs.mrp.springturkey.utils.BooleanUtils;
import devs.mrp.springturkey.utils.EnumUtils;
import devs.mrp.springturkey.utils.UuidUtils;

public enum DeltaTable {

	// TODO refactor repeated XxxModificationConstraints.class

	GROUP(Map.of(
			"name", FieldData.builder().columnName("name").predicate(StringUtils::isAlphanumericSpace).mapeable(GroupModificationConstraints.class).modifiable(true).creatable(true).build(),
			"type", FieldData.builder().columnName("type").predicate(EnumUtils.getEnumPredicate(GroupType.class)).mapeable(GroupModificationConstraints.class).creatable(true).modifiable(false).build(),
			"preventClose", FieldData.builder().columnName("preventClose").predicate(BooleanUtils::isNullableBoolean).mapeable(GroupModificationConstraints.class).modifiable(true).creatable(true).build()
			),
			GroupCreationDelta.class,
			Group.class),
	ACTIVITY(Map.of(
			"categoryType", FieldData.builder().columnName("categoryType").predicate(EnumUtils.getEnumPredicate(CategoryType.class)).mapeable(ActivityModificationConstraints.class).modifiable(true).creatable(true).build(),
			"groupId", FieldData.builder().columnName("group").predicate(UuidUtils::isNullableUuid).mapeable(ActivityModificationConstraints.class).referenzable(Group.class).modifiable(true).creatable(true).build(),
			"preventClose", FieldData.builder().columnName("preventClosing").predicate(BooleanUtils::isNullableBoolean).mapeable(ActivityModificationConstraints.class).modifiable(true).creatable(true).build(),
			"activityName", FieldData.builder().columnName("activityName").predicate(StringUtils::isAlphanumericSpace).mapeable(ActivityModificationConstraints.class).creatable(true).build(),
			"activityType", FieldData.builder().columnName("activityType").predicate(EnumUtils.getEnumPredicate(ActivityPlatform.class)).mapeable(ActivityModificationConstraints.class).creatable(true).build()
			),
			ActivityCreationDelta.class,
			Activity.class),
	CONDITION(Map.of(
			"requiredUsageMs", FieldData.builder().columnName("requiredUsageMs").predicate(StringUtils::isNumeric).mapeable(ConditionModificationConstraints.class).modifiable(true).creatable(true).build(),
			"lastDaysToConsider", FieldData.builder().columnName("lastDaysToConsider").predicate(StringUtils::isNumeric).mapeable(ConditionModificationConstraints.class).modifiable(true).creatable(true).build(),
			"conditionalGroup", FieldData.builder().columnName("conditionalGroup").predicate(UuidUtils::isNullableUuid).mapeable(ConditionModificationConstraints.class).referenzable(Group.class).modifiable(true).creatable(true).build(),
			"targetGroup", FieldData.builder().columnName("targetGroup").predicate(UuidUtils::isNullableUuid).mapeable(ConditionModificationConstraints.class).referenzable(Group.class).modifiable(true).creatable(true).build()
			),
			ConditionCreationDelta.class,
			Condition.class),
	SETTING(Map.of(
			"settingValue", FieldData.builder().columnName("settingValue").predicate(StringUtils::isAlphanumericSpace).mapeable(SettingModificationConstraints.class).modifiable(true).creatable(true).build(),
			"settingKey", FieldData.builder().columnName("settingKey").predicate(StringUtils::isAlphanumeric).mapeable(SettingModificationConstraints.class).creatable(true).build(),
			"platformType", FieldData.builder().columnName("platform").predicate(EnumUtils.getEnumPredicate(PlatformType.class)).mapeable(SettingModificationConstraints.class).creatable(true).build()
			),
			SettingCreationDelta.class,
			Setting.class),
	RANDOM_QUESTION(Map.of(
			"name", FieldData.builder().columnName("name").predicate(StringUtils::isAlphanumericSpace).mapeable(RandomQuestionModificationConstraints.class).modifiable(true).creatable(true).build(),
			"question", FieldData.builder().columnName("question").predicate(StringUtils::isNotBlank).mapeable(RandomQuestionModificationConstraints.class).modifiable(true).creatable(true).build(),
			"frequency", FieldData.builder().columnName("frequency").predicate(StringUtils::isNumeric).mapeable(RandomQuestionModificationConstraints.class).modifiable(true).creatable(true).build(),
			"multiplier", FieldData.builder().columnName("multiplier").predicate(StringUtils::isNumeric).mapeable(RandomQuestionModificationConstraints.class).modifiable(true).creatable(true).build()
			),
			null,
			RandomQuestion.class),
	RANDOM_CHECK(Map.of(
			"name", FieldData.builder().columnName("name").predicate(StringUtils::isAlphanumericSpace).mapeable(RandomCheckModificationConstraints.class).modifiable(true).creatable(true).build(),
			"startActive", FieldData.builder().columnName("startActive").predicate(DeltaTable::parseableTime).mapeable(RandomCheckModificationConstraints.class).modifiable(true).creatable(true).build(),
			"endActive", FieldData.builder().columnName("endActive").predicate(DeltaTable::parseableTime).mapeable(RandomCheckModificationConstraints.class).modifiable(true).creatable(true).build(),
			"minCheckLapse", FieldData.builder().columnName("minCheckLapse").predicate(DeltaTable::parseableTime).mapeable(RandomCheckModificationConstraints.class).modifiable(true).creatable(true).build(),
			"maxCheckLapse", FieldData.builder().columnName("maxCheckLapse").predicate(DeltaTable::parseableTime).mapeable(RandomCheckModificationConstraints.class).modifiable(true).creatable(true).build(),
			"reward", FieldData.builder().columnName("reward").predicate(DeltaTable::parseableTime).mapeable(RandomCheckModificationConstraints.class).modifiable(true).creatable(true).build(),
			"activeDays", FieldData.builder().columnName("activeDays").predicate(p -> false).mapeable(RandomCheckModificationConstraints.class).modifiable(true).creatable(true).build(),
			"negativeQuestions", FieldData.builder().columnName("negativeQuestions").predicate(p -> false).mapeable(RandomCheckModificationConstraints.class).modifiable(true).creatable(true).build(),
			"positiveQuestions", FieldData.builder().columnName("positiveQuestions").predicate(p -> false).mapeable(RandomCheckModificationConstraints.class).modifiable(true).creatable(true).build()
			),
			null,
			RandomCheck.class);

	private Map<String,FieldData> fieldMap;
	private Class<?> entityDtoClass;
	private Class<?> entity;

	DeltaTable(Map<String,FieldData> fieldMap, Class<?> dto, Class<?> entity) {
		this.fieldMap = fieldMap;
		this.entityDtoClass = dto;
		this.entity = entity;
	}

	public Map<String,FieldData> getFieldMap() {
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
