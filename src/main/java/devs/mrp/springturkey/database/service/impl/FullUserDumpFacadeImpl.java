package devs.mrp.springturkey.database.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.database.entity.Activity;
import devs.mrp.springturkey.database.entity.Condition;
import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.entity.Group;
import devs.mrp.springturkey.database.entity.RandomCheck;
import devs.mrp.springturkey.database.entity.RandomQuestion;
import devs.mrp.springturkey.database.entity.Setting;
import devs.mrp.springturkey.database.entity.dto.ExportData;
import devs.mrp.springturkey.database.service.ActivityService;
import devs.mrp.springturkey.database.service.ConditionService;
import devs.mrp.springturkey.database.service.DeviceService;
import devs.mrp.springturkey.database.service.FullUserDumpFacade;
import devs.mrp.springturkey.database.service.GroupService;
import devs.mrp.springturkey.database.service.RandomCheckService;
import devs.mrp.springturkey.database.service.RandomQuestionService;
import devs.mrp.springturkey.database.service.SettingService;
import reactor.core.publisher.Mono;

@Service
public class FullUserDumpFacadeImpl implements FullUserDumpFacade {

	@Autowired
	private DeviceService deviceService;
	@Autowired
	private ActivityService activityService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private ConditionService conditionService;
	@Autowired
	private SettingService settingService;
	@Autowired
	private RandomQuestionService randomQuestionService;
	@Autowired
	private RandomCheckService randomCheckService;

	@Override
	public Mono<ExportData> fullUserDump(UUID currentDeviceId) {
		return Mono.just(new ExportData())
				.zipWith(otherDevicesTime(currentDeviceId)).map(tupla -> tupla.getT1().withOtherDevices(tupla.getT2()))
				.zipWith(userActivities()).map(tupla -> tupla.getT1().withActivities(tupla.getT2()))
				.zipWith(userGroups()).map(tupla -> tupla.getT1().withGroups(tupla.getT2()))
				.zipWith(userConditions()).map(tupla -> tupla.getT1().withConditions(tupla.getT2()))
				.zipWith(userSettings()).map(tupla -> tupla.getT1().withSettings(tupla.getT2()))
				.zipWith(userRandomQuestions()).map(tupla -> tupla.getT1().withRandomQuestions(tupla.getT2()))
				.zipWith(userRandomChecks()).map(tupla -> tupla.getT1().withRandomChecks(tupla.getT2()));
	}

	private Mono<List<Device>> otherDevicesTime(UUID currentDeviceId) {
		return deviceService.getUserOtherDevices(currentDeviceId)
				.collectList();
	}

	private Mono<List<Activity>> userActivities() {
		return activityService.findAllUserActivites()
				.collectList();
	}

	private Mono<List<Group>> userGroups() {
		return groupService.findAllUserGroups()
				.collectList();
	}

	private Mono<List<Condition>> userConditions() {
		return conditionService.findAllUserConditions()
				.collectList();
	}

	private Mono<List<Setting>> userSettings() {
		return settingService.findAllUserSettings()
				.collectList();
	}

	private Mono<List<RandomQuestion>> userRandomQuestions() {
		return randomQuestionService.findAllUserQuestions()
				.collectList();
	}

	private Mono<List<RandomCheck>> userRandomChecks() {
		return randomCheckService.findAllUserChecks()
				.collectList();
	}

}
