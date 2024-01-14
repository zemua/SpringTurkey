package devs.mrp.springturkey.database.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TurkeyEntity {

	abstract void setId(UUID uuid);
	abstract void setDeleted(LocalDateTime dateTime);
	abstract LocalDateTime getDeleted();

}
