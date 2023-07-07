package devs.mrp.springturkey.controller.dto.exportentities;

import devs.mrp.springturkey.database.entity.Setting;
import devs.mrp.springturkey.database.entity.enumerable.PlatformType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class ExportSettingDto {

	private PlatformType platformType;
	private String settingKey;
	private String settingValue;

	public static ExportSettingDto fromSetting(Setting setting) {
		ExportSettingDto dto = new ExportSettingDto();
		dto.platformType = setting.getPlatform();
		dto.settingKey = setting.getSettingKey();
		dto.settingValue = setting.getSettingValue();
		return dto;
	}

}
