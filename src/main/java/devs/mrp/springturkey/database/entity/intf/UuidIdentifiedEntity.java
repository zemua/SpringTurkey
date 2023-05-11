package devs.mrp.springturkey.database.entity.intf;

import java.util.UUID;

public interface UuidIdentifiedEntity {

	public void setId(UUID id);
	public UUID getId();

}
