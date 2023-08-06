package devs.mrp.springturkey.delta.validation.impl;

import org.springframework.stereotype.Service;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataConstrainer;

@Service("deletionConstraints")
public class DeletionDataConstrainer implements DataConstrainer {

	private static final String fieldName = "object";

	@Override
	public int pushDelta(Delta delta) throws WrongDataException {
		if (!DeltaType.DELETION.equals(delta.getDeltaType())) {
			throw new WrongDataException("Wrong delta type " + delta.getDeltaType());
		}
		if (!fieldName.equals(delta.getFieldName())) {
			throw new WrongDataException("Wrong field name, should be 'object': " + delta.getFieldName());
		}
		return 1;
	}

}
