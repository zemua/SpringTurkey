package devs.mrp.springturkey.delta;

import java.util.Map;

import devs.mrp.springturkey.database.entity.Activity;
import devs.mrp.springturkey.database.entity.Condition;
import devs.mrp.springturkey.database.entity.Group;
import devs.mrp.springturkey.database.entity.RandomCheck;
import devs.mrp.springturkey.database.entity.RandomQuestion;
import devs.mrp.springturkey.database.entity.Setting;
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

public enum DeltaTable {

	GROUP(Map.of(
			"name", FieldData.builder().columnName("name").build(),
			"type", FieldData.builder().columnName("type").build(),
			"preventClose", FieldData.builder().columnName("preventClose").build(),
			"deletion", FieldData.builder().columnName("deletion").build()
			),
			GroupCreationDelta.class,
			Group.class,
			GroupModificationConstraints.class),
	ACTIVITY(Map.of(
			"categoryType", FieldData.builder().columnName("categoryType").build(),
			"groupId", FieldData.builder().columnName("group").referenzable(Group.class).build(),
			"preventClose", FieldData.builder().columnName("preventClosing").build(),
			"activityName", FieldData.builder().columnName("activityName").build(),
			"activityType", FieldData.builder().columnName("activityType").build(),
			"deletion", FieldData.builder().columnName("deletion").build()
			),
			ActivityCreationDelta.class,
			Activity.class,
			ActivityModificationConstraints.class),
	CONDITION(Map.of(
			"requiredUsageMs", FieldData.builder().columnName("requiredUsageMs").build(),
			"lastDaysToConsider", FieldData.builder().columnName("lastDaysToConsider").build(),
			"conditionalGroup", FieldData.builder().columnName("conditionalGroup").referenzable(Group.class).build(),
			"targetGroup", FieldData.builder().columnName("targetGroup").referenzable(Group.class).build(),
			"deletion", FieldData.builder().columnName("deletion").build()
			),
			ConditionCreationDelta.class,
			Condition.class,
			ConditionModificationConstraints.class),
	SETTING(Map.of(
			"settingValue", FieldData.builder().columnName("settingValue").build(),
			"settingKey", FieldData.builder().columnName("settingKey").build(),
			"platformType", FieldData.builder().columnName("platform").build(),
			"deletion", FieldData.builder().columnName("deletion").build()
			),
			SettingCreationDelta.class,
			Setting.class,
			SettingModificationConstraints.class),
	RANDOM_QUESTION(Map.of(
			"name", FieldData.builder().columnName("name").build(),
			"question", FieldData.builder().columnName("question").build(),
			"frequency", FieldData.builder().columnName("frequency").build(),
			"multiplier", FieldData.builder().columnName("multiplier").build(),
			"deletion", FieldData.builder().columnName("deletion").build()
			),
			RandomQuestionCreationDelta.class,
			RandomQuestion.class,
			RandomQuestionModificationConstraints.class),
	RANDOM_CHECK(Map.of(
			"name", FieldData.builder().columnName("name").build(),
			"startActive", FieldData.builder().columnName("startActive").build(),
			"endActive", FieldData.builder().columnName("endActive").build(),
			"minCheckLapse", FieldData.builder().columnName("minCheckLapse").build(),
			"maxCheckLapse", FieldData.builder().columnName("maxCheckLapse").build(),
			"reward", FieldData.builder().columnName("reward").build(),
			"activeDays", FieldData.builder().columnName("activeDays").build(),
			"negativeQuestions", FieldData.builder().columnName("negativeQuestions").build(),
			"positiveQuestions", FieldData.builder().columnName("positiveQuestions").build(),
			"deletion", FieldData.builder().columnName("deletion").build()
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

	public Class<?> getCreationConstraints() {
		return entityDtoClass;
	}

	public Class<?> getEntityClass() {
		return entity;
	}

	public Class<?> getModificationConstraints() {
		return modificationConstraints;
	}

}
