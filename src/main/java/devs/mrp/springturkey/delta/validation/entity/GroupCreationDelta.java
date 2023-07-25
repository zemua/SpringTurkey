package devs.mrp.springturkey.delta.validation.entity;

import java.util.UUID;

import devs.mrp.springturkey.database.entity.enumerable.GroupType;
import devs.mrp.springturkey.validation.AlphaNumericSpaceConstraint;

public class GroupCreationDelta {

	private UUID id;

	@AlphaNumericSpaceConstraint
	private String name;

	private GroupType type;

	private Boolean preventClose;

}
