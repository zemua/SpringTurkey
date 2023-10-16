package devs.mrp.springturkey.validation.service.impl;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.validation.service.ValidationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Service
public class ValidationServiceImpl implements ValidationService {

	private ObjectMapper objectMapper = objectMapper();

	private Validator validator = validator();

	@Override
	public void validate(Delta delta) throws WrongDataException {
		if (!isValid(delta.getJsonValue(), constraints(delta))) {
			throw new WrongDataException("Invalid modification in {" +  delta.getJsonValue() + "} for constraints {" + delta.getTable().getModificationConstraints() + "}");
		}
	}

	private Class<?> constraints(Delta delta) throws WrongDataException {
		if (delta.getDeltaType().equals(DeltaType.MODIFICATION)) {
			return delta.getTable().getModificationConstraints();
		} else if (delta.getDeltaType().equals(DeltaType.CREATION)) {
			return delta.getTable().getCreationConstraints();
		}
		throw new WrongDataException("No constraints for the given type " + delta.getDeltaType());
	}

	private boolean isValid(Map<String,Object> value, Class<?> constraints) throws WrongDataException {
		Object converted;
		try {
			converted = convertedObject(value, constraints);
		} catch (IllegalArgumentException e) {
			throw new WrongDataException("Validation failed", e);
		}
		return validate(converted);
	}

	private Object convertedObject(Map<String,Object> value, Class<?> constraints) {
		return objectMapper.convertValue(value, constraints);
	}

	private boolean validate(Object obj) throws WrongDataException {
		Set<ConstraintViolation<Object>> violations = validator.validate(obj);
		if (violations.isEmpty()) {
			return true;
		} else {
			throw new WrongDataException("Validation failed: " + violations.toString());
		}
	}

	private Validator validator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		return factory.getValidator();
	}

	private ObjectMapper objectMapper() { // TODO make an objectMapperProvider bean
		return JsonMapper.builder()
				.addModule(new JavaTimeModule())
				.build();
	}

}
