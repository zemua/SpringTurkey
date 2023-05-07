package devs.mrp.springturkey.database.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.entity.User;
import reactor.core.publisher.Flux;

public interface DeviceRepository extends ReactiveMongoRepository<Device, String> {

	public Flux<Device> findAllByUser(User user);

}
