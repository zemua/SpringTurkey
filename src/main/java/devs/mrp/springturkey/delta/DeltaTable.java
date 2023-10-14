package devs.mrp.springturkey.delta;

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
import devs.mrp.springturkey.delta.validation.entity.RandomCheckCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.RandomQuestionCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.SettingCreationDelta;
import devs.mrp.springturkey.utils.BooleanUtils;
import devs.mrp.springturkey.utils.EnumUtils;
import devs.mrp.springturkey.utils.TimeUtils;
import devs.mrp.springturkey.utils.UuidUtils;

public enum DeltaTable {

	// TODO refactor repeated XxxModificationConstraints.class

	GROUP(Map.of(
			"name", FieldData.builder().columnName("name").predicate(StringUtils::isAlphanumericSpace).modifiable(true).creatable(true).build(),
			"type", FieldData.builder().columnName("type").predicate(EnumUtils.getEnumPredicate(GroupType.class)).creatable(true).modifiable(false).build(),
			"preventClose", FieldData.builder().columnName("preventClose").predicate(BooleanUtils::isNullableBoolean).modifiable(true).creatable(true).build()
			),
			GroupCreationDelta.class,
			Group.class,
			GroupModificationConstraints.class),
	ACTIVITY(Map.of(
			"categoryType", FieldData.builder().columnName("categoryType").predicate(EnumUtils.getEnumPredicate(CategoryType.class)).modifiable(true).creatable(true).build(),
			"groupId", FieldData.builder().columnName("group").predicate(UuidUtils::isNullableUuid).referenzable(Group.class).modifiable(true).creatable(true).build(),
			"preventClose", FieldData.builder().columnName("preventClosing").predicate(BooleanUtils::isNullableBoolean).modifiable(true).creatable(true).build(),
			"activityName", FieldData.builder().columnName("activityName").predicate(StringUtils::isAlphanumericSpace).creatable(true).build(),
			"activityType", FieldData.builder().columnName("activityType").predicate(EnumUtils.getEnumPredicate(ActivityPlatform.class)).creatable(true).build()
			),
			ActivityCreationDelta.class,
			Activity.class,
			ActivityModificationConstraints.class),
	CONDITION(Map.of(
			"requiredUsageMs", FieldData.builder().columnName("requiredUsageMs").predicate(StringUtils::isNumeric).modifiable(true).creatable(true).build(),
			"lastDaysToConsider", FieldData.builder().columnName("lastDaysToConsider").predicate(StringUtils::isNumeric).modifiable(true).creatable(true).build(),
			"conditionalGroup", FieldData.builder().columnName("conditionalGroup").predicate(UuidUtils::isNullableUuid).referenzable(Group.class).modifiable(true).creatable(true).build(),
			"targetGroup", FieldData.builder().columnName("targetGroup").predicate(UuidUtils::isNullableUuid).referenzable(Group.class).modifiable(true).creatable(true).build()
			),
			ConditionCreationDelta.class,
			Condition.class,
			ConditionModificationConstraints.class),
	SETTING(Map.of(
			"settingValue", FieldData.builder().columnName("settingValue").predicate(StringUtils::isAlphanumericSpace).modifiable(true).creatable(true).build(),
			"settingKey", FieldData.builder().columnName("settingKey").predicate(StringUtils::isAlphanumeric).creatable(true).build(),
			"platformType", FieldData.builder().columnName("platform").predicate(EnumUtils.getEnumPredicate(PlatformType.class)).creatable(true).build()
			),
			SettingCreationDelta.class,
			Setting.class,
			SettingModificationConstraints.class),
	RANDOM_QUESTION(Map.of(
			"name", FieldData.builder().columnName("name").predicate(StringUtils::isAlphanumericSpace).modifiable(true).creatable(true).build(),
			"question", FieldData.builder().columnName("question").predicate(StringUtils::isNotBlank).modifiable(true).creatable(true).build(),
			"frequency", FieldData.builder().columnName("frequency").predicate(StringUtils::isNumeric).modifiable(true).creatable(true).build(),
			"multiplier", FieldData.builder().columnName("multiplier").predicate(StringUtils::isNumeric).modifiable(true).creatable(true).build()
			),
			RandomQuestionCreationDelta.class,
			RandomQuestion.class,
			RandomQuestionModificationConstraints.class),
	RANDOM_CHECK(Map.of(
			"name", FieldData.builder().columnName("name").predicate(StringUtils::isAlphanumericSpace).modifiable(true).creatable(true).build(),
			"startActive", FieldData.builder().columnName("startActive").predicate(TimeUtils::isParseableTime).modifiable(true).creatable(true).build(),
			"endActive", FieldData.builder().columnName("endActive").predicate(TimeUtils::isParseableTime).modifiable(true).creatable(true).build(),
			"minCheckLapse", FieldData.builder().columnName("minCheckLapse").predicate(TimeUtils::isParseableTime).modifiable(true).creatable(true).build(),
			"maxCheckLapse", FieldData.builder().columnName("maxCheckLapse").predicate(TimeUtils::isParseableTime).modifiable(true).creatable(true).build(),
			"reward", FieldData.builder().columnName("reward").predicate(TimeUtils::isParseableTime).modifiable(true).creatable(true).build(),
			"activeDays", FieldData.builder().columnName("activeDays").predicate(p -> false).modifiable(true).creatable(true).build(),
			"negativeQuestions", FieldData.builder().columnName("negativeQuestions").predicate(p -> false).modifiable(true).creatable(true).build(),
			"positiveQuestions", FieldData.builder().columnName("positiveQuestions").predicate(p -> false).modifiable(true).creatable(true).build()
			),
			RandomCheckCreationDelta.class,
			RandomCheck.class,
			RandomCheckModificationConstraints.class);

	private Map<String,FieldData> fieldMap;
	private Class<?> entityDtoClass;
	private Class<?> entity;
	private Class<?> modificationConstraints;

	DeltaTable(Map<String,FieldData> fieldMap, Class<?> dto, Class<?> entity, Class<?> modificationConstraints) {
		this.fieldMap = fieldMap;
		this.entityDtoClass = dto;
		this.entity = entity;
		this.modificationConstraints = modificationConstraints;
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

	public Class<?> getModificationConstraints() {
		return modificationConstraints;
	}

}
