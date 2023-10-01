package devs.mrp.springturkey.controller.dto.exportentities;

import java.time.LocalTime;
import java.util.UUID;

import devs.mrp.springturkey.database.entity.Condition;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class ExportConditionDto {

	private UUID conditionalGroupId;
	private UUID targetGroupId;
	private LocalTime requiredUsageMs;
	private Integer lastDaysToConsider;

	public static ExportConditionDto fromCondition(Condition condition) {
		ExportConditionDto dto = new ExportConditionDto();
		dto.conditionalGroupId = condition.getConditionalGroup().getId();
		dto.targetGroupId = condition.getTargetGroup().getId();
		dto.requiredUsageMs = condition.getRequiredUsageMs();
		dto.lastDaysToConsider = condition.getLastDaysToConsider();
		return dto;
	}

}
