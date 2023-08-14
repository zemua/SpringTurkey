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
import devs.mrp.springturkey.database.entity.enumerable.PlatformType;
import devs.mrp.springturkey.database.repository.ActivityRepository;
import devs.mrp.springturkey.database.repository.ConditionRepository;
import devs.mrp.springturkey.database.repository.DeltaRepository;
import devs.mrp.springturkey.database.repository.DeviceRepository;
import devs.mrp.springturkey.database.repository.GroupRepository;
import devs.mrp.springturkey.database.repository.SettingRepository;
import devs.mrp.springturkey.database.repository.UserRepository;
import devs.mrp.springturkey.database.repository.dao.impl.EntityFromDeltaDaoImpl;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.entity.ActivityCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.SettingCreationDelta;

@DataJpaTest
@EnableJpaRepositories(basePackages = "devs.mrp.springturkey.database.repository")
@EnableJpaAuditing
@EntityScan("devs.mrp.springturkey.database.*")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {DeltaFacadeServiceImpl.class, EntityFromDeltaDaoImpl.class})
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
	private DeltaRepository deltaRepository;

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
	void createOneActivity() throws JsonProcessingException {
		var preActivities = activityRepository.findAll();
		var preDeltas = deltaRepository.findAll();
		assertEquals(0, preActivities.size());
		assertEquals(0, preDeltas.size());

		Delta delta = activityCreationDeltaBuilder().build();
		deltaFacadeService.pushCreation(delta);

		var postActivities = activityRepository.findAll();
		var postDeltas = deltaRepository.findAll();
		assertEquals(1, postActivities.size());
		assertEquals(1, postDeltas.size());
	}

	@Test
	@DirtiesContext
	void createOneSetting() throws JsonProcessingException {
		var preSettings = settingRepository.findAll();
		var preDeltas = deltaRepository.findAll();
		assertEquals(0, preSettings.size());
		assertEquals(0, preDeltas.size());

		Delta delta = settingCreationDeltaBuilder().build();
		deltaFacadeService.pushCreation(delta);

		var postSettings = settingRepository.findAll();
		var postDeltas = deltaRepository.findAll();
		assertEquals(1, postSettings.size());
		assertEquals(1, postDeltas.size());
	}

	@Test
	void test() {
		fail("Not yet implemented");
	}

	private Delta.DeltaBuilder activityCreationDeltaBuilder() throws JsonProcessingException {
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

	private Delta.DeltaBuilder settingCreationDeltaBuilder() throws JsonProcessingException {
		return Delta.builder()
				.timestamp(LocalDateTime.now())
				.deltaType(DeltaType.CREATION)
				.table(DeltaTable.SETTING)
				.recordId(UUID.randomUUID())
				.fieldName("object")
				.textValue(objectMapper.writeValueAsString(settingBuilder().build()));
	}

	private SettingCreationDelta.SettingCreationDeltaBuilder settingBuilder() {
		return SettingCreationDelta.builder()
				.platformType(PlatformType.ALL)
				.settingKey("someKey")
				.settingValue("some value");
	}

}
