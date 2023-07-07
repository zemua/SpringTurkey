package devs.mrp.springturkey.database.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import devs.mrp.springturkey.database.entity.Activity;
import devs.mrp.springturkey.database.entity.TurkeyUser;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

	public List<Activity> findAllByUser(TurkeyUser user);

	@Transactional
	@Modifying
	@Query(value = "INSERT INTO turkey_activities (id, turkey_user, activity_name, activity_type, category_type, created, edited) VALUES (?1, ?2, ?3, ?4, ?5, NOW(), NOW())", nativeQuery = true)
	public int insert(UUID id, UUID user, String activityName, String activityType, String categoryType);

}
