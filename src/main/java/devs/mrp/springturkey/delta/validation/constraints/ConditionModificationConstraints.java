package devs.mrp.springturkey.delta.validation.constraints;

import java.time.Duration;
import java.util.UUID;

import devs.mrp.springturkey.validation.NullableMinConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConditionModificationConstraints {

	private UUID conditionalGroup;

	private UUID targetGroup;

	private Duration requiredUsageMs;

	@NullableMinConstraint(min = 0)
	private Integer lastDaysToConsider;

}
