package devs.mrp.springturkey.database.repository.dao.impl;

import java.util.Objects;

import org.springframework.stereotype.Repository;

import devs.mrp.springturkey.database.repository.dao.AbstractEntityFromDeltaDao;
import devs.mrp.springturkey.database.repository.dao.EntityFromDeltaDao;
import devs.mrp.springturkey.exceptions.TurkeySurpriseException;

@Repository("deltaDaoCreation")
public class EntityFromDeltaDaoCreator extends AbstractEntityFromDeltaDao implements EntityFromDeltaDao {

	@Override
	protected void persist(StorableEntityWrapper data, Object dbObject) {
		Object entity = objectMapper.convertValue(data.getEntityMap(), data.getEntityClass());
		if (Objects.nonNull(dbObject)) {
			throw new TurkeySurpriseException("Trying to create an object with already existing id " + data.getEntityMap().toString());
		} else {
			entityManager.merge(entity);
		}
	}

}
