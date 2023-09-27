package devs.mrp.springturkey.delta.validation;

import java.util.Objects;
import java.util.function.Predicate;

import devs.mrp.springturkey.Exceptions.TurkeySurpriseException;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class FieldData {

	@Getter
	@NotEmpty
	private String columnName;

	@NotNull
	private Predicate<String> predicate;

	private boolean canModify;

	private boolean canCreate;

	@Getter
	private Class<?> referenzable;

	public boolean isValidModification(String value) { // TODO change to generic thing as we can receive in the json numbers and other stuff
		return canModify && predicate.test(value);
	}

	public boolean isValidCreation(String value) { // TODO change to generic thing as we can receive in the json numbers and other stuff
		return canCreate && predicate.test(value);
	}

	public static FieldDataBuilder builder() {
		return new FieldDataBuilder();
	}

	public static class FieldDataBuilder {

		private String columnName;

		private Predicate<String> predicate;

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
			if (Objects.isNull(this.columnName) || Objects.isNull(this.predicate)) {
				throw new TurkeySurpriseException("No fields were expected to be null");
			}
			return new FieldData(this.columnName, this.predicate, this.canModify, this.canCreate, this.referenzable);
		}

	}

}
