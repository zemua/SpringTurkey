package devs.mrp.springturkey.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import devs.mrp.springturkey.configuration.SecurityConfig;
import devs.mrp.springturkey.controller.dto.DeltaRequestDto;
import devs.mrp.springturkey.controller.dto.PushDeltaResponseDto;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;

@WebFluxTest(controllers = DeltaController.class)
@Import({SecurityConfig.class})
class DeltaControllerTest {

	@Autowired
	private WebTestClient webClient;

	@Test
	@WithMockUser("some@user.me")
	void testUserExistsResponse() {
		DeltaRequestDto delta = validDelta().build();

		webClient.post().uri("/deltas/push")
		.contentType(MediaType.APPLICATION_JSON)
		.body(BodyInserters.fromValue(List.of(delta)))
		.exchange()
		.expectStatus().isCreated()
		.expectBody(List.class)
		.consumeWith(result -> {
			// TODO compare jsonified dto with result
			assertEquals(PushDeltaResponseDto.builder()
					.uuid(delta.getRecordId())
					.success(true)
					.build(),
					result.getResponseBody().get(0));
		});
	}

	@Test
	@WithMockUser
	void testFails() {
		fail("to be implemented");
	}

	@Test
	@WithMockUser
	void testMultiple() {
		fail("to be implemented");
	}

	private DeltaRequestDto.DeltaRequestDtoBuilder validDelta() {
		return DeltaRequestDto.builder()
				.timestamp(LocalDateTime.now())
				.deltaType(DeltaType.CREATION)
				.table(DeltaTable.SETTING)
				.recordId(UUID.randomUUID())
				.jsonValue(Map.of("key1", "value1", "key2", "value2"));
	}

}
