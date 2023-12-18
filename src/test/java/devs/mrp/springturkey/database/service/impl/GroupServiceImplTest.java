package devs.mrp.springturkey.database.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import devs.mrp.springturkey.components.impl.LoginDetailsReaderImpl;
import devs.mrp.springturkey.database.entity.Group;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.entity.enumerable.GroupType;
import devs.mrp.springturkey.database.repository.GroupRepository;
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
@ContextConfiguration(classes = {LoginDetailsReaderImpl.class, GroupServiceImpl.class})
@EnableJpaAuditing
class GroupServiceImplTest {

	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GroupServiceImpl groupService;

	@Test
	@WithMockUser("some@mail.com")
	void findAllUserGroups() {
		TurkeyUser user = TurkeyUser.builder().externalId("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);

		TurkeyUser otherUser = TurkeyUser.builder().externalId("other@mail.com").build();
		userRepository.save(otherUser);

		Group group1 = Group.builder()
				.id(UUID.randomUUID())
				.user(user)
				.name("group1")
				.type(GroupType.POSITIVE)
				.build();
		Group group2 = Group.builder()
				.id(UUID.randomUUID())
				.user(user)
				.name("group2")
				.type(GroupType.POSITIVE)
				.build();
		Group group3 = Group.builder()
				.id(UUID.randomUUID())
				.user(user)
				.name("group3")
				.type(GroupType.POSITIVE)
				.build();
		Group group4 = Group.builder()
				.id(UUID.randomUUID())
				.user(otherUser)
				.name("group4")
				.type(GroupType.POSITIVE)
				.build();
		groupRepository.save(group1);
		groupRepository.save(group2);
		groupRepository.save(group3);
		groupRepository.save(group4);

		Flux<Group> fluxGroup = groupService.findAllUserGroups();

		StepVerifier.create(fluxGroup)
		.expectNextMatches(g -> g.getUser().getId().equals(user.getId()) && g.getName().equals("group1") && g.getCreated() != null && g.getEdited() != null)
		.expectNextMatches(g -> g.getUser().getId().equals(user.getId()) && g.getName().equals("group2"))
		.expectNextMatches(g -> g.getUser().getId().equals(user.getId()) && g.getName().equals("group3"))
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("wrong@mail.com")
	void findAllUserGroupsWithWrongUser() {
		TurkeyUser user = TurkeyUser.builder().externalId("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);

		TurkeyUser otherUser = TurkeyUser.builder().externalId("other@mail.com").build();
		userRepository.save(otherUser);

		Group group1 = Group.builder()
				.id(UUID.randomUUID())
				.user(user)
				.name("group1")
				.type(GroupType.POSITIVE)
				.build();
		Group group2 = Group.builder()
				.id(UUID.randomUUID())
				.user(user)
				.name("group2")
				.type(GroupType.POSITIVE)
				.build();
		Group group3 = Group.builder()
				.id(UUID.randomUUID())
				.user(user)
				.name("group3")
				.type(GroupType.POSITIVE)
				.build();
		Group group4 = Group.builder()
				.id(UUID.randomUUID())
				.user(otherUser)
				.name("group4")
				.type(GroupType.POSITIVE)
				.build();
		groupRepository.save(group1);
		groupRepository.save(group2);
		groupRepository.save(group3);
		groupRepository.save(group4);

		Flux<Group> fluxGroup = groupService.findAllUserGroups();

		StepVerifier.create(fluxGroup)
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("some@mail.com")
	void insertNewGroup() {
		TurkeyUser user = TurkeyUser.builder().externalId("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);

		Group group1 = Group.builder()
				.id(UUID.randomUUID())
				.user(user)
				.name("group1")
				.type(GroupType.POSITIVE)
				.build();

		Mono<Integer> monoGroup = groupService.addNewGroup(group1);

		StepVerifier.create(monoGroup)
		.expectNext(1)
		.expectComplete()
		.verify();

		Flux<Group> fluxGroup = groupService.findAllUserGroups();

		StepVerifier.create(fluxGroup)
		.expectNextMatches(g -> g.getUser().getId().equals(user.getId())
				&& g.getName().equals("group1")
				&& g.getId().equals(group1.getId())
				&& g.getCreated() != null
				&& g.getEdited() != null)
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("some@mail.com")
	void insertNewGroupEmptyId() {
		TurkeyUser user = TurkeyUser.builder().externalId("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);

		Group group1 = Group.builder()
				.id(UUID.randomUUID())
				.user(user)
				.name("group1")
				.type(GroupType.POSITIVE)
				.build();

		Mono<Integer> monoGroup = groupService.addNewGroup(group1);

		StepVerifier.create(monoGroup)
		.expectNext(1)
		.expectComplete()
		.verify();

		Flux<Group> fluxGroup = groupService.findAllUserGroups();

		StepVerifier.create(fluxGroup)
		.expectNextMatches(g -> g.getUser().getId().equals(user.getId())
				&& g.getName().equals("group1")
				&& g.getId() != null)
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("wrong@mail.com")
	void insertNewGroupWrongUser() {
		TurkeyUser user = TurkeyUser.builder().externalId("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);

		Group group1 = Group.builder()
				.id(UUID.randomUUID())
				.user(user)
				.name("group1")
				.type(GroupType.POSITIVE)
				.build();

		Mono<Integer> monoGroup = groupService.addNewGroup(group1);

		StepVerifier.create(monoGroup)
		.expectError(DoesNotBelongToUserException.class)
		.verify();

		Flux<Group> fluxGroup = groupService.findAllUserGroups();

		StepVerifier.create(fluxGroup)
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("some@mail.com")
	void insertDouble() {
		TurkeyUser user = TurkeyUser.builder().externalId("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);

		Group group1 = Group.builder()
				.id(UUID.randomUUID())
				.user(user)
				.name("group1")
				.type(GroupType.POSITIVE)
				.build();

		Mono<Integer> monoGroup = groupService.addNewGroup(group1);
		Mono<Integer> monoGroup2 = groupService.addNewGroup(group1);

		StepVerifier.create(monoGroup)
		.expectNext(1)
		.expectComplete()
		.verify();

		StepVerifier.create(monoGroup2)
		.expectError(AlreadyExistsException.class)
		.verify();
	}

	@Test
	@WithMockUser("some@mail.com")
	void givenIdIsKept() {
		TurkeyUser user = TurkeyUser.builder().externalId("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);

		Group group1 = Group.builder()
				.id(UUID.randomUUID())
				.user(user)
				.name("group1")
				.type(GroupType.POSITIVE)
				.build();

		groupService.addNewGroup(group1).block();

		Group result = groupService.findAllUserGroups().blockFirst();

		assertEquals(group1.getId(), result.getId());
	}

}
