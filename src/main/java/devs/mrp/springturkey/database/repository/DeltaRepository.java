package devs.mrp.springturkey.database.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import devs.mrp.springturkey.database.entity.DeltaEntity;
import devs.mrp.springturkey.database.entity.TurkeyUser;

public interface DeltaRepository extends JpaRepository<DeltaEntity, Long> {

	public List<DeltaEntity> findByIdGreaterThanAndUserOrderByDeltaTimeStampAsc(Long position, TurkeyUser user);

	public Optional<DeltaEntity> findFirstByUserAndRecordIdOrderByDeltaTimeStampDesc(TurkeyUser user, UUID recordId);

	public List<DeltaEntity> findByUserAndRecordIdAndDeltaTimeStampAfterOrderByDeltaTimeStampDesc(TurkeyUser user, UUID recordId, LocalDateTime timeStamp);

}
