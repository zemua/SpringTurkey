package devs.mrp.springturkey.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.entity.User;
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
		return currentUser().flatMap(user -> deviceService.addDevice(user));
	}

	@Override
	public Flux<Device> getUserDevices() {
		return currentUser().flatMapMany(user -> deviceService.getUserDevices(user));
	}

	@Override
	public Mono<Device> getUserDeviceById(Mono<String> deviceId) {
		return deviceService.getDeviceById(deviceId);
	}

	private Mono<User> currentUser() {
		return userService.getUser()
				.switchIfEmpty(Mono.defer(() -> userService.addCurrentUser()));
	}

}
