package devs.mrp.springturkey.database.repository.dao.impl;

import java.util.Objects;

import org.springframework.stereotype.Repository;

import devs.mrp.springturkey.database.repository.dao.AbstractEntityFromDeltaDao;
import devs.mrp.springturkey.database.repository.dao.EntityFromDeltaDao;
import devs.mrp.springturkey.exceptions.TurkeySurpriseException;

@Repository("deltaDaoDeletion")
public class EntityFromDeltaDaoDeleter extends AbstractEntityFromDeltaDao implements EntityFromDeltaDao {

	@Override
	protected void persist(StorableEntityWrapper data, Object dbObject) {
		if (Objects.isNull(dbObject)) {
			throw new TurkeySurpriseException("Trying to delete an object which id does not exist in the db: " + data.getEntityMap().toString());
		} else {
			entityManager.remove(dbObject);
		}
	}

}
