package devs.mrp.springturkey.database.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.entity.User;
import devs.mrp.springturkey.database.service.DeviceService;
import devs.mrp.springturkey.database.service.UserService;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserDeviceFacadeImpl.class})
class UserDeviceFacadeImplTest {

	@MockBean
	private DeviceService deviceService;
	@MockBean
	private UserService userService;

	@Autowired
	private UserDeviceFacadeImpl userDeviceFacadeImpl;

	@Test
	@WithMockUser("some@user.me")
	@DirtiesContext
	void testAddDeviceSuccess() {
		User user = User.builder().email("some@user.me").id("userId").build();
		Device device = Device.builder()
				.id("generatedId")
				.user(user)
				.usageTime(123456L)
				.build();

		when(deviceService.addDevice()).thenReturn(Mono.just(device));
		when(userService.getUser()).thenReturn(Mono.just(user));
		when(userService.addCurrentUser()).thenReturn(Mono.just(user));

		Mono<Device> monoResult = userDeviceFacadeImpl.addDevice();
		Device deviceResult = monoResult.block();

		assertEquals("generatedId", deviceResult.getId());
		assertEquals(user, deviceResult.getUser());
		assertEquals(123456L, deviceResult.getUsageTime());

		verify(userService, times(0)).addCurrentUser();
	}

	@Test
	@WithMockUser("some@user.me")
	@DirtiesContext
	void testAddDeviceToNotSavedUserCreatesTheUser() {
		User user = User.builder().email("some@user.me").id("userId").build();
		Device device = Device.builder()
				.id("generatedId")
				.user(user)
				.usageTime(123456L)
				.build();

		when(deviceService.addDevice()).thenReturn(Mono.just(device));
		when(userService.getUser()).thenReturn(Mono.empty());
		when(userService.addCurrentUser()).thenReturn(Mono.just(user));

		Mono<Device> monoResult = userDeviceFacadeImpl.addDevice();
		Device deviceResult = monoResult.block();

		assertEquals("generatedId", deviceResult.getId());
		assertEquals(user, deviceResult.getUser());
		assertEquals(123456L, deviceResult.getUsageTime());

		verify(userService, times(1)).addCurrentUser();
	}

	@Test
	@DirtiesContext
	void testGetUserDevices() {
		fail("not yet implemented");
	}

	@Test
	@DirtiesContext
	void testGetUserDeviceById() {
		fail("not yet implemented");
	}

}
