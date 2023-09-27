package devs.mrp.springturkey.delta;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import devs.mrp.springturkey.delta.validation.FieldData;
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
	private String fieldName; // TODO remove this field
	@NotBlank
	private Map<String,Object> jsonValue; // TODO make it a Map<FieldName,Value>

	public Map<String,FieldData> getValidators() {
		return table.getFieldMap();
	}

	public FieldData getFieldData(String key) {
		return getValidators().get(key);
	}

	public Class<?> getEntityClass() {
		return table.getEntityClass();
	}

}
