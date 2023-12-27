package devs.mrp.springturkey.database.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TurkeyEntity {

	UUID getId();
	LocalDateTime getDeleted();

}
