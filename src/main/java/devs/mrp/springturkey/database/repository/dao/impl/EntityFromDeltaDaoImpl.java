package devs.mrp.springturkey.database.repository.dao.impl;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Repository;

import devs.mrp.springturkey.database.repository.dao.EntityFromDeltaDao;
import devs.mrp.springturkey.delta.Delta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class EntityFromDeltaDaoImpl implements EntityFromDeltaDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public int save(Delta delta, Map<String,String> entityMap) {
		// TODO simplify as follows:
		// objectMapper to Map<String,String>
		// copy Map to new one changing property names according to DeltaTable.fieldMap
		// objectMapper to Entity
		// entityManager.persist(Object entity)

		StringBuilder keys = new StringBuilder();
		StringBuilder values = new StringBuilder();
		ArrayList<Entry<String,String>> list = new ArrayList<>(entityMap.entrySet());

		for (int i = 0; i < list.size(); i++) {
			Entry<String,String> entry = list.get(i);
			if (i > 0) {
				keys.append(",");
				values.append(",");
			}
			keys.append(delta.getTable().getFieldMap().get(entry.getKey()).getColumnName());
			values.append(":value"+i);
		}

		Query query = entityManager.createQuery("INSERT INTO " + delta.getTable().getEntityName() + " (" + keys.toString() + ") VALUES (" + values.toString() + ")");

		for (int i = 0; i<list.size(); i++) {
			Entry<String,String> entry = list.get(i);
			query = query.setParameter("value"+i, entry.getValue());
		}

		return query.executeUpdate();
	}

}
