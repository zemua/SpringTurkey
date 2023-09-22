package devs.mrp.springturkey.database.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import devs.mrp.springturkey.database.entity.RandomCheck;
import devs.mrp.springturkey.database.entity.TurkeyUser;

public interface RandomCheckRepository extends JpaRepository<RandomCheck, UUID> {

	public List<RandomCheck> findAllByUser(TurkeyUser user);

}
