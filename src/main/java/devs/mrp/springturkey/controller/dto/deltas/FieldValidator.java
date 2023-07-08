package devs.mrp.springturkey.controller.dto.deltas;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private Pattern pattern;

	public boolean isValid(String value) {
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}

	public static FieldValidatorBuilder builder() {
		return new FieldValidatorBuilder();
	}

	public static class FieldValidatorBuilder {

		private String fieldName;

		private Pattern pattern;

		public FieldValidatorBuilder fieldName(String name) {
			this.fieldName = name;
			return this;
		}

		public FieldValidatorBuilder pattern(String regex) {
			this.pattern = Pattern.compile(regex);
			return this;
		}

		public FieldValidator build() {
			return new FieldValidator(this.fieldName, this.pattern);
		}

	}

}
