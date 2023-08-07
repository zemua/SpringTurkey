package devs.mrp.springturkey.delta.validation.entity;

import java.util.UUID;

import devs.mrp.springturkey.database.entity.enumerable.ActivityPlatform;
import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
import devs.mrp.springturkey.validation.AlphaNumericSpaceConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class ActivityCreationDelta {

	@NotBlank
	@AlphaNumericSpaceConstraint
	private String activityName;

	@NotNull
	private ActivityPlatform activityType;

	@NotNull
	private CategoryType categoryType;

	private UUID groupId;

	private Boolean preventClose;

}
