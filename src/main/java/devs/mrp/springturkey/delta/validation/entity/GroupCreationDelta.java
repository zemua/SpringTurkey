package devs.mrp.springturkey.delta.validation.entity;

import java.util.UUID;

import devs.mrp.springturkey.database.entity.enumerable.GroupType;
import devs.mrp.springturkey.validation.AlphaNumericSpaceConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class GroupCreationDelta {

	@NotNull
	private UUID id;

	@NotNull
	@AlphaNumericSpaceConstraint
	private String name;

	@NotNull
	private GroupType type;

	@NotNull
	private Boolean preventClose;

}
