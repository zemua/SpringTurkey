package devs.mrp.springturkey.delta.validation.constraints;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConditionModificationConstraints {

	private UUID conditionalGroup;

	private UUID targetGroup;

	private Long requiredUsageMs;

	private Integer lastDaysToConsider;

}
