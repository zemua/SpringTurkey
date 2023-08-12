package devs.mrp.springturkey.database.repository.dao.impl;

import org.springframework.stereotype.Repository;

import devs.mrp.springturkey.database.repository.dao.EntityFromDeltaDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class EntityFromDaoImpl implements EntityFromDeltaDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void save(Object entity) {
		entityManager.persist(entity);

	}

}
