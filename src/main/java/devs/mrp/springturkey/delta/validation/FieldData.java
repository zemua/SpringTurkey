package devs.mrp.springturkey.delta.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class FieldData {

	@Getter
	@NotBlank
	private String columnName;

	@Getter
	private Class<?> referenzable;

	@NotNull
	private Validator validator;

	public static FieldDataBuilder builder() {
		return new FieldDataBuilder();
	}

	public static class FieldDataBuilder {

		@NotBlank
		private String columnName;

		@NotBlank
		private Class<?> mapeable;

		private Class<?> referenzable;

		public FieldDataBuilder columnName(String name) {
			this.columnName = name;
			return this;
		}

		public FieldDataBuilder referenzable(Class<?> referenzable) {
			this.referenzable = referenzable;
			return this;
		}

		public FieldData build() {
			return new FieldData(this.columnName, this.referenzable, getValidator());
		}

		private Validator getValidator() { // TODO remove this and use injected
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			return factory.getValidator();
		}

	}

}
