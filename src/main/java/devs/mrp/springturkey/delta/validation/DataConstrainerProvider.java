package devs.mrp.springturkey.delta.validation;

import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.exceptions.WrongDataException;

public interface DataConstrainerProvider {

	public DataConstrainer getFor(DeltaType type) throws WrongDataException;

}
