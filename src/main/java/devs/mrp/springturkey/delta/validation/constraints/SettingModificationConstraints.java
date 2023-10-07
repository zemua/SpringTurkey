package devs.mrp.springturkey.delta.validation.constraints;

import devs.mrp.springturkey.database.entity.enumerable.PlatformType;
import devs.mrp.springturkey.validation.NullableAlphaNumericSpaceConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingModificationConstraints {

	private PlatformType platform;

	@NullableAlphaNumericSpaceConstraint
	private String settingKey;

	@NullableAlphaNumericSpaceConstraint
	private String settingValue;

}
