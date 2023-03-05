package devs.mrp.springturkey.database.device;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.groocraft.couchdb.slacker.DocumentBase;
import com.groocraft.couchdb.slacker.annotation.Document;

@Document("device")
public class Device extends DocumentBase {

	@JsonProperty("user")
	private String user;

	@JsonProperty("uuids")
	private List<UUID> uuid;

}
