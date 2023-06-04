package devs.mrp.springturkey.database.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.entity.User;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

	public List<Device> findAllByUser(User user);

}
