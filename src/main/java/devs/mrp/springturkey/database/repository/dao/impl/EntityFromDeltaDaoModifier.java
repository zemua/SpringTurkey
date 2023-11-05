package devs.mrp.springturkey.database.repository.dao.impl;

import java.util.Objects;

import org.springframework.stereotype.Repository;

import devs.mrp.springturkey.database.repository.dao.AbstractEntityFromDeltaDao;
import devs.mrp.springturkey.database.repository.dao.EntityFromDeltaDao;
import devs.mrp.springturkey.exceptions.TurkeySurpriseException;

@Repository("deltaDaoModification")
public class EntityFromDeltaDaoModifier extends AbstractEntityFromDeltaDao implements EntityFromDeltaDao {

	@Override
	protected void persist(StorableEntityWrapper data, Object dbObject) {
		Object entity = objectMapper.convertValue(data.getEntityMap(), data.getEntityClass());
		if (Objects.isNull(dbObject)) {
			throw new TurkeySurpriseException("Trying to modify an object which id does not exist in the db: " + data.getEntityMap().toString());
		} else {
			entityManager.merge(entity);
		}
	}

}
