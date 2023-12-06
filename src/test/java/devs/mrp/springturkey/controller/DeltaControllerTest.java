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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import devs.mrp.springturkey.configuration.SecurityConfig;
import devs.mrp.springturkey.controller.dto.DeltaRequestDto;
import devs.mrp.springturkey.database.service.DeltaServiceFacade;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.impl.CreationDataConstrainer;
import devs.mrp.springturkey.delta.validation.impl.DataPushConstrainerProviderImpl;
import devs.mrp.springturkey.delta.validation.impl.DeletionDataConstrainer;
import devs.mrp.springturkey.delta.validation.impl.ModificationDeltaFilterService;
import jakarta.validation.Validator;
import reactor.core.publisher.Flux;

@WebFluxTest(controllers = DeltaController.class)
@Import({SecurityConfig.class})
@ContextConfiguration(classes = {DataPushConstrainerProviderImpl.class, CreationDataConstrainer.class,
		DeletionDataConstrainer.class, ModificationDeltaFilterService.class})
class DeltaControllerTest {

	@Autowired
	private WebTestClient webClient;

	@MockBean
	private DeltaServiceFacade deltaFacade;

	@MockBean
	private Validator validator;

	@MockBean
	private ObjectMapper objectMapper;

	@Test
	@WithMockUser("some@user.me")
	void testPushDelta() throws JsonProcessingException {
		DeltaRequestDto delta = validDelta().build();

		webClient.post().uri("/deltas/push")
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(BodyInserters.fromPublisher(Flux.just(delta), DeltaRequestDto.class))
		.exchange()
		.expectStatus().isCreated()
		.expectBody(List.class)
		.consumeWith(result -> {
			List<Map<String,Object>> body = result.getResponseBody();
			Map<String,Object> savedDelta = body.get(0);
			assertEquals(delta.getRecordId().toString(), savedDelta.get("uuid"));
			assertEquals(true, savedDelta.get("success"));
		});
	}

	@Test
	@WithMockUser
	void testInvalidDataFails() {
		DeltaRequestDto delta = validDelta()
				.jsonValue(Map.of("platformType", "ALL", "key1", "invalid·$%·$", "key2", "invalid%&/(%&"))
				.build();

		webClient.post().uri("/deltas/push")
		.contentType(MediaType.APPLICATION_JSON)
		.body(BodyInserters.fromValue(List.of(delta)))
		.exchange()
		.expectStatus().is4xxClientError();
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
				.jsonValue(Map.of("platformType", "ALL", "settingKey", "setting", "settingValue", "value"));
	}

}
