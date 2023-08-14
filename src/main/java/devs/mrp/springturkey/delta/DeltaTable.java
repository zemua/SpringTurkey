package devs.mrp.springturkey.delta;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import devs.mrp.springturkey.database.entity.enumerable.ActivityPlatform;
import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
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
			"preventClose", FieldValidator.builder().columnName("prevent_close").predicate(BooleanUtils::isBoolean).modifiable(true).creatable(true).build()
			),
			GroupCreationDelta.class,
			"group"),
	ACTIVITY(Map.of(
			"categoryType", FieldValidator.builder().columnName("categoryType").predicate(EnumUtils.getEnumPredicate(CategoryType.class)).modifiable(true).creatable(true).build(),
			"groupId", FieldValidator.builder().columnName("group").predicate(UuidUtils::isNullableUuid).modifiable(true).creatable(true).build(),
			"preventClose", FieldValidator.builder().columnName("preventClosing").predicate(BooleanUtils::isNullableBoolean).modifiable(true).creatable(true).build(),
			"activityName", FieldValidator.builder().columnName("activityName").predicate(StringUtils::isAlphanumericSpace).creatable(true).build(),
			"activityType", FieldValidator.builder().columnName("activityType").predicate(EnumUtils.getEnumPredicate(ActivityPlatform.class)).creatable(true).build()
			),
			ActivityCreationDelta.class,
			"activity"),
	CONDITION(Map.of(
			"requiredUsageMs", FieldValidator.builder().columnName("required_usage_ms").predicate(StringUtils::isNumeric).modifiable(true).creatable(true).build(),
			"lastDaysToConsider", FieldValidator.builder().columnName("last_days_to_consider").predicate(StringUtils::isNumeric).modifiable(true).creatable(true).build(),
			"conditionalGroup", FieldValidator.builder().columnName("conditional_group").predicate(StringUtils::isAlphanumericSpace).modifiable(true).creatable(true).build()
			),
			ConditionCreationDelta.class,
			"condition"),
	SETTING(Map.of(
			"settingValue", FieldValidator.builder().columnName("settingValue").predicate(StringUtils::isAlphanumericSpace).modifiable(true).creatable(true).build(),
			"settingKey", FieldValidator.builder().columnName("settingKey").predicate(StringUtils::isAlphanumeric).creatable(true).build(),
			"platformType", FieldValidator.builder().columnName("platform").predicate(EnumUtils.getEnumPredicate(PlatformType.class)).creatable(true).build()
			),
			SettingCreationDelta.class,
			"setting");

	private Map<String,FieldValidator> fieldMap;
	private Class<?> entityDtoClass;
	private String entityName;

	DeltaTable(Map<String,FieldValidator> fieldMap, Class<?> dto, String entity) {
		this.fieldMap = fieldMap;
		this.entityDtoClass = dto;
		this.entityName = entity;
	}

	public Map<String,FieldValidator> getFieldMap() {
		return fieldMap;
	}

	public Class<?> getDtoClass() {
		return entityDtoClass;
	}

	public String getEntityName() {
		return entityName;
	}

}
