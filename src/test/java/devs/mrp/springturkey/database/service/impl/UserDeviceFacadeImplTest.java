package devs.mrp.springturkey.database.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.entity.User;
import devs.mrp.springturkey.database.service.DeviceService;
import devs.mrp.springturkey.database.service.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
		User user = User.builder().id("userId").email("user@mail.me").build();
		Device first = Device.builder().id("firstId").user(user).usageTime(1234L).build();
		Device second = Device.builder().id("secondId").user(user).usageTime(4321L).build();;

		when(deviceService.getUserDevices()).thenReturn(Flux.just(first, second));

		Flux<Device> devicesFlux = deviceService.getUserDevices();

		StepVerifier.create(devicesFlux)
		.expectNext(first)
		.expectNext(second)
		.expectComplete()
		.verify();
	}

	@Test
	@DirtiesContext
	void testGetUserDeviceById() {
		User user = User.builder().id("userId").email("user@mail.me").build();
		Device first = Device.builder().id("firstId").user(user).usageTime(1234L).build();

		when(deviceService.getDeviceById(ArgumentMatchers.any())).thenReturn(Mono.just(first));

		Mono<Device> deviceMono = userDeviceFacadeImpl.getUserDeviceById(Mono.just("firstId"));

		StepVerifier.create(deviceMono)
		.expectNext(first)
		.expectComplete()
		.verify();

		ArgumentCaptor<Mono> deviceCaptor = ArgumentCaptor.forClass(Mono.class);
		verify(deviceService, times(1)).getDeviceById(deviceCaptor.capture());
		assertEquals("firstId", deviceCaptor.getValue().block());
	}

}
