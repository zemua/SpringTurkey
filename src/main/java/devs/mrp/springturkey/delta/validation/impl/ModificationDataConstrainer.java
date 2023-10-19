package devs.mrp.springturkey.delta.validation.impl;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataConstrainer;
import devs.mrp.springturkey.delta.validation.FieldData;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service("modificationConstraints")
@Slf4j
public class ModificationDataConstrainer implements DataConstrainer {

	// TODO change class name to ModificationDataRepositoryFilter or similar, maybe ModificationDeltaFilterService

	@Autowired
	private DeltaFacadeService deltaFacadeService;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public Mono<Integer> pushDelta(Delta delta) throws WrongDataException {
		validate(delta);
		return Mono.just(deltaFacadeService.pushModification(delta));
	}

	private void validate(Delta delta) throws WrongDataException {
		if (! DeltaType.MODIFICATION.equals(delta.getDeltaType())) {
			throw new WrongDataException("Invalid action type: " + delta.getDeltaType());
		}
		if (!isValid(delta.getJsonValue(), delta.getTable().getModificationConstraints())) {
			throw new WrongDataException("Invalid modification in {" +  delta.getJsonValue() + "} for constraints {" + delta.getTable().getModificationConstraints() + "}");
		}
	}

	private Map<String,FieldData> getFieldMap(Delta delta) {
		return delta.getTable().getFieldMap();
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

	private boolean validate(Object obj) throws WrongDataException {
		Set<ConstraintViolation<Object>> violations = validator().validate(obj);
		if (violations.isEmpty()) {
			return true;
		} else {
			throw new WrongDataException("Validation failed: " + violations.toString());
		}
	}

	private Object convertedObject(Map<String,Object> value, Class<?> constraints) {
		return objectMapper.convertValue(value, constraints);
	}

	private Validator validator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		return factory.getValidator();
	}

}
