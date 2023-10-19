package devs.mrp.springturkey.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import devs.mrp.springturkey.Exceptions.TurkeySurpriseException;
import devs.mrp.springturkey.database.repository.DeltaRepository;
import devs.mrp.springturkey.database.repository.dao.EntityFromDeltaDao;
import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.Delta;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class DeltaFacadeServiceImpl implements DeltaFacadeService {

	@Autowired
	private DeltaRepository deltaRepository;

	@Autowired
	private EntityFromDeltaDao entityFromDeltaDao;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	@Transactional
	public Mono<Integer> pushCreation(Delta delta) {
		return entityFromDeltaDao.save(delta)
				.map(i -> {
					if (i > 0) {
						try {
							deltaRepository.save(delta.toEntity());
						} catch (JsonProcessingException e1) {
							// TODO Auto-generated catch block
							log.error("Error getting DeltaEntity from Delta", e1);
						}
					}
					return i;
				}).doOnError(TurkeySurpriseException.class, e -> Mono.error(new TurkeySurpriseException("Error persisting delta", e)));
	}

	@Override
	@Transactional
	public int pushModification(Delta delta) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Transactional
	public int pushDeletion(Delta delta) {
		// TODO Auto-generated method stub
		return 0;
	}

}
