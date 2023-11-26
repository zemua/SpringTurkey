package devs.mrp.springturkey.controller.dto;

import java.util.List;

import devs.mrp.springturkey.controller.dto.exportentities.ExportActivityDto;
import devs.mrp.springturkey.controller.dto.exportentities.ExportConditionDto;
import devs.mrp.springturkey.controller.dto.exportentities.ExportDeviceDto;
import devs.mrp.springturkey.controller.dto.exportentities.ExportGroupDto;
import devs.mrp.springturkey.controller.dto.exportentities.ExportSettingDto;
import devs.mrp.springturkey.database.entity.dto.ExportData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@EqualsAndHashCode
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExportDataDto {

	private ExportDeviceDto otherDevices;
	private List<ExportActivityDto> activities;
	private List<ExportGroupDto> groups;
	private List<ExportConditionDto> conditions;
	private List<ExportSettingDto> settings;

	public static ExportDataDto from(ExportData data) {
		return ExportDataDto.builder()
				.otherDevices(ExportDeviceDto.fromDevices(data.getOtherDevices()))
				.activities(data.getActivities().stream().map(ExportActivityDto::fromActivity).toList())
				.groups(data.getGroups().stream().map(ExportGroupDto::fromGroup).toList())
				.conditions(data.getConditions().stream().map(ExportConditionDto::fromCondition).toList())
				.settings(data.getSettings().stream().map(ExportSettingDto::fromSetting).toList())
				.build();
	}

}
