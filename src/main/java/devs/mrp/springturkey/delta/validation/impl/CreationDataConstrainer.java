package devs.mrp.springturkey.delta.validation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataConstrainer;
import jakarta.validation.Validator;

@Service("creationConstraints")
public class CreationDataConstrainer implements DataConstrainer {

	@Autowired
	private Validator validator;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public int pushDelta(Delta delta) throws WrongDataException {
		if (!DeltaType.CREATION.equals(delta.getDeltaType())) {
			throw new WrongDataException("Delta is not of type 'CREATION'");
		}
		if (isInvalid(delta)) {
			throw new WrongDataException("Invalid data contained in delta");
		}
		// TODO Auto-generated method stub
		return 0;
	}

	private boolean isInvalid(Delta delta) {
		Class<?> clazz = delta.getTable().getEntityClass();
		Object creationDelta;
		try {
			creationDelta = objectMapper.readValue(delta.getTextValue(), clazz);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		var violations = validator.validate(delta);
		return !violations.isEmpty();
	}

}
