package devs.mrp.springturkey.database.repository;

import java.util.UUID;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.entity.User;
import reactor.core.publisher.Flux;

public interface DeviceRepository extends ReactiveMongoRepository<Device, UUID> {

	public Flux<Device> findAllByUser(User user);

}
