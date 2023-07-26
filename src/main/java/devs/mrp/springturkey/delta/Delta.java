package devs.mrp.springturkey.delta;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

@Getter
@Builder
@With
@AllArgsConstructor
@NoArgsConstructor
public class Delta {

	@NotNull
	private LocalDateTime timestamp;
	@NotNull
	private DeltaType deltaType;
	@NotNull
	private DeltaTable table;
	@NotNull // TODO see how to handle for creation
	private UUID recordId;
	@NotBlank
	private String fieldName;
	@NotBlank
	private String textValue;

}
