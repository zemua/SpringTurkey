package devs.mrp.springturkey.database.repository.dao.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import devs.mrp.springturkey.database.entity.TurkeyEntity;
import devs.mrp.springturkey.database.repository.DeltaRepository;
import devs.mrp.springturkey.database.repository.dao.AbstractEntityFromDeltaDao;
import devs.mrp.springturkey.database.repository.dao.EntityFromDeltaDao;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.exceptions.TurkeySurpriseException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository("deltaDaoDeletion")
@Slf4j
public class EntityFromDeltaDaoDeleter extends AbstractEntityFromDeltaDao implements EntityFromDeltaDao {

	@Autowired
	private DeltaRepository deltaRepository;

	@Override
	protected Mono<Object> persist(StorableEntityWrapper data, Object dbObject) {
		boolean deletion = Boolean.TRUE.equals(data.getEntityMap().get("deletion"));
		if (!deletion) {
			log.warn("Not deleting object because delta deletion boolean is false");
			return Mono.empty();
		}
		if (Objects.isNull(dbObject)) {
			throw new TurkeySurpriseException("Trying to delete an object which id does not exist in the db: " + data.getEntityMap().toString());
		}
		if (!(dbObject instanceof TurkeyEntity)) {
			throw new TurkeySurpriseException("Cannot set deleted date on a non-TurkeyEntity: " + dbObject.getClass().getName());
		}
		return canPersist(data)
				.flatMap(isCanPersist -> Boolean.TRUE.equals(isCanPersist) ? Mono.just(store(data, dbObject)) : Mono.empty());
	}

	private Mono<Boolean> canPersist(StorableEntityWrapper data) {
		return Flux.fromIterable(deltaRepository.findByUserAndRecordIdAndDeltaTimeStampAfterOrderByDeltaTimeStampDesc(data.getUser(), data.getRecordId(), data.getTimeStamp()))
				.filter(deltaEntity -> !DeltaType.DELETION.equals(deltaEntity.getDeltaType()))
				.count()
				.map(count -> {
					log.debug("There are {} more recent deltas", count);
					// If more recent deltas are NOT deletions, then there is a conflict, deleted timestamp shall not be set
					return count <= 0L;
				});
	}

	private TurkeyEntity store(StorableEntityWrapper data, Object dbObject) {
		TurkeyEntity turkeyEntity = (TurkeyEntity) dbObject;
		turkeyEntity.setDeleted(data.getTimeStamp());
		entityManager.merge(turkeyEntity);
		return turkeyEntity;
	}

}
