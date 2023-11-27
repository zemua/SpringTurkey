package devs.mrp.springturkey.controller;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import devs.mrp.springturkey.configuration.SecurityConfig;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.service.UserService;
import devs.mrp.springturkey.exceptions.DoesNotExistException;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = UserController.class)
@Import({SecurityConfig.class})
class UserControllerTest {

	@MockBean
	private UserService userService;

	@Autowired
	private WebTestClient webClient;

	@Test
	@WithMockUser("some@user.me")
	void testUserExistsResponse() {
		when(userService.getUser()).thenReturn(Mono.just(new TurkeyUser()));

		webClient.get().uri("/user/exists")
		.exchange()
		.expectStatus().isEqualTo(HttpStatusCode.valueOf(200))
		.expectBody(Boolean.class)
		.isEqualTo(true);
	}

	@Test
	@WithMockUser("some@user.me")
	void testUserNull() {
		when(userService.getUser()).thenReturn(Mono.empty());

		webClient.get().uri("/user/exists")
		.exchange()
		.expectStatus().isEqualTo(HttpStatusCode.valueOf(200))
		.expectBody(Boolean.class)
		.isEqualTo(false);
	}

	@Test
	@WithMockUser("some@user.me")
	void testNonExistsException() {
		when(userService.getUser()).thenReturn(Mono.error(() -> new DoesNotExistException()));

		webClient.get().uri("/user/exists")
		.exchange()
		.expectStatus().isEqualTo(HttpStatusCode.valueOf(200))
		.expectBody(Boolean.class)
		.isEqualTo(false);
	}

	@Test
	@WithMockUser("some@user.me")
	void testCreateUser() {
		when(userService.createCurrentUser()).thenReturn(Mono.just(new TurkeyUser()));

		webClient.post().uri("/user/create")
		.exchange()
		.expectStatus().isEqualTo(HttpStatusCode.valueOf(201))
		.expectBody(Boolean.class)
		.isEqualTo(true);
	}

	@Test
	@WithMockUser("some@user.me")
	void testCreateUserConflict() {
		when(userService.createCurrentUser()).thenReturn(Mono.error(() -> new DataIntegrityViolationException("conflict")));

		webClient.post().uri("/user/create")
		.exchange()
		.expectStatus().isEqualTo(HttpStatusCode.valueOf(409))
		.expectBody(Boolean.class)
		.isEqualTo(false);
	}

}
