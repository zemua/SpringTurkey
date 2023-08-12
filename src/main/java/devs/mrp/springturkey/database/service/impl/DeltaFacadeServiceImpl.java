package devs.mrp.springturkey.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import devs.mrp.springturkey.Exceptions.TurkeySurpriseException;
import devs.mrp.springturkey.database.entity.DeltaEntity;
import devs.mrp.springturkey.database.repository.DeltaRepository;
import devs.mrp.springturkey.database.repository.dao.EntityFromDeltaDao;
import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.Delta;
import jakarta.transaction.Transactional;

@Service
public class DeltaFacadeServiceImpl implements DeltaFacadeService {

	@Autowired
	private DeltaRepository deltaRepository;

	@Autowired
	private EntityFromDeltaDao entityFromDeltaDao;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	@Transactional
	public int pushCreation(Delta delta) {
		Object entity;
		try {
			entity = objectMapper.readValue(delta.getTextValue(), delta.getTable().getEntityClass());
		} catch (JsonProcessingException e) {
			throw new TurkeySurpriseException("Delta content has not been validated: " + delta.toString(), e);
		}
		deltaRepository.save(DeltaEntity.from(delta));
		entityFromDeltaDao.save(entity);
		return 0;
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
