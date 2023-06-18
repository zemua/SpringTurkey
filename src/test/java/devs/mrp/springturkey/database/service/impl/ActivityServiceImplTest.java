package devs.mrp.springturkey.database.service.impl;

import java.util.UUID;

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
import devs.mrp.springturkey.database.entity.enumerable.ActivityType;
import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
import devs.mrp.springturkey.database.repository.ActivityRepository;
import devs.mrp.springturkey.database.repository.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataJpaTest
@EnableJpaRepositories(basePackages = "devs.mrp.springturkey.database.repository")
@EntityScan("devs.mrp.springturkey.database.*")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {LoginDetailsReaderImpl.class, ActivityServiceImpl.class})
class ActivityServiceImplTest {

	@Autowired
	private ActivityRepository activityRepository;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ActivityServiceImpl activityService;

	@Test
	@WithMockUser("some@mail.com")
	void findAllUserActivities() {
		TurkeyUser user = TurkeyUser.builder().email("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);

		TurkeyUser otherUser = TurkeyUser.builder().email("other@mail.com").build();
		userRepository.save(otherUser);

		Activity activity1 = Activity.builder()
				.activityName("app1")
				.activityType(ActivityType.ANDROID_APP)
				.categoryType(CategoryType.NEUTRAL)
				.user(user)
				.build();
		Activity activity2 = Activity.builder()
				.activityName("app2")
				.activityType(ActivityType.MAC_PROCESS)
				.categoryType(CategoryType.NEGATIVE)
				.user(user)
				.build();
		Activity activity3 = Activity.builder()
				.activityName("app3")
				.activityType(ActivityType.UBUNTU_PROCESS)
				.categoryType(CategoryType.POSITIVE)
				.user(user)
				.build();
		Activity activity4 = Activity.builder()
				.activityName("app4")
				.activityType(ActivityType.UBUNTU_PROCESS)
				.categoryType(CategoryType.POSITIVE)
				.user(otherUser)
				.build();
		activityRepository.save(activity1);
		activityRepository.save(activity2);
		activityRepository.save(activity3);
		activityRepository.save(activity4);

		Flux<Activity> fluxActivity = activityService.findAllUserActivites(user);

		StepVerifier.create(fluxActivity)
		.expectNextMatches(activity -> activity.getUser().getId().equals(userResult.getId()) && activity.getActivityName().equals("app1"))
		.expectNextMatches(activity -> activity.getUser().getId().equals(userResult.getId()) && activity.getActivityName().equals("app2"))
		.expectNextMatches(activity -> activity.getUser().getId().equals(userResult.getId()) && activity.getActivityName().equals("app3"))
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("wrong@mail.com")
	void findAllUserActivitiesWithWrongUser() {
		TurkeyUser user = TurkeyUser.builder().email("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);

		Activity activity1 = Activity.builder()
				.activityName("app1")
				.activityType(ActivityType.ANDROID_APP)
				.categoryType(CategoryType.NEUTRAL)
				.user(user)
				.build();
		Activity activity2 = Activity.builder()
				.activityName("app2")
				.activityType(ActivityType.MAC_PROCESS)
				.categoryType(CategoryType.NEGATIVE)
				.user(user)
				.build();
		Activity activity3 = Activity.builder()
				.activityName("app3")
				.activityType(ActivityType.UBUNTU_PROCESS)
				.categoryType(CategoryType.POSITIVE)
				.user(user)
				.build();
		activityRepository.save(activity1);
		activityRepository.save(activity2);
		activityRepository.save(activity3);

		Flux<Activity> fluxActivity = activityService.findAllUserActivites(user);

		StepVerifier.create(fluxActivity)
		.verifyComplete();
	}

	@Test
	@WithMockUser("some@mail.com")
	void insertNewActivity() {
		TurkeyUser user = TurkeyUser.builder().email("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);

		Activity activity1 = Activity.builder()
				.id(UUID.randomUUID())
				.activityName("app1")
				.activityType(ActivityType.ANDROID_APP)
				.categoryType(CategoryType.NEUTRAL)
				.user(user)
				.build();

		Mono<Integer> monoActivity = activityService.addNewActivity(activity1);

		StepVerifier.create(monoActivity)
		.expectNext(1)
		.expectComplete()
		.verify();

		Flux<Activity> fluxActivity = activityService.findAllUserActivites(user);

		StepVerifier.create(fluxActivity)
		.expectNextMatches(activity -> activity.getUser().getId().equals(userResult.getId()) && activity.getActivityName().equals("app1"))
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("wrong@mail.com")
	void insertNewActivityWrongUser() {
		TurkeyUser user = TurkeyUser.builder().email("some@mail.com").build();
		TurkeyUser userResult = userRepository.save(user);

		Activity activity1 = Activity.builder()
				.id(UUID.randomUUID())
				.activityName("app1")
				.activityType(ActivityType.ANDROID_APP)
				.categoryType(CategoryType.NEUTRAL)
				.user(user)
				.build();

		Mono<Integer> monoActivity = activityService.addNewActivity(activity1);

		StepVerifier.create(monoActivity)
		.expectNext(0)
		.expectComplete()
		.verify();

		Flux<Activity> fluxActivity = activityService.findAllUserActivites(user);

		StepVerifier.create(fluxActivity)
		.expectComplete()
		.verify();
	}

}
