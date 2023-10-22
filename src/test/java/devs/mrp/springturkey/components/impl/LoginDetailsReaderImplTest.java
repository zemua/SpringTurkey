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
		String result = reader.getUserId();
		assertEquals("basic@user.me", result);
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "basic@user.me", password = "password", roles = "USER")
	void getUserObject() {
		TurkeyUser tobesaved = TurkeyUser.builder()
				.email("basic@user.me")
				.build();
		userRepository.save(tobesaved);
		TurkeyUser user = reader.getTurkeyUser();
		assertEquals("basic@user.me", user.getEmail());
	}

	@Test
	@WithMockUser(username = "basic@user.me", password = "password", roles = "USER")
	void getNullUserObject() {
		TurkeyUser user = reader.getTurkeyUser();
		assertNull(user);
	}

	@Test
	@WithMockUser(username = "basic@user.me", password = "password", roles = "USER")
	void isCurrentUser() {
		TurkeyUser user = TurkeyUser.builder().email("basic@user.me").build();
		assertTrue(reader.isCurrentUser(user));
		TurkeyUser user2 = TurkeyUser.builder().email("other@user.me").build();
		assertFalse(reader.isCurrentUser(user2));
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "basic@user.me", password = "password", roles = "USER")
	void createCurrentUser() {
		TurkeyUser user = reader.getTurkeyUser();
		assertNull(user);
		user = reader.setupCurrentUser();
		assertEquals("basic@user.me", user.getEmail());
		user = reader.getTurkeyUser();
		assertEquals("basic@user.me", user.getEmail());
		TurkeyUser user2 = reader.setupCurrentUser();
		assertEquals(user.getId(), user2.getId());

		TurkeyUser sameUser = reader.setupCurrentUser();
		assertEquals(user.getId(), sameUser.getId());

		verify(userRepository, times(1)).save(ArgumentMatchers.any());
	}

}
