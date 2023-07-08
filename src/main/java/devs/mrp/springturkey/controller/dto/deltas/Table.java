package devs.mrp.springturkey.controller.dto.deltas;

import static java.util.Map.entry;

import java.util.Map;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.database.entity.enumerable.CategoryType;

public enum Table {

	GROUP(Map.ofEntries(
			entry("name", FieldValidator.builder().fieldName("name").pattern(nameRegex()).build()),
			entry("preventClose", FieldValidator.builder().fieldName("prevent_close").pattern(booleanRegex()).build())
			),null),
	ACTIVITY(Map.ofEntries(
			entry("categoryType", FieldValidator.builder().fieldName("category_type").pattern(regexFromEnum(CategoryType.class)).build()),
			entry("groupId", FieldValidator.builder().fieldName("turkey_group").pattern(uuidRegex()).build()),
			entry("preventClosing", FieldValidator.builder().fieldName("prevent_closing").pattern(booleanRegex()).build())
			), null),
	CONDITION(null, null),
	SETTING(null, null);

	private Map<String,FieldValidator> fieldMap;

	private Table(Map<String,FieldValidator> fields, Object service) {
		this.fieldMap = fields;
	}

	public boolean isValid(String name, String value) {
		return fieldMap.containsKey(name) && fieldMap.get(name).isValid(value);
	}

	public String getFieldName(String name) throws WrongDataException {
		if (!fieldMap.containsKey(name)) {
			throw new WrongDataException("Incorrect field name");
		}
		return fieldMap.get(name).getFieldName();
	}

	private static String nameRegex() {
		return "^\\w+[\\h\\w]*$";
	}

	private static String booleanRegex() {
		return "^true$|^false$";
	}

	private static String uuidRegex() {
		return "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
	}

	private static <T extends Enum<T>> String regexFromEnum(Class<T> enumerable) {
		StringBuilder builder = new StringBuilder();
		Enum<T>[] types = enumerable.getEnumConstants();
		appendToBuilder(builder, types);
		return builder.toString();
	}

	private static <T extends Enum<T>> void appendToBuilder(StringBuilder builder, Enum<T>[] types) {
		for (int i = 0; i < types.length; i++) {
			if (i>0) {
				builder.append("|");
			}
			builder.append("^" + types[i].name() + "$");
		}
	}

}
