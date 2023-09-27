package devs.mrp.springturkey.delta.validation.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataConstrainer;
import devs.mrp.springturkey.delta.validation.FieldData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service("modificationConstraints")
@Slf4j
public class ModificationDataConstrainer implements DataConstrainer {

	@Autowired
	private DeltaFacadeService deltaFacadeService;

	@Override
	public Mono<Integer> pushDelta(Delta delta) throws WrongDataException {
		validate(delta);
		return Mono.just(deltaFacadeService.pushModification(mapDeltaField(delta)));
	}

	private void validate(Delta delta) throws WrongDataException {
		if (! DeltaType.MODIFICATION.equals(delta.getDeltaType())) {
			throw new WrongDataException("Invalid action type: " + delta.getDeltaType());
		}
		if (!getFieldMap(delta).containsKey(delta.getFieldName())) {
			throw new WrongDataException("Invalid field name: " + delta.getFieldName());
		}
		if (!getFieldMap(delta).get(delta.getFieldName()).isValidModification(delta.getJsonValue())) {
			throw new WrongDataException("Invalid modification in field {" +  delta.getFieldName() + "} for value {" + delta.getJsonValue() + "}");
		}
	}

	private Delta mapDeltaField(Delta delta) throws WrongDataException {
		return delta.withFieldName(getColumnName(delta));
	}

	private String getColumnName(Delta delta) throws WrongDataException {
		String name = delta.getFieldName();
		if (!getFieldMap(delta).containsKey(name)) {
			throw new WrongDataException("Incorrect field name");
		}
		return getFieldMap(delta).get(name).getColumnName();
	}

	private Map<String,FieldData> getFieldMap(Delta delta) {
		return delta.getTable().getFieldMap();
	}



}
