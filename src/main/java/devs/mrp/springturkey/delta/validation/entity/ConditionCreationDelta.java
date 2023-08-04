package devs.mrp.springturkey.delta.validation.entity;

import java.util.UUID;

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
	@Min(60000L) // 1 minute
	private Long requiredUsageMs;

	@NotNull
	@Min(0)
	@Max(30)
	private Integer lastDaysToConsider;

}
