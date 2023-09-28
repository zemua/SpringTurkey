package devs.mrp.springturkey.delta.validation.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataConstrainer;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service("creationConstraints")
@Slf4j
public class CreationDataConstrainer implements DataConstrainer {

	@Autowired
	private Validator validator;

	@Autowired
	private DeltaFacadeService deltaFacade;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Mono<Integer> pushDelta(Delta delta) throws WrongDataException {
		if (!DeltaType.CREATION.equals(delta.getDeltaType())) {
			throw new WrongDataException("Delta is not of type 'CREATION'");
		}
		var violations = resolveViolations(delta);
		if (!violations.isEmpty()) {
			throw new WrongDataException("Invalid data for delta with violations: " + violations.toString());
		}
		return deltaFacade.pushCreation(delta);
	}

	private Set<ConstraintViolation<Object>> resolveViolations(Delta delta) {
		Class<?> clazz = delta.getTable().getDtoClass();
		Object creationEntity = null;
		creationEntity = objectMapper.convertValue(delta.getJsonValue(), clazz);
		Set<ConstraintViolation<Object>> violations = new HashSet<>();
		violations.addAll(validator.validate(creationEntity));
		violations.addAll(validator.validate(delta));
		return violations;
	}

}
