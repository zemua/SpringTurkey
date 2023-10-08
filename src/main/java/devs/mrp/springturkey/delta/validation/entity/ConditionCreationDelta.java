package devs.mrp.springturkey.delta.validation.entity;

import java.time.Duration;
import java.util.UUID;

import devs.mrp.springturkey.validation.MinTimeConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class ConditionCreationDelta {

	@NotNull
	private UUID conditionalGroup;

	@NotNull
	private UUID targetGroup;

	@NotNull
	@MinTimeConstraint(minutes = 1)
	private Duration requiredUsageMs;

	@NotNull
	@Min(0)
	@Max(30)
	private Integer lastDaysToConsider;

}
