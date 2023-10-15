package devs.mrp.springturkey.delta.validation;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
	private String columnName;

	private boolean canModify;

	private boolean canCreate;

	@Getter
	private Class<?> referenzable;

	@NotNull
	private Validator validator;

	@NotNull
	private ObjectMapper objectMapper;

	// TODO remove this method
	public boolean isValidModification(Map<String,Object> value, Class<?> constraints) throws WrongDataException {
		return canModify && isValid(value, constraints);
	}

	// TODO move this method to CreationDataConstrainer
	public boolean isValidCreation(Object value) throws WrongDataException {
		return canCreate && validate(value);
	}

	public static FieldDataBuilder builder() {
		return new FieldDataBuilder();
	}

	private boolean isValid(Map<String,Object> value, Class<?> constraints) throws WrongDataException {
		Object converted;
		try {
			converted = convertedObject(value, constraints);
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

	private Object convertedObject(Map<String,Object> value, Class<?> constraints) {
		return objectMapper.convertValue(value, constraints);
	}

	public static class FieldDataBuilder {

		private String columnName;

		private Class<?> mapeable;

		private boolean canModify = false;

		private boolean canCreate = false;

		private Class<?> referenzable;

		public FieldDataBuilder columnName(String name) {
			this.columnName = name;
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
			return new FieldData(this.columnName, this.canModify, this.canCreate, this.referenzable, getValidator(), objectMapper());
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
