package devs.mrp.springturkey.database.service.impl;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import devs.mrp.springturkey.components.impl.LoginDetailsReaderImpl;
import devs.mrp.springturkey.database.entity.Activity;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.entity.Uncloseable;
import devs.mrp.springturkey.database.entity.enumerable.ActivityType;
import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
import devs.mrp.springturkey.database.repository.ActivityRepository;
import devs.mrp.springturkey.database.repository.UncloseableRepository;
import devs.mrp.springturkey.database.repository.UserRepository;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataJpaTest
@EnableJpaRepositories(basePackages = "devs.mrp.springturkey.database.repository")
@EntityScan("devs.mrp.springturkey.database.*")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {LoginDetailsReaderImpl.class, UncloseableServiceImpl.class})
class UncloseableServiceImplTest {

	@Autowired
	private UncloseableRepository uncloseableRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private UncloseableServiceImpl uncloseableService;

	TurkeyUser user;
	TurkeyUser userResult;
	TurkeyUser otherUser;

	@BeforeEach
	void setup() {
		user = TurkeyUser.builder().email("some@mail.com").build();
		userResult = userRepository.save(user);
		otherUser = TurkeyUser.builder().email("other@mail.com").build();
		userRepository.save(otherUser);
	}

	@Test
	@WithMockUser("some@mail.com")
	void findAllUserUncloseables() {
		Activity activity1 = Activity.builder()
				.activityName("app1")
				.activityType(ActivityType.ANDROID_APP)
				.categoryType(CategoryType.NEUTRAL)
				.user(user)
				.build();
		Uncloseable uncloseable1 = Uncloseable.builder()
				.id(activity1.getId())
				.activity(activity1)
				.preventClosing(true)
				.build();
		Activity activity2 = Activity.builder()
				.activityName("app2")
				.activityType(ActivityType.ANDROID_APP)
				.categoryType(CategoryType.NEUTRAL)
				.user(user)
				.build();
		Uncloseable uncloseable2 = Uncloseable.builder()
				.id(activity2.getId())
				.activity(activity2)
				.preventClosing(true)
				.build();
		Activity activity3 = Activity.builder()
				.activityName("app3")
				.activityType(ActivityType.ANDROID_APP)
				.categoryType(CategoryType.NEUTRAL)
				.user(otherUser)
				.build();
		Uncloseable uncloseable3 = Uncloseable.builder()
				.id(activity3.getId())
				.activity(activity3)
				.preventClosing(true)
				.build();

		activityRepository.save(activity1);
		activityRepository.save(activity2);
		activityRepository.save(activity3);

		uncloseableRepository.save(uncloseable1);
		uncloseableRepository.save(uncloseable2);
		uncloseableRepository.save(uncloseable3);

		Flux<Uncloseable> fluxUncloseable = uncloseableService.findAllUserUncloseables(user);

		StepVerifier.create(fluxUncloseable)
		.expectNextMatches(uc -> uc.getActivity().getUser().getId().equals(userResult.getId()) && uc.getActivity().getActivityName().equals("app1"))
		.expectNextMatches(uc -> uc.getActivity().getUser().getId().equals(userResult.getId()) && uc.getActivity().getActivityName().equals("app2"))
		.expectComplete()
		.verify();

		fail("Not yet implemented");
	}

	@Test
	void idDifferentFromActivity() {
		fail("Not yet implemented");
	}

	@Test
	void test() {
		fail("Not yet implemented");
	}

}
