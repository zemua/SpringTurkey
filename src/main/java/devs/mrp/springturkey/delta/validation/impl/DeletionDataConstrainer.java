package devs.mrp.springturkey.delta.validation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataConstrainer;
import reactor.core.publisher.Mono;

@Service("deletionConstraints")
public class DeletionDataConstrainer implements DataConstrainer {

	@Autowired
	private DeltaFacadeService deltaFacadeService;

	private static final String fieldName = "object";
	private static final String textValue = "deletion";

	@Override
	public Mono<Integer> pushDelta(Delta delta) throws WrongDataException {
		if (!DeltaType.DELETION.equals(delta.getDeltaType())) {
			throw new WrongDataException("Wrong delta type " + delta.getDeltaType());
		}
		if (!fieldName.equals(delta.getFieldName())) {
			throw new WrongDataException("Wrong field name, should be 'object': " + delta.getFieldName());
		}
		if (!textValue.equals(delta.getJsonValue())) {
			throw new WrongDataException("Wrong field name, should be 'deletion': " + delta.getFieldName());
		}
		return Mono.just(deltaFacadeService.pushDeletion(delta));
	}

}
