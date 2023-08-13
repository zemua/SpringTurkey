package devs.mrp.springturkey.database.service.impl;

import java.util.Map;

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
import devs.mrp.springturkey.delta.validation.FieldValidator;
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
		Map<String,String> entity;
		try {
			entity = objectMapper.readValue(delta.getTextValue(), Map.class);
		} catch (JsonProcessingException e) {
			throw new TurkeySurpriseException("Delta content has not been correctly validated: " + delta.toString(), e);
		}
		Map<String,FieldValidator> validators = delta.getTable().getFieldMap();
		entity.forEach((k, v) -> {
			FieldValidator validator = validators.get(k);
			if (validator == null) {
				throw new TurkeySurpriseException("Incorrect field " + k + ", delta has not been properly validated: " + delta.toString());
			}
			boolean isValidCreation = validator.isValidCreation(v);
			if (!isValidCreation) {
				throw new TurkeySurpriseException("Incorrect value " + v + " for field " + k + ", delta has not been properly validated: " + delta.toString());
			}
		});
		deltaRepository.save(DeltaEntity.from(delta));
		return entityFromDeltaDao.save(delta, entity);
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
