package devs.mrp.springturkey.database.service.impl;

import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.springturkey.components.impl.LoginDetailsReaderImpl;
import devs.mrp.springturkey.database.entity.User;
import devs.mrp.springturkey.database.repository.UserRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LoginDetailsReaderImpl.class, UserServiceImpl.class})
class UserServiceImplTest {

	@MockBean
	private UserRepository userRepository;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Test
	@WithMockUser("user@mail.me")
	void testAddCurrentUser() {
		UUID userId = UUID.randomUUID();
		User userIn = User.builder().email("user@mail.me").build();
		User userOut = User.builder().id(userId).email("user@mail.me").build();

		when(userRepository.save(ArgumentMatchers.refEq(userIn))).thenReturn(Mono.just(userOut));

		Mono<User> monoUser = userServiceImpl.addCurrentUser();

		StepVerifier.create(monoUser)
		.expectNext(userOut)
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("user@mail.me")
	void testGetUser() {
		UUID userId = UUID.randomUUID();
		User userOut = User.builder().id(userId).email("user@mail.me").build();

		when(userRepository.findByEmail("user@mail.me")).thenReturn(Mono.just(userOut));

		Mono<User> monoUser = userServiceImpl.getUser();

		StepVerifier.create(monoUser)
		.expectNext(userOut)
		.expectComplete()
		.verify();
	}

}
