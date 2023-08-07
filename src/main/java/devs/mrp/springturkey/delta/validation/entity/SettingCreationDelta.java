package devs.mrp.springturkey.delta.validation.entity;

import devs.mrp.springturkey.database.entity.enumerable.PlatformType;
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
public class SettingCreationDelta {

	@NotNull
	private PlatformType platformType;

	@NotBlank
	@AlphaNumericSpaceConstraint
	private String settingKey;

	@NotBlank
	@AlphaNumericSpaceConstraint
	private String settingValue;

}
