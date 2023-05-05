package devs.mrp.springturkey.database.service.impl;

import org.springframework.stereotype.Service;

import devs.mrp.springturkey.database.service.DeviceService;
import reactor.core.publisher.Mono;

@Service
public class DeviceServiceImpl implements DeviceService {

	@Override
	public Mono<String> addDevice(Mono<String> userName) {
		// TODO Auto-generated method stub
		return null;
	}

}
