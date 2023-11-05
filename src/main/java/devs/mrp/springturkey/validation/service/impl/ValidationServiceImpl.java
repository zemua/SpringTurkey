package devs.mrp.springturkey.validation.service.impl;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.exceptions.WrongDataException;
import devs.mrp.springturkey.validation.service.ValidationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ValidationServiceImpl implements ValidationService {

	private Validator validator = validator();

	@Autowired
	private ObjectMapper objectMapper;

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
			log.warn("Validation failed: {}", violations);
			return false;
		}
	}

	private Validator validator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		return factory.getValidator();
	}

}
