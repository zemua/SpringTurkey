package devs.mrp.springturkey.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.Exceptions.DoesNotBelongToUserException;
import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.entity.User;
import devs.mrp.springturkey.database.repository.DeviceRepository;
import devs.mrp.springturkey.database.service.DeviceService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DeviceServiceImpl implements DeviceService {

	@Autowired
	private LoginDetailsReader loginDetailsReader;

	@Autowired
	private DeviceRepository deviceRepository;

	@Override
	public Mono<Device> addDevice(User user) {
		Device device = Device.builder()
				.user(user)
				.usageTime(0L)
				.build();
		return deviceRepository.save(device);
	}

	@Override
	public Flux<Device> getUserDevices(User user) {
		return deviceRepository.findAllByUser(user);
	}

	@Override
	public Flux<Device> getUserOtherDevices(User user, Device device) {
		return getUserDevices(user)
				.filter(d -> !d.getId().equals(device.getId()));
	}

	@Override
	public Mono<Device> getDeviceById(String deviceId) {
		return deviceRepository.findById(deviceId)
				.filter(this::belongsToUser)
				.switchIfEmpty(Mono.error(new DoesNotBelongToUserException()));
	}

	private boolean belongsToUser(Device device) {
		return device.getUser().getEmail().equals(loginDetailsReader.getUsername());
	}

}
