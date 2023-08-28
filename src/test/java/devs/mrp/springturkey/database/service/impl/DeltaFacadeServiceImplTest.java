package devs.mrp.springturkey.database.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.util.Map;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import devs.mrp.springturkey.Exceptions.TurkeySurpriseException;
import devs.mrp.springturkey.components.impl.LoginDetailsReaderImpl;
import devs.mrp.springturkey.database.entity.Activity;
import devs.mrp.springturkey.database.entity.Group;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.entity.enumerable.ActivityPlatform;
import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
import devs.mrp.springturkey.database.entity.enumerable.GroupType;
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
@ContextConfiguration(classes = {DeltaFacadeServiceImpl.class, EntityFromDeltaDaoImpl.class, UserServiceImpl.class, LoginDetailsReaderImpl.class})
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
		objectMapper.registerModule(new JavaTimeModule());
	}

	@Test
	@WithMockUser("some@mail.com")
	@DirtiesContext
	void createOneSetting() throws JsonProcessingException {
		var preSettings = settingRepository.findAll();
		var preDeltas = deltaRepository.findAll();
		assertEquals(0, preSettings.size());
		assertEquals(0, preDeltas.size());

		Delta delta = settingCreationDeltaBuilder().build();
		Integer result = deltaFacadeService.pushCreation(delta).block();

		var postSettings = settingRepository.findAll();
		var postDeltas = deltaRepository.findAll();
		assertEquals(1, postSettings.size());
		assertEquals(1, postDeltas.size());
		assertEquals(1, result);
	}

	@Test
	@WithMockUser("some@mail.com")
	@DirtiesContext
	void createOneActivity() throws JsonProcessingException {
		var preActivities = activityRepository.findAll();
		var preDeltas = deltaRepository.findAll();
		assertEquals(0, preActivities.size());
		assertEquals(0, preDeltas.size());

		Delta delta = activityCreationDeltaBuilder().build();
		Integer result = deltaFacadeService.pushCreation(delta).block();

		var postActivities = activityRepository.findAll();
		var postDeltas = deltaRepository.findAll();
		assertEquals(1, postActivities.size());
		assertEquals(1, postDeltas.size());
		assertEquals(1, result);

		Map<String,String> expected = objectMapper.readValue(delta.getTextValue(), Map.class);
		Activity saved = postActivities.get(0);
		assertEquals(expected.get("activityName"), saved.getActivityName());
		assertNotNull(saved.getUser());
		assertEquals(expected.get("activityType"), saved.getActivityType().name());
		assertEquals(expected.get("categoryType"), saved.getCategoryType().name());
		assertNull(saved.getGroup());
		assertEquals(expected.get("preventClosing"), saved.getPreventClosing());
	}

	@Test
	@WithMockUser("some@mail.com")
	@DirtiesContext
	void createOneActivityWithGroup() throws JsonProcessingException, InterruptedException {
		var preActivities = activityRepository.findAll();
		var preDeltas = deltaRepository.findAll();
		assertEquals(0, preActivities.size());
		assertEquals(0, preDeltas.size());

		Group group = groupBuilder().build();
		groupRepository.save(group);
		Group fetchedGroup = groupRepository.findAll().get(0);

		Delta delta = activityCreationDeltaBuilder()
				.textValue(objectMapper.writeValueAsString(activityBuilder()
						.groupId(fetchedGroup.getId())
						.build()))
				.build();
		Integer result = deltaFacadeService.pushCreation(delta).block();

		var postActivities = activityRepository.findAll();
		var postDeltas = deltaRepository.findAll();
		assertEquals(1, postActivities.size());
		assertEquals(1, postDeltas.size());
		assertEquals(1, result);

		Map<String,String> expected = objectMapper.readValue(delta.getTextValue(), Map.class);
		Activity saved = postActivities.get(0);
		assertEquals(expected.get("activityName"), saved.getActivityName());
		assertNotNull(saved.getUser());
		assertEquals(expected.get("activityType"), saved.getActivityType().name());
		assertEquals(expected.get("categoryType"), saved.getCategoryType().name());
		assertEquals(expected.get("groupId"), saved.getGroup().getId().toString());
		assertEquals(expected.get("preventClosing"), saved.getPreventClosing());
	}

	@Test
	@WithMockUser("some@mail.com")
	@DirtiesContext
	void createOneActivityWithWrongUuid() throws JsonProcessingException, InterruptedException {
		var preActivities = activityRepository.findAll();
		var preDeltas = deltaRepository.findAll();
		assertEquals(0, preActivities.size());
		assertEquals(0, preDeltas.size());

		Map<String,String> deltaMap = objectMapper.convertValue(activityBuilder().build(), Map.class);
		deltaMap.put("groupId", "invalid");
		Delta delta = activityCreationDeltaBuilder()
				.textValue(objectMapper.writeValueAsString(deltaMap))
				.build();

		assertThrows(TurkeySurpriseException.class, () -> deltaFacadeService.pushCreation(delta).block());
	}

	@Test
	void createOneGroup() {
		fail("not yet implemented");
	}

	@Test
	void createOneCondition() {
		fail("not yet implemented");
	}

	@Test
	void testSavedWithExistingIdPreservesId() {
		fail("not yet implemented");
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

	private Group.GroupBuilder groupBuilder() {
		return Group.builder()
				.id(UUID.randomUUID())
				.name("some group name")
				.type(GroupType.NEGATIVE)
				.user(user);
	}

}
