package devs.mrp.springturkey.database.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import devs.mrp.springturkey.database.controller.dto.DeviceIdDto;
import devs.mrp.springturkey.database.controller.dto.UserMailDto;
import devs.mrp.springturkey.database.service.DeviceService;

@WebFluxTest(controllers = DeviceController.class)
class DeviceControllerTest {

	@MockBean
	private DeviceService deviceService;

	@Autowired
	private WebTestClient webClient;

	@Test
	@WithUserDetails(value = "basic@user.me")
	void testAddDevice() {
		UserMailDto mail = UserMailDto.builder().email("basic@user.me").build();

		DeviceIdDto expectedResult = DeviceIdDto.builder().id("someId").build();

		webClient.post().uri("/device/add")
		.body(BodyInserters.fromValue(mail))
		.exchange()
		.expectStatus().isEqualTo(HttpStatusCode.valueOf(201))
		.expectBody(DeviceIdDto.class)
		.isEqualTo(expectedResult);
	}

}
