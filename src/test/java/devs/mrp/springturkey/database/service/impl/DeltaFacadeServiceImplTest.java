package devs.mrp.springturkey.database.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.entity.enumerable.ActivityPlatform;
import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
import devs.mrp.springturkey.database.repository.ActivityRepository;
import devs.mrp.springturkey.database.repository.ConditionRepository;
import devs.mrp.springturkey.database.repository.DeviceRepository;
import devs.mrp.springturkey.database.repository.GroupRepository;
import devs.mrp.springturkey.database.repository.SettingRepository;
import devs.mrp.springturkey.database.repository.UserRepository;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.entity.ActivityCreationDelta;

@DataJpaTest
@EnableJpaRepositories(basePackages = "devs.mrp.springturkey.database.repository")
@EnableJpaAuditing
@EntityScan("devs.mrp.springturkey.database.*")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {DeltaFacadeServiceImpl.class})
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

	private TurkeyUser user;
	private TurkeyUser alternativeUser;

	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setup() {
		user = TurkeyUser.builder().email("some@mail.com").build();
		userRepository.save(user);
		alternativeUser = TurkeyUser.builder().email("other@mail.com").build();
		userRepository.save(alternativeUser);
	}

	@Test
	@DirtiesContext
	void createOne() throws JsonProcessingException {
		var preCreation = activityRepository.findAll();
		assertEquals(0, preCreation.size());

		Delta delta = creationDeltaBuilder().build();
		deltaFacadeService.pushCreation(delta);

		var postCreation = activityRepository.findAll();
		assertEquals(1, postCreation.size());
	}

	@Test
	void test() {
		fail("Not yet implemented");
	}

	private Delta.DeltaBuilder creationDeltaBuilder() throws JsonProcessingException {
		return Delta.builder()
				.timestamp(LocalDateTime.now())
				.deltaType(DeltaType.CREATION)
				.table(DeltaTable.ACTIVITY)
				.recordId(UUID.randomUUID())
				.fieldName("object")
				.textValue(objectMapper.writeValueAsString(activityBuilder().build()));
	}

	private ActivityCreationDelta.ActivityCreationDeltaBuilder activityBuilder() {
		return ActivityCreationDelta.builder()
				.activityName("default name")
				.activityType(ActivityPlatform.ANDROID_APP)
				.categoryType(CategoryType.NEGATIVE);
	}

}
