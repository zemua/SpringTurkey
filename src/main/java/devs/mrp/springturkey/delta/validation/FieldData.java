package devs.mrp.springturkey.delta.validation;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import com.fasterxml.jackson.databind.ObjectMapper;

import devs.mrp.springturkey.Exceptions.TurkeySurpriseException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class FieldData {

	@Getter
	@NotEmpty
	private String columnName;

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

	public boolean isValidModification(Map<String,Object> value) { // TODO change to generic thing as we can receive in the json numbers and other stuff
		return canModify && isValid(value);
	}

	public boolean isValidCreation(String value) { // TODO change to generic thing as we can receive in the json numbers and other stuff
		return canCreate && predicate.test(value);
	}

	public static FieldDataBuilder builder() {
		return new FieldDataBuilder();
	}

	private boolean isValid(Map<String,Object> value) {
		return validator.validate(convertedObject(value), mapeable).isEmpty();
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

		private Validator validator;

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

		public FieldDataBuilder validator(Validator v) {
			this.validator = v;
			return this;
		}

		public FieldData build() {
			if (Objects.isNull(this.columnName) || Objects.isNull(this.mapeable) || Objects.isNull(this.validator)) {
				throw new TurkeySurpriseException("No fields were expected to be null");
			}
			return new FieldData(this.columnName, this.predicate, this.mapeable, this.canModify, this.canCreate, this.referenzable, this.validator, new ObjectMapper());
		}

	}

}
