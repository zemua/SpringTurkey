package devs.mrp.springturkey.database.service.impl;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.springturkey.Exceptions.DoesNotBelongToUserException;
import devs.mrp.springturkey.components.impl.LoginDetailsReaderImpl;
import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.entity.User;
import devs.mrp.springturkey.database.repository.DeviceRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LoginDetailsReaderImpl.class, DeviceServiceImpl.class})
class DeviceServiceImplTest {

	@MockBean
	private DeviceRepository deviceRepository;

	@Autowired
	private DeviceServiceImpl deviceServiceImpl;

	@Test
	@WithMockUser("some@user.me")
	void testAddDevice() {
		User user = User.builder().email("some@mail.com").build();
		Device deviceIn = Device.builder().user(user).usageTime(0L).build();
		Device deviceOut = Device.builder().user(user).usageTime(0L).id("devideId").build();

		when(deviceRepository.save(ArgumentMatchers.refEq(deviceIn))).thenReturn(Mono.just(deviceOut));

		Mono<Device> monoDevice = deviceServiceImpl.addDevice(user);

		StepVerifier.create(monoDevice)
		.expectNext(deviceOut)
		.expectComplete()
		.verify();
	}

	@Test
	void testGetUserDevices() {
		User user = User.builder().email("some@mail.com").build();
		Device deviceOne = Device.builder().user(user).usageTime(1234L).id("deviceOne").build();
		Device deviceTwo = Device.builder().user(user).usageTime(2234L).id("deviceTwo").build();
		Device deviceThree = Device.builder().user(user).usageTime(3234L).id("deviceThree").build();

		when(deviceRepository.findAllByUser(user)).thenReturn(Flux.just(deviceOne, deviceTwo, deviceThree));

		Flux<Device> fluxDevice = deviceServiceImpl.getUserDevices(user);

		StepVerifier.create(fluxDevice)
		.expectNext(deviceOne)
		.expectNext(deviceTwo)
		.expectNext(deviceThree)
		.expectComplete()
		.verify();
	}

	@Test
	void testGetOtherDevices() {
		User user = User.builder().email("some@mail.com").build();
		Device deviceOne = Device.builder().user(user).usageTime(1234L).id("deviceOne").build();
		Device deviceTwo = Device.builder().user(user).usageTime(2234L).id("deviceTwo").build();
		Device deviceThree = Device.builder().user(user).usageTime(3234L).id("deviceThree").build();

		when(deviceRepository.findAllByUser(user)).thenReturn(Flux.just(deviceOne, deviceTwo, deviceThree));

		Flux<Device> fluxDevice = deviceServiceImpl.getUserOtherDevices(user, deviceTwo);

		StepVerifier.create(fluxDevice)
		.expectNext(deviceOne)
		.expectNext(deviceThree)
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("some@mail.com")
	void testGetDeviceById() {
		User user = User.builder().email("some@mail.com").build();
		Device device = Device.builder().user(user).usageTime(1234L).id("deviceId").build();

		when(deviceRepository.findById(device.getId())).thenReturn(Mono.just(device));

		Mono<Device> monoDevice = deviceServiceImpl.getDeviceById("deviceId");

		StepVerifier.create(monoDevice)
		.expectNext(device)
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("another@user.me")
	void testGetDeviceByIdThatDoesNotBelongToCurrentUser() {
		User user = User.builder().email("some@mail.com").build();
		Device device = Device.builder().user(user).usageTime(1234L).id("deviceId").build();

		when(deviceRepository.findById(device.getId())).thenReturn(Mono.just(device));

		Mono<Device> monoDevice = deviceServiceImpl.getDeviceById("deviceId");

		StepVerifier.create(monoDevice)
		.expectError(DoesNotBelongToUserException.class)
		.verify();
	}

}
