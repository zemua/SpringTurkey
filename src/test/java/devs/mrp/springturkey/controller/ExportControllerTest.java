package devs.mrp.springturkey.controller;

import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.configuration.SecurityConfig;
import devs.mrp.springturkey.controller.dto.ExportDataDto;
import devs.mrp.springturkey.database.entity.dto.ExportData;
import devs.mrp.springturkey.database.service.FullUserDumpFacade;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = ExportController.class)
@Import({SecurityConfig.class})
class ExportControllerTest {

	@MockBean
	private ReactiveJwtDecoder jwtDecoder;

	@MockBean
	private FullUserDumpFacade fullUserDumpFacade;

	@Autowired
	private WebTestClient webClient;

	@Test
	@WithMockUser("some@user.me")
	void testFullExport() throws JsonMappingException, JsonProcessingException {
		UUID deviceId = UUID.randomUUID();
		when(fullUserDumpFacade.fullUserDump(deviceId)).thenReturn(Mono.just(mockExportData()));

		ExportDataDto expectedResult = expectedData();

		webClient.get().uri("/export/full/" + deviceId.toString())
		.exchange()
		.expectStatus().isEqualTo(HttpStatusCode.valueOf(200))
		.expectBody(ExportDataDto.class)
		.isEqualTo(expectedResult);
	}

	@Test
	@WithMockUser("some@user.me")
	void testFullExportNotUUID() throws JsonMappingException, JsonProcessingException {
		String deviceId = "not a valid UUID";
		when(fullUserDumpFacade.fullUserDump(ArgumentMatchers.any())).thenReturn(Mono.just(mockExportData()));

		ExportDataDto expectedResult = expectedData();

		webClient.get().uri("/export/full/" + deviceId)
		.exchange()
		.expectStatus().isEqualTo(HttpStatusCode.valueOf(400))
		.expectBody(WrongDataException.class);
	}

	private ExportData mockExportData() throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		String mockJson = "{\"otherDevices\":[{\"id\":\"8316d345-2124-40d9-98bf-095e60513b41\",\"user\":{\"id\":\"3a53ed8d-ee2d-4a09-a51c-aa7926fbf1bd\",\"email\":\"some@user.me\",\"devices\":null,\"created\":null,\"edited\":null,\"deleted\":null},\"deviceType\":\"ANDROID\",\"usageTime\":234,\"created\":null,\"edited\":null,\"deleted\":null},{\"id\":\"6fc517d3-1bae-460d-899c-9f3beb46addb\",\"user\":{\"id\":\"3a53ed8d-ee2d-4a09-a51c-aa7926fbf1bd\",\"email\":\"some@user.me\",\"devices\":null,\"created\":null,\"edited\":null,\"deleted\":null},\"deviceType\":\"ANDROID\",\"usageTime\":345,\"created\":null,\"edited\":null,\"deleted\":null}],\"activities\":[{\"id\":\"d4186edd-0115-4d06-a03f-b5b5bd3fdd20\",\"user\":{\"id\":\"3a53ed8d-ee2d-4a09-a51c-aa7926fbf1bd\",\"email\":\"some@user.me\",\"devices\":null,\"created\":null,\"edited\":null,\"deleted\":null},\"activityName\":\"act1\",\"activityType\":\"ANDROID_APP\",\"categoryType\":\"NEGATIVE\",\"group\":{\"id\":\"08ced3e8-5ce1-4df1-8703-7cf643c8989f\",\"user\":{\"id\":\"3a53ed8d-ee2d-4a09-a51c-aa7926fbf1bd\",\"email\":\"some@user.me\",\"devices\":null,\"created\":null,\"edited\":null,\"deleted\":null},\"name\":\"group1\",\"type\":\"NEGATIVE\",\"preventClose\":null,\"created\":null,\"edited\":null,\"deleted\":null},\"preventClosing\":true,\"created\":null,\"edited\":null,\"deleted\":null},{\"id\":\"3a86b396-4d6a-438e-8d60-5e4ee4eb4a14\",\"user\":{\"id\":\"3a53ed8d-ee2d-4a09-a51c-aa7926fbf1bd\",\"email\":\"some@user.me\",\"devices\":null,\"created\":null,\"edited\":null,\"deleted\":null},\"activityName\":\"act2\",\"activityType\":\"ANDROID_APP\",\"categoryType\":\"NEGATIVE\",\"group\":null,\"preventClosing\":null,\"created\":null,\"edited\":null,\"deleted\":null}],\"groups\":[{\"id\":\"08ced3e8-5ce1-4df1-8703-7cf643c8989f\",\"user\":{\"id\":\"3a53ed8d-ee2d-4a09-a51c-aa7926fbf1bd\",\"email\":\"some@user.me\",\"devices\":null,\"created\":null,\"edited\":null,\"deleted\":null},\"name\":\"group1\",\"type\":\"NEGATIVE\",\"preventClose\":true,\"created\":null,\"edited\":null,\"deleted\":null},{\"id\":\"039eb640-3e7e-4232-9b39-99072ca17992\",\"user\":{\"id\":\"3a53ed8d-ee2d-4a09-a51c-aa7926fbf1bd\",\"email\":\"some@user.me\",\"devices\":null,\"created\":null,\"edited\":null,\"deleted\":null},\"name\":\"group2\",\"type\":\"POSITIVE\",\"preventClose\":null,\"created\":null,\"edited\":null,\"deleted\":null}],\"conditions\":[{\"id\":\"f978b011-1065-4b2d-b829-85974f736d67\",\"user\":{\"id\":\"3a53ed8d-ee2d-4a09-a51c-aa7926fbf1bd\",\"email\":\"some@user.me\",\"devices\":null,\"created\":null,\"edited\":null,\"deleted\":null},\"conditionalGroup\":{\"id\":\"08ced3e8-5ce1-4df1-8703-7cf643c8989f\",\"user\":{\"id\":\"3a53ed8d-ee2d-4a09-a51c-aa7926fbf1bd\",\"email\":\"some@user.me\",\"devices\":null,\"created\":null,\"edited\":null,\"deleted\":null},\"name\":\"group1\",\"type\":\"NEGATIVE\",\"preventClose\":null,\"created\":null,\"edited\":null,\"deleted\":null},\"targetGroup\":{\"id\":\"039eb640-3e7e-4232-9b39-99072ca17992\",\"user\":{\"id\":\"3a53ed8d-ee2d-4a09-a51c-aa7926fbf1bd\",\"email\":\"some@user.me\",\"devices\":null,\"created\":null,\"edited\":null,\"deleted\":null},\"name\":\"group2\",\"type\":\"POSITIVE\",\"preventClose\":null,\"created\":null,\"edited\":null,\"deleted\":null},\"requiredUsageMs\":1234,\"lastDaysToConsider\":3,\"created\":null,\"edited\":null,\"deleted\":null},{\"id\":\"952eb8f0-b539-40e6-a330-6c663d564c3b\",\"user\":{\"id\":\"3a53ed8d-ee2d-4a09-a51c-aa7926fbf1bd\",\"email\":\"some@user.me\",\"devices\":null,\"created\":null,\"edited\":null,\"deleted\":null},\"conditionalGroup\":{\"id\":\"08ced3e8-5ce1-4df1-8703-7cf643c8989f\",\"user\":{\"id\":\"3a53ed8d-ee2d-4a09-a51c-aa7926fbf1bd\",\"email\":\"some@user.me\",\"devices\":null,\"created\":null,\"edited\":null,\"deleted\":null},\"name\":\"group1\",\"type\":\"NEGATIVE\",\"preventClose\":null,\"created\":null,\"edited\":null,\"deleted\":null},\"targetGroup\":{\"id\":\"039eb640-3e7e-4232-9b39-99072ca17992\",\"user\":{\"id\":\"3a53ed8d-ee2d-4a09-a51c-aa7926fbf1bd\",\"email\":\"some@user.me\",\"devices\":null,\"created\":null,\"edited\":null,\"deleted\":null},\"name\":\"group2\",\"type\":\"POSITIVE\",\"preventClose\":null,\"created\":null,\"edited\":null,\"deleted\":null},\"requiredUsageMs\":123,\"lastDaysToConsider\":2,\"created\":null,\"edited\":null,\"deleted\":null}],\"settings\":[{\"id\":\"e88f78dc-ae02-4066-bb54-180c0fc41e0a\",\"user\":{\"id\":\"3a53ed8d-ee2d-4a09-a51c-aa7926fbf1bd\",\"email\":\"some@user.me\",\"devices\":null,\"created\":null,\"edited\":null,\"deleted\":null},\"platform\":\"ALL\",\"settingKey\":\"setting1\",\"settingValue\":\"value1\",\"created\":null,\"edited\":null,\"deleted\":null},{\"id\":\"6ab272a0-c9d1-491c-9edc-1feea399cf60\",\"user\":{\"id\":\"3a53ed8d-ee2d-4a09-a51c-aa7926fbf1bd\",\"email\":\"some@user.me\",\"devices\":null,\"created\":null,\"edited\":null,\"deleted\":null},\"platform\":\"ALL\",\"settingKey\":\"setting2\",\"settingValue\":\"value2\",\"created\":null,\"edited\":null,\"deleted\":null}]}";
		return objectMapper.readValue(mockJson, ExportData.class);
	}

	private ExportDataDto expectedData() throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		String mockJson = "{\"otherDevices\":{\"totalUsage\":579},\"activities\":[{\"activityName\":\"act1\",\"activityType\":\"ANDROID_APP\",\"categoryType\":\"NEGATIVE\",\"groupId\":\"08ced3e8-5ce1-4df1-8703-7cf643c8989f\",\"preventClosing\":true},{\"activityName\":\"act2\",\"activityType\":\"ANDROID_APP\",\"categoryType\":\"NEGATIVE\",\"groupId\":null,\"preventClosing\":null}],\"groups\":[{\"name\":\"group1\",\"type\":\"NEGATIVE\",\"preventClose\":true},{\"name\":\"group2\",\"type\":\"POSITIVE\",\"preventClose\":null}],\"conditions\":[{\"conditionalGroupId\":\"08ced3e8-5ce1-4df1-8703-7cf643c8989f\",\"targetGroupId\":\"039eb640-3e7e-4232-9b39-99072ca17992\",\"requiredUsageMs\":1234,\"lastDaysToConsider\":3},{\"conditionalGroupId\":\"08ced3e8-5ce1-4df1-8703-7cf643c8989f\",\"targetGroupId\":\"039eb640-3e7e-4232-9b39-99072ca17992\",\"requiredUsageMs\":123,\"lastDaysToConsider\":2}],\"settings\":[{\"platformType\":\"ALL\",\"settingKey\":\"setting1\",\"settingValue\":\"value1\"},{\"platformType\":\"ALL\",\"settingKey\":\"setting2\",\"settingValue\":\"value2\"}]}";
		return objectMapper.readValue(mockJson, ExportDataDto.class);
	}

}
