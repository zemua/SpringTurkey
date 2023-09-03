package devs.mrp.springturkey.database.repository.dao.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
import lombok.Builder;
import lombok.Getter;
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
			Map<String,FieldValidator> validators = delta.getTable().getFieldMap();

			Map<String,Object> entityMap = new HashMap<>();
			entityMap.put("id", delta.getRecordId());

			dtoMap(delta).forEach((k,v) -> addToEntityMap(
					EntityDtoDataWrapper.builder()
					.entityMap(entityMap)
					.validator(validators.get(k))
					.key(k)
					.value(v)
					.build()));

			Class<?> entityClass = delta.getTable().getEntityClass();

			return userService.getUser().map(user -> saveEntityMapToUser(entityMap, user, entityClass));
		} catch (JsonProcessingException e) {
			throw new TurkeySurpriseException("Json error, delta should have been validated previously", e);
		}
	}

	private Map<Object,Object> dtoMap(Delta delta) throws JsonMappingException, JsonProcessingException {
		return objectMapper.readValue(delta.getTextValue(), Map.class);
	}

	private void addToEntityMap(EntityDtoDataWrapper data) {
		if (data.getValidator() == null) {
			throw new TurkeySurpriseException("Delta content has not been properly validated, no validator for " + data.getKey());
		}
		if (data.getReferenzable() != null && data.getValue() != null) {
			addToEntityMapWithReferenzable(data);
		} else {
			data.getEntityMap().put(data.getColumnName(), data.getValue());
		}
	}

	private void addToEntityMapWithReferenzable(EntityDtoDataWrapper data) {
		UUID id;
		try {
			id = UUID.fromString(data.getValue());
		} catch (IllegalArgumentException e) {
			throw new TurkeySurpriseException("Invalid UUID value provided", e);
		}
		Object reference = entityManager.getReference(data.getReferenzable(), id);
		data.getEntityMap().put(data.getColumnName(), reference);
	}

	private int saveEntityMapToUser(Map<String,Object> entityMap, TurkeyUser user, Class<?> entityClass) {
		try {
			persistIfNewId(entityMap, user, entityClass);
		} catch (EntityExistsException | IllegalArgumentException | TransactionRequiredException e) {
			throw new TurkeySurpriseException("Error persisting entity from delta", e);
		}
		return 1;
	}

	private void persistIfNewId(Map<String,Object> entityMap, TurkeyUser user, Class<?> entityClass) {
		entityMap.put("user", user);
		Object entity = objectMapper.convertValue(entityMap, entityClass);
		Object object = entityManager.find(entityClass, entityMap.get("id"));
		if (object != null) {
			throw new TurkeySurpriseException("Trying to create an object with already existing id " + entityMap.toString());
		} else {
			entityManager.merge(entity);
		}
	}

	@Getter
	@Builder
	private static class EntityDtoDataWrapper {
		Map<String,Object> entityMap;
		FieldValidator validator;
		Object key;
		Object value;

		public EntityDtoDataWrapper(Map<String,Object> entityMap, FieldValidator validator, Object key, Object value) {
			this.entityMap = entityMap;
			this.validator = validator;
			this.key = key;
			this.value = value;
		}

		public Class<?> getReferenzable() {
			return validator.getReferenzable();
		}

		public String getColumnName() {
			return validator.getColumnName();
		}

		public String getValue() {
			return value == null ? null : String.valueOf(value);
		}
	}

}
