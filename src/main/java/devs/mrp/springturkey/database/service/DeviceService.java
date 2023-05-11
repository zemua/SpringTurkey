package devs.mrp.springturkey.database.service;

import java.util.UUID;

import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DeviceService {

	public Mono<Device> addDevice(User user);

	public Flux<Device> getUserDevices(User user);

	public Flux<Device> getUserOtherDevices(User user, Device device);

	public Mono<Device> getDeviceById(UUID deviceId);

}
