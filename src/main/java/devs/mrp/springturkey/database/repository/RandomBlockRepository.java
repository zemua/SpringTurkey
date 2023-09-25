package devs.mrp.springturkey.database.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import devs.mrp.springturkey.database.entity.RandomQuestion;
import devs.mrp.springturkey.database.entity.TurkeyUser;

public interface RandomBlockRepository extends JpaRepository<RandomQuestion, UUID>{

	public List<RandomQuestion> findAllByUser(TurkeyUser user);

	@Transactional
	@Modifying
	@Query(value = "INSERT INTO random_question (id, turkey_user, type, name, question, frequency, multiplier, created, edited) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, NOW(), NOW())", nativeQuery = true)
	public int insert(UUID id, UUID user, String type, String name, String question, int frequency, int multiplier);

}
