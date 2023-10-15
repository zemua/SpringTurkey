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
			"name", FieldData.builder().columnName("name").modifiable(true).creatable(true).build(),
			"type", FieldData.builder().columnName("type").creatable(true).modifiable(false).build(),
			"preventClose", FieldData.builder().columnName("preventClose").modifiable(true).creatable(true).build()
			),
			GroupCreationDelta.class,
			Group.class,
			GroupModificationConstraints.class),
	ACTIVITY(Map.of(
			"categoryType", FieldData.builder().columnName("categoryType").modifiable(true).creatable(true).build(),
			"groupId", FieldData.builder().columnName("group").referenzable(Group.class).modifiable(true).creatable(true).build(),
			"preventClose", FieldData.builder().columnName("preventClosing").modifiable(true).creatable(true).build(),
			"activityName", FieldData.builder().columnName("activityName").creatable(true).build(),
			"activityType", FieldData.builder().columnName("activityType").creatable(true).build()
			),
			ActivityCreationDelta.class,
			Activity.class,
			ActivityModificationConstraints.class),
	CONDITION(Map.of(
			"requiredUsageMs", FieldData.builder().columnName("requiredUsageMs").modifiable(true).creatable(true).build(),
			"lastDaysToConsider", FieldData.builder().columnName("lastDaysToConsider").modifiable(true).creatable(true).build(),
			"conditionalGroup", FieldData.builder().columnName("conditionalGroup").referenzable(Group.class).modifiable(true).creatable(true).build(),
			"targetGroup", FieldData.builder().columnName("targetGroup").referenzable(Group.class).modifiable(true).creatable(true).build()
			),
			ConditionCreationDelta.class,
			Condition.class,
			ConditionModificationConstraints.class),
	SETTING(Map.of(
			"settingValue", FieldData.builder().columnName("settingValue").modifiable(true).creatable(true).build(),
			"settingKey", FieldData.builder().columnName("settingKey").creatable(true).build(),
			"platformType", FieldData.builder().columnName("platform").creatable(true).build()
			),
			SettingCreationDelta.class,
			Setting.class,
			SettingModificationConstraints.class),
	RANDOM_QUESTION(Map.of(
			"name", FieldData.builder().columnName("name").modifiable(true).creatable(true).build(),
			"question", FieldData.builder().columnName("question").modifiable(true).creatable(true).build(),
			"frequency", FieldData.builder().columnName("frequency").modifiable(true).creatable(true).build(),
			"multiplier", FieldData.builder().columnName("multiplier").modifiable(true).creatable(true).build()
			),
			RandomQuestionCreationDelta.class,
			RandomQuestion.class,
			RandomQuestionModificationConstraints.class),
	RANDOM_CHECK(Map.of(
			"name", FieldData.builder().columnName("name").modifiable(true).creatable(true).build(),
			"startActive", FieldData.builder().columnName("startActive").modifiable(true).creatable(true).build(),
			"endActive", FieldData.builder().columnName("endActive").modifiable(true).creatable(true).build(),
			"minCheckLapse", FieldData.builder().columnName("minCheckLapse").modifiable(true).creatable(true).build(),
			"maxCheckLapse", FieldData.builder().columnName("maxCheckLapse").modifiable(true).creatable(true).build(),
			"reward", FieldData.builder().columnName("reward").modifiable(true).creatable(true).build(),
			"activeDays", FieldData.builder().columnName("activeDays").modifiable(true).creatable(true).build(),
			"negativeQuestions", FieldData.builder().columnName("negativeQuestions").modifiable(true).creatable(true).build(),
			"positiveQuestions", FieldData.builder().columnName("positiveQuestions").modifiable(true).creatable(true).build()
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
