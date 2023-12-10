package devs.mrp.springturkey.database.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
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
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import devs.mrp.springturkey.components.impl.LoginDetailsReaderImpl;
import devs.mrp.springturkey.database.entity.Activity;
import devs.mrp.springturkey.database.entity.DeltaEntity;
import devs.mrp.springturkey.database.entity.Group;
import devs.mrp.springturkey.database.entity.Setting;
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
import devs.mrp.springturkey.database.repository.dao.impl.EntityFromDeltaDaoCreator;
import devs.mrp.springturkey.database.repository.dao.impl.EntityFromDeltaDaoDeleter;
import devs.mrp.springturkey.database.repository.dao.impl.EntityFromDeltaDaoModifier;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.entity.ActivityCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.ConditionCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.GroupCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.SettingCreationDelta;
import devs.mrp.springturkey.exceptions.TurkeySurpriseException;
import devs.mrp.springturkey.utils.impl.ObjectMapperProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataJpaTest
@EnableJpaRepositories(basePackages = "devs.mrp.springturkey.database.repository")
@EnableJpaAuditing
@EntityScan("devs.mrp.springturkey.database.*")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = { DeltaServiceFacadeImpl.class, EntityFromDeltaDaoCreator.class, EntityFromDeltaDaoModifier.class, EntityFromDeltaDaoDeleter.class, UserServiceImpl.class,
		LoginDetailsReaderImpl.class, ObjectMapperProvider.class })
class DeltaServiceFacadeImplTest {

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
	private DeltaServiceFacadeImpl deltaFacadeService;

	private TurkeyUser user;
	private TurkeyUser alternativeUser;

	private ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

	static {
		System.setProperty("logging.level.devs.mrp.springturkey", "DEBUG");
	}

	@BeforeEach
	void setup() {
		user = TurkeyUser.builder().externalId("some@mail.com").build();
		user = userRepository.save(user);
		alternativeUser = TurkeyUser.builder().externalId("other@mail.com").build();
		alternativeUser = userRepository.save(alternativeUser);
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
	void createOneActivityWithNullGroup() throws JsonProcessingException {
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

		Map<String, Object> expected = delta.getJsonValue();
		Activity saved = postActivities.get(0);
		assertEquals(expected.get("activityName"), saved.getActivityName());
		assertEquals("some@mail.com", saved.getUser().getExternalId());
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
				.jsonValue(
						objectMapper.convertValue(activityBuilder().groupId(fetchedGroup.getId()).build(), Map.class))
				.build();
		Integer result = deltaFacadeService.pushCreation(delta).block();

		var postActivities = activityRepository.findAll();
		var postDeltas = deltaRepository.findAll();
		assertEquals(1, postActivities.size());
		assertEquals(1, postDeltas.size());
		assertEquals(1, result);

		Map<String, Object> expected = delta.getJsonValue();
		Activity saved = postActivities.get(0);
		assertEquals(expected.get("activityName"), saved.getActivityName());
		assertEquals("some@mail.com", saved.getUser().getExternalId());
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

		Map<String, Object> deltaMap = objectMapper.convertValue(activityBuilder().build(), Map.class);
		deltaMap.put("groupId", "invalid");
		Delta delta = activityCreationDeltaBuilder().jsonValue(deltaMap).build();

		assertThrows(TurkeySurpriseException.class, () -> deltaFacadeService.pushCreation(delta));
	}

	@Test
	@WithMockUser("some@mail.com")
	@DirtiesContext
	void createOneGroup() throws JsonProcessingException {
		var preSettings = groupRepository.findAll();
		var preDeltas = deltaRepository.findAll();
		assertEquals(0, preSettings.size());
		assertEquals(0, preDeltas.size());

		Delta delta = groupCreationDeltaBuilder().build();
		Integer result = deltaFacadeService.pushCreation(delta).block();

		var postSettings = groupRepository.findAll();
		var postDeltas = deltaRepository.findAll();
		assertEquals(1, postSettings.size());
		assertEquals(1, postDeltas.size());
		assertEquals(1, result);
	}

	@Test
	@WithMockUser("some@mail.com")
	@DirtiesContext
	void createWithBooleanValue() throws JsonProcessingException {
		var preGroups = groupRepository.findAll();
		var preDeltas = deltaRepository.findAll();
		assertEquals(0, preGroups.size());
		assertEquals(0, preDeltas.size());

		Delta delta = groupCreationDeltaBuilder()
				.jsonValue(objectMapper.convertValue(groupCreationBuilder().preventClose(true).build(), Map.class))
				.build();
		Integer result = deltaFacadeService.pushCreation(delta).block();

		var postGroups = groupRepository.findAll();
		var postDeltas = deltaRepository.findAll();
		assertEquals(1, postGroups.size());
		assertEquals(1, postDeltas.size());
		assertEquals(1, result);
	}

	@Test
	@WithMockUser("some@mail.com")
	@DirtiesContext
	void createWithNumberValue() throws JsonProcessingException {
		var preSettings = groupRepository.findAll();
		var preDeltas = deltaRepository.findAll();
		assertEquals(0, preSettings.size());
		assertEquals(0, preDeltas.size());

		Map<String, Object> groupAsMap = objectMapper.convertValue(groupCreationBuilder().build(), Map.class);
		groupAsMap.put("name", 123);

		Delta delta = groupCreationDeltaBuilder().jsonValue(groupAsMap).build();
		Integer result = deltaFacadeService.pushCreation(delta).block();

		var postSettings = groupRepository.findAll();
		var postDeltas = deltaRepository.findAll();
		assertEquals(1, postSettings.size());
		assertEquals(1, postDeltas.size());
		assertEquals(1, result);
	}

	@Test
	@WithMockUser("some@mail.com")
	@DirtiesContext
	void createOneCondition() throws JsonProcessingException {
		var preGroups = activityRepository.findAll();
		var preDeltas = deltaRepository.findAll();
		var preConditions = conditionRepository.findAll();
		assertEquals(0, preGroups.size());
		assertEquals(0, preDeltas.size());
		assertEquals(0, preConditions.size());

		Group group1 = groupBuilder().build();
		groupRepository.save(group1);
		Group group2 = groupBuilder().build();
		groupRepository.save(group2);
		List<Group> fetchedGroups = groupRepository.findAll();
		Group fetchedGroup1 = fetchedGroups.get(0);
		Group fetchedGroup2 = fetchedGroups.get(1);

		Delta delta = conditionCreationDeltaBuilder()
				.jsonValue(objectMapper.convertValue(
						conditionCreationBuilder(fetchedGroup1.getId(), fetchedGroup2.getId()).build(), Map.class))
				.build();
		Integer result = deltaFacadeService.pushCreation(delta).block();

		var postGroups = groupRepository.findAll();
		var postConditions = conditionRepository.findAll();
		var postDeltas = deltaRepository.findAll();
		assertEquals(2, postGroups.size());
		assertEquals(1, postConditions.size());
		assertEquals(1, postDeltas.size());
		assertEquals(1, result);
	}

	@Test
	@WithMockUser("some@mail.com")
	@DirtiesContext
	void testWrongTable() throws JsonProcessingException {
		var preGroups = activityRepository.findAll();
		var preDeltas = deltaRepository.findAll();
		var preConditions = conditionRepository.findAll();
		assertEquals(0, preGroups.size());
		assertEquals(0, preDeltas.size());
		assertEquals(0, preConditions.size());

		Group group1 = groupBuilder().build();
		groupRepository.save(group1);
		Group group2 = groupBuilder().build();
		groupRepository.save(group2);
		List<Group> fetchedGroups = groupRepository.findAll();
		Group fetchedGroup1 = fetchedGroups.get(0);
		Group fetchedGroup2 = fetchedGroups.get(1);

		Delta delta = conditionCreationDeltaBuilder().table(DeltaTable.ACTIVITY)
				.jsonValue(objectMapper.convertValue(
						conditionCreationBuilder(fetchedGroup1.getId(), fetchedGroup2.getId()).build(), Map.class))
				.build();

		assertThrows(TurkeySurpriseException.class, () -> deltaFacadeService.pushCreation(delta));
	}

	@Test
	@WithMockUser("some@mail.com")
	@DirtiesContext
	void testCreateWithExistingId() throws JsonProcessingException {
		Group group = groupBuilder().build();
		groupRepository.save(group);
		Group fetchedGroup = groupRepository.findAll().get(0);

		Delta delta = groupCreationDeltaBuilder().recordId(fetchedGroup.getId()).build();
		Mono<Integer> result = deltaFacadeService.pushCreation(delta);
		assertThrows(TurkeySurpriseException.class, () -> result.block());
	}

	@Test
	@WithMockUser("some@mail.com")
	@DirtiesContext
	void createdDeltaMatches() throws JsonProcessingException {
		var preSettings = settingRepository.findAll();
		var preDeltas = deltaRepository.findAll();
		assertEquals(0, preSettings.size());
		assertEquals(0, preDeltas.size());

		Delta delta = settingCreationDeltaBuilder().build();
		Integer result = deltaFacadeService.pushCreation(delta).block();

		var postSettings = settingRepository.findAll();
		var postDeltas = deltaRepository.findAll();
		DeltaEntity storedDelta = postDeltas.get(0);

		assertEquals(delta.getDeltaType(), storedDelta.getDeltaType());
		assertEquals(delta.getTable(), storedDelta.getDeltaTable());
		assertEquals(delta.getRecordId(), storedDelta.getRecordId());
		assertEquals(delta.getJsonValue(), objectMapper.readValue(storedDelta.getJsonValue(), Map.class));
	}

	@Test
	@WithMockUser("some@mail.com")
	@DirtiesContext
	void persistingEntityErrorCancelsPersistOfDelta() throws JsonProcessingException, InterruptedException {
		Map<String, Object> deltaMap = objectMapper.convertValue(activityBuilder().build(), Map.class);
		deltaMap.put("groupId", "invalid");
		Delta delta = activityCreationDeltaBuilder().jsonValue(deltaMap).build();

		var preActivities = activityRepository.findAll();
		var preDeltas = deltaRepository.findAll();
		assertEquals(0, preActivities.size());
		assertEquals(0, preDeltas.size());

		assertThrows(TurkeySurpriseException.class, () -> deltaFacadeService.pushCreation(delta));

		var postActivities = activityRepository.findAll();
		var postDeltas = deltaRepository.findAll();
		assertEquals(0, postActivities.size());
		assertEquals(0, postDeltas.size());
	}

	@Test
	@WithMockUser("some@mail.com")
	@DirtiesContext
	void modifyOneSetting() throws JsonProcessingException {
		Setting setting = Setting.builder().user(user).platform(PlatformType.ALL).settingKey("someKey")
				.settingValue("original value").build();
		setting = settingRepository.save(setting);
		var preSettings = settingRepository.findAll();
		var preDeltas = deltaRepository.findAll();
		assertEquals(1, preSettings.size());
		assertEquals(0, preDeltas.size());

		Delta delta = settingModificationDeltaBuilder(setting.getId()).build();
		Integer result = deltaFacadeService.pushModification(delta).block();

		var postSettings = settingRepository.findAll();
		var postDeltas = deltaRepository.findAll();
		assertEquals(1, postSettings.size());
		assertEquals(1, postDeltas.size());
		assertEquals(1, result);

		Setting updatedSetting = postSettings.get(0);
		assertEquals("some value", updatedSetting.getSettingValue());
	}

	@Test
	@WithMockUser("some@mail.com")
	@DirtiesContext
	void deleteOneSetting() throws JsonProcessingException {
		Setting setting = Setting.builder().user(user).platform(PlatformType.ALL).settingKey("someKey")
				.settingValue("original value").build();
		setting = settingRepository.save(setting);
		var preSettings = settingRepository.findAll();
		var preDeltas = deltaRepository.findAll();
		assertEquals(1, preSettings.size());
		assertEquals(0, preDeltas.size());

		Delta delta = settingDeletionDeltaBuilder(setting.getId()).build();
		Integer result = deltaFacadeService.pushDeletion(delta).block();

		var postSettings = settingRepository.findAll();
		var postDeltas = deltaRepository.findAll();
		assertEquals(0, postSettings.size());
		assertEquals(1, postDeltas.size());
		assertEquals(1, result);
	}

	@Test
	@WithMockUser("some@mail.com")
	@DirtiesContext
	void dontDeleteOneSetting() throws JsonProcessingException {
		Setting setting = Setting.builder().user(user).platform(PlatformType.ALL).settingKey("someKey")
				.settingValue("original value").build();
		setting = settingRepository.save(setting);
		var preSettings = settingRepository.findAll();
		var preDeltas = deltaRepository.findAll();
		assertEquals(1, preSettings.size());
		assertEquals(0, preDeltas.size());

		Delta delta = settingDeletionDeltaBuilder(setting.getId()).jsonValue(Map.of("deletion", false)).build();
		Integer result = deltaFacadeService.pushDeletion(delta).block();

		var postSettings = settingRepository.findAll();
		var postDeltas = deltaRepository.findAll();
		assertEquals(1, postSettings.size());
		assertEquals(1, postDeltas.size());
		assertEquals(1, result);
	}

	@Test
	@WithMockUser("some@mail.com")
	@DirtiesContext
	void testFindAfterPosition() throws JsonProcessingException {
		assertEquals(0, deltaRepository.findAll().size());

		Delta delta1 = settingCreationDeltaBuilder().jsonValue(Map.of("platformType", "ALL", "settingKey", "someSetting1", "settingValue", "someValue")).build();
		Delta delta2 = settingCreationDeltaBuilder().jsonValue(Map.of("platformType", "ALL", "settingKey", "someSetting2", "settingValue", "someValue")).build();
		Delta delta3 = settingCreationDeltaBuilder().jsonValue(Map.of("platformType", "ALL", "settingKey", "someSetting3", "settingValue", "someValue")).build();
		Delta delta4 = settingCreationDeltaBuilder().jsonValue(Map.of("platformType", "ALL", "settingKey", "someSetting4", "settingValue", "someValue")).build();
		deltaFacadeService.pushCreation(delta1).block();
		deltaFacadeService.pushCreation(delta2).block();
		deltaFacadeService.pushCreation(delta3).block();
		deltaRepository.save(activityCreationDeltaBuilder().build().toEntity(objectMapper, alternativeUser));
		deltaFacadeService.pushCreation(delta4).block();

		assertEquals(5, deltaRepository.findAll().size());

		Flux<Delta> result = deltaFacadeService.findAfterPosition(2L);

		StepVerifier
		.create(result)
		.expectNextMatches(d -> delta3.getRecordId().equals(d.getRecordId()))
		// skips other user's deltas
		.expectNextMatches(d -> delta4.getRecordId().equals(d.getRecordId()))
		.expectComplete()
		.verify();
	}

	@Test
	@WithMockUser("some@mail.com")
	@DirtiesContext
	void testFindMostRecentTimestamp() throws JsonProcessingException {
		assertEquals(0, deltaRepository.findAll().size());

		Delta delta1 = settingCreationDeltaBuilder().timestamp(LocalDateTime.now()).build();
		deltaFacadeService.pushCreation(delta1).block();

		Delta delta2 = settingModificationDeltaBuilder(delta1.getRecordId()).timestamp(LocalDateTime.now().plusHours(1)).build();
		deltaFacadeService.pushModification(delta2).block();

		Delta delta3 = settingModificationDeltaBuilder(delta1.getRecordId()).timestamp(LocalDateTime.now().minusHours(1)).build();
		deltaFacadeService.pushModification(delta3).block();

		Delta result = deltaFacadeService.findMostRecentTimestampForRecord(delta1.getRecordId()).block();

		assertEquals(delta2.getTimestamp(), result.getTimestamp());
	}

	private Delta.DeltaBuilder activityCreationDeltaBuilder() throws JsonProcessingException {
		return Delta.builder().timestamp(LocalDateTime.now()).deltaType(DeltaType.CREATION).table(DeltaTable.ACTIVITY)
				.recordId(UUID.randomUUID()).jsonValue(objectMapper.convertValue(activityBuilder().build(), Map.class));
	}

	private ActivityCreationDelta.ActivityCreationDeltaBuilder activityBuilder() {
		return ActivityCreationDelta.builder().activityName("default name").activityType(ActivityPlatform.ANDROID_APP)
				.categoryType(CategoryType.NEGATIVE);
	}

	private Delta.DeltaBuilder settingCreationDeltaBuilder() throws JsonProcessingException {
		return Delta.builder().timestamp(LocalDateTime.now()).deltaType(DeltaType.CREATION).table(DeltaTable.SETTING)
				.recordId(UUID.randomUUID()).jsonValue(objectMapper.convertValue(settingBuilder().build(), Map.class));
	}

	private Delta.DeltaBuilder settingModificationDeltaBuilder(UUID uuid) {
		return Delta.builder().timestamp(LocalDateTime.now()).deltaType(DeltaType.MODIFICATION).table(DeltaTable.SETTING)
				.recordId(uuid).jsonValue(objectMapper.convertValue(settingBuilder().build(), Map.class));
	}

	private Delta.DeltaBuilder settingDeletionDeltaBuilder(UUID uuid) {
		return Delta.builder().timestamp(LocalDateTime.now()).deltaType(DeltaType.DELETION).table(DeltaTable.SETTING)
				.recordId(uuid).jsonValue(Map.of("deletion", true));
	}

	private SettingCreationDelta.SettingCreationDeltaBuilder settingBuilder() {
		return SettingCreationDelta.builder().platformType(PlatformType.ALL).settingKey("someKey")
				.settingValue("some value");
	}

	private Delta.DeltaBuilder groupCreationDeltaBuilder() throws JsonProcessingException {
		return Delta.builder().timestamp(LocalDateTime.now()).deltaType(DeltaType.CREATION).table(DeltaTable.GROUP)
				.recordId(UUID.randomUUID())
				.jsonValue(objectMapper.convertValue(groupCreationBuilder().build(), Map.class));
	}

	private GroupCreationDelta.GroupCreationDeltaBuilder groupCreationBuilder() {
		return GroupCreationDelta.builder().name("delta name").type(GroupType.NEGATIVE);
	}

	private Group.GroupBuilder groupBuilder() {
		return Group.builder().id(UUID.randomUUID()).name("some group name").type(GroupType.NEGATIVE).user(user);
	}

	private Delta.DeltaBuilder conditionCreationDeltaBuilder() {
		return Delta.builder().timestamp(LocalDateTime.now()).deltaType(DeltaType.CREATION).table(DeltaTable.CONDITION)
				.recordId(UUID.randomUUID());
	}

	private ConditionCreationDelta.ConditionCreationDeltaBuilder conditionCreationBuilder(UUID conditional,
			UUID target) {
		return ConditionCreationDelta.builder().conditionalGroup(conditional).targetGroup(target)
				.requiredUsageMs(Duration.ofHours(0).plusMinutes(6)).lastDaysToConsider(1);
	}

}
