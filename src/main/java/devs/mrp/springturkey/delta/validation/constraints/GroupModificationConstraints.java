package devs.mrp.springturkey.delta.validation.constraints;

import devs.mrp.springturkey.database.entity.enumerable.GroupType;
import devs.mrp.springturkey.validation.NullableAlphaNumericSpaceConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupModificationConstraints {

	@NullableAlphaNumericSpaceConstraint
	private String name;

	private GroupType type;

	private Boolean preventClose;

}
