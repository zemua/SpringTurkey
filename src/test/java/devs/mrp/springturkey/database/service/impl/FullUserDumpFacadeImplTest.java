package devs.mrp.springturkey.database.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import devs.mrp.springturkey.configuration.ServiceBeansConfig;
import devs.mrp.springturkey.database.entity.Activity;
import devs.mrp.springturkey.database.entity.Condition;
import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.entity.Group;
import devs.mrp.springturkey.database.entity.RandomCheck;
import devs.mrp.springturkey.database.entity.RandomQuestion;
import devs.mrp.springturkey.database.entity.Setting;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.entity.dto.ExportData;
import devs.mrp.springturkey.database.entity.enumerable.ActivityPlatform;
import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
import devs.mrp.springturkey.database.entity.enumerable.DeviceType;
import devs.mrp.springturkey.database.entity.enumerable.GroupType;
import devs.mrp.springturkey.database.entity.enumerable.PlatformType;
import devs.mrp.springturkey.database.entity.enumerable.RandomBlockType;
import devs.mrp.springturkey.database.repository.ActivityRepository;
import devs.mrp.springturkey.database.repository.ConditionRepository;
import devs.mrp.springturkey.database.repository.DeviceRepository;
import devs.mrp.springturkey.database.repository.GroupRepository;
import devs.mrp.springturkey.database.repository.RandomCheckRepository;
import devs.mrp.springturkey.database.repository.RandomQuestionRepository;
import devs.mrp.springturkey.database.repository.SettingRepository;
import devs.mrp.springturkey.database.repository.UserRepository;
import devs.mrp.springturkey.database.repository.dao.impl.EntityFromDeltaDaoImpl;

@DataJpaTest
@EnableJpaRepositories(basePackages = "devs.mrp.springturkey.database.repository")
@EntityScan("devs.mrp.springturkey.database.*")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {EntityFromDeltaDaoImpl.class})
@Import({ServiceBeansConfig.class})
@EnableJpaAuditing
class FullUserDumpFacadeImplTest {

	@Autowired
	private FullUserDumpFacadeImpl fullUserDumpFacade;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private ActivityRepository activityRepository;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private ConditionRepository conditionRepository;
	@Autowired
	private SettingRepository settingRepository;
	@Autowired
	private RandomCheckRepository randomCheckRepository;
	@Autowired
	private RandomQuestionRepository randomQuestionRepository;

	private TurkeyUser user;

	private Device device1;
	private Device device2;
	private Device device3;

	private Group group1;
	private Group group2;

	private Activity activity1;
	private Activity activity2;

	private Condition condition1;
	private Condition condition2;

	private Setting setting1;
	private Setting setting2;

	private RandomQuestion randomQuestion1;
	private RandomQuestion randomQuestion2;

	private RandomCheck randomCheck1;
	private RandomCheck randomCheck2;

	@BeforeEach
	void setup() {
		user = userRepository.save(TurkeyUser.builder().email("some@user.me").build());
		device1 = deviceRepository.save(Device.builder().deviceType(DeviceType.ANDROID).usageTime(123L).user(user).build());
		device2 = deviceRepository.save(Device.builder().deviceType(DeviceType.ANDROID).usageTime(234L).user(user).build());
		device3 = deviceRepository.save(Device.builder().deviceType(DeviceType.ANDROID).usageTime(345L).user(user).build());

		group1 = groupRepository.save(Group.builder().user(user).name("group1").type(GroupType.NEGATIVE).build());
		group2 = groupRepository.save(Group.builder().user(user).name("group2").type(GroupType.POSITIVE).build());

		activity1 = activityRepository.save(Activity.builder().user(user).activityName("act1").group(group1).activityType(ActivityPlatform.ANDROID_APP).categoryType(CategoryType.NEGATIVE).build());
		activity2 = activityRepository.save(Activity.builder().user(user).activityName("act2").activityType(ActivityPlatform.ANDROID_APP).categoryType(CategoryType.NEGATIVE).build());

		condition1 = conditionRepository.save(Condition.builder().user(user)
				.conditionalGroup(group1).targetGroup(group2)
				.lastDaysToConsider(3).requiredUsageMs(LocalTime.of(11, 22)).build());
		condition2 = conditionRepository.save(Condition.builder().user(user)
				.conditionalGroup(group1).targetGroup(group2)
				.lastDaysToConsider(2).requiredUsageMs(LocalTime.of(11, 22)).build());

		setting1 = settingRepository.save(Setting.builder().user(user).platform(PlatformType.ALL).settingKey("setting1").settingValue("value1").build());
		setting2 = settingRepository.save(Setting.builder().user(user).platform(PlatformType.ALL).settingKey("setting2").settingValue("value2").build());

		randomQuestion1 = randomQuestionRepository.save(RandomQuestion.builder()
				.user(user)
				.type(RandomBlockType.POSITIVE)
				.name("block one")
				.question("question one")
				.frequency(1)
				.multiplier(1)
				.build());
		randomQuestion2 = randomQuestionRepository.save(RandomQuestion.builder()
				.user(user)
				.type(RandomBlockType.NEGATIVE)
				.name("block two")
				.question("question two")
				.frequency(1)
				.multiplier(1)
				.build());

		randomCheck1 = randomCheckRepository.save(RandomCheck.builder()
				.user(user)
				.name("check one")
				.startActive(LocalTime.of(8, 0))
				.endActive(LocalTime.of(20, 0))
				.minCheckLapse(LocalTime.of(0, 15))
				.maxCheckLapse(LocalTime.of(1, 30))
				.reward(LocalTime.of(1, 0))
				.activeDays(Set.of(DayOfWeek.MONDAY))
				.positiveQuestions(Set.of(randomQuestion1))
				.negativeQuestions(Set.of(randomQuestion2))
				.build());
		randomCheck2 = randomCheckRepository.save(RandomCheck.builder()
				.user(user)
				.name("check two")
				.startActive(LocalTime.of(8, 0))
				.endActive(LocalTime.of(20, 0))
				.minCheckLapse(LocalTime.of(0, 15))
				.maxCheckLapse(LocalTime.of(1, 30))
				.reward(LocalTime.of(1, 0))
				.activeDays(Set.of(DayOfWeek.MONDAY))
				.positiveQuestions(Set.of(randomQuestion1))
				.negativeQuestions(Set.of(randomQuestion2))
				.build());
	}

	@Test
	@WithMockUser("some@user.me")
	void testFullUserDump() throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		Map<Object,Object> expected = mapper.convertValue(expectedData(), Map.class);

		ExportData result = fullUserDumpFacade.fullUserDump(device1.getId()).block();
		Map<Object,Object> mapResult = mapper.convertValue(result, Map.class);

		assertEquals(expected, mapResult);
		assertNotNull(mapResult.get("randomChecks"));
		assertNotNull(mapResult.get("randomQuestions"));
	}

	private ExportData expectedData() {
		return ExportData.builder()
				.activities(List.of(activity1, activity2))
				.conditions(List.of(condition1, condition2))
				.groups(List.of(group1, group2))
				.otherDevices(List.of(device2, device3))
				.settings(List.of(setting1, setting2))
				.randomQuestions(List.of(randomQuestion1, randomQuestion2))
				.randomChecks(List.of(randomCheck1, randomCheck2))
				.build();
	}

}
