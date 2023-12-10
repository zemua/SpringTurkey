package devs.mrp.springturkey.database.service.impl;

import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.DeltaEntity;
import devs.mrp.springturkey.database.repository.DeltaRepository;
import devs.mrp.springturkey.database.repository.dao.EntityFromDeltaDao;
import devs.mrp.springturkey.database.service.DeltaServiceFacade;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.exceptions.TurkeySurpriseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DeltaServiceFacadeImpl implements DeltaServiceFacade {

	@Autowired
	private DeltaRepository deltaRepository;

	@Autowired
	@Qualifier("deltaDaoCreation")
	private EntityFromDeltaDao deltaDaoCreation;

	@Autowired
	@Qualifier("deltaDaoModification")
	private EntityFromDeltaDao deltaDaoModification;

	@Autowired
	@Qualifier("deltaDaoDeletion")
	private EntityFromDeltaDao deltaDaoDeletion;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private LoginDetailsReader loginDetailsReader;

	@Override
	@Transactional
	public Mono<Integer> pushCreation(Delta delta) {
		return persistEntity(deltaDaoCreation.persistDelta(delta), delta);
	}

	@Override
	@Transactional
	public Mono<Integer> pushModification(Delta delta) {
		return persistEntity(deltaDaoModification.persistDelta(delta), delta);
	}

	@Override
	@Transactional
	public Mono<Integer> pushDeletion(Delta delta) {
		return persistEntity(deltaDaoDeletion.persistDelta(delta), delta);
	}

	private Mono<Integer> persistEntity(Mono<Integer> previousResult, Delta delta) {
		return previousResult.filter(i -> i>0)
				.flatMap(i -> saveEntityFromDelta(delta))
				.doOnError(TurkeySurpriseException.class, e -> Mono.error(new TurkeySurpriseException("Error persisting delta", e)));
	}

	private Mono<Integer> saveEntityFromDelta(Delta delta) {
		return loginDetailsReader.getTurkeyUser()
				.map(user -> {
					try {
						return deltaRepository.save(delta.toEntity(objectMapper, user));
					} catch (JsonProcessingException e) {
						throw new TurkeySurpriseException("Error mapping DeltaEntity from Delta", e);
					}
				})
				.map(entity -> Objects.nonNull(entity) ? 1 : 0)
				.switchIfEmpty(Mono.just(0));
	}

	@Override
	public Flux<Delta> findAfterPosition(Long position) {
		return loginDetailsReader.getTurkeyUser()
				.flatMapIterable(user -> deltaRepository.findByIdGreaterThanAndUser(position, user))
				.map(this::deltaFromEntity);
	}

	private Delta deltaFromEntity(DeltaEntity entity) {
		try {
			return Delta.fromEntity(entity, objectMapper);
		} catch (JsonProcessingException e) {
			throw new TurkeySurpriseException("Unexpected error on converting data from DB", e);
		}
	}

	@Override
	public Mono<Delta> findMostRecentTimestampForRecord(UUID recordId) {
		return loginDetailsReader.getTurkeyUser()
				.flatMap(user -> Mono.just(deltaRepository.findFirstByUserAndRecordIdOrderByDeltaTimeStampDesc(user, recordId)))
				.flatMap(optionalDelta -> optionalDelta.isPresent() ? Mono.just(optionalDelta.get()) : Mono.empty())
				.map(entity -> {
					try {
						return Delta.fromEntity(entity, objectMapper);
					} catch (JsonProcessingException e) {
						throw new TurkeySurpriseException("Unexpected error parsing json string from db object: " + entity.getJsonValue(), e);
					}
				});
	}

}
