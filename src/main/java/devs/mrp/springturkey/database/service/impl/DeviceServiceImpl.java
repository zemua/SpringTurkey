package devs.mrp.springturkey.database.service.impl;

import org.springframework.stereotype.Service;

import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.service.DeviceService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DeviceServiceImpl implements DeviceService {

	@Override
	public Mono<Device> addDevice() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<Device> getUserDevices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<Device> getDeviceById(Mono<String> deviceId) {
		// TODO Auto-generated method stub
		return null;
	}

}
