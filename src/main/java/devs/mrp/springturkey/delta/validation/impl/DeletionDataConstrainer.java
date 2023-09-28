package devs.mrp.springturkey.delta.validation.impl;

import java.util.Objects;
import java.util.UUID;

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

	private static final String textValue = "deletion";

	@Override
	public Mono<Integer> pushDelta(Delta delta) throws WrongDataException {
		if (!DeltaType.DELETION.equals(delta.getDeltaType())) {
			throw new WrongDataException("Wrong delta type " + delta.getDeltaType());
		}
		Object deletionObject = getDeletionObject(delta);
		if (Objects.isNull(deletionObject)) {
			throw new WrongDataException("Payload should have a deletion property");
		}
		UUID deletionId;
		try {
			deletionId = UUID.fromString(String.valueOf(deletionObject));
		} catch (IllegalArgumentException e) {
			throw new WrongDataException("Deletion property should be an UUID", e);
		}
		return Mono.just(deltaFacadeService.pushDeletion(delta));
	}

	private Object getDeletionObject(Delta delta) {
		return delta.getJsonValue().get(textValue);
	}

}
