package devs.mrp.springturkey.database.service;

import java.util.UUID;

import devs.mrp.springturkey.database.entity.Device;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserDeviceFacade {

	public Mono<Device> addDevice();

	public Flux<Device> getUserDevices();

	public Mono<Device> getUserDeviceById(Mono<UUID> deviceId);

}
