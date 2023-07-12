package devs.mrp.springturkey.delta.validation;

import java.time.LocalDateTime;
import java.util.UUID;

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
public class ModificationDelta {

	// TODO add field deltaType and change class name to "Delta"

	private LocalDateTime timestamp;
	private Table table;
	private UUID recordId;
	private String fieldName;
	private String textValue;

}
