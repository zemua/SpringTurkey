package devs.mrp.springturkey.controller.dto.exportentities;

import java.util.List;

import devs.mrp.springturkey.database.entity.Device;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class ExportDeviceDto {

	private long totalUsage;

	public static ExportDeviceDto fromDevices(List<Device> devices) {
		ExportDeviceDto deviceDto = new ExportDeviceDto();
		deviceDto.totalUsage = devices.stream().mapToLong(Device::getUsageTime).sum();
		return deviceDto;
	}

}
