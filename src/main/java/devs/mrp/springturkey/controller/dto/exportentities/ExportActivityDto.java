package devs.mrp.springturkey.controller.dto.exportentities;

import java.util.UUID;

import devs.mrp.springturkey.database.entity.Activity;
import devs.mrp.springturkey.database.entity.enumerable.ActivityPlatform;
import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class ExportActivityDto {

	private String activityName;
	private ActivityPlatform activityType;
	private CategoryType categoryType;
	private UUID groupId;
	private Boolean preventClosing;

	public static ExportActivityDto fromActivity(Activity activity) {
		ExportActivityDto dto = new ExportActivityDto();
		dto.activityName = activity.getActivityName();
		dto.activityType = activity.getActivityType();
		dto.categoryType = activity.getCategoryType();
		dto.groupId = activity.getGroup() != null ? activity.getGroup().getId() : null;
		dto.preventClosing = activity.getPreventClosing();
		return dto;
	}

}
