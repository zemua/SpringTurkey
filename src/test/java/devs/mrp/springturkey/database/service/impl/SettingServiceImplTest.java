package devs.mrp.springturkey.database.service.impl;

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

import devs.mrp.springturkey.Exceptions.AlreadyExistsException;
import devs.mrp.springturkey.Exceptions.DoesNotBelongToUserException;
import devs.mrp.springturkey.components.impl.LoginDetailsReaderImpl;
import devs.mrp.springturkey.database.entity.Setting;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.entity.enumerable.PlatformType;
import devs.mrp.springturkey.database.repository.SettingRepository;
import devs.mrp.springturkey.database.repository.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataJpaTest
@EnableJpaRepositories(basePackages = "devs.mrp.springturkey.database.repository")
@EntityScan("devs.mrp.springturkey.database.*")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {LoginDetailsReaderImpl.class, SettingServiceImpl.class})
@EnableJpaAuditing
class SettingServiceImplTest {

	@Autowired
	private SettingRepository settingRepository;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SettingServiceImpl settingService;

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
	void findAllUserSettings() {
		Setting setting1 = Setting.builder()
				.user(user)
				.platform(PlatformType.DESKTOP)
				.settingKey("key1")
				.settingValue("value1")
				.build();
		Setting setting2 = Setting.builder()
				.user(user)
				.platform(PlatformType.DESKTOP)
				.settingKey("key2")
				.settingValue("value2")
				.build();
		Setting setting3 = Setting.builder()
				.user(user)
				.platform(PlatformType.DESKTOP)
				.settingKey("key3")
				.settingValue("value3")
				.build();
		Setting setting4 = Setting.builder()
				.user(otherUser)
				.platform(PlatformType.DESKTOP)
				.settingKey("key4")
				.settingValue("value4")
				.build();
		Setting setting5 = Setting.builder()
				.user(otherUser)
				.platform(PlatformType.DESKTOP)
				.settingKey("key1")
				.settingValue("value5")
				.build();
		settingRepository.save(setting1);
		settingRepository.save(setting2);
		settingRepository.save(setting3);
		settingRepository.save(setting4);
		settingRepository.save(setting5);

		Flux<Setting> fluxSetting = settingService.findAllUserSettings();

		StepVerifier.create(fluxSetting)
		.expectNextMatches(s -> s.getUser().getId().equals(user.getId()) && s.getSettingKey().equals("key1") && s.getCreated() != null && s.getEdited() != null)
		.expectNextMatches(s -> s.getUser().getId().equals(user.getId()) && s.getSettingKey().equals("key2"))
		.expectNextMatches(s -> s.getUser().getId().equals(user.getId()) && s.getSettingKey().equals("key3"))
		.verifyComplete();
	}

	@Test
	@WithMockUser("wrong@mail.com")
	void findAllUserSettingsWithWrongUser() {
		Setting setting1 = Setting.builder()
				.user(user)
				.platform(PlatformType.DESKTOP)
				.settingKey("key1")
				.settingValue("value1")
				.build();
		Setting setting2 = Setting.builder()
				.user(user)
				.platform(PlatformType.DESKTOP)
				.settingKey("key2")
				.settingValue("value2")
				.build();
		Setting setting3 = Setting.builder()
				.user(user)
				.platform(PlatformType.DESKTOP)
				.settingKey("key3")
				.settingValue("value3")
				.build();

		settingRepository.save(setting1);
		settingRepository.save(setting2);
		settingRepository.save(setting3);

		Flux<Setting> fluxSetting = settingService.findAllUserSettings();

		StepVerifier.create(fluxSetting)
		.verifyComplete();
	}

	@Test
	@WithMockUser("some@mail.com")
	void insertNewSetting() {
		Setting setting1 = Setting.builder()
				.id(UUID.randomUUID())
				.user(user)
				.platform(PlatformType.DESKTOP)
				.settingKey("key1")
				.settingValue("value1")
				.build();

		Mono<Integer> monoSetting = settingService.addNewSetting(setting1);

		StepVerifier.create(monoSetting)
		.expectNext(1)
		.expectComplete()
		.verify();

		Flux<Setting> fluxSetting = settingService.findAllUserSettings();

		StepVerifier.create(fluxSetting)
		.expectNextMatches(s -> s.getUser().getId().equals(userResult.getId()) && s.getId().equals(setting1.getId()) && s.getCreated() != null && s.getEdited() != null)
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("some@mail.com")
	void insertNewSettingWithEmptyId() {
		Setting setting1 = Setting.builder()
				.user(user)
				.platform(PlatformType.DESKTOP)
				.settingKey("key1")
				.settingValue("value1")
				.build();

		Mono<Integer> monoSetting = settingService.addNewSetting(setting1);

		StepVerifier.create(monoSetting)
		.expectNext(1)
		.expectComplete()
		.verify();

		Flux<Setting> fluxSetting = settingService.findAllUserSettings();

		StepVerifier.create(fluxSetting)
		.expectNextMatches(s -> s.getUser().getId().equals(userResult.getId()) && s.getId() != null && s.getSettingKey().equals("key1"))
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("wrong@mail.com")
	void insertNewSettingWrongUser() {
		Setting setting1 = Setting.builder()
				.id(UUID.randomUUID())
				.user(user)
				.platform(PlatformType.DESKTOP)
				.settingKey("key1")
				.settingValue("value1")
				.build();

		Mono<Integer> monoSetting = settingService.addNewSetting(setting1);

		StepVerifier.create(monoSetting)
		.expectError(DoesNotBelongToUserException.class)
		.verify();

		Flux<Setting> fluxSetting = settingService.findAllUserSettings();

		StepVerifier.create(fluxSetting)
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("some@mail.com")
	void insertRepeatedId() {
		Setting setting1 = Setting.builder()
				.id(UUID.randomUUID())
				.user(user)
				.platform(PlatformType.DESKTOP)
				.settingKey("key1")
				.settingValue("value1")
				.build();
		Setting setting2 = Setting.builder()
				.id(setting1.getId())
				.user(user)
				.platform(PlatformType.DESKTOP)
				.settingKey("key2")
				.settingValue("value2")
				.build();

		Mono<Integer> monoSetting = settingService.addNewSetting(setting1);

		StepVerifier.create(monoSetting)
		.expectNext(1)
		.expectComplete()
		.verify();

		Mono<Integer> monoSetting2 = settingService.addNewSetting(setting2);

		StepVerifier.create(monoSetting2)
		.expectError(AlreadyExistsException.class)
		.verify();
	}

	@Test
	@WithMockUser("some@mail.com")
	void insertRepeatedConstraint() {
		Setting setting1 = Setting.builder()
				.id(UUID.randomUUID())
				.user(user)
				.platform(PlatformType.DESKTOP)
				.settingKey("key1")
				.settingValue("value1")
				.build();
		Setting setting2 = Setting.builder()
				.id(UUID.randomUUID())
				.user(user)
				.platform(PlatformType.DESKTOP)
				.settingKey("key1")
				.settingValue("value1")
				.build();

		Mono<Integer> monoSetting = settingService.addNewSetting(setting1);

		StepVerifier.create(monoSetting)
		.expectNext(1)
		.expectComplete()
		.verify();

		Mono<Integer> monoSetting2 = settingService.addNewSetting(setting2);

		StepVerifier.create(monoSetting2)
		.expectError(AlreadyExistsException.class)
		.verify();
	}

}
