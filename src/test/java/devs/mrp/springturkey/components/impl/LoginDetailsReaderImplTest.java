package devs.mrp.springturkey.components.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.config.EnableWebFlux;

import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.repository.UserRepository;
import reactor.test.StepVerifier;

@DataJpaTest
@EnableJpaRepositories(basePackages = "devs.mrp.springturkey.database.repository")
@EnableWebFlux
@EntityScan("devs.mrp.springturkey.database.*")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {LoginDetailsReaderImpl.class})
class LoginDetailsReaderImplTest {

	@Autowired
	private LoginDetailsReaderImpl reader;

	@SpyBean
	private UserRepository userRepository;

	@Test
	@WithMockUser(username = "basic@user.me", password = "password", roles = "USER")
	void testGetUsername() {
		String result = reader.getUserId().block();
		assertEquals("basic@user.me", result);
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "basic@user.me", password = "password", roles = "USER")
	void getUserObject() {
		TurkeyUser tobesaved = TurkeyUser.builder()
				.externalId("basic@user.me")
				.build();
		userRepository.save(tobesaved);
		TurkeyUser user = reader.getTurkeyUser().block();
		assertEquals("basic@user.me", user.getExternalId());
	}

	@Test
	@WithMockUser(username = "basic@user.me", password = "password", roles = "USER")
	void getNullUserObject() {
		StepVerifier
		.create(reader.getTurkeyUser())
		.verifyComplete();
	}

	@Test
	@WithMockUser(username = "basic@user.me", password = "password", roles = "USER")
	void isCurrentUser() {
		TurkeyUser user = TurkeyUser.builder().externalId("basic@user.me").build();
		assertTrue(reader.isCurrentUser(user).block());
		TurkeyUser user2 = TurkeyUser.builder().externalId("other@user.me").build();
		assertFalse(reader.isCurrentUser(user2).block());
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "basic@user.me", password = "password", roles = "USER")
	void createCurrentUser() {
		TurkeyUser user = reader.getTurkeyUser().block();
		assertNull(user);
		user = reader.setupCurrentUser().block();
		assertEquals("basic@user.me", user.getExternalId());
		user = reader.getTurkeyUser().block();
		assertEquals("basic@user.me", user.getExternalId());
		TurkeyUser user2 = reader.setupCurrentUser().block();
		assertEquals(user.getId(), user2.getId());

		TurkeyUser sameUser = reader.setupCurrentUser().block();
		assertEquals(user.getId(), sameUser.getId());

		verify(userRepository, times(1)).save(ArgumentMatchers.any());
	}

}
