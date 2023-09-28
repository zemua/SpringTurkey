package devs.mrp.springturkey.delta.validation.constraints;

import java.time.LocalTime;
import java.util.UUID;

import devs.mrp.springturkey.validation.NullableMinConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConditionModificationConstraints {

	private UUID conditionalGroup;

	private UUID targetGroup;

	private LocalTime requiredUsageMs;

	@NullableMinConstraint(min = 0)
	private Integer lastDaysToConsider;

}
