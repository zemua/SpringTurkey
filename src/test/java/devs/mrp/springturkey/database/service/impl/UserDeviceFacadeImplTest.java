package devs.mrp.springturkey.database.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
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

	private UUID userId = UUID.randomUUID();
	private UUID generatedId = UUID.randomUUID();

	@Test
	@DirtiesContext
	@WithMockUser("some@user.me")
	void testAddDeviceSuccess() {
		User user = User.builder().email("some@user.me").id(userId).build();
		Device device = Device.builder()
				.id(generatedId)
				.user(user)
				.usageTime(123456L)
				.build();

		when(deviceService.addDevice(ArgumentMatchers.refEq(user))).thenReturn(Mono.just(device));
		when(userService.getUser()).thenReturn(Mono.just(user));
		when(userService.addCurrentUser()).thenReturn(Mono.just(user));

		Mono<Device> monoResult = userDeviceFacadeImpl.addDevice();
		Device deviceResult = monoResult.block();

		assertEquals(generatedId, deviceResult.getId());
		assertEquals(user, deviceResult.getUser());
		assertEquals(123456L, deviceResult.getUsageTime());

		verify(userService, times(0)).addCurrentUser();
	}

	@Test
	@DirtiesContext
	@WithMockUser("some@user.me")
	void testAddDeviceToNotSavedUserCreatesTheUser() {
		User user = User.builder().email("some@user.me").id(userId).build();
		Device device = Device.builder()
				.id(generatedId)
				.user(user)
				.usageTime(123456L)
				.build();

		when(deviceService.addDevice(ArgumentMatchers.refEq(user))).thenReturn(Mono.just(device));
		when(userService.getUser()).thenReturn(Mono.empty());
		when(userService.addCurrentUser()).thenReturn(Mono.just(user));

		Mono<Device> monoResult = userDeviceFacadeImpl.addDevice();
		Device deviceResult = monoResult.block();

		assertEquals(generatedId, deviceResult.getId());
		assertEquals(user, deviceResult.getUser());
		assertEquals(123456L, deviceResult.getUsageTime());

		verify(userService, times(1)).addCurrentUser();
	}

	@Test
	@DirtiesContext
	@WithMockUser("some@user.me")
	void testGetUserDevices() {
		UUID firstId = UUID.randomUUID();
		UUID secondId = UUID.randomUUID();
		User user = User.builder().id(userId).email("user@mail.me").build();
		Device first = Device.builder().id(firstId).user(user).usageTime(1234L).build();
		Device second = Device.builder().id(secondId).user(user).usageTime(4321L).build();;

		when(deviceService.getUserDevices(ArgumentMatchers.refEq(user))).thenReturn(Flux.just(first, second));

		Flux<Device> devicesFlux = deviceService.getUserDevices(user);

		StepVerifier.create(devicesFlux)
		.expectNext(first)
		.expectNext(second)
		.expectComplete()
		.verify();
	}

	@Test
	@DirtiesContext
	@WithMockUser("some@user.me")
	void testGetUserDeviceById() {
		UUID firstId = UUID.randomUUID();
		User user = User.builder().id(userId).email("user@mail.me").build();
		Device first = Device.builder().id(firstId).user(user).usageTime(1234L).build();

		when(deviceService.getDeviceById(ArgumentMatchers.any())).thenReturn(Mono.just(first));

		Mono<Device> deviceMono = userDeviceFacadeImpl.getUserDeviceById(Mono.just(firstId));

		StepVerifier.create(deviceMono)
		.expectNext(first)
		.expectComplete()
		.verify();

		ArgumentCaptor<UUID> deviceCaptor = ArgumentCaptor.forClass(UUID.class);
		verify(deviceService, times(1)).getDeviceById(deviceCaptor.capture());
		assertEquals(firstId, deviceCaptor.getValue());
	}

}
