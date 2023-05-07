package devs.mrp.springturkey.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<Device> getDeviceById(Mono<String> deviceId) {
		// TODO Auto-generated method stub
		return null;
	}

}
