package devs.mrp.springturkey.database.service;

import java.util.UUID;

import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DeviceService {

	public Mono<Device> addDevice(TurkeyUser user);

	public Flux<Device> getUserDevices(TurkeyUser user);

	public Flux<Device> getUserOtherDevices(TurkeyUser user, Device device);

	public Mono<Device> getDeviceById(UUID deviceId);

}
