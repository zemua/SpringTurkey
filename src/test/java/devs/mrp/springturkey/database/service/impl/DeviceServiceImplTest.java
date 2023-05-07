package devs.mrp.springturkey.database.service.impl;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.springturkey.components.impl.LoginDetailsReaderImpl;
import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.entity.User;
import devs.mrp.springturkey.database.repository.DeviceRepository;
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
		User user = User.builder().build();
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
		fail("Not yet implemented");
	}

	@Test
	void testGetDeviceById() {
		fail("Not yet implemented");
	}

	@Test
	void testGetDeviceByIdThatDoesNotBelongToCurrentUser() {
		fail("Not yet implemented");
	}

}
