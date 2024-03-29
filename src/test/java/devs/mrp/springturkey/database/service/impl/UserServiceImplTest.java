package devs.mrp.springturkey.database.service.impl;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import devs.mrp.springturkey.components.impl.LoginDetailsReaderImpl;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.repository.UserRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ContextConfiguration(classes = {LoginDetailsReaderImpl.class, UserServiceImpl.class})
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "devs.mrp.springturkey.database.repository")
@EntityScan("devs.mrp.springturkey.database.*")
@DataJpaTest
class UserServiceImplTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Test
	@WithMockUser("user@mail.me")
	void testAddCurrentUser() {
		Mono<TurkeyUser> monoUser = userServiceImpl.addCurrentUser();

		StepVerifier.create(monoUser)
		.expectNextMatches(user -> {
			return user.getEmail().equals("user@mail.me") && user.getCreated() != null;
		})
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("user@mail.me")
	void testGetUser() {
		TurkeyUser user = TurkeyUser.builder().email("user@mail.me").build();
		UUID id = userRepository.save(user).getId();

		Mono<TurkeyUser> monoUser = userServiceImpl.getUser();

		StepVerifier.create(monoUser)
		.expectNextMatches(u -> u.getId().equals(id) && u.getEmail().equals("user@mail.me")  && user.getCreated() != null)
		.expectComplete()
		.verify();
	}

}
