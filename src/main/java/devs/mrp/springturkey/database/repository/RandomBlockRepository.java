package devs.mrp.springturkey.database.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import devs.mrp.springturkey.database.entity.RandomBlock;
import devs.mrp.springturkey.database.entity.TurkeyUser;

public interface RandomBlockRepository extends JpaRepository<RandomBlock, UUID>{

	public List<RandomBlock> findAllByUser(TurkeyUser user);

}
