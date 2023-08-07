package devs.mrp.springturkey.delta.validation.entity;

import devs.mrp.springturkey.database.entity.enumerable.GroupType;
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
public class GroupCreationDelta {

	@NotBlank
	@AlphaNumericSpaceConstraint
	private String name;

	@NotNull
	private GroupType type;

	private Boolean preventClose;

}
