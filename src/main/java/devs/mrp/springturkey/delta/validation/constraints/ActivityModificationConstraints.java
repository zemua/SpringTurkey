package devs.mrp.springturkey.delta.validation.constraints;

import java.util.UUID;

import devs.mrp.springturkey.database.entity.enumerable.ActivityPlatform;
import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
import devs.mrp.springturkey.validation.NullableAlphaNumericSpaceConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityModificationConstraints {

	@NullableAlphaNumericSpaceConstraint
	private String activityName;

	private ActivityPlatform activityType;

	private CategoryType categoryType;

	private UUID group;

	private Boolean preventClosing;

}
