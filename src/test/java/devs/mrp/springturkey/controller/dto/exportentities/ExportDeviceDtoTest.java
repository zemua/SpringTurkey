package devs.mrp.springturkey.controller.dto.exportentities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.entity.TurkeyUser;

class ExportDeviceDtoTest {

	@Test
	void testConvert() {
		TurkeyUser user = TurkeyUser.builder().externalId("some@mail.com").build();
		Device device1 = Device.builder().user(user).usageTime(111L).build();
		Device device2 = Device.builder().user(user).usageTime(222L).build();
		Device device3 = Device.builder().user(user).usageTime(333L).build();

		ExportDeviceDto dto = ExportDeviceDto.fromDevices(List.of(device1, device2, device3));

		assertEquals(666L, dto.getTotalUsage());
	}

	@Test
	void testEmptyList() {
		ExportDeviceDto dto = ExportDeviceDto.fromDevices(Collections.EMPTY_LIST);

		assertEquals(0L, dto.getTotalUsage());
	}

}
