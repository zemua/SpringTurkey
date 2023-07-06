package devs.mrp.springturkey.controller.dto.deltas;

import java.time.LocalDateTime;
import java.util.UUID;

public class ModificationDeltaDto {

	private LocalDateTime timestamp;
	private Table table;
	private UUID recordId;
	private String field;
	private String value;

}
