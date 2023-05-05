package devs.mrp.springturkey.database.service;

import reactor.core.publisher.Mono;

public interface DeviceService {

	public Mono<String> addDevice(Mono<String> userName);

}
