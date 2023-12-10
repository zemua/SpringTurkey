package devs.mrp.springturkey.database.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import devs.mrp.springturkey.database.entity.DeltaEntity;
import devs.mrp.springturkey.database.entity.TurkeyUser;

public interface DeltaRepository extends JpaRepository<DeltaEntity, Long> {

	public List<DeltaEntity> findByIdGreaterThanAndUser(Long position, TurkeyUser user);

	public Optional<DeltaEntity> findFirstByUserAndRecordIdOrderByDeltaTimeStampDesc(TurkeyUser user, UUID recordId);

}
