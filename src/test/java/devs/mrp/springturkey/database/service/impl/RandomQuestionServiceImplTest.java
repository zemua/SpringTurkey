package devs.mrp.springturkey.database.service.impl;

import java.util.List;
import java.util.UUID;

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
import devs.mrp.springturkey.database.entity.RandomQuestion;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.entity.enumerable.RandomBlockType;
import devs.mrp.springturkey.database.repository.RandomQuestionRepository;
import devs.mrp.springturkey.database.repository.UserRepository;
import devs.mrp.springturkey.exceptions.AlreadyExistsException;
import devs.mrp.springturkey.exceptions.DoesNotBelongToUserException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataJpaTest
@EnableJpaRepositories(basePackages = "devs.mrp.springturkey.database.repository")
@EntityScan("devs.mrp.springturkey.database.*")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {LoginDetailsReaderImpl.class, RandomQuestionServiceImpl.class})
@EnableJpaAuditing
class RandomQuestionServiceImplTest {

	@Autowired
	private RandomQuestionRepository randomBlockRepository;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RandomQuestionServiceImpl randomBlockServiceImpl;

	private TurkeyUser user;
	private TurkeyUser savedUser;
	private TurkeyUser otherUser;
	private TurkeyUser savedOtherUser;

	@BeforeEach
	void setup() {
		user = TurkeyUser.builder().externalId("some@mail.com").build();
		savedUser = userRepository.save(user);

		otherUser = TurkeyUser.builder().externalId("other@mail.com").build();
		savedOtherUser = userRepository.save(otherUser);
	}

	@Test
	@WithMockUser("some@mail.com")
	void findAllUserRandomBlocks() {
		RandomQuestion block1 = RandomQuestion.builder()
				.id(UUID.randomUUID())
				.user(user)
				.type(RandomBlockType.POSITIVE)
				.name("some name")
				.question("question to evaluate")
				.frequency(2)
				.multiplier(1)
				.build();

		RandomQuestion block2 = RandomQuestion.builder()
				.id(UUID.randomUUID())
				.user(user)
				.type(RandomBlockType.NEGATIVE)
				.name("another name")
				.question("another question to evaluate")
				.frequency(2)
				.multiplier(1)
				.build();

		RandomQuestion block3 = RandomQuestion.builder()
				.id(UUID.randomUUID())
				.user(otherUser)
				.type(RandomBlockType.POSITIVE)
				.name("yet any other name")
				.question("yet any other question to evaluate")
				.frequency(2)
				.multiplier(1)
				.build();

		randomBlockRepository.saveAll(List.of(block1, block2, block3));

		Flux<RandomQuestion> fluxBlocks = randomBlockServiceImpl.findAllUserQuestions();

		StepVerifier.create(fluxBlocks)
		.expectNextMatches(b -> b.getUser().getId().equals(user.getId()) && b.getName().equals("some name") && b.getCreated() != null && b.getEdited() != null)
		.expectNextMatches(b -> b.getUser().getId().equals(user.getId()))
		.verifyComplete();
	}

	@Test
	@WithMockUser("wrong@mail.com")
	void findAllUserWithWrongUser() {
		RandomQuestion block1 = RandomQuestion.builder()
				.id(UUID.randomUUID())
				.user(user)
				.type(RandomBlockType.POSITIVE)
				.name("some name")
				.question("question to evaluate")
				.frequency(2)
				.multiplier(1)
				.build();

		RandomQuestion block2 = RandomQuestion.builder()
				.id(UUID.randomUUID())
				.user(user)
				.type(RandomBlockType.NEGATIVE)
				.name("another name")
				.question("another question to evaluate")
				.frequency(2)
				.multiplier(1)
				.build();

		RandomQuestion block3 = RandomQuestion.builder()
				.id(UUID.randomUUID())
				.user(otherUser)
				.type(RandomBlockType.POSITIVE)
				.name("yet any other name")
				.question("yet any other question to evaluate")
				.frequency(2)
				.multiplier(1)
				.build();

		randomBlockRepository.saveAll(List.of(block1, block2, block3));

		Flux<RandomQuestion> fluxBlocks = randomBlockServiceImpl.findAllUserQuestions();

		StepVerifier.create(fluxBlocks)
		.verifyComplete();
	}

	@Test
	@WithMockUser("some@mail.com")
	void insertNewBlock() {
		RandomQuestion block1 = RandomQuestion.builder()
				.id(UUID.randomUUID())
				.user(user)
				.type(RandomBlockType.POSITIVE)
				.name("some name")
				.question("question to evaluate")
				.frequency(2)
				.multiplier(1)
				.build();

		Flux<RandomQuestion> fluxBlocks;

		fluxBlocks = randomBlockServiceImpl.findAllUserQuestions();
		StepVerifier.create(fluxBlocks)
		.verifyComplete();

		Mono<Integer> monoAdded = randomBlockServiceImpl.addNewQuestion(block1);
		StepVerifier.create(monoAdded)
		.expectNext(1)
		.verifyComplete();

		fluxBlocks = randomBlockServiceImpl.findAllUserQuestions();
		StepVerifier.create(fluxBlocks)
		.expectNextMatches(b -> b.getUser().getId().equals(user.getId())
				&& b.getId().equals(block1.getId())
				&& b.getName().equals("some name")
				&& b.getCreated() != null
				&& b.getEdited() != null)
		.verifyComplete();
	}

	@Test
	@WithMockUser("some@mail.com")
	void insertNewBlockEmptyId() {
		RandomQuestion block1 = RandomQuestion.builder()
				.id(UUID.randomUUID())
				.user(user)
				.type(RandomBlockType.POSITIVE)
				.name("some name")
				.question("question to evaluate")
				.frequency(2)
				.multiplier(1)
				.build();

		Flux<RandomQuestion> fluxBlocks;

		fluxBlocks = randomBlockServiceImpl.findAllUserQuestions();
		StepVerifier.create(fluxBlocks)
		.verifyComplete();

		Mono<Integer> monoAdded = randomBlockServiceImpl.addNewQuestion(block1);
		StepVerifier.create(monoAdded)
		.expectNext(1)
		.verifyComplete();

		fluxBlocks = randomBlockServiceImpl.findAllUserQuestions();
		StepVerifier.create(fluxBlocks)
		.expectNextMatches(b -> b.getUser().getId().equals(user.getId()) && b.getName().equals("some name") && b.getCreated() != null && b.getEdited() != null)
		.verifyComplete();
	}

	@Test
	@WithMockUser("wrong@mail.com")
	void insertNewBlockWrongUser() {
		RandomQuestion block1 = RandomQuestion.builder()
				.id(UUID.randomUUID())
				.user(user)
				.type(RandomBlockType.POSITIVE)
				.name("some name")
				.question("question to evaluate")
				.frequency(2)
				.multiplier(1)
				.build();

		Flux<RandomQuestion> fluxBlocks;

		fluxBlocks = randomBlockServiceImpl.findAllUserQuestions();
		StepVerifier.create(fluxBlocks)
		.verifyComplete();

		Mono<Integer> monoAdded = randomBlockServiceImpl.addNewQuestion(block1);
		StepVerifier.create(monoAdded)
		.expectError(DoesNotBelongToUserException.class)
		.verify();

		fluxBlocks = randomBlockServiceImpl.findAllUserQuestions();
		StepVerifier.create(fluxBlocks)
		.verifyComplete();
	}

	@Test
	@WithMockUser("some@mail.com")
	void insertDuplicated() {
		RandomQuestion block1 = RandomQuestion.builder()
				.id(UUID.randomUUID())
				.user(user)
				.type(RandomBlockType.POSITIVE)
				.name("some name")
				.question("question to evaluate")
				.frequency(2)
				.multiplier(1)
				.build();

		Flux<RandomQuestion> fluxBlocks;

		fluxBlocks = randomBlockServiceImpl.findAllUserQuestions();
		StepVerifier.create(fluxBlocks)
		.verifyComplete();

		Mono<Integer> monoAdded = randomBlockServiceImpl.addNewQuestion(block1);
		StepVerifier.create(monoAdded)
		.expectNext(1)
		.verifyComplete();

		fluxBlocks = randomBlockServiceImpl.findAllUserQuestions();
		StepVerifier.create(fluxBlocks)
		.expectNextMatches(b -> b.getUser().getId().equals(user.getId()) && b.getName().equals("some name") && b.getCreated() != null && b.getEdited() != null)
		.verifyComplete();

		Mono<Integer> monoAdded2 = randomBlockServiceImpl.addNewQuestion(block1);
		StepVerifier.create(monoAdded2)
		.expectError(AlreadyExistsException.class)
		.verify();

		fluxBlocks = randomBlockServiceImpl.findAllUserQuestions();
		StepVerifier.create(fluxBlocks)
		.expectNextMatches(b -> b.getUser().getId().equals(user.getId()) && b.getName().equals("some name") && b.getCreated() != null && b.getEdited() != null)
		.verifyComplete();
	}

}
