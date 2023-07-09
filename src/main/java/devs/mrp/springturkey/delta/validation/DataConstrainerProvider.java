package devs.mrp.springturkey.delta.validation;

import devs.mrp.springturkey.Exceptions.WrongDataException;

public interface DataConstrainerProvider {

	public DataConstrainer getFor(Table table) throws WrongDataException;

}
