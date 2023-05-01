package devs.mrp.springturkey.database.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import devs.mrp.springturkey.database.entity.Device;

public interface DeviceRepository extends ReactiveMongoRepository<Device, String> {

}
