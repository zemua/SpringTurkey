package devs.mrp.springturkey.delta.validation.entity;

import devs.mrp.springturkey.database.entity.enumerable.RandomBlockType;
import devs.mrp.springturkey.validation.AlphaNumericSpaceConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class RandomQuestionCreationDelta {

	@NotNull
	private RandomBlockType type;

	@NotBlank
	@AlphaNumericSpaceConstraint
	private String name;

	@NotBlank
	private String question;

	@Min(value = 1)
	@Max(value = 10)
	private Integer frequency;

	@Min(value = 1)
	@Max(value = 10)
	private Integer multiplier;

}
