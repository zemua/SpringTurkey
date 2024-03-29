package devs.mrp.springturkey.database.service.impl;

import java.time.LocalTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import devs.mrp.springturkey.Exceptions.AlreadyExistsException;
import devs.mrp.springturkey.Exceptions.DoesNotBelongToUserException;
import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.components.impl.LoginDetailsReaderImpl;
import devs.mrp.springturkey.database.entity.Condition;
import devs.mrp.springturkey.database.entity.Group;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.entity.enumerable.GroupType;
import devs.mrp.springturkey.database.repository.ConditionRepository;
import devs.mrp.springturkey.database.repository.GroupRepository;
import devs.mrp.springturkey.database.repository.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataJpaTest
@EnableJpaRepositories(basePackages = "devs.mrp.springturkey.database.repository")
@EntityScan("devs.mrp.springturkey.database.*")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {LoginDetailsReaderImpl.class, ConditionServiceImpl.class})
@EnableJpaAuditing
class ConditionServiceImplTest {

	@Autowired
	private ConditionRepository conditionRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private ConditionServiceImpl conditionService;

	@Test
	@WithMockUser("some@mail.com")
	void findAllUserConditions() {
		TurkeyUser user = TurkeyUser.builder().email("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);

		TurkeyUser otherUser = TurkeyUser.builder().email("other@mail.com").build();
		userRepository.save(otherUser);

		Group group1 = Group.builder()
				.user(user)
				.name("some name")
				.type(GroupType.NEGATIVE)
				.build();
		Group group2 = Group.builder()
				.user(user)
				.name("some other name")
				.type(GroupType.NEGATIVE)
				.build();
		groupRepository.save(group1);
		groupRepository.save(group2);

		Condition condition1 = Condition.builder()
				.user(user)
				.conditionalGroup(group1)
				.targetGroup(group2)
				.requiredUsageMs(LocalTime.of(0, 10))
				.lastDaysToConsider(1)
				.build();
		Condition condition2 = Condition.builder()
				.user(user)
				.conditionalGroup(group1)
				.targetGroup(group2)
				.requiredUsageMs(LocalTime.of(0, 20))
				.lastDaysToConsider(1)
				.build();
		Condition condition3 = Condition.builder()
				.user(user)
				.conditionalGroup(group1)
				.targetGroup(group2)
				.requiredUsageMs(LocalTime.of(0, 30))
				.lastDaysToConsider(1)
				.build();
		Condition condition4 = Condition.builder()
				.user(otherUser)
				.conditionalGroup(group1)
				.targetGroup(group2)
				.requiredUsageMs(LocalTime.of(0, 40))
				.lastDaysToConsider(1)
				.build();
		conditionRepository.save(condition1);
		conditionRepository.save(condition2);
		conditionRepository.save(condition3);
		conditionRepository.save(condition4);

		Flux<Condition> fluxCondition = conditionService.findAllUserConditions();

		StepVerifier.create(fluxCondition)
		.expectNextMatches(c -> c.getUser().getId().equals(user.getId()) && c.getRequiredUsageMs().equals(123L) && c.getCreated() != null && c.getEdited() != null)
		.expectNextMatches(c -> c.getUser().getId().equals(user.getId()) && c.getRequiredUsageMs().equals(234L))
		.expectNextMatches(c -> c.getUser().getId().equals(user.getId()) && c.getRequiredUsageMs().equals(345L))
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("wrong@mail.com")
	void findAllUserConditionsWithWrongUser() {
		TurkeyUser user = TurkeyUser.builder().email("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);

		Group group1 = Group.builder()
				.user(user)
				.name("some name")
				.type(GroupType.NEGATIVE)
				.build();
		Group group2 = Group.builder()
				.user(user)
				.name("some other name")
				.type(GroupType.NEGATIVE)
				.build();
		groupRepository.save(group1);
		groupRepository.save(group2);

		Condition condition1 = Condition.builder()
				.user(user)
				.conditionalGroup(group1)
				.targetGroup(group2)
				.requiredUsageMs(LocalTime.of(0, 10))
				.lastDaysToConsider(1)
				.build();
		Condition condition2 = Condition.builder()
				.user(user)
				.conditionalGroup(group1)
				.targetGroup(group2)
				.requiredUsageMs(LocalTime.of(0, 20))
				.lastDaysToConsider(1)
				.build();
		Condition condition3 = Condition.builder()
				.user(user)
				.conditionalGroup(group1)
				.targetGroup(group2)
				.requiredUsageMs(LocalTime.of(0, 30))
				.lastDaysToConsider(1)
				.build();
		conditionRepository.save(condition1);
		conditionRepository.save(condition2);
		conditionRepository.save(condition3);

		Flux<Condition> fluxCondition = conditionService.findAllUserConditions();

		StepVerifier.create(fluxCondition)
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("some@mail.com")
	void insertNewCondition() {
		TurkeyUser user = TurkeyUser.builder().email("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);

		Group group1 = Group.builder()
				.user(user)
				.name("some name")
				.type(GroupType.NEGATIVE)
				.build();
		Group group2 = Group.builder()
				.user(user)
				.name("some other name")
				.type(GroupType.NEGATIVE)
				.build();
		groupRepository.save(group1);
		groupRepository.save(group2);

		Condition condition1 = Condition.builder()
				.id(UUID.randomUUID())
				.user(user)
				.conditionalGroup(group1)
				.targetGroup(group2)
				.requiredUsageMs(LocalTime.of(0, 10))
				.lastDaysToConsider(1)
				.build();

		Mono<Integer> monoCondition = conditionService.addNewCondition(condition1);

		StepVerifier.create(monoCondition)
		.expectNext(1)
		.expectComplete()
		.verify();

		Flux<Condition> fluxCondition = conditionService.findAllUserConditions();

		StepVerifier.create(fluxCondition)
		.expectNextMatches(c -> c.getUser().getId().equals(user.getId())
				&& c.getId().equals(condition1.getId())
				&& c.getCreated() != null
				&& c.getEdited() != null)
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("some@mail.com")
	void insertNewConditionWithEmtpyIdGetsGenerated() {
		TurkeyUser user = TurkeyUser.builder().email("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);

		Group group1 = Group.builder()
				.user(user)
				.name("some name")
				.type(GroupType.NEGATIVE)
				.build();
		Group group2 = Group.builder()
				.user(user)
				.name("some other name")
				.type(GroupType.NEGATIVE)
				.build();
		groupRepository.save(group1);
		groupRepository.save(group2);

		Condition condition1 = Condition.builder()
				.user(user)
				.conditionalGroup(group1)
				.targetGroup(group2)
				.requiredUsageMs(LocalTime.of(0, 10))
				.lastDaysToConsider(1)
				.build();

		Mono<Integer> monoCondition = conditionService.addNewCondition(condition1);

		StepVerifier.create(monoCondition)
		.expectNext(1)
		.expectComplete()
		.verify();

		Flux<Condition> fluxCondition = conditionService.findAllUserConditions();

		StepVerifier.create(fluxCondition)
		.expectNextMatches(c -> c.getUser().getId().equals(user.getId()) && c.getRequiredUsageMs().equals(123L) && c.getId() != null)
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("wrong@mail.com")
	void insertNewConditionWithWrongUser() {
		TurkeyUser user = TurkeyUser.builder().email("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);

		Group group1 = Group.builder()
				.user(user)
				.name("some name")
				.type(GroupType.NEGATIVE)
				.build();
		Group group2 = Group.builder()
				.user(user)
				.name("some other name")
				.type(GroupType.NEGATIVE)
				.build();
		groupRepository.save(group1);
		groupRepository.save(group2);

		Condition condition1 = Condition.builder()
				.user(user)
				.conditionalGroup(group1)
				.targetGroup(group2)
				.requiredUsageMs(LocalTime.of(0, 10))
				.lastDaysToConsider(1)
				.build();

		Mono<Integer> monoCondition = conditionService.addNewCondition(condition1);

		StepVerifier.create(monoCondition)
		.expectError(DoesNotBelongToUserException.class)
		.verify();

		Flux<Condition> fluxCondition = conditionService.findAllUserConditions();

		StepVerifier.create(fluxCondition)
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("some@mail.com")
	void insertPointingToGroupWithWrongUser() {
		TurkeyUser user = TurkeyUser.builder().email("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);

		TurkeyUser otherUser = TurkeyUser.builder().email("other@mail.com").build();
		userRepository.save(otherUser);

		Group group1 = Group.builder()
				.user(otherUser)
				.name("some name")
				.type(GroupType.NEGATIVE)
				.build();
		Group group2 = Group.builder()
				.user(user)
				.name("some other name")
				.type(GroupType.NEGATIVE)
				.build();
		groupRepository.save(group1);
		groupRepository.save(group2);

		Condition condition1 = Condition.builder()
				.user(user)
				.conditionalGroup(group1)
				.targetGroup(group2)
				.requiredUsageMs(LocalTime.of(0, 10))
				.lastDaysToConsider(1)
				.build();

		Mono<Integer> monoCondition = conditionService.addNewCondition(condition1);

		StepVerifier.create(monoCondition)
		.expectError(DoesNotBelongToUserException.class)
		.verify();

		Flux<Condition> fluxCondition = conditionService.findAllUserConditions();

		StepVerifier.create(fluxCondition)
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("some@mail.com")
	void insertWithBothGroupsEqual() {
		TurkeyUser user = TurkeyUser.builder().email("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);

		Group group1 = Group.builder()
				.user(user)
				.name("some name")
				.type(GroupType.NEGATIVE)
				.build();
		groupRepository.save(group1);

		Condition condition1 = Condition.builder()
				.user(user)
				.conditionalGroup(group1)
				.targetGroup(group1)
				.requiredUsageMs(LocalTime.of(0, 10))
				.lastDaysToConsider(1)
				.build();

		Mono<Integer> monoCondition = conditionService.addNewCondition(condition1);

		StepVerifier.create(monoCondition)
		.expectError(WrongDataException.class)
		.verify();

		Flux<Condition> fluxCondition = conditionService.findAllUserConditions();

		StepVerifier.create(fluxCondition)
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("some@mail.com")
	void insertRepeatedId() {
		TurkeyUser user = TurkeyUser.builder().email("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);

		Group group1 = Group.builder()
				.user(user)
				.name("some name")
				.type(GroupType.NEGATIVE)
				.build();
		Group group2 = Group.builder()
				.user(user)
				.name("some other name")
				.type(GroupType.NEGATIVE)
				.build();
		groupRepository.save(group1);
		groupRepository.save(group2);

		Condition condition1 = Condition.builder()
				.id(UUID.randomUUID())
				.user(user)
				.conditionalGroup(group1)
				.targetGroup(group2)
				.requiredUsageMs(LocalTime.of(0, 10))
				.lastDaysToConsider(1)
				.build();

		Mono<Integer> monoCondition = conditionService.addNewCondition(condition1);
		Mono<Integer> monoCondition2 = conditionService.addNewCondition(condition1);

		StepVerifier.create(monoCondition)
		.expectNext(1)
		.expectComplete()
		.verify();

		StepVerifier.create(monoCondition2)
		.expectError(AlreadyExistsException.class)
		.verify();

		Flux<Condition> fluxCondition = conditionService.findAllUserConditions();

		StepVerifier.create(fluxCondition)
		.expectNextMatches(c -> c.getUser().getId().equals(user.getId()) && c.getId().equals(condition1.getId()))
		.expectComplete()
		.verify();
	}

}
