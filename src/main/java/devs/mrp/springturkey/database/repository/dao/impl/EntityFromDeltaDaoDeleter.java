package devs.mrp.springturkey.database.repository.dao.impl;

import java.util.Objects;

import org.springframework.stereotype.Repository;

import devs.mrp.springturkey.database.entity.TurkeyEntity;
import devs.mrp.springturkey.database.repository.dao.AbstractEntityFromDeltaDao;
import devs.mrp.springturkey.database.repository.dao.EntityFromDeltaDao;
import devs.mrp.springturkey.exceptions.TurkeySurpriseException;
import lombok.extern.slf4j.Slf4j;

@Repository("deltaDaoDeletion")
@Slf4j
public class EntityFromDeltaDaoDeleter extends AbstractEntityFromDeltaDao implements EntityFromDeltaDao {

	@Override
	protected Object persist(StorableEntityWrapper data, Object dbObject) {
		boolean deletion = Boolean.TRUE.equals(data.getEntityMap().get("deletion"));
		if (!deletion) {
			log.warn("Not deleting object because delta deletion boolean is false");
			return null;
		}
		if (Objects.isNull(dbObject)) {
			throw new TurkeySurpriseException("Trying to delete an object which id does not exist in the db: " + data.getEntityMap().toString());
		}
		if (!(dbObject instanceof TurkeyEntity)) {
			throw new TurkeySurpriseException("Cannot set deleted date on a non-TurkeyEntity: " + dbObject.getClass().getName());
		}
		TurkeyEntity turkeyEntity = (TurkeyEntity) dbObject;
		turkeyEntity.setDeleted(data.getTimeStamp());
		entityManager.merge(turkeyEntity);
		return turkeyEntity;
	}

}
