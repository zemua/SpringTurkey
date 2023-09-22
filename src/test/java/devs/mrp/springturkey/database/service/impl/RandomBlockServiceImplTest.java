package devs.mrp.springturkey.database.service.impl;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import devs.mrp.springturkey.components.impl.LoginDetailsReaderImpl;
import devs.mrp.springturkey.database.entity.RandomBlock;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.entity.enumerable.RandomBlockType;
import devs.mrp.springturkey.database.repository.RandomBlockRepository;
import devs.mrp.springturkey.database.repository.UserRepository;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataJpaTest
@EnableJpaRepositories(basePackages = "devs.mrp.springturkey.database.repository")
@EntityScan("devs.mrp.springturkey.database.*")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {LoginDetailsReaderImpl.class, RandomBlockServiceImpl.class})
@EnableJpaAuditing
class RandomBlockServiceImplTest {

	// TODO implement tests

	@Autowired
	private RandomBlockRepository randomBlockRepository;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RandomBlockServiceImpl randomBlockServiceImpl;

	private TurkeyUser user;
	private TurkeyUser savedUser;
	private TurkeyUser otherUser;
	private TurkeyUser savedOtherUser;

	@BeforeEach
	void setup() {
		user = TurkeyUser.builder().email("some@mail.com").build();
		savedUser = userRepository.save(user);

		otherUser = TurkeyUser.builder().email("other@mail.com").build();
		savedOtherUser = userRepository.save(otherUser);
	}

	@Test
	@WithMockUser("some@mail.com")
	void findAllUserRandomBlocks() {
		RandomBlock block1 = RandomBlock.builder()
				.user(user)
				.type(RandomBlockType.POSITIVE)
				.name("some name")
				.question("question to evaluate")
				.frequency(2)
				.multiplier(1)
				.build();

		randomBlockRepository.save(block1);

		Flux<RandomBlock> fluxBlocks = randomBlockServiceImpl.findAllUserBlocks();

		StepVerifier.create(fluxBlocks)
		.expectNextMatches(b -> b.getUser().getId().equals(user.getId()) && b.getName().equals("some name") && b.getCreated() != null && b.getEdited() != null)
		.verifyComplete();

		fail("not yet implemented");
	}

}
