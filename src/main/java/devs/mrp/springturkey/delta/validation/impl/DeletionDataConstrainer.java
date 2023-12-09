package devs.mrp.springturkey.delta.validation.impl;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.database.service.DeltaServiceFacade;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataPushConstrainer;
import devs.mrp.springturkey.exceptions.WrongDataException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service("deletionConstraints")
@Slf4j
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
		log.debug("Saving deletion delta {}", delta);
		return deltaFacadeService.pushDeletion(delta);
	}

	private Object getDeletionObject(Delta delta) throws WrongDataException {
		Map<String,Object> json = delta.getJsonValue();
		if (Objects.isNull(json)) {
			throw new WrongDataException("Json payload is null");
		}
		return json.get(TEXT_VALUE);
	}

	private boolean notTrue(Object obj) {
		return !(obj instanceof Boolean) && (!(obj instanceof String s) || !StringUtils.equalsIgnoreCase(String.valueOf(Boolean.TRUE), s));
	}

}
