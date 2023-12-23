package devs.mrp.springturkey.delta.validation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class FieldData {

	@Getter
	@NotBlank
	private String columnName;

	@Getter
	private Class<?> referenzable;

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
			return new FieldData(this.columnName, this.referenzable);
		}

	}

}
