package devs.mrp.springturkey.database.controller;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import devs.mrp.springturkey.configuration.SecurityConfig;
import devs.mrp.springturkey.database.controller.dto.DeviceIdDto;
import devs.mrp.springturkey.database.service.DeviceService;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = DeviceController.class)
@Import({SecurityConfig.class})
@ActiveProfiles("debug")
class DeviceControllerTest {

	@MockBean
	private DeviceService deviceService;

	@Autowired
	private WebTestClient webClient;

	@Test
	@WithMockUser(username = "basic@user.me", password = "password", roles = "USER")
	void testAddDeviceSuccess() {
		DeviceIdDto expectedResult = DeviceIdDto.builder().id("someDeviceId").build();
		when(deviceService.addDevice(ArgumentMatchers.any())).thenReturn(Mono.just("someDeviceId"));

		webClient.post().uri("/device/add")
		.exchange()
		.expectStatus().isEqualTo(HttpStatusCode.valueOf(201))
		.expectBody(DeviceIdDto.class)
		.isEqualTo(expectedResult);
	}

	@Test
	@WithMockUser(username = "basic@user.me", password = "password", roles = "USER")
	void testExceptionOnDeviceServiceError() {
		DeviceIdDto expectedResult = DeviceIdDto.builder().id("someDeviceId").build();
		when(deviceService.addDevice(ArgumentMatchers.any())).thenReturn(Mono.error(() -> new Exception())); // TODO set a more specific exception

		webClient.post().uri("/device/add")
		.exchange()
		.expectStatus().is5xxServerError();
		fail("not yet implemented");
	}

}
