package devs.mrp.springturkey.database.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.repository.DeviceRepository;
import devs.mrp.springturkey.database.repository.UserRepository;
import devs.mrp.springturkey.database.service.DeviceService;
import devs.mrp.springturkey.exceptions.DoesNotBelongToUserException;
import devs.mrp.springturkey.utils.Duple;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DeviceServiceImpl implements DeviceService {

	@Autowired
	private LoginDetailsReader loginDetailsReader;

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public Mono<Device> addDevice() {
		return loginDetailsReader.setupCurrentUser().map(user -> Device.builder()
				.user(user)
				.usageTime(0L)
				.build())
				.flatMap(device -> Mono.just(deviceRepository.save(device)));
	}

	@Override
	public Flux<Device> getUserDevices() {
		return loginDetailsReader.getTurkeyUser().flatMapMany(user -> Flux.fromIterable(deviceRepository.findAllByUser(user)))
				.flatMap(device -> loginDetailsReader.isCurrentUser(device.getUser())
						.map(isCurrent -> new Duple<Device,Boolean>(device, isCurrent)))
				.filter(Duple::getValue2)
				.map(Duple::getValue1);
	}

	@Override
	public Flux<Device> getUserOtherDevices(UUID deviceId) {
		if (deviceId == null) {
			return Flux.error(new DoesNotBelongToUserException("No devices available as null"));
		}
		return getDeviceById(deviceId)
				.flatMapMany(existingDevice -> getUserDevices())
				.filter(d -> !d.getId().equals(deviceId));
	}

	@Override
	public Mono<Device> getDeviceById(UUID deviceId) {
		return Mono.just(deviceRepository.findById(deviceId))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.flatMap(device -> loginDetailsReader.isCurrentUser(device.getUser())
						.map(isCurrent -> new Duple<Device,Boolean>(device, isCurrent)))
				.filter(Duple::getValue2)
				.map(Duple::getValue1)
				.switchIfEmpty(Mono.error(new DoesNotBelongToUserException()));
	}

}
