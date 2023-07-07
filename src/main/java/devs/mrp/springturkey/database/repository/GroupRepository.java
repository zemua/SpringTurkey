package devs.mrp.springturkey.database.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import devs.mrp.springturkey.database.entity.Group;
import devs.mrp.springturkey.database.entity.TurkeyUser;

public interface GroupRepository extends JpaRepository<Group, UUID> {

	public List<Group> findAllByUser(TurkeyUser user);

	@Transactional
	@Modifying
	@Query(value = "INSERT INTO turkey_group (id, turkey_user, name, type, created, edited) VALUES (?1, ?2, ?3, ?4, NOW(), NOW())", nativeQuery = true)
	public int insert(UUID id, UUID user, String name, String type);

}
