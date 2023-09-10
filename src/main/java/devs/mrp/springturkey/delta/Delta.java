package devs.mrp.springturkey.delta;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import devs.mrp.springturkey.delta.validation.FieldValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

@Getter
@Builder
@With
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Delta {

	@NotNull
	private LocalDateTime timestamp;
	@NotNull
	private DeltaType deltaType;
	@NotNull
	private DeltaTable table;
	@NotNull
	private UUID recordId;
	@NotBlank
	private String fieldName;
	@NotBlank
	private String textValue;

	public Map<String,FieldValidator> getValidators() {
		return table.getFieldMap();
	}

	public FieldValidator getValidator(String key) {
		return getValidators().get(key);
	}

	public Class<?> getEntityClass() {
		return table.getEntityClass();
	}

}
