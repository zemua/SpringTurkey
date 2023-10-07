package devs.mrp.springturkey.delta.validation.constraints;

import devs.mrp.springturkey.database.entity.enumerable.RandomBlockType;
import devs.mrp.springturkey.validation.NullableAlphaNumericSpaceConstraint;
import devs.mrp.springturkey.validation.NullableMinMaxConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RandomQuestionModificationConstraints {

	private RandomBlockType type;

	@NullableAlphaNumericSpaceConstraint
	private String name;

	private String question;

	@NullableMinMaxConstraint(min = 1, max = 10)
	private Integer frequency;

	@NullableMinMaxConstraint(min = 1, max = 10)
	private Integer multiplier;

}
