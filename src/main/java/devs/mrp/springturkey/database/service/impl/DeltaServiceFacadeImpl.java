package devs.mrp.springturkey.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import devs.mrp.springturkey.database.repository.DeltaRepository;
import devs.mrp.springturkey.database.repository.dao.EntityFromDeltaDao;
import devs.mrp.springturkey.database.service.DeltaServiceFacade;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.exceptions.TurkeySurpriseException;
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
				.doOnNext(i -> saveEntityFromDelta(delta))
				.doOnError(TurkeySurpriseException.class, e -> Mono.error(new TurkeySurpriseException("Error persisting delta", e)));
	}

	private void saveEntityFromDelta(Delta delta) {
		try {
			deltaRepository.save(delta.toEntity(objectMapper));
		} catch (JsonProcessingException e1) {
			throw new TurkeySurpriseException("Error mapping DeltaEntity from Delta", e1);
		}
	}

}
