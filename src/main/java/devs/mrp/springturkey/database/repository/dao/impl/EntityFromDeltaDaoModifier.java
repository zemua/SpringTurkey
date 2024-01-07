package devs.mrp.springturkey.database.repository.dao.impl;

import java.util.Map;
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

@Repository("deltaDaoModification")
@Slf4j
public class EntityFromDeltaDaoModifier extends AbstractEntityFromDeltaDao implements EntityFromDeltaDao {

	@Autowired
	private DeltaRepository deltaRepository;

	@Override
	protected Mono<Object> persist(StorableEntityWrapper data, Object dbObject) {
		if (Objects.isNull(dbObject)) {
			throw new TurkeySurpriseException("Trying to modify an object which id does not exist in the db: " + data.getEntityMap().toString());
		}
		@SuppressWarnings("unchecked")
		Map<String,Object> entityMap = objectMapper.convertValue(dbObject, Map.class);
		entityMap.putAll(data.getEntityMap());
		Object entity = objectMapper.convertValue(entityMap, data.getEntityClass());
		if (!(entity instanceof TurkeyEntity)) {
			throw new TurkeySurpriseException("Trying to set existing id to an object that is not TurkeyEntity: " + entity.getClass().getName());
		}
		Mono<TurkeyEntity> turkeyEntity = removeDeletion((TurkeyEntity) entity, data)
				.flatMap(this::removeOverrides);

		return save(turkeyEntity, data);
	}

	private Mono<TurkeyEntity> removeDeletion(TurkeyEntity entity, StorableEntityWrapper data) {
		if (Objects.isNull(entity.getDeleted())) {
			return Mono.just(entity);
		}
		return Flux.fromIterable(deltaRepository.findByUserAndRecordIdAndDeltaTimeStampAfterOrderByDeltaTimeStampDesc(data.getUser(), data.getRecordId(), data.getTimeStamp()))
				.filter(deltaEntity -> DeltaType.DELETION.equals(deltaEntity.getDeltaType()))
				.count()
				.map(count -> {
					log.debug("There are {} more recent deletion deltas", count);
					// If more recent deltas are deletions, then deleted timestamp shall not be erased by this one
					// but if there are not, means this is more recent than the deletion, so we revert it
					if (count <= 0) {
						entity.setDeleted(null);
					}
					return entity;
				});
	}

	private Mono<TurkeyEntity> removeOverrides(TurkeyEntity entity) {
		// TODO implement
		return Mono.just(entity);
	}

	private Mono<Object> save(Mono<TurkeyEntity> turkeyEntity, StorableEntityWrapper data) {
		return turkeyEntity.map(entity -> {
			entity.setId(data.getRecordId());
			return entity;
		}).map(entity -> {
			log.debug("Merging entity {}", entity);
			return entityManager.merge(entity);
		});
	}

}
