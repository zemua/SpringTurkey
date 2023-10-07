package devs.mrp.springturkey.database.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import devs.mrp.springturkey.database.entity.Condition;
import devs.mrp.springturkey.database.entity.TurkeyUser;

public interface ConditionRepository extends JpaRepository<Condition, UUID> {

	public List<Condition> findAllByUser(TurkeyUser user);

	@Transactional
	@Modifying
	@Query(value = "INSERT INTO turkey_condition (id, turkey_user, conditional_group, target_group, required_usage_ms, last_days_to_consider, created, edited) VALUES (?1, ?2, ?3, ?4, ?5, ?6, NOW(), NOW())", nativeQuery = true)
	public int insert(UUID id, UUID user, UUID conditionalGroup, UUID targetGroup, LocalTime requiredUsageMs, Integer lastDaysToConsider);

}
