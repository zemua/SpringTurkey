package devs.mrp.springturkey.controller.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class DeltaRequestDto {

	@NotNull
	@JsonProperty("timestamp")
	private LocalDateTime timestamp;
	@NotNull
	@JsonProperty("deltaType")
	private DeltaType deltaType;
	@NotNull
	@JsonProperty("table")
	private DeltaTable table;
	@NotNull
	@JsonProperty("recordId")
	private UUID recordId;
	@NotEmpty
	@JsonProperty("jsonValue")
	private Map<String,Object> jsonValue;

	public Delta toDelta() {
		return Delta.builder()
				.timestamp(timestamp)
				.deltaType(deltaType)
				.table(table)
				.recordId(recordId)
				.build();
	}

}
