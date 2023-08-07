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
import org.springframework.test.context.ContextConfiguration;

import devs.mrp.springturkey.components.impl.LoginDetailsReaderImpl;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.repository.ActivityRepository;
import devs.mrp.springturkey.database.repository.ConditionRepository;
import devs.mrp.springturkey.database.repository.DeviceRepository;
import devs.mrp.springturkey.database.repository.GroupRepository;
import devs.mrp.springturkey.database.repository.SettingRepository;
import devs.mrp.springturkey.database.repository.UserRepository;

@DataJpaTest
@EnableJpaRepositories(basePackages = "devs.mrp.springturkey.database.repository")
@EnableJpaAuditing
@EntityScan("devs.mrp.springturkey.database.*")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {LoginDetailsReaderImpl.class, ActivityServiceImpl.class})
class DeltaFacadeServiceImplTest {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private ActivityRepository activityRepository;
	@Autowired
	private ConditionRepository conditionRepository;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private SettingRepository settingRepository;

	@Autowired
	private DeltaFacadeServiceImpl deltaFacadeService;

	@BeforeEach
	void setup() {
		TurkeyUser user = TurkeyUser.builder().email("some@mail.com").build();
		userRepository.save(user);
	}

	@Test
	void test() {
		fail("Not yet implemented");
	}

}
