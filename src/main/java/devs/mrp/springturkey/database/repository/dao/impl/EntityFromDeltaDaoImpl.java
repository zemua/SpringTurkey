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
import devs.mrp.springturkey.delta.validation.FieldData;
import jakarta.annotation.Nonnull;
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

	// TODO refactor usage of data.getColumnName() as we may modify more than one field

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
			Map<String,Object> modifiableEntityMap = new HashMap<>();
			addFieldsToEntityMap(delta, modifiableEntityMap);

			return userService.getUser().map(user -> persistEntityMapToDb(
					StorableEntityWrapper.builder()
					.entityMap(modifiableEntityMap)
					.user(user)
					.entityClass(delta.getEntityClass())
					.build()));

		} catch (JsonProcessingException e) {
			throw new TurkeySurpriseException("Json error, delta should have been validated previously", e);
		}
	}

	private void addFieldsToEntityMap(Delta delta, Map<String,Object> modifiableEntityMap) throws JsonMappingException, JsonProcessingException {
		modifiableEntityMap.put("id", delta.getRecordId());
		dtoMap(delta).forEach((k,v) -> addFieldToEntityMap(
				EntityDtoFieldWrapper.builder()
				.entityMap(modifiableEntityMap)
				.fieldData(delta.getFieldData(String.valueOf(k)))
				.key(k)
				.value(v)
				.build()));
	}

	@SuppressWarnings("unchecked")
	private Map<Object,Object> dtoMap(Delta delta) throws JsonMappingException, JsonProcessingException {
		return objectMapper.convertValue(delta.getJsonValue(), Map.class); // TODO make it right
	}

	private void addFieldToEntityMap(EntityDtoFieldWrapper data) {
		if (data.getFieldData() == null) {
			throw new TurkeySurpriseException("Delta content has not been properly validated, no validator for " + data.getKey());
		}
		if (data.getReferenzable() != null && data.getValue() != null) {
			addToEntityMapWithReferenzable(data);
		} else {
			data.getEntityMap().put(data.getColumnName(), data.getValue());
		}
	}

	private void addToEntityMapWithReferenzable(EntityDtoFieldWrapper data) {
		UUID id;
		try {
			id = UUID.fromString(data.getValue());
		} catch (IllegalArgumentException e) {
			throw new TurkeySurpriseException("Invalid UUID value provided", e);
		}
		Object reference = entityManager.getReference(data.getReferenzable(), id);
		data.getEntityMap().put(data.getColumnName(), reference);
	}

	private int persistEntityMapToDb(StorableEntityWrapper data) {
		try {
			persistIfNewId(data);
		} catch (EntityExistsException | IllegalArgumentException | TransactionRequiredException e) {
			throw new TurkeySurpriseException("Error persisting entity from delta", e);
		}
		return 1;
	}

	private void persistIfNewId(StorableEntityWrapper data) {
		data.getEntityMap().put("user", data.getUser());
		Object entity = objectMapper.convertValue(data.getEntityMap(), data.getEntityClass());
		Object object = entityManager.find(data.getEntityClass(), data.getEntityMap().get("id"));
		if (object != null) {
			throw new TurkeySurpriseException("Trying to create an object with already existing id " + data.getEntityMap().toString());
		} else {
			entityManager.merge(entity);
		}
	}

	@Getter
	@Builder
	private static class EntityDtoFieldWrapper {
		Map<String,Object> entityMap;
		FieldData fieldData;
		Object key;
		Object value;

		public EntityDtoFieldWrapper(Map<String,Object> entityMap, FieldData fieldData, Object key, Object value) {
			this.entityMap = entityMap;
			this.fieldData = fieldData;
			this.key = key;
			this.value = value;
		}

		public Class<?> getReferenzable() {
			return fieldData.getReferenzable();
		}

		public String getColumnName() {
			return fieldData.getColumnName();
		}

		public String getValue() {
			return value == null ? null : String.valueOf(value);
		}
	}

	@Getter
	@Builder
	private static class StorableEntityWrapper {
		@Nonnull
		Map<String,Object> entityMap;
		@Nonnull
		TurkeyUser user;
		@Nonnull
		Class<?> entityClass;
	}

}
