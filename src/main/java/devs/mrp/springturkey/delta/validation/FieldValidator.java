package devs.mrp.springturkey.delta.validation;

import java.util.Objects;
import java.util.function.Predicate;

import devs.mrp.springturkey.Exceptions.TurkeySurpriseException;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class FieldValidator {

	@Getter
	@NotEmpty
	private String columnName;

	@NotNull
	private Predicate<String> predicate;

	public boolean isValid(String value) {
		return predicate.test(value);
	}

	public static FieldValidatorBuilder builder() {
		return new FieldValidatorBuilder();
	}

	public static class FieldValidatorBuilder {

		private String columnName;

		private Predicate<String> predicate;

		public FieldValidatorBuilder columnName(String name) {
			this.columnName = name;
			return this;
		}

		public FieldValidatorBuilder predicate(Predicate<String> p) {
			this.predicate = p;
			return this;
		}

		public FieldValidator build() {
			if (Objects.isNull(this.columnName) || Objects.isNull(this.predicate)) {
				throw new TurkeySurpriseException("No fields were expected to be null");
			}
			return new FieldValidator(this.columnName, this.predicate);
		}

	}

}
