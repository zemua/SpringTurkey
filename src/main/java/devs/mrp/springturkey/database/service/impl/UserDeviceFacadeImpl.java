package devs.mrp.springturkey.database.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.service.DeviceService;
import devs.mrp.springturkey.database.service.UserDeviceFacade;
import devs.mrp.springturkey.database.service.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserDeviceFacadeImpl implements UserDeviceFacade {

	@Autowired
	private DeviceService deviceService;
	@Autowired
	private UserService userService;

	@Override
	public Mono<Device> addDevice() {
		return userService.getUser()
				.switchIfEmpty(Mono.defer(() -> userService.addCurrentUser()))
				.flatMap(user -> deviceService.addDevice());
	}

	@Override
	public Flux<Device> getUserDevices() {
		return userService.getUser().flatMapMany(user -> deviceService.getUserDevices());
	}

	@Override
	public Mono<Device> getUserDeviceById(Mono<UUID> deviceId) {
		return deviceId.flatMap(id -> deviceService.getDeviceById(id));
	}

}
