package devs.mrp.springturkey.database.repository.dao.impl;

import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Repository;

import devs.mrp.springturkey.database.entity.TurkeyEntity;
import devs.mrp.springturkey.database.repository.dao.AbstractEntityFromDeltaDao;
import devs.mrp.springturkey.database.repository.dao.EntityFromDeltaDao;
import devs.mrp.springturkey.exceptions.TurkeySurpriseException;
import lombok.extern.slf4j.Slf4j;

@Repository("deltaDaoModification")
@Slf4j
public class EntityFromDeltaDaoModifier extends AbstractEntityFromDeltaDao implements EntityFromDeltaDao {

	@Override
	protected Object persist(StorableEntityWrapper data, Object dbObject) {
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
		TurkeyEntity turkeyEntity = (TurkeyEntity) entity;
		turkeyEntity.setId(data.getRecordId());
		log.debug("Merging entity {}", turkeyEntity);
		return entityManager.merge(turkeyEntity);
	}

}
