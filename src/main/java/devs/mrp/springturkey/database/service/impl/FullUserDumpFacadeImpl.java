package devs.mrp.springturkey.database.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.entity.dto.ExportData;
import devs.mrp.springturkey.database.service.ActivityService;
import devs.mrp.springturkey.database.service.ConditionService;
import devs.mrp.springturkey.database.service.DeviceService;
import devs.mrp.springturkey.database.service.FullUserDumpFacade;
import devs.mrp.springturkey.database.service.GroupService;
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

	@Override
	public Mono<ExportData> fullUserDump(UUID currentDeviceId) {
		return Mono.just(new ExportData())
				.zipWith(otherDevicesTime(currentDeviceId)).map(tupla -> tupla.getT1().withOtherDevices(tupla.getT2()));
	}

	private Mono<Map<Object,Object>> addToMap(Map<Object,Object> map, Mono<Map<?,?>> toAdd) {
		return toAdd.map(mapToAdd -> {
			map.putAll(mapToAdd);
			return map;
		});
	}

	private Mono<List<Device>> otherDevicesTime(UUID currentDeviceId) {
		return deviceService.getUserOtherDevices(currentDeviceId)
				.collectList();
		//.map(Device::getUsageTime)
		//.switchIfEmpty(Flux.just(0L))
		//.reduce((t1,t2) -> t1+t2)
		//.map(usageTime -> Collections.singletonMap("usageTime", usageTime));
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
