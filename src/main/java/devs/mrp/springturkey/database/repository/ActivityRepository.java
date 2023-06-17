package devs.mrp.springturkey.database.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import devs.mrp.springturkey.database.entity.Activity;
import devs.mrp.springturkey.database.entity.TurkeyUser;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

	public List<Activity> findAllByUser(TurkeyUser user);

}
