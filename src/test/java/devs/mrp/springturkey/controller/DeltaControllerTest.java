package devs.mrp.springturkey.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.fasterxml.jackson.core.JsonProcessingException;

import devs.mrp.springturkey.configuration.SecurityConfig;
import devs.mrp.springturkey.controller.dto.DeltaRequestDto;
import devs.mrp.springturkey.database.service.DeltaServiceFacade;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.impl.CreationDataConstrainer;
import devs.mrp.springturkey.delta.validation.impl.DataPushConstrainerProviderImpl;
import devs.mrp.springturkey.delta.validation.impl.DeletionDataConstrainer;
import devs.mrp.springturkey.delta.validation.impl.ModificationDeltaFilterService;
import devs.mrp.springturkey.utils.impl.ObjectMapperProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = DeltaController.class)
@Import({SecurityConfig.class, ObjectMapperProvider.class})
@ContextConfiguration(classes = {DeltaController.class, DataPushConstrainerProviderImpl.class, CreationDataConstrainer.class,
		DeletionDataConstrainer.class, ModificationDeltaFilterService.class})
class DeltaControllerTest {

	@Autowired
	private WebTestClient webClient;

	@MockBean
	private DeltaServiceFacade deltaFacade;

	@MockBean
	private ReactiveJwtDecoder jwtDecoder;

	static {
		System.setProperty("logging.level.devs.mrp.springturkey", "DEBUG");
	}

	@BeforeEach
	void setup() {
		when(deltaFacade.pushCreation(any())).thenReturn(Mono.just(1));
		when(deltaFacade.pushModification(any())).thenReturn(Mono.just(1));
	}

	@Test
	@WithMockUser("some@user.me")
	void testPushCreationDeltaSuccess() throws JsonProcessingException {
		DeltaRequestDto delta = validCreationDelta().build();

		webClient.post().uri("/deltas/push")
		.contentType(MediaType.APPLICATION_JSON)
		.body(BodyInserters.fromPublisher(Flux.just(delta), DeltaRequestDto.class))
		.exchange()
		.expectStatus().is2xxSuccessful()
		.expectBody(List.class)
		.consumeWith(result -> {
			List<Map<String,Object>> body = result.getResponseBody();
			Map<String,Object> savedDelta = body.get(0);
			assertEquals(delta.getRecordId().toString(), savedDelta.get("uuid"));
			assertEquals(true, savedDelta.get("success"));
		});
	}

	@Test
	@WithMockUser("some@user.me")
	void testPushModificationDeltaSuccess() throws JsonProcessingException {
		DeltaRequestDto delta = validModificationDelta().build();

		webClient.post().uri("/deltas/push")
		.contentType(MediaType.APPLICATION_JSON)
		.body(BodyInserters.fromPublisher(Flux.just(delta), DeltaRequestDto.class))
		.exchange()
		.expectStatus().is2xxSuccessful()
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
	void testInvalidCreationDataFails() {
		DeltaRequestDto delta = validCreationDelta()
				.jsonValue(Map.of("platformType", "ALL", "settingKey", "invalid·$%·$", "settingValue", "invalid%&/(%&"))
				.build();

		webClient.post().uri("/deltas/push")
		.contentType(MediaType.APPLICATION_JSON)
		.body(BodyInserters.fromValue(List.of(delta)))
		.exchange()
		.expectStatus().is2xxSuccessful()
		.expectBody(List.class)
		.consumeWith(result -> {
			List<Map<String,Object>> body = result.getResponseBody();
			Map<String,Object> savedDelta = body.get(0);
			assertEquals(delta.getRecordId().toString(), savedDelta.get("uuid"));
			assertEquals(false, savedDelta.get("success"));
		});
	}

	@Test
	@WithMockUser
	void testInvalidModificationDataFails() {
		DeltaRequestDto delta = validModificationDelta()
				.jsonValue(Map.of("settingValue", "invalid%&/(%&"))
				.build();

		webClient.post().uri("/deltas/push")
		.contentType(MediaType.APPLICATION_JSON)
		.body(BodyInserters.fromValue(List.of(delta)))
		.exchange()
		.expectStatus().is2xxSuccessful()
		.expectBody(List.class)
		.consumeWith(result -> {
			List<Map<String,Object>> body = result.getResponseBody();
			Map<String,Object> savedDelta = body.get(0);
			assertEquals(delta.getRecordId().toString(), savedDelta.get("uuid"));
			assertEquals(false, savedDelta.get("success"));
		});
	}

	@Test
	@WithMockUser
	void testCreationWithWrongKeyName() {
		DeltaRequestDto wrongDelta = validCreationDelta()
				.jsonValue(Map.of("platformType", "ALL", "unknownKey", "blablabla", "anotherUnknown", "blablabla"))
				.build();

		webClient.post().uri("/deltas/push")
		.contentType(MediaType.APPLICATION_JSON)
		.body(BodyInserters.fromPublisher(Flux.just(wrongDelta), DeltaRequestDto.class))
		.exchange()
		.expectStatus().is2xxSuccessful()
		.expectBody(List.class)
		.consumeWith(result -> {
			List<Map<String,Object>> body = result.getResponseBody();
			Map<String,Object> savedDelta;

			savedDelta = body.get(0);
			assertEquals(wrongDelta.getRecordId().toString(), savedDelta.get("uuid"));
			assertEquals(false, savedDelta.get("success"));
		});
	}

	@Test
	@WithMockUser
	void testModificationWithWrongKeyName() {
		DeltaRequestDto wrongDelta = validModificationDelta()
				.jsonValue(Map.of("unknownKey", "blablabla"))
				.build();

		webClient.post().uri("/deltas/push")
		.contentType(MediaType.APPLICATION_JSON)
		.body(BodyInserters.fromPublisher(Flux.just(wrongDelta), DeltaRequestDto.class))
		.exchange()
		.expectStatus().is2xxSuccessful()
		.expectBody(List.class)
		.consumeWith(result -> {
			List<Map<String,Object>> body = result.getResponseBody();
			Map<String,Object> savedDelta;

			savedDelta = body.get(0);
			assertEquals(wrongDelta.getRecordId().toString(), savedDelta.get("uuid"));
			assertEquals(false, savedDelta.get("success"));
		});
	}

	@Test
	@WithMockUser
	void testCreationWithNullJsonObject() {
		DeltaRequestDto wrongDelta = validCreationDelta()
				.jsonValue(null)
				.build();

		webClient.post().uri("/deltas/push")
		.contentType(MediaType.APPLICATION_JSON)
		.body(BodyInserters.fromPublisher(Flux.just(wrongDelta), DeltaRequestDto.class))
		.exchange()
		.expectStatus().is2xxSuccessful()
		.expectBody(List.class)
		.consumeWith(result -> {
			List<Map<String,Object>> body = result.getResponseBody();
			Map<String,Object> savedDelta;

			savedDelta = body.get(0);
			assertEquals(wrongDelta.getRecordId().toString(), savedDelta.get("uuid"));
			assertEquals(false, savedDelta.get("success"));
		});
	}

	@Test
	@WithMockUser
	void testModificationWithNullJsonObject() {
		DeltaRequestDto wrongDelta = validModificationDelta()
				.jsonValue(null)
				.build();

		webClient.post().uri("/deltas/push")
		.contentType(MediaType.APPLICATION_JSON)
		.body(BodyInserters.fromPublisher(Flux.just(wrongDelta), DeltaRequestDto.class))
		.exchange()
		.expectStatus().is2xxSuccessful()
		.expectBody(List.class)
		.consumeWith(result -> {
			List<Map<String,Object>> body = result.getResponseBody();
			Map<String,Object> savedDelta;

			savedDelta = body.get(0);
			assertEquals(wrongDelta.getRecordId().toString(), savedDelta.get("uuid"));
			assertEquals(false, savedDelta.get("success"));
		});
	}

	@Test
	@WithMockUser
	void testMultiple() {
		DeltaRequestDto correctDelta = validCreationDelta().build();
		DeltaRequestDto wrongDelta = validCreationDelta()
				.jsonValue(Map.of("platformType", "ALL", "settingKey", "invalid·$%·$", "settingValue", "invalid%&/(%&"))
				.build();
		DeltaRequestDto modificationDelta = validModificationDelta().build();

		webClient.post().uri("/deltas/push")
		.contentType(MediaType.APPLICATION_JSON)
		.body(BodyInserters.fromPublisher(Flux.just(correctDelta, wrongDelta, correctDelta, modificationDelta), DeltaRequestDto.class))
		.exchange()
		.expectStatus().is2xxSuccessful()
		.expectBody(List.class)
		.consumeWith(result -> {
			List<Map<String,Object>> body = result.getResponseBody();
			Map<String,Object> savedDelta;

			savedDelta = body.get(0);
			assertEquals(correctDelta.getRecordId().toString(), savedDelta.get("uuid"));
			assertEquals(true, savedDelta.get("success"));

			savedDelta = body.get(1);
			assertEquals(wrongDelta.getRecordId().toString(), savedDelta.get("uuid"));
			assertEquals(false, savedDelta.get("success"));

			savedDelta = body.get(2);
			assertEquals(correctDelta.getRecordId().toString(), savedDelta.get("uuid"));
			assertEquals(true, savedDelta.get("success"));

			savedDelta = body.get(3);
			assertEquals(modificationDelta.getRecordId().toString(), savedDelta.get("uuid"));
			assertEquals(true, savedDelta.get("success"));
		});
	}

	private DeltaRequestDto.DeltaRequestDtoBuilder validCreationDelta() {
		return DeltaRequestDto.builder()
				.timestamp(LocalDateTime.now())
				.deltaType(DeltaType.CREATION)
				.table(DeltaTable.SETTING)
				.recordId(UUID.randomUUID())
				.jsonValue(Map.of("platformType", "ALL", "settingKey", "setting", "settingValue", "value"));
	}

	private DeltaRequestDto.DeltaRequestDtoBuilder validModificationDelta() {
		return DeltaRequestDto.builder()
				.timestamp(LocalDateTime.now())
				.deltaType(DeltaType.MODIFICATION)
				.table(DeltaTable.SETTING)
				.recordId(UUID.randomUUID())
				.jsonValue(Map.of("settingValue", "value"));
	}

}
