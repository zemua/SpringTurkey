package devs.mrp.springturkey.database.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.service.ActivityService;
import devs.mrp.springturkey.database.service.ConditionService;
import devs.mrp.springturkey.database.service.DeviceService;
import devs.mrp.springturkey.database.service.FullUserDumpFacade;
import devs.mrp.springturkey.database.service.GroupService;
import devs.mrp.springturkey.database.service.SettingService;
import reactor.core.publisher.Flux;
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

	@Override
	public Mono<Map<Object, Object>> fullUserDump(UUID currentDeviceId) {
		return Mono.just(new HashMap<Object, Object>())
				.flatMap(map -> addToMap(map, otherDevicesTime(currentDeviceId)))
				.flatMap(map -> addToMap(map, userActivities()))
				.flatMap(map -> addToMap(map, userGroups()))
				.flatMap(map -> addToMap(map, userConditions()))
				.flatMap(map -> addToMap(map, userSettings()));
	}

	private Mono<Map<Object,Object>> addToMap(Map<Object,Object> map, Mono<Map<?,?>> toAdd) {
		return toAdd.map(mapToAdd -> {
			map.putAll(mapToAdd);
			return map;
		});
	}

	private Mono<Map<?,?>> otherDevicesTime(UUID currentDeviceId) {
		return deviceService.getUserOtherDevices(currentDeviceId)
				.map(Device::getUsageTime)
				.switchIfEmpty(Flux.just(0L))
				.reduce((t1,t2) -> t1+t2)
				.map(usageTime -> Collections.singletonMap("usageTime", usageTime));
	}

	private Mono<Map<?,?>> userActivities() {
		return activityService.findAllUserActivites()
				.collectList()
				.map(activites -> Collections.singletonMap("activities", activites));
	}

	private Mono<Map<?,?>> userGroups() {
		return groupService.findAllUserGroups()
				.collectList()
				.map(groups -> Collections.singletonMap("groups", groups));
	}

	private Mono<Map<?,?>> userConditions() {
		return conditionService.findAllUserConditions()
				.collectList()
				.map(conditions -> Collections.singletonMap("conditions", conditions));
	}

	private Mono<Map<?,?>> userSettings() {
		return settingService.findAllUserSettings()
				.collectList()
				.map(settings -> Collections.singletonMap("settings", settings));
	}

}
