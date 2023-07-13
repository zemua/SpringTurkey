package devs.mrp.springturkey.delta.validation;

import java.util.function.Predicate;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class FieldValidator {

	@Getter
	@NotEmpty
	private String fieldName;

	@NotNull
	private Predicate<String> predicate;

	public boolean isValid(String value) {
		return predicate.test(value);
	}

	public static FieldValidatorBuilder builder() {
		return new FieldValidatorBuilder();
	}

	public static class FieldValidatorBuilder {

		private String fieldName;

		private Predicate<String> predicate;

		public FieldValidatorBuilder fieldName(String name) {
			this.fieldName = name;
			return this;
		}

		public FieldValidatorBuilder predicate(Predicate<String> p) {
			this.predicate = p;
			return this;
		}

		public FieldValidator build() {
			FieldValidator result = new FieldValidator(this.fieldName, this.predicate);
			return result;
		}

	}

}
