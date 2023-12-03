package devs.mrp.springturkey.controller.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DeltaRequestDto {

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

}
