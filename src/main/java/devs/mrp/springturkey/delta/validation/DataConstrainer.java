package devs.mrp.springturkey.delta.validation;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.delta.Delta;

public interface DataConstrainer {

	public int pushDelta(Delta delta) throws WrongDataException;

}
