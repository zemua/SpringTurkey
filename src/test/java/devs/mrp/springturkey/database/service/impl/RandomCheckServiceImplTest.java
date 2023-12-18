package devs.mrp.springturkey.database.service.impl;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
import devs.mrp.springturkey.database.entity.RandomCheck;
import devs.mrp.springturkey.database.entity.RandomQuestion;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.entity.enumerable.RandomBlockType;
import devs.mrp.springturkey.database.repository.RandomCheckRepository;
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
@ContextConfiguration(classes = {LoginDetailsReaderImpl.class, RandomCheckServiceImpl.class})
@EnableJpaAuditing
class RandomCheckServiceImplTest {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RandomQuestionRepository randomBlockRepository;
	@Autowired
	private RandomCheckRepository randomCheckRepository;

	@Autowired
	private RandomCheckServiceImpl randomCheckServiceImpl;

	private TurkeyUser user;
	private TurkeyUser savedUser;
	private TurkeyUser otherUser;
	private TurkeyUser savedOtherUser;

	private RandomQuestion positiveBlock1;
	private RandomQuestion positiveBlock2;
	private RandomQuestion negativeBlock1;
	private RandomQuestion negativeBlock2;
	private RandomQuestion wrongUserBlock;

	@BeforeEach
	void setup() {
		user = TurkeyUser.builder().externalId("some@mail.com").build();
		savedUser = userRepository.save(user);

		otherUser = TurkeyUser.builder().externalId("other@mail.com").build();
		savedOtherUser = userRepository.save(otherUser);

		positiveBlock1 = RandomQuestion.builder()
				.id(UUID.randomUUID())
				.user(savedUser)
				.type(RandomBlockType.POSITIVE)
				.name("positive one")
				.question("are you positive?")
				.frequency(1)
				.multiplier(1)
				.build();
		randomBlockRepository.save(positiveBlock1);

		positiveBlock2 = RandomQuestion.builder()
				.id(UUID.randomUUID())
				.user(savedUser)
				.type(RandomBlockType.POSITIVE)
				.name("positive two")
				.question("are you also positive?")
				.frequency(1)
				.multiplier(1)
				.build();
		randomBlockRepository.save(positiveBlock2);

		negativeBlock1 = RandomQuestion.builder()
				.id(UUID.randomUUID())
				.user(savedUser)
				.type(RandomBlockType.NEGATIVE)
				.name("negative one")
				.question("are you negative?")
				.frequency(1)
				.multiplier(1)
				.build();
		randomBlockRepository.save(negativeBlock1);

		negativeBlock2 = RandomQuestion.builder()
				.id(UUID.randomUUID())
				.user(savedUser)
				.type(RandomBlockType.NEGATIVE)
				.name("negative two")
				.question("are you also negative?")
				.frequency(1)
				.multiplier(1)
				.build();
		randomBlockRepository.save(negativeBlock2);

		wrongUserBlock = RandomQuestion.builder()
				.id(UUID.randomUUID())
				.user(savedOtherUser)
				.type(RandomBlockType.POSITIVE)
				.name("wrong user")
				.question("do you belong to the wrong user?")
				.frequency(1)
				.multiplier(1)
				.build();
		randomBlockRepository.save(wrongUserBlock);
	}

	@Test
	@WithMockUser("some@mail.com")
	void findAllUserRandomChecks() {
		RandomCheck randomCheck1 = RandomCheck.builder()
				.id(UUID.randomUUID())
				.user(user)
				.name("some name")
				.startActive(LocalTime.NOON)
				.endActive(LocalTime.MIDNIGHT)
				.minCheckLapse(LocalTime.of(0, 15))
				.maxCheckLapse(LocalTime.of(1, 0))
				.reward(LocalTime.of(0, 30))
				.activeDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))
				.negativeQuestions(Set.of(positiveBlock1, positiveBlock2))
				.positiveQuestions(Set.of(negativeBlock1, negativeBlock2))
				.build();

		RandomCheck randomCheck2 = RandomCheck.builder()
				.id(UUID.randomUUID())
				.user(user)
				.name("other name")
				.startActive(LocalTime.NOON)
				.endActive(LocalTime.MIDNIGHT)
				.minCheckLapse(LocalTime.of(0, 15))
				.maxCheckLapse(LocalTime.of(1, 0))
				.reward(LocalTime.of(0, 30))
				.activeDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))
				.negativeQuestions(Set.of(positiveBlock1))
				.positiveQuestions(Collections.emptySet())
				.build();

		RandomCheck randomCheck3 = RandomCheck.builder()
				.id(UUID.randomUUID())
				.user(otherUser)
				.name("no name")
				.startActive(LocalTime.NOON)
				.endActive(LocalTime.MIDNIGHT)
				.minCheckLapse(LocalTime.of(0, 15))
				.maxCheckLapse(LocalTime.of(1, 0))
				.reward(LocalTime.of(0, 30))
				.activeDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))
				.negativeQuestions(Set.of(positiveBlock1, positiveBlock2))
				.positiveQuestions(Set.of(negativeBlock1, negativeBlock2))
				.build();

		randomCheckRepository.saveAll(List.of(randomCheck1, randomCheck2, randomCheck3));

		Flux<RandomCheck> fluxChecks = randomCheckServiceImpl.findAllUserChecks();

		StepVerifier.create(fluxChecks)
		.expectNextMatches(c -> c.getUser().getId().equals(user.getId()) && c.getName().equals("some name") && c.getCreated() != null && c.getEdited() != null)
		.expectNextMatches(c -> c.getName().equals("other name"))
		.verifyComplete();
	}

	@Test
	@WithMockUser("wrong@mail.com")
	void findAllUserWithWrongUser() {
		RandomCheck randomCheck1 = RandomCheck.builder()
				.id(UUID.randomUUID())
				.user(user)
				.name("some name")
				.startActive(LocalTime.NOON)
				.endActive(LocalTime.MIDNIGHT)
				.minCheckLapse(LocalTime.of(0, 15))
				.maxCheckLapse(LocalTime.of(1, 0))
				.reward(LocalTime.of(0, 30))
				.activeDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))
				.negativeQuestions(Set.of(positiveBlock1, positiveBlock2))
				.positiveQuestions(Set.of(negativeBlock1, negativeBlock2))
				.build();

		RandomCheck randomCheck2 = RandomCheck.builder()
				.id(UUID.randomUUID())
				.user(user)
				.name("other name")
				.startActive(LocalTime.NOON)
				.endActive(LocalTime.MIDNIGHT)
				.minCheckLapse(LocalTime.of(0, 15))
				.maxCheckLapse(LocalTime.of(1, 0))
				.reward(LocalTime.of(0, 30))
				.activeDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))
				.negativeQuestions(Set.of(positiveBlock1))
				.positiveQuestions(Collections.emptySet())
				.build();

		RandomCheck randomCheck3 = RandomCheck.builder()
				.id(UUID.randomUUID())
				.user(otherUser)
				.name("no name")
				.startActive(LocalTime.NOON)
				.endActive(LocalTime.MIDNIGHT)
				.minCheckLapse(LocalTime.of(0, 15))
				.maxCheckLapse(LocalTime.of(1, 0))
				.reward(LocalTime.of(0, 30))
				.activeDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))
				.negativeQuestions(Set.of(positiveBlock1, positiveBlock2))
				.positiveQuestions(Set.of(negativeBlock1, negativeBlock2))
				.build();

		randomCheckRepository.saveAll(List.of(randomCheck1, randomCheck2, randomCheck3));

		Flux<RandomCheck> fluxChecks = randomCheckServiceImpl.findAllUserChecks();

		StepVerifier.create(fluxChecks)
		.verifyComplete();
	}

	@Test
	@WithMockUser("some@mail.com")
	void insertNewCheck() {
		RandomCheck randomCheck1 = RandomCheck.builder()
				.id(UUID.randomUUID())
				.user(user)
				.name("some name")
				.startActive(LocalTime.NOON)
				.endActive(LocalTime.MIDNIGHT)
				.minCheckLapse(LocalTime.of(0, 15))
				.maxCheckLapse(LocalTime.of(1, 0))
				.reward(LocalTime.of(0, 30))
				.activeDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))
				.negativeQuestions(Set.of(positiveBlock1, positiveBlock2))
				.positiveQuestions(Set.of(negativeBlock1, negativeBlock2))
				.build();

		Flux<RandomCheck> fluxChecks;

		fluxChecks = randomCheckServiceImpl.findAllUserChecks();
		StepVerifier.create(fluxChecks)
		.verifyComplete();

		Mono<Integer> monoAdded = randomCheckServiceImpl.addNewCheck(randomCheck1);
		StepVerifier.create(monoAdded)
		.expectNext(1)
		.verifyComplete();

		fluxChecks = randomCheckServiceImpl.findAllUserChecks();
		StepVerifier.create(fluxChecks)
		.expectNextMatches(c -> c.getUser().getId().equals(user.getId())
				&& c.getName().equals("some name")
				&& c.getId().equals(randomCheck1.getId())
				&& c.getCreated() != null
				&& c.getEdited() != null)
		.verifyComplete();
	}

	@Test
	@WithMockUser("some@mail.com")
	void insertNewCheckWithEmptyId() {
		RandomCheck randomCheck1 = RandomCheck.builder()
				.user(user)
				.name("some name")
				.startActive(LocalTime.NOON)
				.endActive(LocalTime.MIDNIGHT)
				.minCheckLapse(LocalTime.of(0, 15))
				.maxCheckLapse(LocalTime.of(1, 0))
				.reward(LocalTime.of(0, 30))
				.activeDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))
				.negativeQuestions(Set.of(positiveBlock1, positiveBlock2))
				.positiveQuestions(Set.of(negativeBlock1, negativeBlock2))
				.build();

		Flux<RandomCheck> fluxChecks;

		fluxChecks = randomCheckServiceImpl.findAllUserChecks();
		StepVerifier.create(fluxChecks)
		.verifyComplete();

		Mono<Integer> monoAdded = randomCheckServiceImpl.addNewCheck(randomCheck1);
		StepVerifier.create(monoAdded)
		.expectNext(1)
		.verifyComplete();

		fluxChecks = randomCheckServiceImpl.findAllUserChecks();
		StepVerifier.create(fluxChecks)
		.expectNextMatches(c -> c.getUser().getId().equals(user.getId()) && c.getName().equals("some name") && c.getCreated() != null && c.getEdited() != null)
		.verifyComplete();
	}

	@Test
	@WithMockUser("wrong@mail.com")
	void insertNewCheckWrongUser() {
		RandomCheck randomCheck1 = RandomCheck.builder()
				.id(UUID.randomUUID())
				.user(user)
				.name("some name")
				.startActive(LocalTime.NOON)
				.endActive(LocalTime.MIDNIGHT)
				.minCheckLapse(LocalTime.of(0, 15))
				.maxCheckLapse(LocalTime.of(1, 0))
				.reward(LocalTime.of(0, 30))
				.activeDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))
				.negativeQuestions(Set.of(positiveBlock1, positiveBlock2))
				.positiveQuestions(Set.of(negativeBlock1, negativeBlock2))
				.build();

		Flux<RandomCheck> fluxChecks;

		fluxChecks = randomCheckServiceImpl.findAllUserChecks();
		StepVerifier.create(fluxChecks)
		.verifyComplete();

		Mono<Integer> monoAdded = randomCheckServiceImpl.addNewCheck(randomCheck1);
		StepVerifier.create(monoAdded)
		.expectError(DoesNotBelongToUserException.class);

		fluxChecks = randomCheckServiceImpl.findAllUserChecks();
		StepVerifier.create(fluxChecks)
		.verifyComplete();
	}

	@Test
	@WithMockUser("some@mail.com")
	void insertDuplicatedCheck() {
		RandomCheck randomCheck1 = RandomCheck.builder()
				.id(UUID.randomUUID())
				.user(user)
				.name("some name")
				.startActive(LocalTime.NOON)
				.endActive(LocalTime.MIDNIGHT)
				.minCheckLapse(LocalTime.of(0, 15))
				.maxCheckLapse(LocalTime.of(1, 0))
				.reward(LocalTime.of(0, 30))
				.activeDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))
				.negativeQuestions(Set.of(positiveBlock1, positiveBlock2))
				.positiveQuestions(Set.of(negativeBlock1, negativeBlock2))
				.build();

		Flux<RandomCheck> fluxChecks;

		fluxChecks = randomCheckServiceImpl.findAllUserChecks();
		StepVerifier.create(fluxChecks)
		.verifyComplete();

		Mono<Integer> monoAdded;

		monoAdded = randomCheckServiceImpl.addNewCheck(randomCheck1);
		StepVerifier.create(monoAdded)
		.expectNext(1)
		.verifyComplete();

		fluxChecks = randomCheckServiceImpl.findAllUserChecks();
		StepVerifier.create(fluxChecks)
		.expectNextMatches(c -> c.getUser().getId().equals(user.getId()) && c.getName().equals("some name") && c.getCreated() != null && c.getEdited() != null)
		.verifyComplete();

		monoAdded = randomCheckServiceImpl.addNewCheck(randomCheck1);
		StepVerifier.create(monoAdded)
		.expectError(AlreadyExistsException.class);

		fluxChecks = randomCheckServiceImpl.findAllUserChecks();
		StepVerifier.create(fluxChecks)
		.expectNextMatches(c -> c.getUser().getId().equals(user.getId()) && c.getName().equals("some name") && c.getCreated() != null && c.getEdited() != null)
		.verifyComplete();
	}

}
