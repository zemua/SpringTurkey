package devs.mrp.springturkey.database.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TurkeyEntity {

	void setId(UUID uuid);
	void setDeleted(LocalDateTime dateTime);

}
