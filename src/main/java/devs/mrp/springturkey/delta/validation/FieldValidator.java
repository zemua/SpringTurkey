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

	private boolean canModify;

	private boolean canCreate;

	public boolean isValidModification(String value) {
		return canModify && predicate.test(value);
	}

	public boolean isValidCreation(String value) {
		return canCreate && predicate.test(value);
	}

	public static FieldValidatorBuilder builder() {
		return new FieldValidatorBuilder();
	}

	public static class FieldValidatorBuilder {

		private String columnName;

		private Predicate<String> predicate;

		private boolean canModify = false;

		private boolean canCreate = false;

		public FieldValidatorBuilder columnName(String name) {
			this.columnName = name;
			return this;
		}

		public FieldValidatorBuilder predicate(Predicate<String> p) {
			this.predicate = p;
			return this;
		}

		public FieldValidatorBuilder modifiable(boolean b) {
			this.canModify = b;
			return this;
		}

		public FieldValidatorBuilder creatable(boolean b) {
			this.canCreate = b;
			return this;
		}

		public FieldValidator build() {
			if (Objects.isNull(this.columnName) || Objects.isNull(this.predicate)) {
				throw new TurkeySurpriseException("No fields were expected to be null");
			}
			return new FieldValidator(this.columnName, this.predicate, this.canModify, this.canCreate);
		}

	}

}
