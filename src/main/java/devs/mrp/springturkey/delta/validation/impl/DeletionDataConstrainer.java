package devs.mrp.springturkey.delta.validation.impl;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.database.service.DeltaServiceFacade;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataPushConstrainer;
import devs.mrp.springturkey.exceptions.WrongDataException;
import reactor.core.publisher.Mono;

@Service("deletionConstraints")
public class DeletionDataConstrainer implements DataPushConstrainer {

	@Autowired
	private DeltaServiceFacade deltaFacadeService;

	private static final String TEXT_VALUE = "deletion";

	@Override
	public Mono<Integer> pushDelta(Delta delta) throws WrongDataException {
		if (!DeltaType.DELETION.equals(delta.getDeltaType())) {
			throw new WrongDataException("Wrong delta type " + delta.getDeltaType());
		}
		Object deletionObject = getDeletionObject(delta);
		if (Objects.isNull(deletionObject)) {
			throw new WrongDataException("Payload should have a deletion property");
		}
		if (notTrue(deletionObject)) {
			throw new WrongDataException("Deletion action is not set to true");
		}
		return deltaFacadeService.pushDeletion(delta);
	}

	private Object getDeletionObject(Delta delta) {
		return delta.getJsonValue().get(TEXT_VALUE);
	}

	private boolean notTrue(Object obj) {
		return !(obj instanceof Boolean) && (!(obj instanceof String s) || !StringUtils.equalsIgnoreCase(String.valueOf(Boolean.TRUE), s));
	}

}
