package devs.mrp.springturkey.delta;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import devs.mrp.springturkey.database.entity.Activity;
import devs.mrp.springturkey.database.entity.Condition;
import devs.mrp.springturkey.database.entity.Group;
import devs.mrp.springturkey.database.entity.Setting;
import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
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
			"name", FieldValidator.builder().columnName("name").predicate(StringUtils::isAlphanumericSpace).build(),
			"preventClose", FieldValidator.builder().columnName("prevent_close").predicate(BooleanUtils::isBoolean).build()
			),
			GroupCreationDelta.class,
			Group.class),
	ACTIVITY(Map.of(
			"categoryType", FieldValidator.builder().columnName("category_type").predicate(EnumUtils.getEnumPredicate(CategoryType.class)).build(),
			"groupId", FieldValidator.builder().columnName("turkey_group").predicate(UuidUtils::isUuid).build(),
			"preventClosing", FieldValidator.builder().columnName("prevent_closing").predicate(BooleanUtils::isBoolean).build()
			),
			ActivityCreationDelta.class,
			Activity.class),
	CONDITION(Map.of(
			"requiredUsageMs", FieldValidator.builder().columnName("required_usage_ms").predicate(StringUtils::isNumeric).build(),
			"lastDaysToConsider", FieldValidator.builder().columnName("last_days_to_consider").predicate(StringUtils::isNumeric).build(),
			"conditionalGroup", FieldValidator.builder().columnName("conditional_group").predicate(StringUtils::isAlphanumericSpace).build()
			),
			ConditionCreationDelta.class,
			Condition.class),
	SETTING(Map.of(
			"settingValue", FieldValidator.builder().columnName("setting_value").predicate(StringUtils::isAlphanumericSpace).build()
			),
			SettingCreationDelta.class,
			Setting.class);

	private Map<String,FieldValidator> fieldMap;
	private Class<?> entityDtoClass;
	private Class<?> entityClass;

	DeltaTable(Map<String,FieldValidator> fieldMap, Class<?> dto, Class<?> entity) {
		this.fieldMap = fieldMap;
		this.entityDtoClass = dto;
		this.entityClass = entity;
	}

	public Map<String,FieldValidator> getFieldMap() {
		return fieldMap;
	}

	public Class<?> getDtoClass() {
		return entityDtoClass;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

}
