package devs.mrp.springturkey.database.service.impl;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import devs.mrp.springturkey.Exceptions.DoesNotBelongToUserException;
import devs.mrp.springturkey.components.impl.LoginDetailsReaderImpl;
import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.entity.enumerable.DeviceType;
import devs.mrp.springturkey.database.repository.DeviceRepository;
import devs.mrp.springturkey.database.repository.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataJpaTest
@EnableJpaRepositories(basePackages = "devs.mrp.springturkey.database.repository")
@EnableJpaAuditing
@EntityScan("devs.mrp.springturkey.database.*")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {LoginDetailsReaderImpl.class, DeviceServiceImpl.class})
class DeviceServiceImplTest {

	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DeviceServiceImpl deviceServiceImpl;

	@Test
	@WithMockUser("some@user.me")
	void testAddDevice() {
		TurkeyUser user = TurkeyUser.builder().email("some@user.me").build();
		TurkeyUser userResult = userRepository.save(user);
		Device expectedDevice = Device.builder().user(user).usageTime(0L).build();

		Mono<Device> monoDevice = deviceServiceImpl.addDevice();

		StepVerifier.create(monoDevice)
		.expectNextMatches(device -> device.getUser().getId().equals(userResult.getId()) && device.getCreated() != null)
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("some@mail.com")
	void testGetUserDevices() {
		TurkeyUser user = TurkeyUser.builder().email("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);
		Device deviceOne = Device.builder().user(user).usageTime(1234L).deviceType(DeviceType.ANDROID).build();
		Device deviceTwo = Device.builder().user(user).usageTime(2234L).deviceType(DeviceType.IOS).build();
		Device deviceThree = Device.builder().user(user).usageTime(3234L).deviceType(DeviceType.LINUX).build();
		deviceRepository.save(deviceOne);
		deviceRepository.save(deviceTwo);
		deviceRepository.save(deviceThree);

		Flux<Device> fluxDevice = deviceServiceImpl.getUserDevices();

		StepVerifier.create(fluxDevice)
		.expectNextMatches(device -> device.getUser().getId().equals(userResult.getId()) && device.getUsageTime().equals(1234L) && device.getCreated() != null)
		.expectNextMatches(device -> device.getUser().getId().equals(userResult.getId()) && device.getUsageTime().equals(2234L))
		.expectNextMatches(device -> device.getUser().getId().equals(userResult.getId()) && device.getUsageTime().equals(3234L))
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("wronguser@mail.com")
	void testGetUserDevicesWithWrongUser() {
		TurkeyUser user = TurkeyUser.builder().email("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);
		Device deviceOne = Device.builder().user(user).usageTime(1234L).deviceType(DeviceType.ANDROID).build();
		Device deviceTwo = Device.builder().user(user).usageTime(2234L).deviceType(DeviceType.IOS).build();
		Device deviceThree = Device.builder().user(user).usageTime(3234L).deviceType(DeviceType.LINUX).build();
		deviceRepository.save(deviceOne);
		deviceRepository.save(deviceTwo);
		deviceRepository.save(deviceThree);

		Flux<Device> fluxDevice = deviceServiceImpl.getUserDevices();

		StepVerifier.create(fluxDevice)
		.verifyComplete();
	}

	@Test
	@WithMockUser("some@mail.com")
	void testGetOtherDevices() {
		TurkeyUser user = TurkeyUser.builder().email("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);
		Device deviceOne = Device.builder().user(user).usageTime(1234L).deviceType(DeviceType.ANDROID).build();
		Device deviceTwo = Device.builder().user(user).usageTime(2234L).deviceType(DeviceType.IOS).build();
		Device deviceThree = Device.builder().user(user).usageTime(3234L).deviceType(DeviceType.LINUX).build();
		deviceRepository.save(deviceOne);
		UUID idTwo = deviceRepository.save(deviceTwo).getId();
		deviceRepository.save(deviceThree);

		Device filteringDevice = Device.builder().user(user).usageTime(2234L).deviceType(DeviceType.IOS).id(idTwo).build();

		Flux<Device> fluxDevice = deviceServiceImpl.getUserOtherDevices(filteringDevice.getId());

		StepVerifier.create(fluxDevice)
		.expectNextMatches(device -> device.getUser().getId().equals(userResult.getId()) && device.getUsageTime().equals(1234L))
		.expectNextMatches(device -> device.getUser().getId().equals(userResult.getId()) && device.getUsageTime().equals(3234L))
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("wronguser@mail.com")
	void testGetOtherDevicesWithWrongUser() {
		TurkeyUser user = TurkeyUser.builder().email("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);
		Device deviceOne = Device.builder().user(user).usageTime(1234L).deviceType(DeviceType.ANDROID).build();
		Device deviceTwo = Device.builder().user(user).usageTime(2234L).deviceType(DeviceType.IOS).build();
		Device deviceThree = Device.builder().user(user).usageTime(3234L).deviceType(DeviceType.LINUX).build();
		deviceRepository.save(deviceOne);
		UUID idTwo = deviceRepository.save(deviceTwo).getId();
		deviceRepository.save(deviceThree);

		Device filteringDevice = Device.builder().user(user).usageTime(2234L).deviceType(DeviceType.IOS).id(idTwo).build();

		Flux<Device> fluxDevice = deviceServiceImpl.getUserOtherDevices(filteringDevice.getId());

		StepVerifier.create(fluxDevice)
		.expectError(DoesNotBelongToUserException.class)
		.verify();
	}

	@Test
	@WithMockUser("some@mail.com")
	void testGetDeviceById() {
		TurkeyUser user = TurkeyUser.builder().email("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);
		Device device = Device.builder().user(user).usageTime(1234L).deviceType(DeviceType.ANDROID).build();
		Device savedDevice = deviceRepository.save(device);

		Mono<Device> monoDevice = deviceServiceImpl.getDeviceById(savedDevice.getId());

		StepVerifier.create(monoDevice)
		.expectNextMatches(d -> d.getUser().getId().equals(userResult.getId()))
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("another@user.me")
	void testGetDeviceByIdThatDoesNotBelongToCurrentUser() {
		TurkeyUser user = TurkeyUser.builder().email("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);
		Device device = Device.builder().user(user).usageTime(1234L).deviceType(DeviceType.ANDROID).build();
		Device savedDevice = deviceRepository.save(device);

		Mono<Device> monoDevice = deviceServiceImpl.getDeviceById(savedDevice.getId());

		StepVerifier.create(monoDevice)
		.expectError(DoesNotBelongToUserException.class)
		.verify();
	}

}
