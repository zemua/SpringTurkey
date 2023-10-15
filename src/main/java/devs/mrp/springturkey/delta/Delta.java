package devs.mrp.springturkey.delta;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import devs.mrp.springturkey.database.entity.DeltaEntity;
import devs.mrp.springturkey.delta.validation.FieldData;
import jakarta.validation.constraints.NotEmpty;
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

	@NotEmpty
	private Map<String,Object> jsonValue;

	public Map<String,FieldData> getValidators() {
		return table.getFieldMap();
	}

	public FieldData getFieldData(String key) {
		return getValidators().get(key);
	}

	public Class<?> getEntityClass() {
		return table.getEntityClass();
	}

	public DeltaEntity toEntity() throws JsonProcessingException {
		return DeltaEntity.builder()
				.deltaTimeStamp(this.getTimestamp())
				.deltaType(this.getDeltaType())
				.deltaTable(this.getTable())
				.recordId(this.getRecordId())
				.jsonValue(serializeJson())
				.build();
	}

	// TODO avoid creation of new object Mapper
	private String serializeJson() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		return objectMapper.writeValueAsString(this.jsonValue);
	}

	// TODO avoid creation of new object mapper
	private Map<String,Object> deserializeJson(String json) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		return objectMapper.readValue(json, Map.class);
	}

}
