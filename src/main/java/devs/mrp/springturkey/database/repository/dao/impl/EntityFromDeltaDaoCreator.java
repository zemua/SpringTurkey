package devs.mrp.springturkey.database.repository.dao.impl;

import java.util.Objects;

import org.springframework.stereotype.Repository;

import devs.mrp.springturkey.database.entity.TurkeyEntity;
import devs.mrp.springturkey.database.repository.dao.AbstractEntityFromDeltaDao;
import devs.mrp.springturkey.database.repository.dao.EntityFromDeltaDao;
import devs.mrp.springturkey.exceptions.TurkeySurpriseException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Repository("deltaDaoCreation")
@Slf4j
public class EntityFromDeltaDaoCreator extends AbstractEntityFromDeltaDao implements EntityFromDeltaDao {

	@Override
	protected Mono<Object> persist(StorableEntityWrapper data, Object dbObject) {
		Object entity = objectMapper.convertValue(data.getEntityMap(), data.getEntityClass());
		if (Objects.nonNull(dbObject)) {
			throw new TurkeySurpriseException("Trying to create an object with already existing id " + data.getEntityMap().toString());
		}
		if (!(entity instanceof TurkeyEntity)) {
			throw new TurkeySurpriseException("Cannot pre-set the ID on a non-TurkeyEntity object: " + entity.getClass().getName());
		}
		TurkeyEntity turkeyEntity = (TurkeyEntity) entity;
		turkeyEntity.setId(data.getRecordId());
		log.debug("Merging entity {}", entity);
		return Mono.just(entityManager.merge(entity));
	}

}
