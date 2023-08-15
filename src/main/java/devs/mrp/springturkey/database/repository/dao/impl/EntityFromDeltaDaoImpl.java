package devs.mrp.springturkey.database.repository.dao.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import devs.mrp.springturkey.database.repository.dao.EntityFromDeltaDao;
import devs.mrp.springturkey.database.service.UserService;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.validation.FieldValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class EntityFromDeltaDaoImpl implements EntityFromDeltaDao {

	@PersistenceContext
	private EntityManager entityManager;

	private ObjectMapper objectMapper;

	@Autowired
	private UserService userService;

	public EntityFromDeltaDaoImpl() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}

	@Override
	public int save(Delta delta) {
		try {
			Map<String,String> dtoMap = objectMapper.readValue(delta.getTextValue(), Map.class);
			Map<String,FieldValidator> validators = delta.getTable().getFieldMap();
			Map<String,Object> entityMap = new HashMap<>();
			dtoMap.forEach((k,v) -> {
				String columnName = validators.get(k).getColumnName();
				entityMap.put(columnName, v);
				if (validators.get(k).getReferenzable() != null) {
					UUID id = UUID.fromString(v);
					Object reference = entityManager.getReference(validators.get(k).getReferenzable(), id);
					entityMap.put(columnName, reference);
				}
			});
			entityMap.put("user", userService.getUser().block());
			Object entity = objectMapper.convertValue(entityMap, delta.getTable().getEntityClass());
			entityManager.persist(entity);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
		}
		return 0;
	}

}
