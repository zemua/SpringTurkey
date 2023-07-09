package devs.mrp.springturkey.delta.validation;

import devs.mrp.springturkey.Exceptions.WrongDataException;

public interface DataConstrainer {

	public int pushDelta(ModificationDelta delta) throws WrongDataException;

}
