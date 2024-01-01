package devs.mrp.springturkey.database.repository.dao;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import devs.mrp.springturkey.database.entity.TurkeyUser;
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

@Slf4j
public abstract class AbstractEntityFromDeltaDao implements EntityFromDeltaDao {

	// TODO if we are deleting, and there is any more recent record, then discard current deletion
	// TODO if there are more recent modification deltas, and we are modifying, then discard fields in this one that were set on a later timestamp
	// TODO if we are modifying a record that exists as deleted, then remove the deleted date

	@PersistenceContext
	protected EntityManager entityManager;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	private UserService userService;

	@Override
	public Mono<Integer> persistTurkeyDataFromDelta(Delta delta) {
		Map<String,Object> modifiableEntityMap = new HashMap<>();
		addFieldsToEntityMap(delta, modifiableEntityMap);

		return userService.getUser()
				.map(user -> persistEntityMapToDb(
						StorableEntityWrapper.builder()
						.recordId(delta.getRecordId())
						.timeStamp(delta.getTimestamp())
						.entityMap(modifiableEntityMap)
						.user(user)
						.entityClass(delta.getEntityClass())
						.build()));
	}

	private void addFieldsToEntityMap(Delta delta, Map<String,Object> modifiableEntityMap) {
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
		Object persisted;
		try {
			data.getEntityMap().put("user", data.getUser());
			log.debug("Fetching object from db for class {} and id {}", data.getEntityClass(), data.getRecordId());
			Object dbObject = entityManager.find(data.getEntityClass(), data.getRecordId());
			persisted = persist(data, dbObject);
		} catch (EntityExistsException | IllegalArgumentException | TransactionRequiredException e) {
			throw new TurkeySurpriseException("Error persisting entity from delta", e);
		}
		return Objects.isNull(persisted) ? 0 : 1;
	}

	protected abstract Object persist(StorableEntityWrapper data, Object dbObject);

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
	protected static class StorableEntityWrapper {
		@Nonnull
		UUID recordId;
		@Nonnull
		LocalDateTime timeStamp;
		@Nonnull
		Map<String,Object> entityMap;
		@Nonnull
		TurkeyUser user;
		@Nonnull
		Class<?> entityClass;
	}

}
