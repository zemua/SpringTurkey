package devs.mrp.springturkey.delta.validation;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.delta.DeltaTable;

public interface DataConstrainerProvider {

	public DataConstrainer getFor(DeltaTable table) throws WrongDataException;

}
