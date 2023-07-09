package devs.mrp.springturkey.delta.validation;

import java.util.Map;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.database.service.DeltaFacade;

public abstract class DataConstrainerTemplate implements DataConstrainer {

	@Override
	public int pushDelta(ModificationDelta delta) throws WrongDataException {
		if (!isValid(delta)) {
			throw new WrongDataException("Incorrect field name");
		}
		return getDeltaFacade().pushDelta(mapDeltaField(delta));
	}

	private boolean isValid(ModificationDelta delta) {
		String name = delta.getFieldName();
		String value = delta.getTextValue();
		return getFieldMap().containsKey(name) && getFieldMap().get(name).isValid(value);
	}

	private ModificationDelta mapDeltaField(ModificationDelta delta) throws WrongDataException {
		return delta.withFieldName(getFieldName(delta));
	}

	private String getFieldName(ModificationDelta delta) throws WrongDataException {
		String name = delta.getFieldName();
		if (!getFieldMap().containsKey(name)) {
			throw new WrongDataException("Incorrect field name");
		}
		return getFieldMap().get(name).getFieldName();
	}

	protected abstract Map<String,FieldValidator> getFieldMap();

	protected abstract DeltaFacade getDeltaFacade();

	protected static final String nameRegex() {
		return "^\\w+[\\h\\w]*$";
	}

	protected static final String booleanRegex() {
		return "^true$|^false$";
	}

	protected static final String uuidRegex() {
		return "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
	}

	protected static final <T extends Enum<T>> String regexFromEnum(Class<T> enumerable) {
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
