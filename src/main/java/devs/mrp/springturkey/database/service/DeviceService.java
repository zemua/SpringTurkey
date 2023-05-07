package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.database.entity.Device;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DeviceService {

	public Mono<Device> addDevice();

	public Flux<Device> getUserDevices();

	public Mono<Device> getDeviceById(Mono<String> deviceId);

}
