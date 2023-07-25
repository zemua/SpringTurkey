package devs.mrp.springturkey.delta.validation;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.delta.DeltaType;

public interface DataConstrainerProvider {

	public DataConstrainer getFor(DeltaType type) throws WrongDataException;

}
