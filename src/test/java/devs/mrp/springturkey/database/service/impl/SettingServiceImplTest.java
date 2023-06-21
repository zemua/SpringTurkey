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
import devs.mrp.springturkey.database.entity.Setting;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.entity.enumerable.PlatformType;
import devs.mrp.springturkey.database.entity.ids.SettingId;
import devs.mrp.springturkey.database.repository.SettingRepository;
import devs.mrp.springturkey.database.repository.UserRepository;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataJpaTest
@EnableJpaRepositories(basePackages = "devs.mrp.springturkey.database.repository")
@EntityScan("devs.mrp.springturkey.database.*")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {LoginDetailsReaderImpl.class, SettingServiceImpl.class})
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
				.settingId(new SettingId(user, PlatformType.DESKTOP, "key1"))
				.settingValue("value1")
				.build();
		Setting setting2 = Setting.builder()
				.settingId(new SettingId(user, PlatformType.DESKTOP, "key2"))
				.settingValue("value2")
				.build();
		Setting setting3 = Setting.builder()
				.settingId(new SettingId(user, PlatformType.DESKTOP, "key3"))
				.settingValue("value3")
				.build();
		Setting setting4 = Setting.builder()
				.settingId(new SettingId(otherUser, PlatformType.DESKTOP, "key4"))
				.settingValue("value4")
				.build();
		Setting setting5 = Setting.builder()
				.settingId(new SettingId(otherUser, PlatformType.DESKTOP, "key1"))
				.settingValue("value5")
				.build();
		settingRepository.save(setting1);
		settingRepository.save(setting2);
		settingRepository.save(setting3);
		settingRepository.save(setting4);
		settingRepository.save(setting5);

		Flux<Setting> fluxSetting = settingService.findAllUserSettings(user);

		StepVerifier.create(fluxSetting)
		.expectNextMatches(s -> s.getUser().getId().equals(user.getId()) && s.getSettingId().equals("key1"))
		.expectNextMatches(s -> s.getUser().getId().equals(user.getId()) && s.getSettingId().equals("key2"))
		.expectNextMatches(s -> s.getSettingId().getUser().getId().equals(user.getId()) && s.getSettingId().equals("key3"))
		.verifyComplete();
	}

	@Test
	void test() {
		fail("Not yet implemented");
	}

}
