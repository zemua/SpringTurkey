package devs.mrp.springturkey.delta.validation;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import devs.mrp.springturkey.Exceptions.TurkeySurpriseException;
import devs.mrp.springturkey.Exceptions.WrongDataException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class FieldData {

	@Getter
	private String columnName; // TODO remove

	private Predicate<String> predicate; // TODO remove

	@NotNull
	private Class<?> mapeable;

	private boolean canModify;

	private boolean canCreate;

	@Getter
	private Class<?> referenzable;

	@NotNull
	private Validator validator;

	@NotNull
	private ObjectMapper objectMapper;

	public boolean isValidModification(Map<String,Object> value) throws WrongDataException {
		return canModify && isValid(value);
	}

	public boolean isValidCreation(Object value) throws WrongDataException {
		return canCreate && validate(value);
	}

	public static FieldDataBuilder builder() {
		return new FieldDataBuilder();
	}

	private boolean isValid(Map<String,Object> value) throws WrongDataException {
		Object converted;
		try {
			converted = convertedObject(value);
		} catch (IllegalArgumentException e) {
			throw new WrongDataException("Validation failed", e);
		}
		return validate(converted);
	}

	private boolean validate(Object obj) throws WrongDataException {
		Set<ConstraintViolation<Object>> violations = validator.validate(obj);
		if (violations.isEmpty()) {
			return true;
		} else {
			throw new WrongDataException("Validation failed: " + violations.toString());
		}
	}

	private Object convertedObject(Map<String,Object> value) {
		return objectMapper.convertValue(value, mapeable);
	}

	public static class FieldDataBuilder {

		private String columnName;

		private Predicate<String> predicate;

		private Class<?> mapeable;

		private boolean canModify = false;

		private boolean canCreate = false;

		private Class<?> referenzable;

		public FieldDataBuilder columnName(String name) {
			this.columnName = name;
			return this;
		}

		public FieldDataBuilder predicate(Predicate<String> p) {
			this.predicate = p;
			return this;
		}

		public FieldDataBuilder mapeable(Class<?> clazz) {
			this.mapeable = clazz;
			return this;
		}

		public FieldDataBuilder modifiable(boolean b) {
			this.canModify = b;
			return this;
		}

		public FieldDataBuilder creatable(boolean b) {
			this.canCreate = b;
			return this;
		}

		public FieldDataBuilder referenzable(Class<?> referenzable) {
			this.referenzable = referenzable;
			return this;
		}

		public FieldData build() {
			if (Objects.isNull(this.mapeable)) {
				throw new TurkeySurpriseException("No fields were expected to be null");
			}
			return new FieldData(this.columnName, this.predicate, this.mapeable, this.canModify, this.canCreate, this.referenzable, getValidator(), objectMapper());
		}

	}

	private static Validator getValidator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		return factory.getValidator();
	}

	private static ObjectMapper objectMapper() {
		return JsonMapper.builder()
				.addModule(new JavaTimeModule())
				.build();
	}

}
