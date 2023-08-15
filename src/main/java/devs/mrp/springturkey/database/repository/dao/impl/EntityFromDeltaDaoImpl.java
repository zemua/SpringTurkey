package devs.mrp.springturkey.database.repository.dao.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import devs.mrp.springturkey.Exceptions.TurkeySurpriseException;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.repository.dao.EntityFromDeltaDao;
import devs.mrp.springturkey.database.service.UserService;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.validation.FieldValidator;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TransactionRequiredException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
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
	public Mono<Integer> save(Delta delta) {
		try {
			Map<String,String> dtoMap = objectMapper.readValue(delta.getTextValue(), Map.class);
			Map<String,FieldValidator> validators = delta.getTable().getFieldMap();
			Map<String,Object> entityMap = new HashMap<>();
			dtoMap.forEach((k,v) -> addToEntityMap(entityMap, validators, k, v));
			Class<?> entityClass = delta.getTable().getEntityClass();
			return userService.getUser().map(user -> saveEntityToUser(entityMap, user, entityClass));
		} catch (JsonProcessingException e) {
			throw new TurkeySurpriseException("Json error, delta should have been validated previously", e);
		}
	}

	private void addToEntityMap(Map<String,Object> entityMap, Map<String,FieldValidator> validators, String key, String value) {
		String columnName = validators.get(key).getColumnName();
		Class<?> referenzable = validators.get(key).getReferenzable();
		if (referenzable != null) {
			UUID id = UUID.fromString(value);
			Object reference = entityManager.getReference(validators.get(key).getReferenzable(), id);
			entityMap.put(columnName, reference);
		} else {
			entityMap.put(columnName, value);
		}
	}

	private int saveEntityToUser(Map<String,Object> entityMap, TurkeyUser user, Class<?> entityClass) {
		entityMap.put("user", user);
		Object entity = objectMapper.convertValue(entityMap, entityClass);
		try {
			entityManager.persist(entity);
		} catch (EntityExistsException | IllegalArgumentException | TransactionRequiredException e) {
			throw new TurkeySurpriseException("Error persisting entity from delta", e);
		}
		return 1;
	}

}
