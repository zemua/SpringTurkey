package devs.mrp.springturkey.database.repository.dao.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.repository.dao.EntityFromDeltaDao;
import devs.mrp.springturkey.database.service.UserService;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.validation.FieldData;
import devs.mrp.springturkey.exceptions.TurkeySurpriseException;
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

	private static final String ID_FIELD = "id";

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserService userService;

	@Override
	public Mono<Integer> save(Delta delta) {
		Map<String,Object> modifiableEntityMap = new HashMap<>();
		addFieldsToEntityMap(delta, modifiableEntityMap);

		return userService.getUser().map(user -> persistEntityMapToDb(
				StorableEntityWrapper.builder()
				.entityMap(modifiableEntityMap)
				.user(user)
				.entityClass(delta.getEntityClass())
				.build()));
	}

	private void addFieldsToEntityMap(Delta delta, Map<String,Object> modifiableEntityMap) {
		modifiableEntityMap.put(ID_FIELD, delta.getRecordId());
		delta.getJsonValue().forEach((k,v) -> addFieldToEntityMap(
				EntityDtoFieldWrapper.builder()
				.entityMap(modifiableEntityMap)
				.fieldData(delta.getFieldData(String.valueOf(k)))
				.key(k)
				.value(v)
				.build()));
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
			id = UUID.fromString(String.valueOf(data.getValue()));
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

		public Object getValue() {
			return value;
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
