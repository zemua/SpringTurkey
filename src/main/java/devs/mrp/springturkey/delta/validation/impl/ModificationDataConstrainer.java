package devs.mrp.springturkey.delta.validation.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataConstrainer;
import devs.mrp.springturkey.delta.validation.FieldValidator;

@Service("modificationConstraints")
public class ModificationDataConstrainer implements DataConstrainer {

	@Autowired
	private DeltaFacadeService deltaFacadeService;

	@Override
	public int pushDelta(Delta delta) throws WrongDataException {
		if (!isValid(delta)) {
			throw new WrongDataException("Incorrect field name");
		}
		return deltaFacadeService.pushModification(mapDeltaField(delta));
	}

	private boolean isValid(Delta delta) {
		return DeltaType.MODIFICATION.equals(delta.getDeltaType())
				&& getFieldMap(delta).containsKey(delta.getFieldName())
				&& getFieldMap(delta).get(delta.getFieldName()).isValidModification(delta.getTextValue());
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

	private Map<String,FieldValidator> getFieldMap(Delta delta) {
		return delta.getTable().getFieldMap();
	}



}
