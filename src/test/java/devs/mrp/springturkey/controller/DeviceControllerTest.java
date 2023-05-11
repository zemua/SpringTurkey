package devs.mrp.springturkey.controller;

import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import devs.mrp.springturkey.configuration.SecurityConfig;
import devs.mrp.springturkey.controller.dto.DeviceIdDto;
import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.service.UserDeviceFacade;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = DeviceController.class)
@Import({SecurityConfig.class})
@ActiveProfiles("debug")
class DeviceControllerTest {

	@MockBean
	private UserDeviceFacade deviceService;
	@MockBean
	private ReactiveJwtDecoder jwtDecoder;

	@Autowired
	private WebTestClient webClient;

	@Test
	@WithMockUser(username = "basic@user.me", password = "password", roles = "USER")
	void testAddDeviceSuccess() {
		UUID uuid = UUID.randomUUID();
		DeviceIdDto expectedResult = DeviceIdDto.builder().id(uuid).build();
		when(deviceService.addDevice()).thenReturn(Mono.just(Device.builder().id(uuid).build()));

		webClient.post().uri("/device/add")
		.exchange()
		.expectStatus().isEqualTo(HttpStatusCode.valueOf(201))
		.expectBody(DeviceIdDto.class)
		.isEqualTo(expectedResult);
	}

	@Test
	@WithMockUser(username = "basic@user.me", password = "password", roles = "USER")
	void testExceptionOnDeviceServiceError() {
		when(deviceService.addDevice()).thenReturn(Mono.error(() -> new Exception()));

		webClient.post().uri("/device/add")
		.exchange()
		.expectStatus().is5xxServerError();
	}

}
